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

import me.alexand.scat.statistic.collector.model.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.collector.model.IANAAbstractDataTypes.*;
import static me.alexand.scat.statistic.collector.model.TemplateType.CS_REQ;
import static me.alexand.scat.statistic.collector.utils.InfoModelEntities.*;

/**
 * @author asidorov84@gmail.com
 */
public interface IPFIXMessageTestEntities {
    IPFIXTemplateRecord CS_REQ_TEMPLATE = IPFIXTemplateRecord.builder()
            .templateID(256)
            .fieldCount(13)
            .exportTime(LocalDateTime.of(2017, 10, 16, 13, 52, 12).toEpochSecond(ZoneOffset.ofHours(3)))
            .type(CS_REQ)
            .fieldSpecifiers(asList(convertFromInfoModelEntity(TIMESTAMP),
                    convertFromInfoModelEntity(LOGIN),
                    convertFromInfoModelEntity(SOURCE_IP),
                    convertFromInfoModelEntity(DESTINATION_IP),
                    convertFromInfoModelEntity(HOSTNAME),
                    convertFromInfoModelEntity(PATH),
                    convertFromInfoModelEntity(REFER),
                    convertFromInfoModelEntity(USER_AGENT),
                    convertFromInfoModelEntity(COOKIE),
                    convertFromInfoModelEntity(SESSION_ID),
                    convertFromInfoModelEntity(LOCKED),
                    convertFromInfoModelEntity(HOST_TYPE),
                    convertFromInfoModelEntity(METHOD)))
            .build();

    IPFIXHeader IPFIX_HEADER_WITH_CS_REQ_TEMPLATE = IPFIXHeader.builder()
            .version(10)
            .length(128)
            .exportTime(LocalDateTime.of(2017, 10, 16, 13, 52, 12).toEpochSecond(ZoneOffset.ofHours(3)))
            .sequenceNumber(47_108_922)
            .observationDomainID(1)
            .build();

    IPFIXSet IPFIX_SET_WITH_CS_REQ_TEMPLATE = IPFIXSet.builder()
            .setID(2)
            .length(112)
            .records(asList(CS_REQ_TEMPLATE))
            .build();

    IPFIXMessage IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE = IPFIXMessage.builder()
            .header(IPFIX_HEADER_WITH_CS_REQ_TEMPLATE)
            .sets(asList(IPFIX_SET_WITH_CS_REQ_TEMPLATE))
            .build();

    IPFIXDataRecord IPFIX_CS_REQ_DATA_RECORD = IPFIXDataRecord.builder()
            .type(CS_REQ)
            .fieldValues(asList(
                    IPFIXFieldValue.builder().name("timestamp").type(DATE_TIME_SECONDS).build(),
                    IPFIXFieldValue.builder().name("login").type(STRING).build(),
                    IPFIXFieldValue.builder().name("ipSrc").type(IPV4_ADDRESS).build(),
                    IPFIXFieldValue.builder().name("ipDst").type(IPV4_ADDRESS).build(),
                    IPFIXFieldValue.builder().name("domain").type(STRING).build(),
                    IPFIXFieldValue.builder().name("path").type(STRING).build(),
                    IPFIXFieldValue.builder().name("refer").type(STRING).build(),
                    IPFIXFieldValue.builder().name("userAgent").type(STRING).build(),
                    IPFIXFieldValue.builder().name("cookie").type(STRING).build(),
                    IPFIXFieldValue.builder().name("sessionID").type(UNSIGNED64).build(),
                    IPFIXFieldValue.builder().name("locked").type(UNSIGNED64).build(),
                    IPFIXFieldValue.builder().name("hostType").type(UNSIGNED8).build(),
                    IPFIXFieldValue.builder().name("method").type(UNSIGNED8).build()))
            .build();

    IPFIXHeader IPFIX_HEADER_WITH_CS_REQ_DATA = IPFIXHeader.builder()
            .version(10)
            .length(565)
            .exportTime(LocalDateTime.of(2017, 10, 16, 13, 52, 18).toEpochSecond(ZoneOffset.ofHours(3)))
            .sequenceNumber(47_130_182)
            .observationDomainID(1)
            .build();

    IPFIXSet IPFIX_SET_WITH_CS_REQ_DATA = IPFIXSet.builder()
            .setID(256)
            .length(549)
            .records(asList(IPFIX_CS_REQ_DATA_RECORD))
            .build();

    IPFIXMessage IPFIX_MESSAGE_WITH_CS_REQ_DATA = IPFIXMessage.builder()
            .header(IPFIX_HEADER_WITH_CS_REQ_DATA)
            .sets(asList(IPFIX_SET_WITH_CS_REQ_DATA))
            .build();

    static IPFIXFieldSpecifier convertFromInfoModelEntity(InfoModelEntity entity) {
        return IPFIXFieldSpecifier.builder()
                .enterpriseBit(entity.getEnterpriseNumber() != 0)
                .informationElementIdentifier(entity.getInformationElementId())
                .fieldLength(entity.getType().getLength())
                .enterpriseNumber(entity.getEnterpriseNumber())
                .build();
    }
}
