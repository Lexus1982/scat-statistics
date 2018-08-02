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

package me.alexand.scat.statistic.common.repository;

import me.alexand.scat.statistic.common.entities.ClickCount;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Тесты для проверки хранилища сущностей ClickCount
 *
 * @author asidorov84@gmail.com
 */
public class ClickCountRepositoryTests extends AbstractCommonTests {
    private static final ClickCount FIRST_APRIL_COUNT = ClickCount.builder()
            .date(LocalDate.of(2018, 4, 1))
            .count(BigInteger.valueOf(16))
            .build();

    private static final ClickCount SECOND_APRIL_COUNT = ClickCount.builder()
            .date(LocalDate.of(2018, 4, 2))
            .count(BigInteger.valueOf(6))
            .build();

    @Autowired
    private ClickCountRepository repository;

    @Test
    public void testSaveAll() {
        assertEquals(2, repository.saveAll(asList(FIRST_APRIL_COUNT, SECOND_APRIL_COUNT)));
        repository.saveAll(asList(FIRST_APRIL_COUNT, SECOND_APRIL_COUNT));
    }
}
