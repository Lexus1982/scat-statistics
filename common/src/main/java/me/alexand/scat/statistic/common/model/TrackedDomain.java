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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс, описывающий доменное имя, которое необходимо отслеживать
 * Доменное имя задается в виде регулярного выражения стадарта POSIX
 *
 * @author asidorov84@gmail.com
 */
public class TrackedDomain {
    private final String regexPattern;
    private final boolean isActive;
    private final LocalDateTime dateAdded;

    private TrackedDomain(TrackedDomain.Builder builder) {
        this.regexPattern = builder.regexPattern;
        this.isActive = builder.isActive;
        this.dateAdded = builder.dateAdded;
    }

    public static TrackedDomain.Builder builder() {
        return new TrackedDomain.Builder();
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackedDomain that = (TrackedDomain) o;
        return isActive == that.isActive &&
                Objects.equals(regexPattern, that.regexPattern) &&
                Objects.equals(dateAdded, that.dateAdded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regexPattern, isActive, dateAdded);
    }

    @Override
    public String toString() {
        return "TrackedDomain{" +
                "regexPattern='" + regexPattern + '\'' +
                ", isActive=" + isActive +
                ", dateAdded=" + dateAdded +
                '}';
    }

    public static class Builder {
        private String regexPattern;
        private boolean isActive;
        private LocalDateTime dateAdded;

        private Builder() {
        }

        public Builder regexPattern(String regexPattern) {
            this.regexPattern = regexPattern;
            return this;
        }

        public Builder active(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder dateAdded(LocalDateTime dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public TrackedDomain build() {
            return new TrackedDomain(this);
        }
    }
}