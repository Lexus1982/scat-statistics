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

package me.alexand.scat.statistic.collector.network;

import me.alexand.scat.statistic.collector.service.StatCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.MAX_PRIORITY;
import static java.util.Arrays.copyOf;
import static me.alexand.scat.statistic.collector.model.IPFIXHeader.IPFIX_MESSAGE_HEADER_LENGTH;
import static me.alexand.scat.statistic.collector.model.IPFIXHeader.IPFIX_MESSAGE_VERSION;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.fourBytesToLong;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.twoBytesToInt;
import static me.alexand.scat.statistic.collector.utils.Constants.TCP_LISTEN_BACKLOG;

/**
 * TCP-приемник пакетов c IPFIX-сообщениями.
 * <p>
 * Реализация на основе сетевого протокола TCP. Создается отдельный поток, задачей которого является прослушивание
 * сокета и, при установлении каждого нового подключения, создание новой сессии (так же в отдельном потоке). В рамках
 * сессии создается DataInputStream из которого данные поступают в виде непрерывного потока байт. Чтобы отличить
 * пакеты (IPFIX-сообщения) друг от друга, сначала считывается 16-байтный заголовок сообщения и вычисляется его длина.
 * Затем уже считывается тело сообщения, и вместе с заголовком помещается во внутренний буфер. Доступ к пакетам
 * осуществляется через метод getNextPacket().
 * <p>
 * Обязательными параметрами для создания экземпляра являются IP-адрес и порт для создания сокета, а также размер
 * внутреннего буфера (в пакетах) и размер приемного буфера сокета для TCP (SO_RCVBUF)
 *
 * @author asidorov84@gmail.com
 */
@Component
@Lazy
public final class TCPPacketsReceiver implements PacketsReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPPacketsReceiver.class);
    private final int bufferCapacity;
    private final BlockingQueue<byte[]> packetsBuffer;

    private final ServerSocket serverSocket;
    private final Thread connectionListenerThread;
    private final List<Thread> sessionThreads = new ArrayList<>();

    private final StatCollector statCollector;

    @Autowired
    public TCPPacketsReceiver(@Value("${net.address}") String address,
                              @Value("${net.port}") int port,
                              @Value("${packet.buffer.capacity}") int bufferCapacity,
                              @Value("${socket.receive.buffer.size}") int socketReceiveBufferSize,
                              StatCollector statCollector) throws IOException {
        if (bufferCapacity <= 0) {
            throw new IllegalArgumentException(String.format("Illegal value of buffer capacity: %d", bufferCapacity));
        }

        if (socketReceiveBufferSize <= 0) {
            throw new IllegalArgumentException(String.format("Illegal value of socket receive buffer size: %d",
                    socketReceiveBufferSize));
        }

        this.statCollector = statCollector;
        this.bufferCapacity = bufferCapacity;

        packetsBuffer = new ArrayBlockingQueue<>(bufferCapacity);
        LOGGER.debug("Initialize internal packets buffer with size: {}", bufferCapacity);

        serverSocket = new ServerSocket(port, TCP_LISTEN_BACKLOG, InetAddress.getByName(address));
        serverSocket.setReceiveBufferSize(socketReceiveBufferSize);

        LOGGER.debug("Created server socket on {}:{}",
                serverSocket.getInetAddress().getHostAddress(),
                serverSocket.getLocalPort());

        connectionListenerThread = new Thread(new Server(), "connection-listener-thread");
    }
    
    @Override
    public void start() {
        connectionListenerThread.start();
    }

    @Override
    public byte[] getNextPacket() throws InterruptedException {
        return packetsBuffer.take();
    }

    @Override
    public boolean shutdown() {
        //Прекращаем слушать новые подключения к коллектору
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        LOGGER.debug("...server socket closed");

        connectionListenerThread.interrupt();
        try {
            while (connectionListenerThread.isAlive()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        
        //Закрываем все текущие открытые подключения
        sessionThreads.forEach(Thread::interrupt);
        
        return true;
    }

    @Override
    public int getRemainingPacketsCount() {
        return bufferCapacity - packetsBuffer.remainingCapacity();
    }

    private class Server implements Runnable {
        @Override
        public void run() {
            int sessionsCounter = 1;

            try {
                LOGGER.info("Started listening for incoming connections...");

                while (!connectionListenerThread.isInterrupted()) {
                    Socket sessionSocket = serverSocket.accept();

                    LOGGER.info("Got connection from {}:{}",
                            sessionSocket.getInetAddress().getHostAddress(),
                            sessionSocket.getPort());

                    LOGGER.debug("Start new TCP session with id = {}", sessionsCounter);
                    LOGGER.debug("Receive buffer size of connection socket: {}", sessionSocket.getReceiveBufferSize());

                    Session session = new Session(sessionSocket, sessionsCounter);
                    Thread sessionThread = new Thread(session, String.format("tcp-session-%d-thread", sessionsCounter++));
                    sessionThread.setPriority(MAX_PRIORITY);
                    sessionThread.start();
                    sessionThreads.add(sessionThread);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }

            LOGGER.info("Stopped listening for incoming connections...");
        }
    }

    private class Session implements Runnable {
        private static final long MAX_SEQUENCE_NUMBER = 4294967296L;
        private final Socket socket;
        private final int id;
        private final byte[] packetBuffer = new byte[65535];
        private final byte[] header = new byte[IPFIX_MESSAGE_HEADER_LENGTH];

        private long domainID;
        private long prevSequenceNumber;
        private boolean isFirstPacket = true;

        Session(Socket socket, int id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            LOGGER.info("Start receiving packets within new session (id = {})...", id);

            try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                while (!Thread.currentThread().isInterrupted()) {
                    dis.readFully(header, 0, IPFIX_MESSAGE_HEADER_LENGTH);
                    System.arraycopy(header, 0, packetBuffer, 0, IPFIX_MESSAGE_HEADER_LENGTH);

                    int version = twoBytesToInt(header, 0);
                    if (version != IPFIX_MESSAGE_VERSION) {
                        LOGGER.debug("Illegal version of message: {}", version);
                        LOGGER.debug("Closing session (id = {})...", id);
                        break;
                    }

                    int fullMessageLength = twoBytesToInt(header, 2);
                    long currentSequenceNumber = fourBytesToLong(header, 8);

                    if (isFirstPacket) {
                        //DomainID не меняется в рамках TCP-сессии.
                        domainID = fourBytesToLong(header, 12);
                        prevSequenceNumber = currentSequenceNumber;
                        isFirstPacket = false;
                    } else {
                        long exportedRecordsCounter;
                        //если текущее значение меньше чем предыдущее, значит было достигнуто максимальное значение
                        //для беззнакового 4-х байтового типа (2^32)
                        if (currentSequenceNumber < prevSequenceNumber) {
                            exportedRecordsCounter = MAX_SEQUENCE_NUMBER - prevSequenceNumber + currentSequenceNumber;
                        } else {
                            exportedRecordsCounter = currentSequenceNumber - prevSequenceNumber;
                        }

                        statCollector.registerExportedRecords(domainID, exportedRecordsCounter);
                        prevSequenceNumber = currentSequenceNumber;
                    }

                    //Теперь, зная длину всего сообщения, читаем его тело
                    dis.readFully(packetBuffer, IPFIX_MESSAGE_HEADER_LENGTH, fullMessageLength - IPFIX_MESSAGE_HEADER_LENGTH);

                    //И копируем его в буфер
                    if (!packetsBuffer.offer(copyOf(packetBuffer, fullMessageLength))) {
                        statCollector.registerInputBufferOverflow();
                    }
                }

                socket.close();
                LOGGER.debug("Socket of session (id = {}) closed", id);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }

            LOGGER.info("Stop receiving packets within new session (id = {})...", id);
        }
    }
}
