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

import me.alexand.scat.statistic.common.entities.CollectorStatRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static me.alexand.scat.statistic.common.data.CollectorStatRecordTestEntities.TEST_RECORDS;
import static org.junit.Assert.*;

/**
 * @author asidorov84@gmail.com
 */
public class CollectorStatRecordRepositoryTests extends AbstractCommonTests {
    @Autowired
    private CollectorStatRecordRepository repository;

    @Test
    public void testGetLastRecordsPerCollectorInstance() {
        List<CollectorStatRecord> actual = repository.findAll();
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        TEST_RECORDS.forEach(r -> assertTrue(actual.contains(r)));
    }
}
