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
import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.repository.ClickCountRepository;
import me.alexand.scat.statistic.common.utils.ColumnOrder;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.api.controllers.ClickCountRestController.URL;
import static me.alexand.scat.statistic.common.data.ClickCountTestEntities.*;
import static me.alexand.scat.statistic.common.utils.ColumnOrder.DESC;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты контроллера ресурсов ClickCount
 *
 * @author asidorov84@gmail.com
 */
public class ClickCountControllerTests extends AbstractControllerTests {

    @Autowired
    private ClickCountRepository clickCountRepository;

    @Test
    public void testGetAll() throws Exception {
        when(clickCountRepository.findBetween(isNull(), isNull(), eq(SortingAndPagination.builder()
                .offset(0)
                .limit(0)
                .build())))
                .thenReturn(CLICK_COUNT_LIST);

        String responseContent = mockMvc.perform(get(URL + "/per/day"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ClickCount> actual = om.readValue(responseContent, new TypeReference<List<ClickCount>>() {
        });
        assertEquals(CLICK_COUNT_LIST, actual);
    }

    @Test
    public void testGetWithSortingAndPagination() throws Exception {
        long page = 2;
        long size = 3;
        String orderColumnName = "date";
        ColumnOrder columnOrder = DESC;

        List<ClickCount> expected = asList(COUNTER_20180802, COUNTER_20180801, COUNTER_20180731);

        when(clickCountRepository.findBetween(isNull(), isNull(), eq(SortingAndPagination.builder()
                .offset(size * (page - 1))
                .limit(size)
                .orderingColumn(orderColumnName, columnOrder)
                .build())))
                .thenReturn(expected);

        String responseContent = mockMvc.perform(get(URL + "/per/day")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("order", String.format("%s,%s", orderColumnName, columnOrder.name().toLowerCase())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ClickCount> actual = om.readValue(responseContent, new TypeReference<List<ClickCount>>() {
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testGetBetweenDates() throws Exception {
        LocalDate start = LocalDate.of(2018, 7, 27);
        LocalDate end = LocalDate.of(2018, 7, 29);

        List<ClickCount> expected = asList(COUNTER_20180727, COUNTER_20180728, COUNTER_20180729);

        when(clickCountRepository.findBetween(eq(start), eq(end), eq(SortingAndPagination.builder()
                .offset(0)
                .limit(0)
                .build())))
                .thenReturn(expected);

        String responseContent = mockMvc.perform(get(URL + "/per/day")
                .param("from", start.format(DateTimeFormatter.ISO_DATE))
                .param("to", end.format(DateTimeFormatter.ISO_DATE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ClickCount> actual = om.readValue(responseContent, new TypeReference<List<ClickCount>>() {
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testGetBetweenDatesWithEmpty() throws Exception {
        LocalDate start = LocalDate.of(2999, 7, 27);
        LocalDate end = LocalDate.of(2999, 7, 29);

        List<ClickCount> expected = new ArrayList<>();

        when(clickCountRepository.findBetween(eq(start), eq(end), eq(SortingAndPagination.builder()
                .offset(0)
                .limit(0)
                .build())))
                .thenReturn(expected);

        String responseContent = mockMvc.perform(get(URL + "/per/day")
                .param("from", start.format(DateTimeFormatter.ISO_DATE))
                .param("to", end.format(DateTimeFormatter.ISO_DATE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ClickCount> actual = om.readValue(responseContent, new TypeReference<List<ClickCount>>() {
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testGetBetweenDatesWithSortingAndPagination() throws Exception {
        LocalDate start = LocalDate.of(2018, 7, 27);
        LocalDate end = LocalDate.of(2018, 7, 30);
        long page = 2;
        long size = 2;
        String orderColumnName = "date";
        ColumnOrder columnOrder = DESC;

        List<ClickCount> expected = asList(COUNTER_20180728, COUNTER_20180727);

        when(clickCountRepository.findBetween(eq(start), eq(end), eq(SortingAndPagination.builder()
                .offset(size * (page - 1))
                .limit(size)
                .orderingColumn(orderColumnName, columnOrder)
                .build())))
                .thenReturn(expected);

        String responseContent = mockMvc.perform(get(URL + "/per/day")
                .param("from", start.format(DateTimeFormatter.ISO_DATE))
                .param("to", end.format(DateTimeFormatter.ISO_DATE))
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("order", String.format("%s,%s", orderColumnName, columnOrder.name().toLowerCase())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ClickCount> actual = om.readValue(responseContent, new TypeReference<List<ClickCount>>() {
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testGetOne() throws Exception {
        LocalDate date = LocalDate.of(2018, 8, 1);

        when(clickCountRepository.findByDate(date))
                .thenReturn(COUNTER_20180801);

        String responseContent = mockMvc.perform(get(URL + "/for/{date}", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClickCount actual = om.readValue(responseContent, new TypeReference<ClickCount>() {
        });

        assertEquals(COUNTER_20180801, actual);
    }

    @Test
    public void testGetOneNotFound() throws Exception {
        LocalDate date = LocalDate.of(2999, 8, 1);

        when(clickCountRepository.findByDate(date))
                .thenReturn(null);

        mockMvc.perform(get(URL + "/for/{date}", date))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }
}
