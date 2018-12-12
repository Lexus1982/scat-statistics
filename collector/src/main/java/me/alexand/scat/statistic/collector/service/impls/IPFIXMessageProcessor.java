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

import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import me.alexand.scat.statistic.collector.service.InputPacketsQueue;
import me.alexand.scat.statistic.collector.service.OutputRecordsQueue;
import me.alexand.scat.statistic.collector.service.StatCollector;
import me.alexand.scat.statistic.collector.utils.exceptions.IPFIXParseException;
import me.alexand.scat.statistic.collector.utils.exceptions.OutputQueueOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Процессор для обработки полученных пакетов с IPFIX-сообщениями
 *
 * @author asidorov84@gmail.com
 */

@Component
@Scope("prototype")
public final class IPFIXMessageProcessor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXMessageProcessor.class);

    private static volatile int processorsCounter = 0;
    private int processorId;

    private final StatCollector statCollector;
    private final InputPacketsQueue inputPacketsQueue;
    private final OutputRecordsQueue outputRecordsQueue;
    private final IPFIXParser parser;

    public IPFIXMessageProcessor(IPFIXParser parser,
                                 InputPacketsQueue inputPacketsQueue,
                                 OutputRecordsQueue outputRecordsQueue,
                                 StatCollector statCollector) {
        synchronized (IPFIXMessageProcessor.class) {
            processorId = ++processorsCounter;
        }

        this.inputPacketsQueue = inputPacketsQueue;
        this.outputRecordsQueue = outputRecordsQueue;
        this.parser = parser;
        this.statCollector = statCollector;
    }

    @Override
    public void run() {
        LOGGER.info("...start new processor with id = {}", processorId);
        statCollector.registerProcessorThread();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                IPFIXMessage message = parser.parse(inputPacketsQueue.takeNext());
                statCollector.registerProcessedPacket();

                try {
                    outputRecordsQueue.put(message.getDataRecords());
                } catch (OutputQueueOverflowException e) {
                    statCollector.registerOutputQueueOverflow();
                }
            } catch (IPFIXParseException e) {
                statCollector.registerParseFail();
            } catch (InterruptedException e) {
                break;
            }
        }

        LOGGER.info("...shutdown of processor with id = {} complete", processorId);
        statCollector.unregisterProcessorThread();
    }
}