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

package me.alexand.scat.statistic.api.service;

import me.alexand.scat.statistic.api.utils.exceptions.ResourceNotFoundException;
import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.repository.ClickCountRepository;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервисный класс для сущностей ClickCount
 *
 * @author asidorov84@gmail.com
 */
@Service
public class ClickCountService {
    private final ClickCountRepository repository;

    public ClickCountService(ClickCountRepository repository) {
        this.repository = repository;
    }

    public List<ClickCount> getAll(LocalDate start, LocalDate end, SortingAndPagination sortingAndPagination) {
        return repository.findBetween(start, end, sortingAndPagination);
    }

    public ClickCount getOne(LocalDate date) {
        ClickCount result = repository.findByDate(date);
        if (result == null) throw new ResourceNotFoundException();
        return result;
    }
}
