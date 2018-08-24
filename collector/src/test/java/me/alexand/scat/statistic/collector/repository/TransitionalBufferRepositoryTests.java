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

package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.TestConfig;
import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.collector.entities.DataRecordsTestEntities.CS_REQ_DATA_RECORD_1;
import static me.alexand.scat.statistic.collector.model.TemplateType.*;
import static me.alexand.scat.statistic.common.data.ClickCountTestEntities.COUNTER_20180401;
import static me.alexand.scat.statistic.common.data.ClickCountTestEntities.COUNTER_20180402;
import static me.alexand.scat.statistic.common.data.DomainRegexTestEntities.TEST_MAIL_RU;
import static me.alexand.scat.statistic.common.data.DomainRegexTestEntities.TEST_VK_COM;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

/**
 * @author asidorov84@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@SqlGroup({
        @Sql(
                scripts = "classpath:sql/populate.sql",
                executionPhase = BEFORE_TEST_METHOD,
                config = @SqlConfig(transactionMode = ISOLATED)
        ),
        @Sql(
                scripts = "classpath:sql/clear.sql",
                executionPhase = AFTER_TEST_METHOD,
                config = @SqlConfig(transactionMode = ISOLATED)
        )
})
public class TransitionalBufferRepositoryTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransitionalBufferRepositoryTests.class);
    private static final long EXPECTED_CS_REQ_RECORDS_COUNT = 22;
    private static final LocalDateTime DELETE_BEFORE_DATE_TIME = LocalDateTime.parse("2018-04-01T17:06:10");
    private static final long EXPECTED_DELETED_RECORDS_COUNT = 6;

    @Autowired
    private TransitionalBufferRepository repository;

    @Test
    public void testGetCountCSReq() {
        assertEquals(EXPECTED_CS_REQ_RECORDS_COUNT, repository.getCount(CS_REQ));
    }

    @Test
    public void testSaveCSReq() {
        assertTrue(repository.save(CS_REQ_DATA_RECORD_1));
    }

    @Test
    public void testDeleteBetween() {
        assertEquals(EXPECTED_DELETED_RECORDS_COUNT, repository.delete(CS_REQ, DELETE_BEFORE_DATE_TIME));
        assertEquals(0, repository.delete(CS_REQ, LocalDateTime.parse("2017-04-01T17:06:10")));
    }

    @Test
    public void testGetTrackedResults() {
        List<TrackedDomainRequests> actual = repository.getTrackedDomainRequests(
                asList(TEST_MAIL_RU, TEST_VK_COM),
                LocalDateTime.parse("2018-04-01T17:06:10"),
                LocalDateTime.parse("2018-04-01T17:07:09"));

        assertNotNull(actual);
        assertTrue(actual.size() == 5);
    }

    @Test
    public void testGetMinEventTime() {
        LocalDateTime actual = repository.getMinEventTime(CS_REQ);
        assertNotNull(actual);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMinEventTime(CS_RESP);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMinEventTime(GENERIC);
        LOGGER.info("actual result: {}", String.valueOf(actual));
    }

    @Test
    public void testGetMaxEventTime() {
        LocalDateTime actual = repository.getMaxEventTime(CS_REQ);
        assertNotNull(actual);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMaxEventTime(CS_RESP);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMaxEventTime(GENERIC);
        LOGGER.info("actual result: {}", String.valueOf(actual));
    }

    @Test
    public void testGetClickCount() {
        List<ClickCount> expected = asList(COUNTER_20180401, COUNTER_20180402);
        List<ClickCount> actual = repository.getClickCount(LocalDateTime.of(2018, 3, 1, 0, 0, 0), LocalDateTime.of(2018, 5, 1, 0, 0, 0));
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        expected.forEach(cc -> assertTrue(actual.contains(cc)));
    }
}
