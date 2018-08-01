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
 * IP Flow Information Export (IPFIX) message Set.
 * <pre>
 * -----------------------------------------------------------------
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |              Set ID           |            Length             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             record                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             record                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             record                            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       padding (optional)                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 *
 * @author asidorov84@gmail.com
 * @see <a href="https://tools.ietf.org/html/rfc7011#section-3.3">RFC-7011</a>
 * @see IPFIXRecord
 */

public final class IPFIXSet {
    private final int setID;
    private final int length;
    private final List<? extends IPFIXRecord> records;

    public static IPFIXSet.Builder builder() {
        return new IPFIXSet.Builder();
    }

    private IPFIXSet(IPFIXSet.Builder builder) {
        this.setID = builder.setID;
        this.length = builder.length;
        this.records = builder.records;
    }

    public int getSetID() {
        return setID;
    }

    public int getLength() {
        return length;
    }

    public List<? extends IPFIXRecord> getRecords() {
        return records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXSet ipfixSet = (IPFIXSet) o;
        return setID == ipfixSet.setID &&
                length == ipfixSet.length &&
                Objects.equals(records, ipfixSet.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(setID, length, records);
    }

    @Override
    public String toString() {
        return "IPFIXSet{" +
                "setID=" + setID +
                ", length=" + length +
                ", records=" + records +
                '}';
    }

    public static class Builder {
        private int setID;
        private int length;
        private List<? extends IPFIXRecord> records = new ArrayList<>();

        private Builder() {
        }

        public Builder setID(int setID) {
            this.setID = setID;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder records(List<? extends IPFIXRecord> records) {
            this.records = records;
            return this;
        }

        public IPFIXSet build() {
            return new IPFIXSet(this);
        }
    }
}