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

package me.alexand.scat.statistic.collector.entities;

import me.alexand.scat.statistic.common.model.TrackedResult;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author asidorov84@gmail.com
 */
public interface TrackedResultsTestEntities {
    TrackedResult TEST = TrackedResult.builder()
            .regexPattern(".*vk\\.com$")
            .address("176.221.0.224")
            .login("polyakov_al@setka.ru")
            .firstTime(LocalDateTime.of(2018, 4, 1, 17, 6, 17))
            .lastTime(LocalDateTime.of(2018, 4, 1, 17, 6, 19))
            .count(BigInteger.valueOf(3))
            .build();
}
