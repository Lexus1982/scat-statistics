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
 * IP Flow Information Export (IPFIX) message header.
 * <pre>
 * -----------------------------------------------------------------
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Version Number        |            Length             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Export Time                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Sequence Number                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                   Observation Domain ID                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 *
 * @author asidorov84@gmail.com
 * @see  <a href="https://tools.ietf.org/html/rfc7011#section-3.1">RFC-7011</a>
 */

public final class IPFIXHeader {
    public static final int IPFIX_MESSAGE_HEADER_LENGTH = 16;
    public static final int IPFIX_MESSAGE_VERSION = 0x0A;

    private final int version = IPFIX_MESSAGE_VERSION;
    private final int length;
    private final long exportTime;
    private final long sequenceNumber;
    private final long observationDomainID;

    public static IPFIXHeader.Builder builder() {
        return new IPFIXHeader.Builder();
    }

    private IPFIXHeader(IPFIXHeader.Builder builder) {
        this.length = builder.length;
        this.exportTime = builder.exportTime;
        this.sequenceNumber = builder.sequenceNumber;
        this.observationDomainID = builder.observationDomainID;
    }

    public int getVersion() {
        return version;
    }

    public int getLength() {
        return length;
    }

    public long getExportTime() {
        return exportTime;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public long getObservationDomainID() {
        return observationDomainID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXHeader that = (IPFIXHeader) o;
        return length == that.length &&
                exportTime == that.exportTime &&
                sequenceNumber == that.sequenceNumber &&
                observationDomainID == that.observationDomainID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, length, exportTime, sequenceNumber, observationDomainID);
    }

    @Override
    public String toString() {
        return "IPFIXHeader{" +
                "version=" + version +
                ", length=" + length +
                ", exportTime=" + exportTime +
                ", sequenceNumber=" + sequenceNumber +
                ", observationDomainID=" + observationDomainID +
                '}';
    }

    public static class Builder {
        private int length;
        private long exportTime;
        private long sequenceNumber;
        private long observationDomainID;

        private Builder() {
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder exportTime(long exportTime) {
            this.exportTime = exportTime;
            return this;
        }

        public Builder sequenceNumber(long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder observationDomainID(long observationDomainID) {
            this.observationDomainID = observationDomainID;
            return this;
        }

        public IPFIXHeader build() {
            return new IPFIXHeader(this);
        }
    }
}