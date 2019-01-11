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
import me.alexand.scat.statistic.collector.model.ImportDataTemplate;
import me.alexand.scat.statistic.collector.repository.IPFIXDataRecordRepository;
import me.alexand.scat.statistic.collector.service.OutputRecordsQueue;
import me.alexand.scat.statistic.collector.service.StatCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
@Component
@Scope("prototype")
public final class IPFIXRecordsWriter implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXRecordsWriter.class);

    private final ImportDataTemplate dataTemplate;
    private final int batchSize;
    private final boolean isWrite;

    @Autowired
    private OutputRecordsQueue recordsQueue;
    
    @Autowired
    private IPFIXDataRecordRepository repository;

    @Autowired
    private StatCollector statCollector;

    public IPFIXRecordsWriter(ImportDataTemplate dataTemplate, int batchSize) {
        LOGGER.info("Initializing {} records writer with batch size = {}", dataTemplate.getName(), batchSize);
        this.dataTemplate = dataTemplate;
        this.batchSize = batchSize;
        this.isWrite = dataTemplate.isExport();
    }

    @Override
    public void run() {
        LOGGER.info("Started {} records writer...", dataTemplate.getName());

        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<IPFIXDataRecord> recordsToWrite = recordsQueue.takeNextBatch(dataTemplate, batchSize);

                if (isWrite) {
                    statCollector.registerExportedRecord(repository.save(dataTemplate, recordsToWrite));
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("Stopped {} records writer...", dataTemplate.getName());
    }
}
