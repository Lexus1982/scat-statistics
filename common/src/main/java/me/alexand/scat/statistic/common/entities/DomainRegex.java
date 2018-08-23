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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import me.alexand.scat.statistic.common.utils.Constants;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Шаблон доменных имен.
 * <p>
 * Шаблон представляет собой корректное регулярное выражение.
 * Описание синтаксиса регулярных выражений представлен в классе {@code java.util.regex.Pattern}
 *
 * @author asidorov84@gmail.com
 * @see java.util.regex.Pattern
 */
@JsonDeserialize(builder = DomainRegex.Builder.class)
public final class DomainRegex {
    private final long id;
    private final String pattern;
    
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private final LocalDateTime dateAdded;

    private DomainRegex(DomainRegex.Builder builder) {
        this.id = builder.id;
        this.pattern = builder.pattern;
        this.dateAdded = builder.dateAdded;
    }

    public static DomainRegex.Builder builder() {
        return new DomainRegex.Builder();
    }

    public long getId() {
        return id;
    }

    public String getPattern() {
        return pattern;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainRegex that = (DomainRegex) o;
        return id == that.id &&
                Objects.equals(pattern, that.pattern) &&
                Objects.equals(dateAdded, that.dateAdded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pattern, dateAdded);
    }

    @Override
    public String toString() {
        return "DomainRegex{" +
                "id=" + id +
                ", pattern='" + pattern + '\'' +
                ", dateAdded=" + dateAdded +
                '}';
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private long id;
        private String pattern;
        
        @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
        private LocalDateTime dateAdded;

        private Builder() {
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder pattern(String regexPattern) {
            this.pattern = regexPattern;
            return this;
        }

        public Builder dateAdded(LocalDateTime dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public DomainRegex build() {
            return new DomainRegex(this);
        }
    }
}