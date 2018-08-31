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

package me.alexand.scat.statistic.cms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author asidorov84@gmail.com
 */
@Controller
public class PagesController {
    @GetMapping("/")
    public String indexPage() {
        return "redirect:status";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login-page";
    }

    @GetMapping("/admin/users")
    public String usersPage() {
        return "users-page";
    }

    @GetMapping("/status")
    public String statusPage() {
        return "status-page";
    }

    @GetMapping("/admin/domains")
    public String domainsPage() {
        return "domains-page";
    }

    @GetMapping("/reports/click/count")
    public String clickCountPage() {
        return "click-count-page";
    }

    @GetMapping("/reports/tracked/requests")
    public String trackedRequestsPage() {
        return "tracked-requests-page";
    }
}