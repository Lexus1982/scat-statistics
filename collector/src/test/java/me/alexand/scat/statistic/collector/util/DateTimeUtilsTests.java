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

package me.alexand.scat.statistic.collector.util;


import org.junit.Test;

import java.time.LocalDateTime;

import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.getFormattedDateTime;
import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.getFormattedDifferenceBetweenLocalDateTime;
import static org.junit.Assert.assertEquals;

public class DateTimeUtilsTests {
    private static final LocalDateTime LOCAL_DATE_TIME_1 = LocalDateTime.of(2018, 3, 1, 12, 30, 45);
    private static final LocalDateTime LOCAL_DATE_TIME_2 = LocalDateTime.of(2018, 7, 20, 16, 38, 27);

    @Test
    public void testGetFormattedDateTime() {
        assertEquals("12:30:45 01/03/2018", getFormattedDateTime(LOCAL_DATE_TIME_1));
    }

    @Test
    public void testGetFormattedDifferenceBetweenLocalDateTime() {
        String expected = "141 days 4 hours 7 minutes and 42 seconds";
        String actual = getFormattedDifferenceBetweenLocalDateTime(LOCAL_DATE_TIME_1, LOCAL_DATE_TIME_2);
        assertEquals(expected, actual);
    }
}
