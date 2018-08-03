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

package me.alexand.scat.statistic.common.repository;

import me.alexand.scat.statistic.common.entities.DomainRegex;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.assertEquals;


/**
 * Тесты для проверки хранилища шаблонов доменных имен
 *
 * @author asidorov84@gmail.com
 */
public class DomainRegexRepositoryTests extends AbstractCommonTests {
    private static final long POPULATED_DOMAINS_COUNT = 2;

    private static final String VK_COM_REGEX_PATTERN = ".*vk\\.com$";
    private static final String MAIL_RU_REGEX_PATTERN = ".*mail\\.ru$";
    private static final String OK_RU_REGEX_PATTERN = ".*ok\\.ru$";

    private static final DomainRegex TEST_VK_COM = DomainRegex.builder()
            .id(1)
            .pattern(VK_COM_REGEX_PATTERN)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    private static final DomainRegex TEST_MAIL_RU = DomainRegex.builder()
            .id(2)
            .pattern(MAIL_RU_REGEX_PATTERN)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();


    @Autowired
    private DomainRegexRepository repository;

    @Test
    public void testGetAll() {
        List<DomainRegex> actual = repository.getAll();
        Assert.assertNotNull(actual);
        assertEquals(actual.size(), POPULATED_DOMAINS_COUNT);
        Assert.assertTrue(actual.contains(TEST_VK_COM));
        Assert.assertTrue(actual.contains(TEST_MAIL_RU));

        repository.delete(TEST_VK_COM.getId());
        repository.delete(TEST_MAIL_RU.getId());
        actual = repository.getAll();
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isEmpty());
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
        Assert.assertNull(actual);
        assertEquals(POPULATED_DOMAINS_COUNT, repository.getCount());
    }

    @Test
    public void testAdd() {
        DomainRegex actual = repository.add(OK_RU_REGEX_PATTERN);
        Assert.assertNotNull(actual);
        assertEquals(OK_RU_REGEX_PATTERN, actual.getPattern());

        List<DomainRegex> domainRegexList = repository.getAll();
        Assert.assertNotNull(domainRegexList);

        Assert.assertTrue(domainRegexList.stream()
                .map(DomainRegex::getPattern)
                .anyMatch(pattern -> pattern.equals(OK_RU_REGEX_PATTERN)));
    }

    @Test
    public void testDelete() {
        Assert.assertTrue(repository.delete(TEST_VK_COM.getId()));
        Assert.assertFalse(repository.delete(0));
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