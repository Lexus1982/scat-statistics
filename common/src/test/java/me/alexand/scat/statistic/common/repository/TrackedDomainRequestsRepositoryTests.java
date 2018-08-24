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

import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static me.alexand.scat.statistic.common.data.TrackedDomainRequestsTestEntities.TEST_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тесты для проверки хранилища сущностей TrackedDomainRequests
 *
 * @author asidorov84@gmail.com
 */
public class TrackedDomainRequestsRepositoryTests extends AbstractCommonTests {
    @Autowired
    private TrackedDomainRequestsRepository repository;

    @Test
    public void testSave() {
        assertEquals(1, repository.save(TEST_1));
    }

    @Test
    public void testSaveDuplicate() {
        assertEquals(1, repository.save(TEST_1));
        assertEquals(1, repository.save(TEST_1));
    }

    @Test
    public void testSaveAll() {
        assertEquals(1, repository.saveAll(Collections.singletonList(TEST_1)));
    }

    @Test
    public void testFindByAddress() {
        List<TrackedDomainRequests> actual = repository.findBetween(LocalDate.of(2018, 8, 1), LocalDate.of(2018, 8, 1), null, null);
        assertNotNull(actual);
    }
}
