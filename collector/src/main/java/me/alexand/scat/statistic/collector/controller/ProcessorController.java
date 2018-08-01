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

import me.alexand.scat.statistic.collector.network.PacketsReceiver;
import me.alexand.scat.statistic.collector.service.DataTemplateService;
import me.alexand.scat.statistic.collector.service.IPFIXMessageProcessor;
import me.alexand.scat.statistic.collector.service.IPFIXMessageProcessorFactory;
import me.alexand.scat.statistic.collector.utils.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Загружает шаблоны СКАТ. Создает и запускает в разных потоках процессоры для обработки.
 *
 * @author asidorov84@gmail.com
 * @see IPFIXMessageProcessor
 */

@Component
public final class ProcessorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorController.class);
    private final PacketsReceiver receiver;
    private final ExecutorService processorsPool;

    @Autowired
    public ProcessorController(@Value("${processors.count}") final int processorsCount,
                               PacketsReceiver receiver,
                               IPFIXMessageProcessorFactory IPFIXMessageProcessorFactory,
                               DataTemplateService dataTemplateService) {
        if (processorsCount <= 0) {
            throw new IllegalArgumentException(String.format("illegal processors count: %d", processorsCount));
        }

        LOGGER.info("Loading SCAT templates");
        dataTemplateService.load();
        
        this.receiver = receiver;

        LOGGER.info("Initializing processors thread pool with fixed thread count: {}", processorsCount);
        processorsPool = Executors.newFixedThreadPool(processorsCount, new ThreadFactoryBuilder()
                .name("processors-pool-processor-%d")
                .daemon(false)
                .priority(NORM_PRIORITY)
                .build()
        );

        LOGGER.info("Starting processors");
        for (int i = 0; i < processorsCount; i++) {
            processorsPool.submit(IPFIXMessageProcessorFactory.getProcessor());
        }
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutdown begin...");
        
        //Прекращаем прием пакетов
        if (receiver.shutdown()) {
            LOGGER.info("...receiver shutdown successfully");
        }
        
        //Ждем пока все пакеты из приемного буфера будут переданы на обработку
        try {
            while (receiver.getRemainingPacketsCount() != 0) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
        
        if (receiver.getRemainingPacketsCount() == 0) {
            LOGGER.info("...input buffer became empty");
        }

        //Останавливаем обработку
        try {
            processorsPool.shutdownNow();
            LOGGER.info("...waiting until all processors stopped");
            processorsPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.info("Normal shutdown failed");
            return;
        }

        LOGGER.info("Shutdown complete successfully");
    }
}