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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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
    public static final String URL = BASE_URL + "/total/requests";
    private final ClickCountService clickCountService;

    public ClickCountRestController(ClickCountService clickCountService) {
        this.clickCountService = clickCountService;
    }

    @GetMapping("/per/day")
    public List<ClickCount> getPerDay(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                      @SPRequestParam SortingAndPagination sortingAndPagination) {
        return clickCountService.getPerDay(from, to, sortingAndPagination);
    }

    @GetMapping("/for/{date}")
    public ClickCount getOne(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return clickCountService.getOne(date);
    }
}
