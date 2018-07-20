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

package me.alexand.scat.statistic.collector.model;

import java.util.Objects;

/**
 * Единица данных определенного типа
 *
 * @author asidorov84@gmail.com
 */

public class IPFIXFieldValue {
    private final String name;
    private final Object value;
    private final IANAAbstractDataTypes type;

    public static IPFIXFieldValue.Builder builder() {
        return new IPFIXFieldValue.Builder();
    }

    private IPFIXFieldValue(IPFIXFieldValue.Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.type = builder.type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public IANAAbstractDataTypes getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXFieldValue that = (IPFIXFieldValue) o;
        return Objects.equals(name, that.name) &&
                //Objects.equals(value, that.value) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, type);
    }

    @Override
    public String toString() {
        return "IPFIXFieldValue{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }

    public static class Builder {
        private String name;
        private Object value;
        private IANAAbstractDataTypes type;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder type(IANAAbstractDataTypes type) {
            this.type = type;
            return this;
        }

        public IPFIXFieldValue build() {
            return new IPFIXFieldValue(this);
        }
    }
}