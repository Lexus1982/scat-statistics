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

package me.alexand.scat.statistic.collector.entities;

import me.alexand.scat.statistic.common.model.TrackedDomain;

import java.time.LocalDateTime;

/**
 * @author asidorov84@gmail.com
 */
public interface TrackedDomainsTestEntities {
    TrackedDomain TEST_VK_COM = TrackedDomain.builder()
            .regexPattern(".*vk\\.com$")
            .active(true)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    TrackedDomain TEST_MAIL_RU = TrackedDomain.builder()
            .regexPattern(".*mail\\.ru$")
            .active(true)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    TrackedDomain TEST_UPDATED_MAIL_RU = TrackedDomain.builder()
            .regexPattern(".*mail\\.ru$")
            .active(false)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    TrackedDomain TEST_OK_RU = TrackedDomain.builder()
            .regexPattern(".*ok\\.ru$")
            .active(true)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();
}
