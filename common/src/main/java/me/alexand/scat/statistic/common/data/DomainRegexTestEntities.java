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

package me.alexand.scat.statistic.common.data;

import me.alexand.scat.statistic.common.entities.DomainRegex;

import java.time.LocalDateTime;

/**
 * Экземпляры класса DomainRegex для проведения тестов
 *
 * @author asidorov84@gmail.com
 */
public interface DomainRegexTestEntities {
    int POPULATED_DOMAINS_COUNT = 3;

    String INVALID_PATTERN = "\\\\1111\\q";
    String VK_COM_REGEX_PATTERN = ".*vk\\.com$";
    String MAIL_RU_REGEX_PATTERN = ".*mail\\.ru$";
    String OK_RU_REGEX_PATTERN = ".*ok\\.ru$";

    DomainRegex TEST_VK_COM = DomainRegex.builder()
            .id(1)
            .pattern(VK_COM_REGEX_PATTERN)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .active(true)
            .build();

    DomainRegex TEST_MAIL_RU = DomainRegex.builder()
            .id(2)
            .pattern(MAIL_RU_REGEX_PATTERN)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .active(true)
            .build();

    DomainRegex TEST_DELETE = DomainRegex.builder()
            .id(3)
            .pattern(".*delete\\.ru$")
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .active(true)
            .build();
}
