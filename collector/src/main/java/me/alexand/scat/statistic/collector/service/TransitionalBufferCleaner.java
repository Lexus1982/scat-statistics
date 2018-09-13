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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static me.alexand.scat.statistic.collector.utils.Constants.INTERIM_BUFFER_CLEANER_RUN_FREQUENCY;
import static me.alexand.scat.statistic.collector.utils.Constants.INTERIM_BUFFER_DEPTH;
import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.DATE_TIME_FORMATTER;
import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.getFormattedDateTime;

/**
 * Класс для очистки буфера от старых IPFIX-записей
 * Единственный метод
 *
 * @author asidorov84@gmail.com
 */

@Service
public class TransitionalBufferCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransitionalBufferCleaner.class);

    private TransitionalBufferRepository transitionalBufferRepository;
    
    private LocalDateTime afterEventTime = LocalDateTime.now().minusMinutes(INTERIM_BUFFER_DEPTH);

    @Autowired
    public TransitionalBufferCleaner(TransitionalBufferRepository transitionalBufferRepository) {
        this.transitionalBufferRepository = transitionalBufferRepository;

    }

    @Scheduled(fixedRate = INTERIM_BUFFER_CLEANER_RUN_FREQUENCY, initialDelay = INTERIM_BUFFER_CLEANER_RUN_FREQUENCY)
    public void clean() {
        LOGGER.info("Start cleaner...");
        LocalDateTime beforeEventTime = LocalDateTime.now().minusMinutes(INTERIM_BUFFER_DEPTH);

        LOGGER.info("\tdeleting all records of all types in buffer between {} and {}",
                afterEventTime.format(DATE_TIME_FORMATTER),
                beforeEventTime.format(DATE_TIME_FORMATTER));

        long totalRecordsDeleted = 0;

        for (TemplateType type : TemplateType.values()) {
            long recordsDeleted = transitionalBufferRepository.deleteBetween(type, afterEventTime, beforeEventTime);
            totalRecordsDeleted += recordsDeleted;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("\t-------------------------------------------------------");
                LOGGER.debug("\tdeleted records from {}: {}", type, recordsDeleted);
                LOGGER.debug("\trecords in {} is now: {}", type, transitionalBufferRepository.getCount(type));
                LOGGER.debug("\tminimum event time in {}: {}", type, getFormattedDateTime(transitionalBufferRepository.getMinEventTime(type)));
                LOGGER.debug("\tmaximum event time in {}: {}", type, getFormattedDateTime(transitionalBufferRepository.getMaxEventTime(type)));
            }
        }
        
        afterEventTime = beforeEventTime;
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\t-------------------------------------------------------");
        }
        LOGGER.info("\ttotal records deleted: {}", totalRecordsDeleted);
        LOGGER.info("Stop cleaner\n");
    }
}
