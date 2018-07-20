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

import me.alexand.scat.statistic.collector.model.IANAAbstractDataTypes;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static me.alexand.scat.statistic.collector.utils.InfoModelEntities.INFO_MODEL_DATA_LIST;
import static me.alexand.scat.statistic.collector.utils.InfoModelEntities.SESSION_ID;
import static org.junit.Assert.*;

/**
 * @author asidorov84@gmail.com
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class InfoModelRepositoryTests {

    @Autowired
    private InfoModelRepository repository;

    @Before
    public void before() {
        repository.clear();
        INFO_MODEL_DATA_LIST.forEach(repository::save);
    }

    @Test
    public void testGetAll() {
        List<InfoModelEntity> actual = repository.getAll();
        assertEquals(INFO_MODEL_DATA_LIST, actual);
    }

    @Test
    public void testGetById() {
        for (InfoModelEntity expected : INFO_MODEL_DATA_LIST) {
            InfoModelEntity actual = repository.getById(expected.getId());
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetByEnterpriseNumberAndInformationElementIdentifier() throws Exception {
        for (InfoModelEntity expected : INFO_MODEL_DATA_LIST) {
            InfoModelEntity actual = repository.getByEnterpriseNumberAndInformationElementIdentifier(
                    expected.getEnterpriseNumber(),
                    expected.getInformationElementId());
            assertEquals(expected, actual);
        }
    }

    @Test(expected = UnknownInfoModelException.class)
    public void testGetByUnknownEnterpriseNumberAndInformationElementIdentifier() throws Exception {
        repository.getByEnterpriseNumberAndInformationElementIdentifier(Long.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void testDelete() {
        assertTrue(repository.delete(SESSION_ID.getId()));
        assertNull(repository.getById(SESSION_ID.getId()));
    }

    @Test
    public void testSave() {
        InfoModelEntity expected = InfoModelEntity.builder()
                .enterpriseNumber(Long.MAX_VALUE)
                .informationElementId(Integer.MAX_VALUE)
                .type(IANAAbstractDataTypes.DATE_TIME_NANOSECONDS)
                .name("Test")
                .build();

        InfoModelEntity actual = repository.save(expected);

        assertNotNull(actual);
        expected.setId(actual.getId());
        assertEquals(expected, actual);
    }

    @Test
    public void testGetCount() {
        assertEquals(INFO_MODEL_DATA_LIST.size(), repository.getCount());
    }

    @Test
    public void testClear() {
        repository.clear();
        assertEquals(0, repository.getAll().size());
    }
}
