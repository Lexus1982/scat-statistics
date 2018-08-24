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

import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Экземпляры класса TrackedDomainRequests для тестов
 *
 * @author asidorov84@gmail.com
 */
public interface TrackedDomainRequestsTestEntities {
    TrackedDomainRequests TEST = TrackedDomainRequests.builder()
            .date(LocalDate.of(2018, 4, 1))
            .domainRegex(DomainRegexTestEntities.TEST_VK_COM)
            .address("176.221.0.224")
            .login("polyakov_al@setka.ru")
            .firstTime(LocalTime.of(17, 6, 17))
            .lastTime(LocalTime.of(17, 6, 19))
            .count(BigInteger.valueOf(3))
            .build();
}