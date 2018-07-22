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
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.TransitionalBufferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author asidorov84@gmail.com
 */

@Component
public class TransitionalBufferRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransitionalBufferRecorder.class);
    private static final int OUTPUT_BUFFER_SIZE = 100_000_000;

    private final Map<TemplateType, Thread> recorderThreads = new HashMap<>();
    private final Map<TemplateType, BlockingQueue<List<IPFIXDataRecord>>> recordsBuffers = new HashMap<>();
    private final TransitionalBufferRepository transitionalBufferRepository;

    @Autowired
    public TransitionalBufferRecorder(TransitionalBufferRepository transitionalBufferRepository) {
        LOGGER.info("initializing recorders...");
        this.transitionalBufferRepository = transitionalBufferRepository;

        for (TemplateType type : TemplateType.values()) {
            recordsBuffers.put(type, new ArrayBlockingQueue<>(OUTPUT_BUFFER_SIZE));
        }

        for (TemplateType templateType : TemplateType.values()) {
            String threadName = String.format("%s-recorder-thread", templateType.getName().toLowerCase());
            Thread recorderThread = new Thread(new Recorder(templateType), threadName);
            recorderThreads.put(templateType, recorderThread);
            recorderThread.start();
        }

    }

    public void transfer(TemplateType type, List<IPFIXDataRecord> records) {
        recordsBuffers.get(type).offer(records);
    }

    private class Recorder implements Runnable {
        private final TemplateType templateType;

        public Recorder(TemplateType templateType) {
            this.templateType = templateType;
        }

        @Override
        public void run() {
            BlockingQueue<List<IPFIXDataRecord>> buffer = recordsBuffers.get(templateType);
            LOGGER.info("start recorder with template type: {}", templateType);

            try {
                while (!recorderThreads.get(templateType).isInterrupted()) {
                    List<IPFIXDataRecord> records = buffer.take();
                    transitionalBufferRepository.save(templateType, records);
                }

            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
