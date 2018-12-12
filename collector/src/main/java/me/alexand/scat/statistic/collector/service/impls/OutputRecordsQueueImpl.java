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

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.service.OutputRecordsQueue;
import me.alexand.scat.statistic.collector.utils.exceptions.OutputQueueOverflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * @author asidorov84@gmail.com
 */
@Component
public class OutputRecordsQueueImpl implements OutputRecordsQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputRecordsQueue.class);

    private final int queueLength;
    private final Map<TemplateType, BlockingQueue<IPFIXDataRecord>> recordsQueues = new HashMap<>();

    public OutputRecordsQueueImpl(@Value("${output.records.queue.length}") int queueLength) {
        if (queueLength <= 0) {
            throw new IllegalArgumentException(String.format("Illegal length of output records queue: %d", queueLength));
        }

        this.queueLength = queueLength;

        Arrays.stream(TemplateType.values())
                .forEach(type -> recordsQueues.put(type, new ArrayBlockingQueue<>(queueLength)));
        
        LOGGER.debug("initialize output queues with length: {}", queueLength);
    }

    @Override
    public void put(List<IPFIXDataRecord> dataRecords) throws OutputQueueOverflowException {
        for (IPFIXDataRecord record : dataRecords) {
            TemplateType type = record.getType();
            BlockingQueue<IPFIXDataRecord> queue = recordsQueues.get(type);
            
            if (!queue.offer(record)) {
                throw new OutputQueueOverflowException("output records queue overflow detected");
            }
        }
    }

    @Override
    public List<IPFIXDataRecord> takeNextBatch(TemplateType type, int batchSize) throws InterruptedException {
        List<IPFIXDataRecord> resultBatch = new ArrayList<>(batchSize);
        BlockingQueue<IPFIXDataRecord> queue = recordsQueues.get(type);

        int addedCount = 0;
        
        while (addedCount < batchSize) {
            addedCount += queue.drainTo(resultBatch, batchSize - addedCount);
            
            if (addedCount < batchSize) {
                resultBatch.add(queue.take());
                addedCount++;
            }
        }
        
        return resultBatch;
    }

    @Override
    public Map<TemplateType, Long> getRemainingRecordsCount() {
        Map<TemplateType, Long> result = new HashMap<>();
        recordsQueues.entrySet().forEach(e -> result.put(e.getKey(), (long) (queueLength - e.getValue().remainingCapacity())));
        return result;
    }
}
