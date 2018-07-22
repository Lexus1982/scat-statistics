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

import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Хранилище InfoModelEntity
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class InMemoryInfoModelRepositoryImpl implements InfoModelRepository {
    private final Map<Integer, InfoModelEntity> repository = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public InfoModelEntity save(InfoModelEntity entity) {
        Objects.requireNonNull(entity);

        if (entity.getId() == 0) {
            entity.setId(counter.getAndIncrement());
        }

        repository.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public boolean delete(int id) {
        return repository.remove(id) != null;
    }

    @Override
    public List<InfoModelEntity> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public InfoModelEntity getById(int id) {
        return repository.get(id);
    }

    @Override
    public InfoModelEntity getByEnterpriseNumberAndInformationElementIdentifier(
            long enterpriseNumber,
            int informationElementId) throws UnknownInfoModelException {
        return repository.values().stream()
                .filter(entity -> entity.getEnterpriseNumber() == enterpriseNumber &&
                        entity.getInformationElementId() == informationElementId)
                .findFirst()
                .orElseThrow(() -> new UnknownInfoModelException(
                        String.format("unknown info model with enterprise number = %d and identifier = %d",
                                enterpriseNumber,
                                informationElementId)));
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
