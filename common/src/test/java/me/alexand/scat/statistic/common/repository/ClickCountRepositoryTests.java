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
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.common.utils.ClickCountTestEntities.*;
import static me.alexand.scat.statistic.common.utils.ColumnOrder.ASC;
import static org.junit.Assert.*;

/**
 * Тесты для проверки хранилища сущностей ClickCount
 *
 * @author asidorov84@gmail.com
 */
public class ClickCountRepositoryTests extends AbstractCommonTests {

    @Autowired
    private ClickCountRepository repository;

    @Test
    public void testSaveAll() {
        List<ClickCount> clickCounts = asList(COUNTER_20180401, COUNTER_20180402);

        assertEquals(clickCounts.size(), repository.saveAll(clickCounts));
        assertNotNull(repository.findByDate(TEST_DATE_2018_04_01));
        assertNotNull(repository.findByDate(TEST_DATE_2018_04_02));

        assertEquals(clickCounts.size(), repository.saveAll(clickCounts));
        ClickCount clickCount = repository.findByDate(TEST_DATE_2018_04_01);
        assertNotNull(clickCount);
        assertEquals(clickCount.getCount(), COUNTER_20180401.getCount().multiply(BigInteger.valueOf(2)));

        clickCount = repository.findByDate(TEST_DATE_2018_04_02);
        assertNotNull(clickCount);
        assertEquals(clickCount.getCount(), COUNTER_20180402.getCount().multiply(BigInteger.valueOf(2)));
    }

    @Test
    public void testFindAll() {
        List<ClickCount> actual = repository.findAll();
        assertNotNull(actual);
        assertEquals(CLICK_COUNT_SET.size(), actual.size());
        CLICK_COUNT_SET.forEach(cc -> assertTrue(actual.contains(cc)));
    }

    @Test
    public void testFindAllWithSortingAndPaginationParameter() {
        SortingAndPagination sortingAndPagination = SortingAndPagination.builder()
                .offset(1)
                .limit(2)
                .orderingColumn("date", ASC)
                .build();

        List<ClickCount> expected = asList(COUNTER_20180726, COUNTER_20180727);
        List<ClickCount> actual = repository.findAll(sortingAndPagination);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        expected.forEach(cc -> assertTrue(actual.contains(cc)));
    }

    @Test
    public void testFindBetweenWithAllParameters() {
        List<ClickCount> actual = repository.findBetween(COUNTER_20180725.getDate(), COUNTER_20180725.getDate());
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(COUNTER_20180725, actual.get(0));
    }

    @Test
    public void testFindBetweenWithoutEnd() {
        List<ClickCount> expected = asList(COUNTER_20180804, COUNTER_20180805);
        List<ClickCount> actual = repository.findBetween(LocalDate.of(2018, 8, 4), null);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        expected.forEach(cc -> assertTrue(actual.contains(cc)));
    }

    @Test
    public void testFindBetweenWithoutStart() {
        List<ClickCount> expected = asList(COUNTER_20180725, COUNTER_20180726);
        List<ClickCount> actual = repository.findBetween(null, LocalDate.of(2018, 7, 26));
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        expected.forEach(cc -> assertTrue(actual.contains(cc)));
    }

    @Test
    public void testFindBetweenWithSortingAndPaginationParameters() {
        SortingAndPagination sortingAndPagination = SortingAndPagination.builder()
                .offset(2)
                .limit(3)
                .orderingColumn("date", ASC)
                .build();

        List<ClickCount> expected = asList(COUNTER_20180727, COUNTER_20180728, COUNTER_20180729);
        List<ClickCount> actual = repository.findBetween(LocalDate.of(2018, 7, 25),
                LocalDate.of(2018, 8, 5),
                sortingAndPagination);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        expected.forEach(cc -> assertTrue(actual.contains(cc)));
    }

    @Test
    public void testFindByDate() {
        ClickCount actual = repository.findByDate(COUNTER_20180725.getDate());
        assertNotNull(actual);
        assertEquals(COUNTER_20180725, actual);
    }
}
