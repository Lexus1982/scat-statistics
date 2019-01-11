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

package me.alexand.scat.statistic.collector.entities;

import me.alexand.scat.statistic.collector.model.ImportDataTemplate;

import java.util.List;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.collector.entities.InfoModelEntities.*;

/**
 * Шаблоны экспортируемых данных АПК "СКАТ"
 *
 * @author asidorov84@gmail.com
 */
public interface SCATDataTemplateEntities {
    ImportDataTemplate CS_REQ_IMPORT_TEMPLATE = ImportDataTemplate.builder()
            .name("cs_req")
            .specifiers(asList(TIMESTAMP,
                    LOGIN,
                    SOURCE_IP,
                    DESTINATION_IP,
                    HOSTNAME,
                    PATH,
                    REFER,
                    USER_AGENT,
                    COOKIE,
                    SESSION_ID,
                    LOCKED,
                    HOST_TYPE,
                    METHOD))
            .isExport(true)
            .build();

    ImportDataTemplate CS_RESP_IMPORT_TEMPLATE = ImportDataTemplate.builder()
            .name("cs_resp")
            .specifiers(asList(TIMESTAMP,
                    LOGIN,
                    SOURCE_IP,
                    DESTINATION_IP,
                    RESULT_CODE,
                    CONTENT_LENGTH,
                    CONTENT_TYPE,
                    SESSION_ID))
            .isExport(false)
            .build();

    ImportDataTemplate GENERIC_IMPORT_TEMPLATE = ImportDataTemplate.builder()
            .name("generic")
            .specifiers(asList(OCTET_DELTA_COUNT,
                    PACKET_DELTA_COUNT,
                    PROTOCOL_IDENTIFIER,
                    IP_CLASS_OF_SERVICE,
                    SOURCE_TRANSPORT_PORT,
                    SOURCE_IPV4_ADDRESS,
                    DESTINATION_TRANSPORT_PORT,
                    DESTINATION_IPV4_ADDRESS,
                    BGP_SOURCE_AS,
                    BGP_DESTINATION_AS,
                    FLOW_START,
                    FLOW_END,
                    IN_SNMP,
                    OUT_SNMP,
                    IP_VERSION,
                    SESSION_ID,
                    HTTP_HOST,
                    DPI_PROTOCOL,
                    LOGIN_2,
                    PORT_NAT_SRC_ADDR,
                    PORT_NAT_SRC_PORT))
            .isExport(false)
            .build();

    List<ImportDataTemplate> DATA_TEMPLATE_LIST = asList(CS_REQ_IMPORT_TEMPLATE, CS_RESP_IMPORT_TEMPLATE, GENERIC_IMPORT_TEMPLATE);
}