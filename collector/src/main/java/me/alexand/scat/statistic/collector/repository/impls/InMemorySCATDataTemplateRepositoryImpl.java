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

package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.SCATDataTemplate;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.SCATDataTemplateRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author asidorov84@gmail.com
 */

@Repository
public class InMemorySCATDataTemplateRepositoryImpl implements SCATDataTemplateRepository {
    private final Map<TemplateType, SCATDataTemplate> repository = new ConcurrentHashMap<>();
    private final Collection<SCATDataTemplate> allTemplates = new ArrayList<>();

    @Override
    public Collection<SCATDataTemplate> getAll() {
        return allTemplates;
    }

    @Override
    public void save(SCATDataTemplate template) {
        Objects.requireNonNull(template);
        repository.put(template.getType(), template);
        synchronized (allTemplates) {
            allTemplates.clear();
            allTemplates.addAll(repository.values());
        }
    }
}