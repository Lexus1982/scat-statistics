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

import java.util.List;
import java.util.Objects;

/**
 * @author asidorov84@gmail.com
 */
public class IPFIXDataRecord extends AbstractIPFIXRecord {
    private final TemplateType type;
    private final List<IPFIXFieldValue> fieldValues;

    public static IPFIXDataRecord.Builder builder() {
        return new IPFIXDataRecord.Builder();
    }

    private IPFIXDataRecord(IPFIXDataRecord.Builder builder) {
        this.type = builder.type;
        this.fieldValues = builder.fieldValues;
    }

    public TemplateType getType() {
        return type;
    }

    public List<IPFIXFieldValue> getFieldValues() {
        return fieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXDataRecord that = (IPFIXDataRecord) o;
        return type == that.type &&
                Objects.equals(fieldValues, that.fieldValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, fieldValues);
    }

    @Override
    public String toString() {
        return "IPFIXDataRecord{" +
                "type=" + type +
                ", fieldValues=" + fieldValues +
                '}';
    }

    public static class Builder {
        private TemplateType type;
        private List<IPFIXFieldValue> fieldValues;

        private Builder() {
        }

        public Builder type(TemplateType type) {
            this.type = type;
            return this;
        }

        public Builder fieldValues(List<IPFIXFieldValue> fieldValues) {
            this.fieldValues = fieldValues;
            return this;
        }

        public IPFIXDataRecord build() {
            return new IPFIXDataRecord(this);
        }
    }
}