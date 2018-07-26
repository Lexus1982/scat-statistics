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

import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class StatCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatCollector.class);

    private final AtomicInteger activeProcessorsCounter = new AtomicInteger(0);
    private final AtomicInteger inputBufferOverflowCounter = new AtomicInteger(0);
    private final Map<TemplateType, Integer> recorderBuffersOverflowCounter;
    private final Map<Integer, Long> receivedPacketsCounter = new ConcurrentHashMap<>();
    private final Map<Integer, Long> processedPacketsCounter = new ConcurrentHashMap<>();
    private final Map<Integer, Long> processedPacketsTotalTimeCounter = new ConcurrentHashMap<>();
    private final Map<Long, Long> recordsCounter = new ConcurrentHashMap<>();

    private final LocalDateTime applicationStart = LocalDateTime.now();
    private LocalDateTime lastReportDateTime;

    public StatCollector() {
        lastReportDateTime = applicationStart;
        recorderBuffersOverflowCounter = new ConcurrentHashMap<>();
        
        for (TemplateType templateType : TemplateType.values()) {
            recorderBuffersOverflowCounter.put(templateType, 0);
        }
    }

    public void registerProcessorThread(int processorId) {
        activeProcessorsCounter.incrementAndGet();
        receivedPacketsCounter.put(processorId, 0L);
        processedPacketsCounter.put(processorId, 0L);
        processedPacketsTotalTimeCounter.put(processorId, 0L);
    }

    public void unregisterProcessorThread() {
        activeProcessorsCounter.decrementAndGet();
    }

    public void registerInputBufferOverflow() {
        inputBufferOverflowCounter.incrementAndGet();
    }

    public void registerRecorderBufferOverflow(TemplateType type) {
        recorderBuffersOverflowCounter.merge(type, 1, (oldValue, newValue) -> (oldValue + newValue));
    }

    public void registerReceivedPacket(int processorId) {
        receivedPacketsCounter.merge(processorId, 1L, (oldValue, newValue) -> oldValue + newValue);
    }

    public void registerExportedRecords(long domainID, long exportedRecordsNumber) {
        recordsCounter.merge(domainID, exportedRecordsNumber, (oldValue, newValue) -> oldValue + newValue);
    }

    public void registerProcessedPacket(int processorId, long time) {
        processedPacketsCounter.merge(processorId, 1L, (oldValue, newValue) -> oldValue + newValue);
        processedPacketsTotalTimeCounter.merge(processorId, time, (oldValue, newValue) -> oldValue + newValue);
    }

    public void registerProcessedRecords(long domainID, long processedRecordsNumber) {
        recordsCounter.merge(domainID, processedRecordsNumber, (oldValue, newValue) -> oldValue - newValue);
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 5_000)
    public void report() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        long secondsSinceLastReport = ChronoUnit.SECONDS.between(lastReportDateTime, currentDateTime);
        lastReportDateTime = currentDateTime;

        List<Map.Entry<Integer, Long>> receivedPacketsCountersForNow = new ArrayList<>(receivedPacketsCounter.entrySet());
        resetPacketsCounters(receivedPacketsCounter);

        List<Map.Entry<Integer, Long>> processedPacketsCountersForNow = new ArrayList<>(processedPacketsCounter.entrySet());
        resetPacketsCounters(processedPacketsCounter);

        List<Map.Entry<Integer, Long>> processedPacketsTotalTimeCountersForNow = new ArrayList<>(processedPacketsTotalTimeCounter.entrySet());
        resetPacketsCounters(processedPacketsTotalTimeCounter);

        StringBuilder sb = new StringBuilder("\n\nStart periodical collector report....\n");

        sb.append("\n\tuptime: ")
                .append(getUptime(currentDateTime))
                .append("\n");

        sb.append("\tactive parser threads: ")
                .append(activeProcessorsCounter.get())
                .append("\n\n");

        sb.append("\tinput buffer overflows: ")
                .append(inputBufferOverflowCounter.get())
                .append("\n");

        sb.append("\trecorder buffers overflows: ")
                .append(recorderBuffersOverflowCounter.entrySet())
                .append("\n\n");

        sb.append("\tpackets received rates per processor: ")
                .append(getReceivedPacketsRatesPerProcessor(receivedPacketsCountersForNow, secondsSinceLastReport))
                .append("\n");

        sb.append("\ttotal packets received rates: ")
                .append(getTotalReceivedPacketsRates(receivedPacketsCountersForNow, secondsSinceLastReport))
                .append(" pps\n\n");

        sb.append("\tpackets processed rates per processor: ")
                .append(getProcessedPacketsRatesPerProcessor(processedPacketsCountersForNow, secondsSinceLastReport))
                .append("\n");

        sb.append("\ttotal packets processed rates: ")
                .append(getTotalProcessedPacketsRates(processedPacketsCountersForNow, secondsSinceLastReport))
                .append(" pps\n\n");

        sb.append("\tpackets average parse time: ")
                .append(getProcessedPacketsAverageParseTime(processedPacketsTotalTimeCountersForNow,
                        processedPacketsCountersForNow))
                .append("\n\n");

        sb.append("\trecords that not yet processed per domain: ")
                .append(recordsCounter.entrySet())
                .append("\n");

        sb.append("\n...end of periodical collector report\n");

        LOGGER.info(sb.toString());
    }

    private void resetPacketsCounters(Map<?, Long> packetsCounter) {
        packetsCounter.entrySet().forEach(e -> e.setValue(Long.valueOf(0L)));
    }

    private String getUptime(LocalDateTime currentDateTime) {
        return DateTimeUtils.getFormattedDifferenceBetweenLocalDateTime(applicationStart, currentDateTime);
    }

    private String getReceivedPacketsRatesPerProcessor(List<Map.Entry<Integer, Long>> receivedPacketsCountersForNow,
                                                       final long secondsSinceLastReport) {
        return receivedPacketsCountersForNow.stream()
                .map(e -> String.format("% d: %d pps", e.getKey(), e.getValue() / secondsSinceLastReport))
                .collect(toList())
                .toString();
    }

    private long getTotalReceivedPacketsRates(List<Map.Entry<Integer, Long>> receivedPacketsCountersForNow,
                                              final long secondsSinceLastReport) {
        return receivedPacketsCountersForNow.stream()
                .mapToLong(Map.Entry::getValue)
                .sum() / secondsSinceLastReport;
    }

    private String getProcessedPacketsRatesPerProcessor(List<Map.Entry<Integer, Long>> processedPacketsCountersForNow,
                                                        final long secondsSinceLastReport) {
        return processedPacketsCountersForNow.stream()
                .map(e -> String.format("% d: %d pps", e.getKey(), e.getValue() / secondsSinceLastReport))
                .collect(toList())
                .toString();
    }

    private long getTotalProcessedPacketsRates(List<Map.Entry<Integer, Long>> processedPacketsCountersForNow,
                                               final long secondsSinceLastReport) {
        return processedPacketsCountersForNow.stream()
                .mapToLong(Map.Entry::getValue)
                .sum() / secondsSinceLastReport;
    }

    private String getProcessedPacketsAverageParseTime(List<Map.Entry<Integer, Long>> processedPacketsTotalTimeCountersForNow,
                                                       List<Map.Entry<Integer, Long>> processedPacketsCountersForNow) {
        return processedPacketsTotalTimeCountersForNow.stream()
                .map(e -> {
                    Long v = e.getValue();

                    long processedPackets = processedPacketsCountersForNow.stream()
                            .filter(e1 -> e1.getKey().equals(e.getKey()))
                            .mapToLong(Map.Entry::getValue)
                            .findFirst().orElse(0);

                    long parseTime = processedPackets != 0 ? v / processedPackets : 0;
                    return String.format("%d: %d ns", e.getKey(), parseTime);
                })
                .collect(toList())
                .toString();
    }
}
