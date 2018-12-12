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

package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.service.InputPacketsQueue;
import me.alexand.scat.statistic.collector.utils.exceptions.InputQueueOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Реализация очереди входящих пакетов
 *
 * @author asidorov84@gmail.com
 */
@Component
public class InputPacketsQueueImpl implements InputPacketsQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputPacketsQueue.class);

    private final int queueLength;
    private final BlockingQueue<byte[]> packetsQueue;

    public InputPacketsQueueImpl(@Value("${input.packet.queue.length}") int queueLength) {
        if (queueLength <= 0) {
            throw new IllegalArgumentException(String.format("Illegal length of input packets queue: %d", queueLength));
        }

        this.queueLength = queueLength;
        this.packetsQueue = new ArrayBlockingQueue<>(queueLength);
        LOGGER.debug("initialize input packets queue with length: {}", queueLength);
    }

    @Override
    public void put(byte[] rawPacket) throws InputQueueOverflowException {
        if (!packetsQueue.offer(rawPacket)) {
            throw new InputQueueOverflowException("input queue overflow detected");
        }
    }

    @Override
    public byte[] takeNext() throws InterruptedException {
        return packetsQueue.take();
    }

    @Override
    public long getRemainingPacketsCount() {
        return queueLength - packetsQueue.remainingCapacity();
    }
}
