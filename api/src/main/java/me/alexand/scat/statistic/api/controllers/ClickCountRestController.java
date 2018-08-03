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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
@RestController
@RequestMapping("api/click/count")//TODO подумать на адресами
public class ClickCountRestController {
    private final ClickCountService clickCountService;

    public ClickCountRestController(ClickCountService clickCountService) {
        this.clickCountService = clickCountService;
    }

    @GetMapping("test")
    @ResponseBody
    public List<ClickCount> test() {
        return clickCountService.getAll();
    }
    
    //TODO
}
