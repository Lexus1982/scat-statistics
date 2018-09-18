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
import me.alexand.scat.statistic.collector.repository.TransitionalBufferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.DATE_TIME_FORMATTER;

/**
 * Класс для очистки буфера от старых IPFIX-записей
 * При создании экземпляра запускает отдельный поток, который периодически с интервалом в
 * runInterval секунд
 * удаляет записи из всех таблиц внутреннего буфера. Глубина буфера соответствует параметру bufferDepth, заданного
 * в минутах. Работа класса основана на показаниях системных часов.
 *
 * @author asidorov84@gmail.com
 */

@Service
public class TransitionalBufferCleaner implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransitionalBufferCleaner.class);

    private final TransitionalBufferRepository bufferRepository;
    private final Thread cleaner;
    private final long runInterval;//в секундах
    private final long bufferDepth;//в минутах
    private LocalDateTime startEventTime;

    public TransitionalBufferCleaner(TransitionalBufferRepository transitionalBufferRepository,
                                     @Value("${transitional.buffer.cleaner.interval}") long runInterval,
                                     @Value("${transitional.buffer.depth}") long bufferDepth) {
        if (runInterval <= 0) {
            throw new IllegalArgumentException("Illegal run interval specified");
        }
        
        if (bufferDepth <= 0) {
            throw new IllegalArgumentException("Illegal buffer depth specified");
        }

        this.bufferRepository = transitionalBufferRepository;
        this.runInterval = runInterval;
        this.bufferDepth = bufferDepth;
        this.startEventTime = LocalDateTime.now().minusMinutes(bufferDepth);

        cleaner = new Thread(this, "transitional-buffer-cleaner-thread");
        cleaner.setDaemon(true);
        cleaner.start();
    }

    @Override
    public void run() {
        LOGGER.info("[Cleaner thread started]");
        LOGGER.info("cleaner interval is {} seconds", runInterval);
        LOGGER.info("buffer depth is {} minute", bufferDepth);

        while (!cleaner.isInterrupted()) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(runInterval));
                LocalDateTime endEventTime = LocalDateTime.now().minusMinutes(bufferDepth);
                long totalRecordsDeleted = 0;

                for (TemplateType type : TemplateType.values()) {
                    totalRecordsDeleted += bufferRepository.deleteBetween(type, startEventTime, endEventTime);
                }

                LOGGER.debug("{} records of all types in buffer between {} and {} deleted",
                        totalRecordsDeleted,
                        startEventTime.format(DATE_TIME_FORMATTER),
                        endEventTime.format(DATE_TIME_FORMATTER));
                
                startEventTime = endEventTime;
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }

        LOGGER.info("[Cleaner thread stopped]");
    }
}
