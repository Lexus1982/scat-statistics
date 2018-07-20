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

package me.alexand.scat.statistic.collector.utils;

import me.alexand.scat.statistic.collector.model.InfoModelEntity;

import java.util.Arrays;
import java.util.List;

import static me.alexand.scat.statistic.collector.model.IANAAbstractDataTypes.*;

/**
 * @author asidorov84@gmail.com
 */

public interface InfoModelEntities {
    Integer IANA_ENTERPRISE_NUMBER = 0;
    Integer VAS_EXPERTS_ENTERPRISE_NUMBER = 43823;

    InfoModelEntity TIMESTAMP = InfoModelEntity.builder()
            .id(1)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1001)
            .name("timestamp")
            .type(DATE_TIME_SECONDS)
            .build();

    InfoModelEntity LOGIN = InfoModelEntity.builder()
            .id(2)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1002)
            .name("login")
            .type(STRING)
            .build();

    InfoModelEntity SOURCE_IP = InfoModelEntity.builder()
            .id(3)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1003)
            .name("ipSrc")
            .type(IPV4_ADDRESS)
            .build();

    InfoModelEntity DESTINATION_IP = InfoModelEntity.builder()
            .id(4)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1004)
            .name("ipDst")
            .type(IPV4_ADDRESS)
            .build();

    InfoModelEntity HOSTNAME = InfoModelEntity.builder()
            .id(5)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1005)
            .name("domain")
            .type(STRING)
            .build();

    InfoModelEntity PATH = InfoModelEntity.builder()
            .id(6)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1006)
            .name("path")
            .type(STRING)
            .build();

    InfoModelEntity REFER = InfoModelEntity.builder()
            .id(7)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1007)
            .name("refer")
            .type(STRING)
            .build();

    InfoModelEntity USER_AGENT = InfoModelEntity.builder()
            .id(8)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1008)
            .name("userAgent")
            .type(STRING)
            .build();

    InfoModelEntity COOKIE = InfoModelEntity.builder()
            .id(9)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1009)
            .name("cookie")
            .type(STRING)
            .build();

    InfoModelEntity LOCKED = InfoModelEntity.builder()
            .id(10)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1010)
            .name("locked")
            .type(UNSIGNED64)
            .build();

    InfoModelEntity HOST_TYPE = InfoModelEntity.builder()
            .id(11)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1011)
            .name("hostType")
            .type(UNSIGNED8)
            .build();

    InfoModelEntity METHOD = InfoModelEntity.builder()
            .id(12)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1012)
            .name("method")
            .type(UNSIGNED8)
            .build();

    InfoModelEntity RESULT_CODE = InfoModelEntity.builder()
            .id(13)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1020)
            .name("resultCode")
            .type(UNSIGNED32)
            .build();

    InfoModelEntity CONTENT_LENGTH = InfoModelEntity.builder()
            .id(14)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1021)
            .name("contentLength")
            .type(UNSIGNED64)
            .build();

    InfoModelEntity CONTENT_TYPE = InfoModelEntity.builder()
            .id(15)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(1022)
            .name("contentType")
            .type(STRING)
            .build();

    InfoModelEntity SESSION_ID = InfoModelEntity.builder()
            .id(16)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(2000)
            .name("sessionID")
            .type(UNSIGNED64)
            .build();

    InfoModelEntity HTTP_HOST = InfoModelEntity.builder()
            .id(17)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(2001)
            .name("httpHost")
            .type(STRING)
            .build();

    InfoModelEntity DPI_PROTOCOL = InfoModelEntity.builder()
            .id(18)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(2002)
            .name("dpiProtocol")
            .type(UNSIGNED16)
            .build();

    InfoModelEntity LOGIN_2 = InfoModelEntity.builder()
            .id(19)
            .enterpriseNumber(VAS_EXPERTS_ENTERPRISE_NUMBER)
            .informationElementId(2003)
            .name("login")
            .type(STRING)
            .build();

    InfoModelEntity OCTET_DELTA_COUNT = InfoModelEntity.builder()
            .id(20)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(1)
            .name("octetDeltaCount")
            .type(UNSIGNED64)
            .build();

    InfoModelEntity PACKET_DELTA_COUNT = InfoModelEntity.builder()
            .id(21)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(2)
            .name("packetDeltaCount")
            .type(UNSIGNED64)
            .build();

    InfoModelEntity PROTOCOL_IDENTIFIER = InfoModelEntity.builder()
            .id(22)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(4)
            .name("protocolIdentifier")
            .type(UNSIGNED8)
            .build();

    InfoModelEntity IP_CLASS_OF_SERVICE = InfoModelEntity.builder()
            .id(23)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(5)
            .name("ipClassOfService")
            .type(UNSIGNED8)
            .build();

    InfoModelEntity SOURCE_TRANSPORT_PORT = InfoModelEntity.builder()
            .id(24)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(7)
            .name("sourceTransportPort")
            .type(UNSIGNED16)
            .build();

    InfoModelEntity SOURCE_IPV4_ADDRESS = InfoModelEntity.builder()
            .id(25)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(8)
            .name("sourceIpv4Address")
            .type(IPV4_ADDRESS)
            .build();

    InfoModelEntity DESTINATION_TRANSPORT_PORT = InfoModelEntity.builder()
            .id(26)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(11)
            .name("destinationTransportPort")
            .type(UNSIGNED16)
            .build();

    InfoModelEntity DESTINATION_IPV4_ADDRESS = InfoModelEntity.builder()
            .id(27)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(12)
            .name("destinationIpv4Address")
            .type(IPV4_ADDRESS)
            .build();

    InfoModelEntity BGP_SOURCE_AS = InfoModelEntity.builder()
            .id(28)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(16)
            .name("bgpSourceAsNumber")
            .type(UNSIGNED32)
            .build();

    InfoModelEntity BGP_DESTINATION_AS = InfoModelEntity.builder()
            .id(29)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(17)
            .name("bgpDestinationAsNumber")
            .type(UNSIGNED32)
            .build();

    InfoModelEntity FLOW_START = InfoModelEntity.builder()
            .id(30)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(152)
            .name("flowStartMillisecond")
            .type(DATE_TIME_MILLISECONDS)
            .build();

    InfoModelEntity FLOW_END = InfoModelEntity.builder()
            .id(31)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(153)
            .name("flowEndMillisecond")
            .type(DATE_TIME_MILLISECONDS)
            .build();

    InfoModelEntity IN_SNMP = InfoModelEntity.builder()
            .id(32)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(10)
            .name("inputSNMP")
            .type(UNSIGNED16)
            .build();

    InfoModelEntity OUT_SNMP = InfoModelEntity.builder()
            .id(33)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(14)
            .name("outputSNMP")
            .type(UNSIGNED16)
            .build();

    InfoModelEntity IP_VERSION = InfoModelEntity.builder()
            .id(34)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(60)
            .name("ipVersion")
            .type(UNSIGNED8)
            .build();

    InfoModelEntity PORT_NAT_SRC_ADDR = InfoModelEntity.builder()
            .id(35)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(225)
            .name("portNATSourceIPv4Address")
            .type(IPV4_ADDRESS)
            .build();

    InfoModelEntity PORT_NAT_SRC_PORT = InfoModelEntity.builder()
            .id(36)
            .enterpriseNumber(IANA_ENTERPRISE_NUMBER)
            .informationElementId(227)
            .name("portNATSourceTransportPort")
            .type(UNSIGNED16)
            .build();

    List<InfoModelEntity> INFO_MODEL_DATA_LIST = Arrays.asList(
            TIMESTAMP,
            LOGIN,
            SOURCE_IP,
            DESTINATION_IP,
            HOSTNAME,
            PATH,
            REFER,
            USER_AGENT,
            COOKIE,
            LOCKED,
            HOST_TYPE,
            METHOD,
            RESULT_CODE,
            CONTENT_LENGTH,
            CONTENT_TYPE,
            SESSION_ID,
            HTTP_HOST,
            DPI_PROTOCOL,
            LOGIN_2,
            OCTET_DELTA_COUNT,
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
            PORT_NAT_SRC_ADDR,
            PORT_NAT_SRC_PORT
    );
}
