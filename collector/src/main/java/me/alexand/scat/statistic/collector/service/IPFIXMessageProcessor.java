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

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.model.IPFIXSet;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.network.PacketsReceiver;
import me.alexand.scat.statistic.collector.utils.exceptions.IPFIXParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Процессор, управляющий логикой получения пакета по сети,
 * преобразования его в IPFIX-сообщение и сохранения декодированных
 * данных во внутреннем буфере
 *
 * @author asidorov84@gmail.com
 */

@Component
@Scope("prototype")
public final class IPFIXMessageProcessor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXMessageProcessor.class);

    private static volatile int processorsCounter = 0;
    private int processorId;

    private final int batchSize;
    private final Map<TemplateType, List<IPFIXDataRecord>> interimStorage = new HashMap<>();

    private final StatCollector statCollector;
    private final PacketsReceiver receiver;
    private final IPFIXParser parser;
    private final TransitionalBufferRecorder transitionalBufferRecorder;

    @Autowired
    public IPFIXMessageProcessor(@Value("${processor.records.batch.size}") int batchSize,
                                 IPFIXParser parser,
                                 PacketsReceiver receiver,
                                 TransitionalBufferRecorder transitionalBufferRecorder,
                                 StatCollector statCollector) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException(String.format("batch size of records is illegal: %s", batchSize));
        }
        this.batchSize = batchSize;

        synchronized (IPFIXMessageProcessor.class) {
            processorId = ++processorsCounter;
        }

        this.parser = parser;
        this.receiver = receiver;
        this.transitionalBufferRecorder = transitionalBufferRecorder;
        this.statCollector = statCollector;

        for (TemplateType type : TemplateType.values()) {
            interimStorage.put(type, new ArrayList<>(batchSize));
        }
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        LOGGER.info("...start new processor with id = {}", processorId);
        statCollector.registerProcessorThread(processorId);

        while (!currentThread.isInterrupted()) {
            byte[] rawPacket;

            try {
                rawPacket = receiver.getNextPacket();
                statCollector.registerReceivedPacket(processorId);
            } catch (InterruptedException e) {
                break;
            }

            try {
                long t0 = System.nanoTime();
                IPFIXMessage message = parser.parse(rawPacket);
                long t1 = System.nanoTime();

                statCollector.registerProcessedPacket(processorId, t1 - t0);
                long processedRecordsNumber = 0;

                for (IPFIXSet set : message.getSets()) {
                    int setID = set.getSetID();

                    if (setID >= 256 && setID <= 65535) {
                        processedRecordsNumber += set.getRecords().size();

                        set.getRecords().forEach(record -> {
                            if (record instanceof IPFIXDataRecord) {
                                IPFIXDataRecord dataRecord = (IPFIXDataRecord) record;
                                TemplateType dataRecordType = dataRecord.getType();
                                List<IPFIXDataRecord> batchList = interimStorage.get(dataRecordType);
                                batchList.add(dataRecord);

                                if (batchList.size() == batchSize) {
                                    transitionalBufferRecorder.transfer(dataRecordType, batchList);
                                    interimStorage.put(dataRecordType, new ArrayList<>(batchSize));
                                }
                            }
                        });
                    }
                }

                statCollector.registerProcessedRecords(message.getHeader().getObservationDomainID(), processedRecordsNumber);
            } catch (IPFIXParseException e) {
                //TODO сделать учет ошибок разного типа и выводить их в периодическом отчете
            }
        }

        LOGGER.info("...shutdown of processor with id = {} complete", processorId);
        statCollector.unregisterProcessorThread();
    }
}