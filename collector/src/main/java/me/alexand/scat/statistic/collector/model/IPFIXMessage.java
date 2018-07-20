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
 * IP Flow Information Export (IPFIX) message
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
 *
 * @author asidorov84@gmail.com
 * @link https://tools.ietf.org/html/rfc7011
 */
public class IPFIXMessage {
    private final IPFIXHeader header;
    private final List<IPFIXSet> sets;

    public static IPFIXMessage.Builder builder() {
        return new IPFIXMessage.Builder();
    }

    private IPFIXMessage(IPFIXMessage.Builder builder) {
        this.header = builder.header;
        this.sets = builder.sets;
    }

    public IPFIXHeader getHeader() {
        return header;
    }

    public List<IPFIXSet> getSets() {
        return sets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXMessage that = (IPFIXMessage) o;
        return Objects.equals(header, that.header) &&
                Objects.equals(sets, that.sets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, sets);
    }

    @Override
    public String toString() {
        return "IPFIXMessage{" +
                "header=" + header +
                ", sets=" + sets +
                '}';
    }

    public static class Builder {
        private IPFIXHeader header;
        private List<IPFIXSet> sets = new ArrayList<>();

        private Builder() {
        }

        public Builder header(IPFIXHeader header) {
            this.header = header;
            return this;
        }

        public Builder sets(List<IPFIXSet> sets) {
            this.sets = sets;
            return this;
        }

        public IPFIXMessage build() {
            return new IPFIXMessage(this);
        }
    }
}