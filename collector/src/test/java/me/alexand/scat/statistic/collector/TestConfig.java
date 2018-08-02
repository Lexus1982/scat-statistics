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

package me.alexand.scat.statistic.collector;

import me.alexand.scat.statistic.collector.config.TransitionalBufferConfig;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.repository.SCATDataTemplateRepository;
import me.alexand.scat.statistic.collector.repository.TransitionalBufferRepository;
import me.alexand.scat.statistic.collector.repository.impls.InMemoryInfoModelRepositoryImpl;
import me.alexand.scat.statistic.collector.repository.impls.InMemorySCATDataTemplateRepositoryImpl;
import me.alexand.scat.statistic.collector.repository.impls.TransitionalBufferRepositoryImpl;
import me.alexand.scat.statistic.collector.service.DataTemplateService;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import me.alexand.scat.statistic.collector.service.impls.DataTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author asidorov84@gmail.com
 */
@Configuration
@PropertySource("classpath:app-test.properties")
@Import(TransitionalBufferConfig.class)
public class TestConfig {
    @Autowired
    private TransitionalBufferConfig transitionalBufferConfig;

    @Bean
    public TransitionalBufferRepository transitionalBufferRepository() {
        return new TransitionalBufferRepositoryImpl(transitionalBufferConfig.bufferJdbcTemplate());
    }

    @Bean
    public SCATDataTemplateRepository scatDataTemplateRepository() {
        return new InMemorySCATDataTemplateRepositoryImpl();
    }

    @Bean
    public InfoModelRepository infoModelRepository() {
        return new InMemoryInfoModelRepositoryImpl();
    }

    @Bean
    public DataTemplateService dataTemplateService() {
        return new DataTemplateServiceImpl(scatDataTemplateRepository(), infoModelRepository());
    }

    @Bean
    public IPFIXParser ipfixParser() {
        return new IPFIXParser(dataTemplateService(), infoModelRepository());
    }
}