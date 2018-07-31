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

import me.alexand.scat.statistic.common.model.DomainRegex;
import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import static me.alexand.scat.statistic.collector.entities.DomainRegexTestEntities.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

/**
 * @author asidorov84@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
@SqlGroup({
        @Sql(
                scripts = "classpath:db/postgres/populate.sql",
                executionPhase = BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        transactionManager = "postgresqlTM",
                        transactionMode = ISOLATED,
                        dataSource = "postgresqlTestDataSource")
        ),
        @Sql(
                scripts = "classpath:db/postgres/clear.sql",
                executionPhase = AFTER_TEST_METHOD,
                config = @SqlConfig(
                        transactionManager = "postgresqlTM",
                        transactionMode = ISOLATED,
                        dataSource = "postgresqlTestDataSource")
        )
})
public class DomainRegexRepositoryTests {
    @Autowired
    private DomainRegexRepository repository;

    @Test
    public void testGetAll() {
        List<DomainRegex> actual = repository.getAll();
        assertNotNull(actual);
        assertEquals(actual.size(), POPULATED_DOMAINS_COUNT);
        assertTrue(actual.contains(TEST_VK_COM));
        assertTrue(actual.contains(TEST_MAIL_RU));
        
        repository.delete(TEST_VK_COM.getId());
        repository.delete(TEST_MAIL_RU.getId());
        actual = repository.getAll();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testGetCount() {
        assertEquals(POPULATED_DOMAINS_COUNT, repository.getCount());
        repository.delete(TEST_VK_COM.getId());
        repository.delete(TEST_MAIL_RU.getId());
        assertEquals(0, repository.getCount());
    }

    @Test
    public void testAddDuplicate() {
        DomainRegex actual = repository.add(MAIL_RU_REGEX_PATTERN);
        assertNull(actual);
        assertEquals(POPULATED_DOMAINS_COUNT, repository.getCount());
    }

    @Test
    public void testAdd() {
        DomainRegex actual = repository.add(OK_RU_REGEX_PATTERN);
        assertNotNull(actual);
        assertEquals(OK_RU_REGEX_PATTERN, actual.getPattern());

        List<DomainRegex> domainRegexList = repository.getAll();
        assertNotNull(domainRegexList);

        assertTrue(domainRegexList.stream()
                .map(DomainRegex::getPattern)
                .anyMatch(pattern -> pattern.equals(OK_RU_REGEX_PATTERN)));
    }

    @Test
    public void testDelete() {
        assertTrue(repository.delete(TEST_VK_COM.getId()));
        assertFalse(repository.delete(0));
    }

    @Test(expected = PatternSyntaxException.class)
    public void testAddWithInvalidSyntax() {
        repository.add("\\\\1111\\q");
    }

    @Test(expected = NullPointerException.class)
    public void testAddWithNull() {
        repository.add(null);
    }

    @Test(expected = PatternSyntaxException.class)
    public void testAddWithEmpty() {
        repository.add("");
    }
}