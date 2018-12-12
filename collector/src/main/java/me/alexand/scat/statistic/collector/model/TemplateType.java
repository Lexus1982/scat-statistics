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

/**
 * Типы шаблонов экспортируемых данных АПК "СКАТ"
 *
 * @author asidorov84@gmail.com
 */
public enum TemplateType {
    CS_REQ("cs_req", "INSERT INTO ipfix_data.cs_req(event_datetime, login, ip_src, ip_dst, hostname, " +
            " path, refer, user_agent, cookie, session_id, locked, host_type, method) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"),
    CS_RESP("cs_resp", "INSERT INTO ipfix_data.cs_resp(event_datetime, login, ip_src, ip_dst, result_code, " +
            " content_length, content_type, session_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
    /*GENERIC("generic", "")*/;

    private String name;
    private String insertStatement;

    TemplateType(String name, String insertStatement) {
        this.name = name;
        this.insertStatement = insertStatement;
    }

    public String getName() {
        return name;
    }

    public String getInsertStatement() {
        return insertStatement;
    }
}