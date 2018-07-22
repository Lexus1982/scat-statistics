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

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.DataTemplateRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author asidorov84@gmail.com
 */

@Repository
public class InMemoryDataTemplateRepositoryImpl implements DataTemplateRepository {
    private final Map<TemplateType, DataTemplate> repository = new HashMap<>();

    @Override
    public List<DataTemplate> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public DataTemplate save(DataTemplate template) {
        Objects.requireNonNull(template);
        repository.put(template.getType(), template);
        return template;
    }

    @Override
    public DataTemplate getByType(TemplateType type) {
        Objects.requireNonNull(type);
        return repository.get(type);
    }

    @Override
    public boolean delete(TemplateType type) {
        Objects.requireNonNull(type);
        return repository.remove(type) != null;
    }

    @Override
    public long getCount() {
        return repository.size();
    }

    @Override
    public void clear() {
        repository.clear();
    }
}