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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author asidorov84@gmail.com
 */
@RestController
@RequestMapping("api/tracked/domain/requests")//TODO подумать над адресами
public class TrackedDomainRequestsRestController {
    private final TrackedDomainRequestsService trackedDomainRequestsService;

    public TrackedDomainRequestsRestController(TrackedDomainRequestsService trackedDomainRequestsService) {
        this.trackedDomainRequestsService = trackedDomainRequestsService;
    }

    //TODO нужны методы для получения с фильтрами
    // 1. по дате (от и/или до)
    // 2. по id шаблона домена
    // 3. по адресу (like)
    // 4. по логину (like)
    
    
}
