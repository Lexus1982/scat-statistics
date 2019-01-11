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

import me.alexand.scat.statistic.common.entities.CollectorStatRecord;
import me.alexand.scat.statistic.common.repository.CollectorStatRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author asidorov84@gmail.com
 */

@Component
public class StatCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatCollector.class);
    private static final int FREQUENCY = 60_000;//in ms

    private final AtomicInteger activeProcessorsCounter = new AtomicInteger(0);
    private final AtomicInteger inputQueueOverflowCounter = new AtomicInteger(0);
    private final AtomicInteger outputQueueOverflowCounter = new AtomicInteger(0);
    private final AtomicInteger receivedPacketsCounter = new AtomicInteger(0);
    private final AtomicInteger processedPacketsCounter = new AtomicInteger(0);
    private final AtomicInteger failedParseCounter = new AtomicInteger(0);
    private final AtomicInteger exportedRecordsCounter = new AtomicInteger(0);
    
    private final UUID uuid;
    private final String address;
    private final int port;
    private final LocalDateTime applicationStart = LocalDateTime.now();
    private LocalDateTime lastReportDateTime;
    
    private final CollectorStatRecordRepository statRecordRepository;

    public StatCollector(@Value("${net.address}") String address,
                         @Value("${net.port}") int port,
                         CollectorStatRecordRepository statRecordRepository) {
        lastReportDateTime = applicationStart;
        uuid = UUID.randomUUID();
        this.address = address;
        this.port = port;
        this.statRecordRepository = statRecordRepository;
    }

    public void registerProcessorThread() {
        activeProcessorsCounter.incrementAndGet();
    }

    public void unregisterProcessorThread() {
        activeProcessorsCounter.decrementAndGet();
    }

    public void registerReceivedPacket() {
        receivedPacketsCounter.incrementAndGet();
    }
    
    public void registerInputQueueOverflow() {
        inputQueueOverflowCounter.incrementAndGet();
    }
    
    public void registerOutputQueueOverflow() {
        outputQueueOverflowCounter.incrementAndGet();
    }

    public void registerProcessedPacket() {
        processedPacketsCounter.incrementAndGet();
    }

    public void registerParseFail() {
        failedParseCounter.incrementAndGet();
    }
    
    public void registerExportedRecord(int count) {
        exportedRecordsCounter.updateAndGet(operand -> operand + count);
    }

    @Scheduled(fixedDelay = FREQUENCY, initialDelay = 5_000)
    public void report() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int period = (int) ChronoUnit.SECONDS.between(lastReportDateTime, currentDateTime);
        lastReportDateTime = currentDateTime;

        CollectorStatRecord statRecord = CollectorStatRecord.builder()
                .uuid(uuid)
                .address(address)
                .port(port)
                .started(applicationStart)
                .lastUpdated(lastReportDateTime)
                .period(period)
                .processorsThreadsCount(activeProcessorsCounter.get())
                .packetsReceivedCount(receivedPacketsCounter.getAndSet(0))
                .packetsProcessedCount(processedPacketsCounter.getAndSet(0))
                .packetsParseFailedCount(failedParseCounter.getAndSet(0))
                .inputQueueOverflowCount(inputQueueOverflowCounter.get())
                .outputQueueOverflowCount(outputQueueOverflowCounter.get())
                .recordsExportedCount(exportedRecordsCounter.getAndSet(0))
                .build();
        
        if (!statRecordRepository.save(statRecord)) {
            LOGGER.error("failed to save collector statistics");
        }
    }
}
