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

package me.alexand.scat.statistic.api.config;

import me.alexand.scat.statistic.common.repository.ClickCountRepository;
import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import me.alexand.scat.statistic.common.repository.TrackedDomainRequestsRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация контекста Spring для проведения тестов
 *
 * @author asidorov84@gmail.com
 */
@Configuration
@ComponentScan(basePackages = "me.alexand.scat.statistic.api.service")
public class TestConfig {
    @Bean
    public ClickCountRepository clickCountMockRepository() {
        return Mockito.mock(ClickCountRepository.class);
    }

    @Bean
    public DomainRegexRepository domainRegexMockRepository() {
        return Mockito.mock(DomainRegexRepository.class);
    }

    @Bean
    public TrackedDomainRequestsRepository trackedDomainRequestsMockRepository() {
        return Mockito.mock(TrackedDomainRequestsRepository.class);
    }
}
