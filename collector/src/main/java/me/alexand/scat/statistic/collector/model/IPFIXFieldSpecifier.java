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
 * IP Flow Information Export (IPFIX) Field Specifier.
 * <pre>
 * -----------------------------------------------------------------
 * +0                   1                   2                   3
 * +0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |E|  Information Element ident. |          Field Length         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                     Enterprise Number                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 *
 * @author asidorov84@gmail.com
 * @see <a href="https://tools.ietf.org/html/rfc7011#section-3.2">RFC-7011</a>
 */
public final class IPFIXFieldSpecifier {
    private final boolean enterpriseBit;
    private final int informationElementIdentifier;
    private final int fieldLength;
    private final long enterpriseNumber;

    private IPFIXFieldSpecifier(IPFIXFieldSpecifier.Builder builder) {
        this.enterpriseBit = builder.enterpriseBit;
        this.informationElementIdentifier = builder.informationElementIdentifier;
        this.fieldLength = builder.fieldLength;
        this.enterpriseNumber = builder.enterpriseNumber;
    }

    public static IPFIXFieldSpecifier.Builder builder() {
        return new IPFIXFieldSpecifier.Builder();
    }

    public boolean isEnterpriseBit() {
        return enterpriseBit;
    }

    public int getInformationElementIdentifier() {
        return informationElementIdentifier;
    }

    public int getFieldLength() {
        return fieldLength;
    }

    public long getEnterpriseNumber() {
        return enterpriseNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPFIXFieldSpecifier that = (IPFIXFieldSpecifier) o;
        return enterpriseBit == that.enterpriseBit &&
                informationElementIdentifier == that.informationElementIdentifier &&
                fieldLength == that.fieldLength &&
                enterpriseNumber == that.enterpriseNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enterpriseBit, informationElementIdentifier, fieldLength, enterpriseNumber);
    }

    @Override
    public String toString() {
        return "IPFIXFieldSpecifier{" +
                "enterpriseBit=" + enterpriseBit +
                ", informationElementIdentifier=" + informationElementIdentifier +
                ", fieldLength=" + fieldLength +
                ", enterpriseNumber=" + enterpriseNumber +
                '}';
    }

    public static class Builder {
        private boolean enterpriseBit;
        private int informationElementIdentifier;
        private int fieldLength;
        private long enterpriseNumber;

        private Builder() {
        }

        public Builder enterpriseBit(boolean enterpriseBit) {
            this.enterpriseBit = enterpriseBit;
            return this;
        }

        public Builder informationElementIdentifier(int informationElementIdentifier) {
            this.informationElementIdentifier = informationElementIdentifier;
            return this;
        }

        public Builder fieldLength(int fieldLength) {
            this.fieldLength = fieldLength;
            return this;
        }

        public Builder enterpriseNumber(long enterpriseNumber) {
            this.enterpriseNumber = enterpriseNumber;
            return this;
        }

        public IPFIXFieldSpecifier build() {
            return new IPFIXFieldSpecifier(this);
        }
    }
}