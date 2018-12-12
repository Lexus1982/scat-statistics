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

package me.alexand.scat.statistic.collector.controller;

import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.service.IPFIXMessageProcessorFactory;
import me.alexand.scat.statistic.collector.service.IPFIXRecordsWriterFactory;
import me.alexand.scat.statistic.collector.service.TCPPacketsReceiver;
import me.alexand.scat.statistic.collector.service.impls.IPFIXMessageProcessor;
import me.alexand.scat.statistic.collector.utils.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.NORM_PRIORITY;

/**
 * Контроллер коллектора.
 * <p>
 * Создает и запускает в разных потоках процессоры для обработки.
 *
 * @author asidorov84@gmail.com
 * @see IPFIXMessageProcessor
 */

@Component
public final class CollectorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorController.class);
    
    private final IPFIXRecordsWriterFactory recordsWriterFactory;
    private final IPFIXMessageProcessorFactory messageProcessorFactory;
    private final TCPPacketsReceiver packetsReceiver;
    
    private final ExecutorService processorsPool;
    private final ExecutorService writersPool;

    public CollectorController(@Value("${processors.count}") final int processorsCount,
                               @Value("${records.writer.batch.size}") int batchSize,
                               IPFIXRecordsWriterFactory recordsWriterFactory,
                               IPFIXMessageProcessorFactory messageProcessorFactory,
                               TCPPacketsReceiver packetsReceiver) {
        if (processorsCount <= 0) {
            throw new IllegalArgumentException(String.format("illegal processors count: %d", processorsCount));
        }

        if (batchSize <= 0) {
            throw new IllegalArgumentException(String.format("illegal batch size: %d", batchSize));
        }
        
        this.recordsWriterFactory = recordsWriterFactory;
        this.messageProcessorFactory = messageProcessorFactory;
        this.packetsReceiver = packetsReceiver;
        
        int templatesCount = TemplateType.values().length;

        LOGGER.info("Initializing records writers thread pool with fixed thread count: {}", templatesCount);

        writersPool = Executors.newFixedThreadPool(templatesCount, new ThreadFactoryBuilder()
                .name("writers-pool-writer-%d")
                .daemon(false)
                .priority(NORM_PRIORITY)
                .build()
        );

        LOGGER.info("Initializing processors thread pool with fixed thread count: {}", processorsCount);

        processorsPool = Executors.newFixedThreadPool(processorsCount, new ThreadFactoryBuilder()
                .name("processors-pool-processor-%d")
                .daemon(false)
                .priority(NORM_PRIORITY)
                .build()
        );

        runRecordsWriters(batchSize);
        runMessageProcessors(processorsCount);
        
        LOGGER.info("Starting packets receiver...");
        packetsReceiver.start();
    }

    private void runMessageProcessors(int processorsCount) {
        LOGGER.info("Starting message processors...");
        for (int i = 0; i < processorsCount; i++) {
            processorsPool.submit(messageProcessorFactory.getProcessor());
        }
    }

    private void runRecordsWriters(int batchSize) {
        LOGGER.info("Staring records writers...");
        for (TemplateType type : TemplateType.values()) {
            writersPool.submit(recordsWriterFactory.getWriter(type, batchSize));
        }
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutdown begin...");
        
        //Останавливаем прием пакетов
        LOGGER.info("...stopping packets receiver");
        packetsReceiver.shutdown();
        LOGGER.info("...packets receiver stopped");
        
        //Останавливаем обработку
        LOGGER.info("...stopping all message processors");
        try {
            processorsPool.shutdownNow();
            LOGGER.info("...waiting until all message processors stopped");
            processorsPool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.info("Normal shutdown failed");
            return;
        }
        LOGGER.info("...all message processors stopped");

        LOGGER.info("...stopping all records writers");
        //Останавливаем писателей
        try {
            writersPool.shutdownNow();
            LOGGER.info("...waiting until all records writers stopped");
            writersPool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.info("Normal shutdown failed");
            return;
        }

        LOGGER.info("...all records writers stopped");
        LOGGER.info("Shutdown complete successfully");
    }
}