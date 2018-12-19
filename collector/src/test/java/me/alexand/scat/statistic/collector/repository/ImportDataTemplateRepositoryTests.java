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
import me.alexand.scat.statistic.collector.model.ImportDataTemplate;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownTemplateTypeException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;

import static me.alexand.scat.statistic.collector.entities.InfoModelEntities.*;
import static me.alexand.scat.statistic.collector.entities.SCATDataTemplateEntities.CS_REQ_IMPORT_TEMPLATE;
import static me.alexand.scat.statistic.collector.entities.SCATDataTemplateEntities.DATA_TEMPLATE_LIST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author asidorov84@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImportDataTemplateRepositoryTests {
    
    @Autowired
    private ImportDataTemplateRepository repository;
    
    @Test
    public void testFindAll() {
        Collection<ImportDataTemplate> actual = repository.findAll();
        assertNotNull(actual);
        DATA_TEMPLATE_LIST.forEach(t -> Assert.assertTrue(actual.contains(t)));
    }

    @Test
    public void testGetCount() {
        assertEquals(DATA_TEMPLATE_LIST.size(), repository.getCount());
    }

    @Test
    public void testGetInfoModel() throws UnknownInfoModelException {
        InfoModelEntity actual = repository.getInfoModel(SESSION_ID.getEnterpriseNumber(), SESSION_ID.getInformationElementId());
        assertNotNull(actual);
        assertEquals(SESSION_ID, actual);
    }

    @Test(expected = UnknownInfoModelException.class)
    public void testGetUnknownInfoModel() throws UnknownInfoModelException {
        repository.getInfoModel(0, 0);
    }

    @Test
    public void testFindByInfoModelEntities() throws UnknownTemplateTypeException {
        ImportDataTemplate actual = repository.findByInfoModelEntities(CS_REQ_IMPORT_TEMPLATE.getSpecifiers());
        assertNotNull(actual);
        assertEquals(CS_REQ_IMPORT_TEMPLATE, actual);
    }

    @Test(expected = UnknownTemplateTypeException.class)
    public void testFindUnknownImportTemplate() throws UnknownTemplateTypeException {
        repository.findByInfoModelEntities(Arrays.asList(SESSION_ID, LOGIN, LOCKED));
    }
}
