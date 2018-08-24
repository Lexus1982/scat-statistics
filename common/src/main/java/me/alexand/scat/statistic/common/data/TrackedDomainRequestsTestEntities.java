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
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Экземпляры класса TrackedDomainRequests для тестов
 *
 * @author asidorov84@gmail.com
 */
public interface TrackedDomainRequestsTestEntities {
    TrackedDomainRequests TEST_1 = TrackedDomainRequests.builder()
            .date(LocalDate.of(2018, 8, 1))
            .domainRegex(DomainRegexTestEntities.TEST_VK_COM)
            .address("127.0.0.1")
            .login("login1")
            .firstTime(LocalTime.of(0, 40, 43))
            .lastTime(LocalTime.of(0, 40, 43))
            .count(BigInteger.valueOf(2))
            .build();

    TrackedDomainRequests TEST_2 = TrackedDomainRequests.builder()
            .date(LocalDate.of(2018, 8, 1))
            .domainRegex(DomainRegexTestEntities.TEST_VK_COM)
            .address("127.0.0.2")
            .login("login2")
            .firstTime(LocalTime.of(0, 40, 48))
            .lastTime(LocalTime.of(23, 46, 15))
            .count(BigInteger.valueOf(66))
            .build();

    TrackedDomainRequests TEST_3 = TrackedDomainRequests.builder()
            .date(LocalDate.of(2018, 8, 2))
            .domainRegex(DomainRegexTestEntities.TEST_MAIL_RU)
            .address("127.0.0.1")
            .login("login1")
            .firstTime(LocalTime.of(0, 41, 26))
            .lastTime(LocalTime.of(23, 5, 16))
            .count(BigInteger.valueOf(55))
            .build();

    TrackedDomainRequests TEST_4 = TrackedDomainRequests.builder()
            .date(LocalDate.of(2018, 8, 3))
            .domainRegex(DomainRegexTestEntities.TEST_MAIL_RU)
            .address("127.0.0.3")
            .login("login3")
            .firstTime(LocalTime.of(0, 41, 34))
            .lastTime(LocalTime.of(23, 29, 20))
            .count(BigInteger.valueOf(143))
            .build();

    TrackedDomainRequests TEST_5 = TrackedDomainRequests.builder()
            .date(LocalDate.of(2018, 8, 5))
            .domainRegex(DomainRegexTestEntities.TEST_VK_COM)
            .address("127.0.0.5")
            .login("")
            .firstTime(LocalTime.of(0, 41, 13))
            .lastTime(LocalTime.of(21, 40, 32))
            .count(BigInteger.valueOf(208))
            .build();

    List<TrackedDomainRequests> TRACKED_DOMAIN_REQUESTS_LIST = asList(
            TEST_1,
            TEST_2,
            TEST_3,
            TEST_4,
            TEST_5);
}