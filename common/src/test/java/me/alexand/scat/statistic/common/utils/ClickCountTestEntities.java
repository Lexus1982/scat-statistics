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

package me.alexand.scat.statistic.common.utils;

import me.alexand.scat.statistic.common.entities.ClickCount;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Экземпляры класса ClickCount для тестов
 *
 * @author asidorov84@gmail.com
 */
public interface ClickCountTestEntities {
    LocalDate TEST_DATE_2018_04_01 = LocalDate.of(2018, 4, 1);
    LocalDate TEST_DATE_2018_04_02 = LocalDate.of(2018, 4, 2);

    ClickCount COUNTER_20180401 = ClickCount.builder()
            .date(TEST_DATE_2018_04_01)
            .count(BigInteger.valueOf(16))
            .build();

    ClickCount COUNTER_20180402 = ClickCount.builder()
            .date(TEST_DATE_2018_04_02)
            .count(BigInteger.valueOf(6))
            .build();

    ClickCount COUNTER_20180725 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 25))
            .count(BigInteger.valueOf(20180725))
            .build();

    ClickCount COUNTER_20180726 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 26))
            .count(BigInteger.valueOf(20180726))
            .build();

    ClickCount COUNTER_20180727 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 27))
            .count(BigInteger.valueOf(20180727))
            .build();

    ClickCount COUNTER_20180728 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 28))
            .count(BigInteger.valueOf(20180728))
            .build();

    ClickCount COUNTER_20180729 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 29))
            .count(BigInteger.valueOf(20180729))
            .build();

    ClickCount COUNTER_20180730 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 30))
            .count(BigInteger.valueOf(20180730))
            .build();

    ClickCount COUNTER_20180731 = ClickCount.builder()
            .date(LocalDate.of(2018, 7, 31))
            .count(BigInteger.valueOf(20180731))
            .build();

    ClickCount COUNTER_20180801 = ClickCount.builder()
            .date(LocalDate.of(2018, 8, 1))
            .count(BigInteger.valueOf(20180801))
            .build();

    ClickCount COUNTER_20180802 = ClickCount.builder()
            .date(LocalDate.of(2018, 8, 2))
            .count(BigInteger.valueOf(20180802))
            .build();

    ClickCount COUNTER_20180803 = ClickCount.builder()
            .date(LocalDate.of(2018, 8, 3))
            .count(BigInteger.valueOf(20180803))
            .build();

    ClickCount COUNTER_20180804 = ClickCount.builder()
            .date(LocalDate.of(2018, 8, 4))
            .count(BigInteger.valueOf(20180804))
            .build();

    ClickCount COUNTER_20180805 = ClickCount.builder()
            .date(LocalDate.of(2018, 8, 5))
            .count(BigInteger.valueOf(20180805))
            .build();

    Set<ClickCount> CLICK_COUNT_SET = Stream.of(
            COUNTER_20180725,
            COUNTER_20180726,
            COUNTER_20180727,
            COUNTER_20180728,
            COUNTER_20180729,
            COUNTER_20180730,
            COUNTER_20180731,
            COUNTER_20180801,
            COUNTER_20180802,
            COUNTER_20180803,
            COUNTER_20180804,
            COUNTER_20180805)
            .collect(Collectors.toSet());
}
