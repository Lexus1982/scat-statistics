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

import me.alexand.scat.statistic.api.service.DomainRegexService;
import me.alexand.scat.statistic.api.utils.SPRequestParam;
import me.alexand.scat.statistic.common.entities.DomainRegex;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import me.alexand.scat.statistic.common.utils.exceptions.DomainPatternAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.PatternSyntaxException;

import static me.alexand.scat.statistic.api.utils.Constants.BASE_URL;

/**
 * REST-контроллер ресурсов DomainRegex
 *
 * @author asidorov84@gmail.com
 */
@RestController
@RequestMapping(DomainRegexRestController.URL)
public class DomainRegexRestController {
    public static final String URL = BASE_URL + "/tracked/domain";

    private final DomainRegexService domainRegexService;

    public DomainRegexRestController(DomainRegexService domainRegexService) {
        this.domainRegexService = domainRegexService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody String pattern) {
        domainRegexService.add(pattern);
    }

    @GetMapping("/all")
    public List<DomainRegex> getAll(@SPRequestParam SortingAndPagination sortingAndPagination) {
        return domainRegexService.getAll(sortingAndPagination);
    }

    @ExceptionHandler(PatternSyntaxException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "invalid pattern syntax")
    private void patternFormatError() {
    }

    @ExceptionHandler(DomainPatternAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "pattern already exists")
    private void domainRegexExistsError() {
    }
}
