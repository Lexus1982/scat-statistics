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
import java.time.LocalTime;
import java.util.Objects;

/**
 * Класс, описывающий совершенные абонентами веб-запросы на доменые имена, заданные шаблоном
 *
 * @author asidorov84@gmail.com
 */
public class TrackedDomainRequests {
    private final LocalDate date;
    private final String pattern;
    private final String address;
    private final String login;
    private final LocalTime firstTime;
    private final LocalTime lastTime;
    private final BigInteger count;

    private TrackedDomainRequests(TrackedDomainRequests.Builder builder) {
        this.date = builder.date;
        this.pattern = builder.pattern;
        this.address = builder.address;
        this.login = builder.login;
        this.firstTime = builder.firstTime;
        this.lastTime = builder.lastTime;
        this.count = builder.count;
    }

    public static TrackedDomainRequests.Builder builder() {
        return new TrackedDomainRequests.Builder();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getPattern() {
        return pattern;
    }

    public String getAddress() {
        return address;
    }

    public String getLogin() {
        return login;
    }

    public LocalTime getFirstTime() {
        return firstTime;
    }

    public LocalTime getLastTime() {
        return lastTime;
    }

    public BigInteger getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedDomainRequests that = (TrackedDomainRequests) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(pattern, that.pattern) &&
                Objects.equals(address, that.address) &&
                Objects.equals(login, that.login) &&
                Objects.equals(firstTime, that.firstTime) &&
                Objects.equals(lastTime, that.lastTime) &&
                Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, pattern, address, login, firstTime, lastTime, count);
    }

    @Override
    public String toString() {
        return "TrackedDomainRequests{" +
                "date=" + date +
                ", pattern='" + pattern + '\'' +
                ", address='" + address + '\'' +
                ", login='" + login + '\'' +
                ", firstTime=" + firstTime +
                ", lastTime=" + lastTime +
                ", count=" + count +
                '}';
    }

    public static class Builder {
        private LocalDate date;
        private String pattern;
        private String address;
        private String login;
        private LocalTime firstTime;
        private LocalTime lastTime;
        private BigInteger count;

        private Builder() {
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder firstTime(LocalTime firstTime) {
            this.firstTime = firstTime;
            return this;
        }

        public Builder lastTime(LocalTime lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        public Builder count(BigInteger count) {
            this.count = count;
            return this;
        }

        public TrackedDomainRequests build() {
            return new TrackedDomainRequests(this);
        }
    }
}