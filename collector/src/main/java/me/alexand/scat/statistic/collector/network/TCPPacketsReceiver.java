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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.MAX_PRIORITY;
import static java.util.Arrays.copyOf;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.fourBytesToLong;
import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.twoBytesToInt;
import static me.alexand.scat.statistic.collector.utils.Constants.MESSAGE_HEADER_LENGTH;

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
@Component("TCPPacketsReceiver")
@Lazy
public final class TCPPacketsReceiver implements PacketsReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPPacketsReceiver.class);
    private final BlockingQueue<byte[]> packetsBuffer;

    private final ServerSocket serverSocket;
    private final Thread connectionListenerThread;
    private final List<Thread> sessionThreads = new ArrayList<>(10);

    private final StatCollector statCollector;

    public TCPPacketsReceiver(@Value("${net.address}") String address,
                              @Value("${net.port}") int port,
                              @Value("${packet.buffer.capacity}") int bufferCapacity,
                              @Value("${socket.receive.buffer.size}") int socketReceiveBufferSize,
                              StatCollector statCollector) throws IOException {
        if (bufferCapacity <= 0) {
            throw new IllegalArgumentException("illegal size of buffer");
        }

        if (socketReceiveBufferSize <= 0) {
            throw new IllegalArgumentException("illegal SO_RCVBUF size");
        }

        this.statCollector = statCollector;

        packetsBuffer = new ArrayBlockingQueue<>(bufferCapacity);
        LOGGER.debug("Initialize internal packets buffer with size: {}", bufferCapacity);

        serverSocket = new ServerSocket(port, 10, InetAddress.getByName(address));
        serverSocket.setReceiveBufferSize(socketReceiveBufferSize);
        
        LOGGER.debug("Created server socket on {}:{}",
                serverSocket.getInetAddress().getHostAddress(),
                serverSocket.getLocalPort());

        connectionListenerThread = new Thread(new Server(), "connection-listener-thread");
        connectionListenerThread.start();
    }

    @Override
    public byte[] getNextPacket() throws InterruptedException {
        return packetsBuffer.take();
    }

    @PreDestroy
    private void shutdown() {
        try {
            serverSocket.close();
            LOGGER.debug("Server socket closed");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        sessionThreads.forEach(Thread::interrupt);
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
            } catch (SocketException e) {}
            catch (IOException e) {
                LOGGER.error(e.getMessage());
            }

            LOGGER.info("Stopped listening for incoming connections...");
        }
    }

    private class Session implements Runnable {
        private final Socket socket;
        private final int id;
        private final byte[] packetBuffer = new byte[65535];
        private final byte[] header = new byte[MESSAGE_HEADER_LENGTH];

        private long sequenceNumberOffset;
        private boolean isFirstPacket = true;

        public Session(Socket socket, int id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            LOGGER.debug("Start receiving packets within new session (id = {})...", id);

            try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                while (!Thread.currentThread().isInterrupted()) {
                    //Сообщения передаются в виде непрерывного набора байт. Чтобы отличить их между собой,
                    //нужно сначала прочитать 16-ти байтовый заголовок очередного сообщения. В заголовке
                    //3-й и 4-й байт означают длину всего сообщения (включая сам заголовок) в байтах.

                    //читаем первые 16 байт заголовка
                    dis.readFully(header, 0, header.length);

                    //копируем заголовок в общий массив байт целого сообщения
                    System.arraycopy(header, 0, packetBuffer, 0, header.length);

                    int fullMessageLength = twoBytesToInt(header, 2);
                    
                    //Для каждого домена поле sequenceNumber означает количество переданных от СКАТа записей (кроме шаблонов)
                    //Задача: нужно подсчитать, сколько было передано записей СКАТом с момента создании сессии
                    //При получении первого пакета сохраняем количество уже переданных в sequenceNumberOffset (до создания данной сессии)
                    long sequenceNumber = fourBytesToLong(header, 8);
                    
                    //TODO Интересно, а domainID в рамках сессии меняется?
                    long domainID = fourBytesToLong(header, 12);

                    if (isFirstPacket) {
                        sequenceNumberOffset = sequenceNumber;
                        isFirstPacket = false;
                    } else {
                        statCollector.registerExportedRecords(domainID, sequenceNumber - sequenceNumberOffset);
                    }

                    //Теперь, зная длину сообщения, читаем его тело
                    dis.readFully(packetBuffer, MESSAGE_HEADER_LENGTH, fullMessageLength - MESSAGE_HEADER_LENGTH);

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
