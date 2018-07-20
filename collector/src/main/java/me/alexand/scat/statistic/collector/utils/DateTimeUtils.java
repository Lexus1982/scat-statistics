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

package me.alexand.scat.statistic.collector.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author asidorov84@gmail.com
 */
public interface DateTimeUtils {
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    int SECONDS_PER_MINUTE = 60;
    int SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE;
    int SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR;

    static String getFormattedDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "N/A";
    }

    static String getFormattedDifferenceBetweenLocalDateTime(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);

        long totalSeconds = duration.getSeconds();
        long days = totalSeconds / SECONDS_PER_DAY;
        long hours = (totalSeconds % SECONDS_PER_DAY) / SECONDS_PER_HOUR;
        long minutes = ((totalSeconds % SECONDS_PER_DAY) % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        long seconds = ((totalSeconds % SECONDS_PER_DAY) % SECONDS_PER_HOUR) % SECONDS_PER_MINUTE;

        return String.format("%d days %d hours %d minutes and %d seconds", days, hours, minutes, seconds);
    }
}
