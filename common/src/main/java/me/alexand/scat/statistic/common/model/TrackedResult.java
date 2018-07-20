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

package me.alexand.scat.statistic.common.model;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author asidorov84@gmail.com
 */
public class TrackedResult {
    private final String regexPattern;
    private final String address;
    private final String login;
    private final LocalDateTime firstTime;
    private final LocalDateTime lastTime;
    private final BigInteger count;

    private TrackedResult(TrackedResult.Builder builder) {
        this.regexPattern = builder.regexPattern;
        this.address = builder.address;
        this.login = builder.login;
        this.firstTime = builder.firstTime;
        this.lastTime = builder.lastTime;
        this.count = builder.count;
    }

    public static TrackedResult.Builder builder() {
        return new TrackedResult.Builder();
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public String getAddress() {
        return address;
    }

    public String getLogin() {
        return login;
    }

    public LocalDateTime getFirstTime() {
        return firstTime;
    }

    public LocalDateTime getLastTime() {
        return lastTime;
    }

    public BigInteger getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedResult that = (TrackedResult) o;
        return Objects.equals(regexPattern, that.regexPattern) &&
                Objects.equals(address, that.address) &&
                Objects.equals(login, that.login) &&
                Objects.equals(firstTime, that.firstTime) &&
                Objects.equals(lastTime, that.lastTime) &&
                Objects.equals(count, that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regexPattern, address, login, firstTime, lastTime, count);
    }

    @Override
    public String toString() {
        return "TrackedResult{" +
                "regexPattern='" + regexPattern + '\'' +
                ", address=" + address +
                ", login='" + login + '\'' +
                ", firstTime=" + firstTime +
                ", lastTime=" + lastTime +
                ", count=" + count +
                '}';
    }

    public static class Builder {
        private String regexPattern;
        private String address;
        private String login;
        private LocalDateTime firstTime;
        private LocalDateTime lastTime;
        private BigInteger count;

        private Builder() {
        }

        public Builder regexPattern(String hostname) {
            this.regexPattern = hostname;
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

        public Builder firstTime(LocalDateTime firstTime) {
            this.firstTime = firstTime;
            return this;
        }

        public Builder lastTime(LocalDateTime lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        public Builder count(BigInteger count) {
            this.count = count;
            return this;
        }

        public TrackedResult build() {
            return new TrackedResult(this);
        }
    }
}