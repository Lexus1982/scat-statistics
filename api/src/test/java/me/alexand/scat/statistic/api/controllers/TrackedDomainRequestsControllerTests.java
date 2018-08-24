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

import com.fasterxml.jackson.core.type.TypeReference;
import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import me.alexand.scat.statistic.common.repository.TrackedDomainRequestsRepository;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

import static me.alexand.scat.statistic.api.controllers.TrackedDomainRequestsRestController.URL;
import static me.alexand.scat.statistic.common.data.TrackedDomainRequestsTestEntities.TRACKED_DOMAIN_REQUESTS_LIST;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author asidorov84@gmail.com
 */
public class TrackedDomainRequestsControllerTests extends AbstractControllerTests {
    @Autowired
    private TrackedDomainRequestsRepository trackedDomainRequestsRepository;

    @Test
    public void testGetAll() throws Exception {
        when(trackedDomainRequestsRepository.findBetween(isNull(), isNull(), anyMap(), eq(SortingAndPagination.builder()
                .offset(0)
                .limit(0)
                .build())))
                .thenReturn(TRACKED_DOMAIN_REQUESTS_LIST);

        String responseContent = mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();


        List<TrackedDomainRequests> actual = om.readValue(responseContent, new TypeReference<List<TrackedDomainRequests>>() {
        });
        assertEquals(TRACKED_DOMAIN_REQUESTS_LIST, actual);
    }
}
