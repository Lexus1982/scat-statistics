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
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.Arrays.copyOf;

/**
 * Приемник UDP-пакетов
 * <p>
 * В отдельном потоке получает UDP-пакеты на указанном сокете
 * и сохраняет во внутреннем буфере.
 *
 * @author asidorov84@gmail.com
 */

@Component
public class UDPPacketsReceiver implements PacketsReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPPacketsReceiver.class);
    private static final int UDP_RECEIVE_BUFFER_SIZE = 100 * 1024 * 1024;

    private final DatagramSocket socket;
    private final byte[] buffer = new byte[65535];
    private final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

    private final Thread receiverThread;
    private final BlockingQueue<byte[]> packetsBuffer;

    private final StatCollector statCollector;

    public UDPPacketsReceiver(@Value("${net.address}") String listenAddress,
                              @Value("${net.port}") int listenPort,
                              @Value("${packet.buffer.capacity}") int bufferCapacity,
                              StatCollector statCollector) throws SocketException, UnknownHostException {
        this.statCollector = statCollector;

        socket = new DatagramSocket(listenPort, InetAddress.getByName(listenAddress));
        LOGGER.info("Creating socket on {}:{}",
                socket.getLocalAddress().getHostAddress(),
                socket.getLocalPort());

        socket.setReceiveBufferSize(UDP_RECEIVE_BUFFER_SIZE);
        LOGGER.info("Set socket receive buffer size: {}", UDP_RECEIVE_BUFFER_SIZE);
        LOGGER.info("Receive buffer size: {}", socket.getReceiveBufferSize());

        packetsBuffer = new ArrayBlockingQueue<>(bufferCapacity);
        LOGGER.info("Initialize packets buffer with size: {}", bufferCapacity);

        receiverThread = new Thread(new Receiver(), "udp-receiver-thread");
        receiverThread.setPriority(Thread.MAX_PRIORITY);
        receiverThread.start();
    }

    @Override
    public byte[] getNextPacket() throws InterruptedException {
        return packetsBuffer.take();
    }

    @PreDestroy
    private void shutdown() {
        socket.close();
    }

    private class Receiver implements Runnable {
        @Override
        public void run() {
            LOGGER.info("Start receiving packets...");

            try {
                while (!receiverThread.isInterrupted()) {
                    packet.setLength(buffer.length);
                    socket.receive(packet);

                    if (!packetsBuffer.offer(copyOf(buffer, packet.getLength()))) {
                        statCollector.registerInputBufferOverflow();
                    }
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
            shutdown();
            LOGGER.info("Stop receiving packets...");
        }
    }
}