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
import me.alexand.scat.statistic.common.entities.ClickCount;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static me.alexand.scat.statistic.api.utils.Constants.BASE_URL;

/**
 * @author asidorov84@gmail.com
 */
@RestController
@RequestMapping(ClickCountRestController.URL)
public class ClickCountRestController {
    public static final String URL = BASE_URL + "/requests/total";
    private final ClickCountService clickCountService;

    public ClickCountRestController(ClickCountService clickCountService) {
        this.clickCountService = clickCountService;
    }

    @GetMapping("get")
    @ResponseBody
    public List<ClickCount> get(@RequestParam(name = "start", required = false) LocalDate start,
                                @RequestParam(name = "end", required = false) LocalDate end,
                                @RequestParam(name = "page", required = false, defaultValue = "0") long page,
                                @RequestParam(name = "size", required = false, defaultValue = "0") long size,
                                @RequestParam(name = "order", required = false) String[] ordering) {
        return clickCountService.get(start, end, page, size, ordering);
    }
}
