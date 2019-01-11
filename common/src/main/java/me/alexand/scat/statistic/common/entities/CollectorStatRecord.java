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
import java.util.UUID;

/**
 * @author asidorov84@gmail.com
 */
@JsonDeserialize(builder = CollectorStatRecord.Builder.class)
public class CollectorStatRecord {
    private final UUID uuid;
    private final String address;
    private final int port;
    
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private final LocalDateTime started;
    
    private final int period;
    
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private final LocalDateTime lastUpdated;
    
    private final int processorsThreadsCount;
    private final int packetsReceivedCount;
    private final int packetsProcessedCount;
    private final int packetsParseFailedCount;
    private final int inputQueueOverflowCount;
    private final int outputQueueOverflowCount;
    private final int recordsExportedCount;

    private CollectorStatRecord(CollectorStatRecord.Builder builder) {
        this.uuid = builder.uuid;
        this.address = builder.address;
        this.port = builder.port;
        this.started = builder.started;
        this.period = builder.period;
        this.lastUpdated = builder.lastUpdated;
        this.processorsThreadsCount = builder.processorsThreadsCount;
        this.packetsReceivedCount = builder.packetsReceivedCount;
        this.packetsProcessedCount = builder.packetsProcessedCount;
        this.packetsParseFailedCount = builder.packetsParseFailedCount;
        this.inputQueueOverflowCount = builder.inputQueueOverflowCount;
        this.outputQueueOverflowCount = builder.outputQueueOverflowCount;
        this.recordsExportedCount = builder.recordsExportedCount;
    }

    public static CollectorStatRecord.Builder builder() {
        return new CollectorStatRecord.Builder();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public int getPeriod() {
        return period;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public int getProcessorsThreadsCount() {
        return processorsThreadsCount;
    }

    public int getPacketsReceivedCount() {
        return packetsReceivedCount;
    }

    public int getPacketsProcessedCount() {
        return packetsProcessedCount;
    }

    public int getPacketsParseFailedCount() {
        return packetsParseFailedCount;
    }

    public int getInputQueueOverflowCount() {
        return inputQueueOverflowCount;
    }

    public int getOutputQueueOverflowCount() {
        return outputQueueOverflowCount;
    }

    public int getRecordsExportedCount() {
        return recordsExportedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectorStatRecord that = (CollectorStatRecord) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "CollectorStatRecord{" +
                "uuid=" + uuid +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", started=" + started +
                ", period=" + period +
                ", lastUpdated=" + lastUpdated +
                ", processorsThreadsCount=" + processorsThreadsCount +
                ", packetsReceivedCount=" + packetsReceivedCount +
                ", packetsProcessedCount=" + packetsProcessedCount +
                ", packetsParseFailedCount=" + packetsParseFailedCount +
                ", inputQueueOverflowCount=" + inputQueueOverflowCount +
                ", outputQueueOverflowCount=" + outputQueueOverflowCount +
                ", recordsExportedCount=" + recordsExportedCount +
                '}';
    }
    
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private UUID uuid;
        private String address;
        private int port;
        
        @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
        private LocalDateTime started;
        
        private int period;
        
        @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
        private LocalDateTime lastUpdated;
        
        private int processorsThreadsCount;
        private int packetsReceivedCount;
        private int packetsProcessedCount;
        private int packetsParseFailedCount;
        private int inputQueueOverflowCount;
        private int outputQueueOverflowCount;
        private int recordsExportedCount;

        private Builder() {
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder started(LocalDateTime started) {
            this.started = started;
            return this;
        }

        public Builder period(int period) {
            this.period = period;
            return this;
        }

        public Builder lastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder processorsThreadsCount(int processorsThreadsCount) {
            this.processorsThreadsCount = processorsThreadsCount;
            return this;
        }

        public Builder packetsReceivedCount(int packetsReceivedCount) {
            this.packetsReceivedCount = packetsReceivedCount;
            return this;
        }

        public Builder packetsProcessedCount(int packetsProcessedCount) {
            this.packetsProcessedCount = packetsProcessedCount;
            return this;
        }

        public Builder packetsParseFailedCount(int packetsParseFailedCount) {
            this.packetsParseFailedCount = packetsParseFailedCount;
            return this;
        }

        public Builder inputQueueOverflowCount(int inputQueueOverflowCount) {
            this.inputQueueOverflowCount = inputQueueOverflowCount;
            return this;
        }

        public Builder outputQueueOverflowCount(int outputQueueOverflowCount) {
            this.outputQueueOverflowCount = outputQueueOverflowCount;
            return this;
        }

        public Builder recordsExportedCount(int recordsExportedCount) {
            this.recordsExportedCount = recordsExportedCount;
            return this;
        }

        public CollectorStatRecord build() {
            return new CollectorStatRecord(this);
        }
    }
}
