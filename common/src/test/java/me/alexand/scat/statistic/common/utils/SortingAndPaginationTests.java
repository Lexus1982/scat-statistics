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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Тесты класса SortingAndPagination
 *
 * @author asidorov84@gmail.com
 */
public class SortingAndPaginationTests {
    @Test
    public void testWithLegalParameters() {
        String suffix = SortingAndPagination.builder()
                .offset(0)
                .limit(10)
                .orderingColumn("column1", ColumnOrder.DESC)
                .orderingColumn("column2", ColumnOrder.ASC)
                .orderingColumn("column3")
                .build()
                .formSQLSuffix();

        assertEquals("ORDER BY column1 DESC, column2 ASC, column3 ASC OFFSET 0 LIMIT 10", suffix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithIllegalOffsetParameter() {
        SortingAndPagination.builder()
                .offset(-1)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithIllegalLimitParameter() {
        SortingAndPagination.builder()
                .limit(-1)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void testWithIllegalSortingParameters() {
        SortingAndPagination.builder()
                .orderingColumn(null, null)
                .build();
    }

    @Test
    public void testWithoutSortingParameters() {
        String suffix = SortingAndPagination.builder()
                .offset(0)
                .limit(10)
                .build()
                .formSQLSuffix();

        assertEquals(" OFFSET 0 LIMIT 10", suffix);
    }

    @Test
    public void testWithOnlySortingParameters() {
        String suffix = SortingAndPagination.builder()
                .orderingColumn("column1", ColumnOrder.DESC)
                .orderingColumn("column2", ColumnOrder.ASC)
                .orderingColumn("column3")
                .build()
                .formSQLSuffix();

        assertEquals("ORDER BY column1 DESC, column2 ASC, column3 ASC OFFSET 0 LIMIT ALL", suffix);
    }
}
