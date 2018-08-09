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

package me.alexand.scat.statistic.api.controllers;

import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import me.alexand.scat.statistic.common.utils.exceptions.DomainRegexAlreadyExistsException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.regex.PatternSyntaxException;

import static me.alexand.scat.statistic.api.controllers.DomainRegexRestController.URL;
import static me.alexand.scat.statistic.common.data.DomainRegexTestEntities.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты контроллера ресурсов DomainRegex
 *
 * @author asidorov84@gmail.com
 */
public class DomainRegexControllerTests extends AbstractControllerTests {
    @Autowired
    private DomainRegexRepository domainRegexRepository;

    @Test
    public void testNormalAdd() throws Exception {
        when(domainRegexRepository.add(VK_COM_REGEX_PATTERN))
                .thenReturn(TEST_VK_COM);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(VK_COM_REGEX_PATTERN))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddDuplicate() throws Exception {
        when(domainRegexRepository.add(VK_COM_REGEX_PATTERN))
                .thenThrow(DomainRegexAlreadyExistsException.class);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(VK_COM_REGEX_PATTERN))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void testAddWithIllegalPatternSyntax() throws Exception {
        when(domainRegexRepository.add(INVALID_PATTERN))
                .thenThrow(PatternSyntaxException.class);

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(INVALID_PATTERN))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddEmpty() throws Exception {
        mockMvc.perform(post(URL))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
