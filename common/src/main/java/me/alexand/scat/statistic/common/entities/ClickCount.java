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

package me.alexand.scat.statistic.common.entities;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Общее количество веб-запросов, совершенных абонентами в указанную дату
 *
 * @author asidorov84@gmail.com
 */
public final class ClickCount {
    private final LocalDate date;
    private final BigInteger count;

    private ClickCount(ClickCount.Builder builder) {
        this.date = builder.date;
        this.count = builder.count;
    }

    public static ClickCount.Builder builder() {
        return new ClickCount.Builder();
    }

    public LocalDate getDate() {
        return date;
    }

    public BigInteger getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClickCount that = (ClickCount) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, count);
    }

    @Override
    public String toString() {
        return "ClickCount{" +
                "date=" + date +
                ", count=" + count +
                '}';
    }

    public static class Builder {
        private LocalDate date;
        private BigInteger count;

        public Builder() {
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder count(BigInteger count) {
            this.count = count;
            return this;
        }

        public ClickCount build() {
            return new ClickCount(this);
        }
    }
}