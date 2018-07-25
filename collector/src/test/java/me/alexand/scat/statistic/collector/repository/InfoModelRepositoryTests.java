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

import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.repository.impls.InMemoryInfoModelRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.alexand.scat.statistic.collector.utils.InfoModelEntities.INFO_MODEL_DATA_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Тесты хранилища информационных элементов
 *
 * @author asidorov84@gmail.com
 */

public class InfoModelRepositoryTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoModelRepositoryTests.class);
    private final InfoModelRepository repository = new InMemoryInfoModelRepositoryImpl();

    @Before
    public void before() {
        INFO_MODEL_DATA_LIST.forEach(repository::save);
    }

    @Test
    public void testGetByEnterpriseNumberAndInformationElementIdentifier() {
        INFO_MODEL_DATA_LIST.forEach(expected -> {
            long t0 = System.nanoTime();
            InfoModelEntity actual = repository.getByEnterpriseNumberAndInformationElementIdentifier(
                    expected.getEnterpriseNumber(),
                    expected.getInformationElementId());
            long t1 = System.nanoTime();
            long ns = t1 - t0;
            long ms = ns / 1_000_000;
            LOGGER.info("Element acquisition time is {} ns ({} ms)", ns, ms);
            assertEquals(expected, actual);

        });
    }

    @Test
    public void testGetByUnknownEnterpriseNumberAndInformationElementIdentifier() {
        assertNull(repository.getByEnterpriseNumberAndInformationElementIdentifier(Long.MAX_VALUE, Integer.MAX_VALUE));
    }
}
