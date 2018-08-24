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

import me.alexand.scat.statistic.api.service.TrackedDomainRequestsService;
import me.alexand.scat.statistic.api.utils.SPRequestParam;
import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static me.alexand.scat.statistic.api.utils.Constants.BASE_URL;

/**
 * @author asidorov84@gmail.com
 */
@RestController
@RequestMapping(TrackedDomainRequestsRestController.URL)
public class TrackedDomainRequestsRestController {
    public static final String URL = BASE_URL + "/tracked/requests/statistics";
    private final TrackedDomainRequestsService trackedDomainRequestsService;

    public TrackedDomainRequestsRestController(TrackedDomainRequestsService trackedDomainRequestsService) {
        this.trackedDomainRequestsService = trackedDomainRequestsService;
    }

    @GetMapping
    public List<TrackedDomainRequests> get(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                           @RequestParam(required = false) Integer domainId,
                                           @RequestParam(required = false) String address,
                                           @RequestParam(required = false) String login,
                                           @SPRequestParam SortingAndPagination sortingAndPagination) {
        return trackedDomainRequestsService.get(from,
                to,
                domainId,
                address,
                login,
                sortingAndPagination);
    }
}
