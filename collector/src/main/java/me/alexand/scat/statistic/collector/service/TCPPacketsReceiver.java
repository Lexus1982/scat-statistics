/*
 * Copyright 2018 Alexander Sidorov (asidorov84@gmail.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.utils.exceptions.InputQueueOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.copyOf;
import static me.alexand.scat.statistic.collector.model.IPFIXHeader.IPFIX_MESSAGE_HEADER_LENGTH;
import static me.alexand.scat.statistic.collector.model.IPFIXHeader.IPFIX_MESSAGE_VERSION;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.twoBytesToInt;


/**
 * TCP-приемник пакетов c IPFIX-сообщениями.
 * <p>
 * Реализация на основе сетевого протокола TCP. Создается отдельный поток, задачей которого является прослушивание
 * сокета и, при установлении каждого нового подключения, создание новой сессии (так же в отдельном потоке). В рамках
 * сессии создается DataInputStream из которого данные поступают в виде непрерывного потока байт. Чтобы отличить
 * пакеты (IPFIX-сообщения) друг от друга, сначала считывается 16-байтный заголовок сообщения и вычисляется его длина.
 * Затем уже считывается тело сообщения, и вместе с заголовком отправляется в очередь.
 * <p>
 * Обязательными параметрами для создания экземпляра являются IP-адрес и порт для инициализации серверного сокета,
 * а также размер приемного буфера сокета для TCP (SO_RCVBUF)
 *
 * @author asidorov84@gmail.com
 */
@Component
public final class TCPPacketsReceiver extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPPacketsReceiver.class);
    private static final int TCP_LISTEN_BACKLOG = 10;

    private final ServerSocket serverSocket;
    private final List<Thread> sessionThreads = new ArrayList<>();
    private final InputPacketsQueue inputPacketsQueue;

    private final StatCollector statCollector;

    public TCPPacketsReceiver(@Value("${net.address}") String address,
                              @Value("${net.port}") int port,
                              @Value("${socket.receive.buffer.size}") int socketReceiveBufferSize,
                              InputPacketsQueue inputPacketsQueue,
                              StatCollector statCollector) throws IOException {
        if (socketReceiveBufferSize <= 0) {
            throw new IllegalArgumentException(String.format("Illegal value of socket receive buffer size: %d",
                    socketReceiveBufferSize));
        }

        this.inputPacketsQueue = inputPacketsQueue;
        this.statCollector = statCollector;

        serverSocket = new ServerSocket(port, TCP_LISTEN_BACKLOG, InetAddress.getByName(address));
        serverSocket.setReceiveBufferSize(socketReceiveBufferSize);

        LOGGER.debug("Created server socket on {}:{}",
                serverSocket.getInetAddress().getHostAddress(),
                serverSocket.getLocalPort());

        setName("connection-listener-thread");
    }

    @Override
    public void run() {
        LOGGER.info("Started listening for incoming connections...");

        try {
            int sessionCounter = 1;

            while (!isInterrupted()) {
                Socket sessionSocket = serverSocket.accept();
                sessionThreads.add(new Session(sessionSocket, sessionCounter++));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("Stopped listening for incoming connections...");
    }

    public void shutdown() {
        //Прекращаем слушать новые подключения к коллектору
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.debug("...server socket closed");

        //Закрываем все текущие сессии
        sessionThreads.forEach(Thread::interrupt);

        while (!sessionThreads.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private class Session extends Thread {
        private final Socket socket;
        private final int id;
        private final byte[] rawFullPacket = new byte[65535];
        private final byte[] rawHeader = new byte[IPFIX_MESSAGE_HEADER_LENGTH];

        private Session(Socket socket, int id) {
            this.socket = socket;
            this.id = id;
            setName(String.format("tcp-session-%d-thread", id));
            setPriority(MAX_PRIORITY);
            start();
        }

        @Override
        public void run() {
            LOGGER.info("Got connection from {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
            LOGGER.debug("Start new TCP session with id = {}", id);

            try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                while (!isInterrupted()) {
                    //Читаем заголовок сообщения
                    dis.readFully(rawHeader, 0, IPFIX_MESSAGE_HEADER_LENGTH);

                    int version = twoBytesToInt(rawHeader, 0);
                    if (version != IPFIX_MESSAGE_VERSION) {
                        LOGGER.error("Illegal version of message: {}", version);
                        LOGGER.error("Closing session (id = {})...", id);
                        break;
                    }

                    int fullMessageLength = twoBytesToInt(rawHeader, 2);

                    //Вставляем заголовок
                    System.arraycopy(rawHeader, 0, rawFullPacket, 0, IPFIX_MESSAGE_HEADER_LENGTH);

                    //Теперь, зная длину всего сообщения, читаем его тело...
                    dis.readFully(rawFullPacket, IPFIX_MESSAGE_HEADER_LENGTH, fullMessageLength - IPFIX_MESSAGE_HEADER_LENGTH);

                    //...и отправляем все сообщение в очередь
                    try {
                        inputPacketsQueue.put(copyOf(rawFullPacket, fullMessageLength));
                        statCollector.registerReceivedPacket();
                    } catch (InputQueueOverflowException e) {
                        statCollector.registerInputQueueOverflow();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

            sessionThreads.remove(this);
            LOGGER.info("Stop receiving packets within new session (id = {})...", id);
        }
    }
}
