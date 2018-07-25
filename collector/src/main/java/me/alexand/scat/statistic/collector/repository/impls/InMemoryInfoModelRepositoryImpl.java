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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import org.springframework.stereotype.Repository;

/**
 * Реализация хранилища InfoModelEntity на основе {@code HashBasedTable}
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class InMemoryInfoModelRepositoryImpl implements InfoModelRepository {
    private final Table<Long, Integer, InfoModelEntity> repository = HashBasedTable.create();

    @Override
    public void save(InfoModelEntity entity) {
        if (entity == null) {
            throw new NullPointerException();
        }
        repository.put(entity.getEnterpriseNumber(), entity.getInformationElementId(), entity);
    }

    @Override
    public InfoModelEntity getByEnterpriseNumberAndInformationElementIdentifier(
            long enterpriseNumber,
            int informationElementId) {
        return repository.get(enterpriseNumber, informationElementId);
    }
}
