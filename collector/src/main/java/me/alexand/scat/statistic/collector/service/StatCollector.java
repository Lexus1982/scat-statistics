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

import me.alexand.scat.statistic.collector.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author asidorov84@gmail.com
 */

@Component
public class StatCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatCollector.class);

    private final AtomicInteger activeProcessorsCounter = new AtomicInteger(0);
    private final AtomicInteger inputQueueOverflowCounter = new AtomicInteger(0);
    private final AtomicInteger outputQueueOverflowCounter = new AtomicInteger(0);
    private final AtomicLong receivedPacketsCounter = new AtomicLong(0);
    private final AtomicLong processedPacketsCounter = new AtomicLong(0);
    private final AtomicLong failedParseCounter = new AtomicLong(0);
    
    private final InputPacketsQueue inputPacketsQueue;
    private final OutputRecordsQueue outputRecordsQueue;

    private final LocalDateTime applicationStart = LocalDateTime.now();
    private LocalDateTime lastReportDateTime;

    public StatCollector(InputPacketsQueue inputPacketsQueue, OutputRecordsQueue outputRecordsQueue) {
        this.inputPacketsQueue = inputPacketsQueue;
        this.outputRecordsQueue = outputRecordsQueue;
        lastReportDateTime = applicationStart;
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

    @Scheduled(fixedDelay = 60_000, initialDelay = 5_000)
    public void report() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        long secondsSinceLastReport = ChronoUnit.SECONDS.between(lastReportDateTime, currentDateTime);
        lastReportDateTime = currentDateTime;

        StringBuilder sb = new StringBuilder("\n\nStart periodical collector report....\n");

        sb.append("\n\tuptime: ")
                .append(getUptime(currentDateTime))
                .append("\n");

        sb.append("\tactive parser threads: ")
                .append(activeProcessorsCounter.get())
                .append("\n\n");

        sb.append("\tpackets receive rate: ")
                .append(receivedPacketsCounter.getAndSet(0) / secondsSinceLastReport)
                .append(" pps\n");

        sb.append("\tpackets processed rate: ")
                .append(processedPacketsCounter.getAndSet(0) / secondsSinceLastReport)
                .append(" pps\n");

        sb.append("\tpackets parse failed rate: ")
                .append(failedParseCounter.getAndSet(0) / secondsSinceLastReport)
                .append(" pps\n");

        sb.append("\tinput packets queue overflows: ")
                .append(inputQueueOverflowCounter.get())
                .append("\n");

        sb.append("\toutput records queue overflows: ")
                .append(outputQueueOverflowCounter.get())
                .append("\n");


        sb.append("\tpackets number in input queue for now: ")
                .append(inputPacketsQueue.getRemainingPacketsCount())
                .append(" \n");

        sb.append("\trecords number in output queue for now: ")
                .append(outputRecordsQueue.getRemainingRecordsCount())
                .append(" \n");

        sb.append("\n...end of periodical collector report\n");

        LOGGER.info(sb.toString());
    }

    private String getUptime(LocalDateTime currentDateTime) {
        return DateTimeUtils.getFormattedDifferenceBetweenLocalDateTime(applicationStart, currentDateTime);
    }
}
