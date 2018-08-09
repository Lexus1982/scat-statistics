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

import me.alexand.scat.statistic.api.service.ClickCountService;
import me.alexand.scat.statistic.api.utils.SPRequestParam;
import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

import static me.alexand.scat.statistic.api.utils.Constants.BASE_URL;

/**
 * REST-контроллер ресурсов ClickCount
 *
 * @author asidorov84@gmail.com
 */
@RestController
@RequestMapping(ClickCountRestController.URL)
public class ClickCountRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickCountRestController.class);
    public static final String URL = BASE_URL + "/clickcount";
    private final ClickCountService clickCountService;

    public ClickCountRestController(ClickCountService clickCountService) {
        this.clickCountService = clickCountService;
    }

    @GetMapping("get")
    @ResponseBody
    public List<ClickCount> getAll(@RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                   @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                   @SPRequestParam SortingAndPagination sortingAndPagination,
                                   HttpServletRequest request) {
        LOGGER.debug("got request {} from {}:{} with parameters: start='{}', end='{}', sap='{}'",
                request.getPathInfo(),
                request.getRemoteAddr(),
                request.getRemotePort(),
                start,
                end,
                sortingAndPagination);
        return clickCountService.getAll(start, end, sortingAndPagination);
    }

    @GetMapping("get/{date}")
    public ClickCount getOne(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             HttpServletRequest request) {
        LOGGER.debug("got request {} from {}:{}",
                request.getPathInfo(),
                request.getRemoteAddr(),
                request.getRemotePort(),
                date);
        return clickCountService.getOne(date);
    }
}
