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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * IP Flow Information Export (IPFIX) message.
 * <pre>
 * +----------------------------------------------------+
 * | Message Header                                     |
 * +----------------------------------------------------+
 * | Set                                                |
 * +----------------------------------------------------+
 * | Set                                                |
 * +----------------------------------------------------+
 * ...
 * +----------------------------------------------------+
 * | Set                                                |
 * +----------------------------------------------------+
 * </pre>
 *
 * @author asidorov84@gmail.com
 * @see <a href="https://tools.ietf.org/html/rfc7011#section-3">RFC-7011</a>
 * @see IPFIXHeader
 */
public final class IPFIXMessage {
    private final IPFIXHeader header;
    private final List<IPFIXDataRecord> dataRecords;

    public static IPFIXMessage.Builder builder() {
        return new IPFIXMessage.Builder();
    }

    private IPFIXMessage(IPFIXMessage.Builder builder) {
        this.header = builder.header;
        this.dataRecords = builder.dataRecords;
    }

    public IPFIXHeader getHeader() {
        return header;
    }

    public List<IPFIXDataRecord> getDataRecords() {
        return dataRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXMessage that = (IPFIXMessage) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(dataRecords, that.dataRecords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, dataRecords);
    }

    @Override
    public String toString() {
        return "IPFIXMessage{" +
                "header=" + header +
                ", dataRecords=" + dataRecords +
                '}';
    }

    public static class Builder {
        private IPFIXHeader header;
        private List<IPFIXDataRecord> dataRecords = new ArrayList<>();

        private Builder() {
        }

        public Builder header(IPFIXHeader header) {
            this.header = header;
            return this;
        }

        public Builder dataRecords(List<IPFIXDataRecord> dataRecords) {
            this.dataRecords = dataRecords;
            return this;
        }

        public IPFIXMessage build() {
            return new IPFIXMessage(this);
        }
    }
}