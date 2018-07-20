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

import java.sql.Types;

import static java.sql.Types.*;

/**
 * Типы данных согласно классификации IANA
 *
 * @author asidorov84@gmail.com
 */

public enum IANAAbstractDataTypes {
    UNSIGNED8(1, SMALLINT),
    UNSIGNED16(2, INTEGER),
    UNSIGNED32(4, BIGINT),
    UNSIGNED64(8, DECIMAL),
    SIGNED8(1, SMALLINT),
    SIGNED16(2, INTEGER),
    SIGNED32(4, BIGINT),
    SIGNED64(8, DECIMAL),
    FLOAT32(4, DOUBLE),
    FLOAT64(8, DOUBLE),
    BOOLEAN(0, Types.BOOLEAN),
    MAC_ADDRESS(0, VARCHAR),
    OCTET_ARRAY(0, VARCHAR),
    STRING(65535, VARCHAR),
    DATE_TIME_SECONDS(4, TIMESTAMP),
    DATE_TIME_MILLISECONDS(8, TIMESTAMP),
    DATE_TIME_MICROSECONDS(8, TIMESTAMP),
    DATE_TIME_NANOSECONDS(8, TIMESTAMP),
    IPV4_ADDRESS(4, CHAR),
    IPV6_ADDRESS(0, CHAR);

    private int length;
    private int sqlType;

    IANAAbstractDataTypes(int length, int sqlType) {
        this.length = length;
        this.sqlType = sqlType;
    }

    public int getLength() {
        return length;
    }

    public int getSqlType() {
        return sqlType;
    }
}