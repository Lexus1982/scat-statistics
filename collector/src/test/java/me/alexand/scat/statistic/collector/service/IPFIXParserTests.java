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

package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.repository.DataTemplateRepository;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.utils.BytesConvertUtils;
import me.alexand.scat.statistic.collector.utils.exceptions.MalformedMessageException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static me.alexand.scat.statistic.collector.entities.IPFIXMessageTestEntities.IPFIX_MESSAGE_WITH_CS_REQ_DATA;
import static me.alexand.scat.statistic.collector.entities.IPFIXMessageTestEntities.IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE;
import static me.alexand.scat.statistic.collector.entities.RawPacketsEntities.*;
import static me.alexand.scat.statistic.collector.utils.DataTemplateEntities.DATA_TEMPLATE_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тесты IPFIX-парсера
 *
 * @author asidorov84@gmail.com
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class IPFIXParserTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXParserTests.class);

    @Autowired
    private IPFIXParser parser;

    @Autowired
    private DataTemplateRepository dataTemplateRepository;

    @Autowired
    private InfoModelRepository infoModelRepository;

    @Before
    public void before() {
        DATA_TEMPLATE_LIST.forEach(dataTemplate -> {
            dataTemplateRepository.save(dataTemplate);
            dataTemplate.getSpecifiers().forEach(infoModelRepository::save);
        });
    }

    @Test
    public void testParseCSREQTemplate() throws Exception {
        IPFIXMessage actual = parser.parse(RAW_CS_REQ_TEMPLATE);
        assertNotNull(actual);

        LOGGER.info("\n\nexpected:\n\t {}\n\nactual:\n\t {}\n", IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE, actual);

        assertEquals(IPFIX_MESSAGE_WITH_CS_REQ_TEMPLATE, actual);
    }

    @Test
    public void testParseCSREQData() throws Exception {
        assertNotNull(parser.parse(RAW_CS_REQ_TEMPLATE));

        IPFIXMessage actual = parser.parse(RAW_CS_REQ_DATA_PAYLOAD);
        assertNotNull(actual);

        LOGGER.info("\n\nexpected:\n\t {}\n\nactual:\n\t {}\n", IPFIX_MESSAGE_WITH_CS_REQ_DATA, actual);

        assertEquals(IPFIX_MESSAGE_WITH_CS_REQ_DATA, actual);
    }

    @Test
    public void testParser() throws Exception {
        parser.parse(RAW_TEMPLATES_PAYLOAD);
    }

    @Test
    public void testParseGenericData() throws Exception {
        parser.parse(RAW_GENERIC_TEMPLATE);

        IPFIXMessage actual = parser.parse(RAW_GENERIC_DATA);

        byte[] raw_flow_start = {0x00, 0x00, 0x01, 0x62, 0x13, 0x27, 0x38, (byte) 0x85};
        byte[] raw_flow_end = {0x00, 0x00, 0x01, 0x63, 0x13, 0x27, 0x45, 0x47};

        BigInteger flowStart = BytesConvertUtils.eightBytesToBigInteger(raw_flow_start);
        BigInteger flowEnd = BytesConvertUtils.eightBytesToBigInteger(raw_flow_end);

        System.out.println("Flow start (ms): " + flowStart);
        System.out.println("Flow end (ms): " + flowEnd);
        System.out.println("Duration (ms): " + flowEnd.subtract(flowStart));
        System.out.println("Flow start at: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(flowStart.longValue()), ZoneId.systemDefault()));
        System.out.println("Flow end at: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(flowEnd.longValue()), ZoneId.systemDefault()));

    }

    @Test(expected = NullPointerException.class)
    public void testParseWithNullPayload() throws Exception {
        parser.parse(null);
    }

    @Test(expected = MalformedMessageException.class)
    public void testParseInvalidLengthPayload() throws Exception {
        parser.parse(INVALID_PAYLOAD);
    }


    @Test(expected = MalformedMessageException.class)
    public void testParseWithShortPayload() throws Exception {
        parser.parse(new byte[15]);
    }

    @Test(expected = UnknownProtocolException.class)
    public void testParseUnknownProtocolPayload() throws Exception {
        parser.parse(new byte[16]);
    }
}