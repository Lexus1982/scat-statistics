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
import me.alexand.scat.statistic.collector.model.ImportDataTemplate;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.repository.ImportDataTemplateRepository;
import me.alexand.scat.statistic.collector.utils.SCATDataTemplateEntities;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownTemplateTypeException;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author asidorov84@gmail.com
 */
@Repository
public class ImportDataTemplateRepositoryImpl implements ImportDataTemplateRepository {
    private final Map<List<InfoModelEntity>, ImportDataTemplate> dataTemplateMap = new HashMap<>();
    private final Table<Long, Integer, InfoModelEntity> infoModelEntityTable = HashBasedTable.create();

    {
        load();
    }

    @Override
    public Collection<ImportDataTemplate> findAll() {
        return dataTemplateMap.values();
    }

    @Override
    public int getCount() {
        return dataTemplateMap.size();
    }

    @Override
    public InfoModelEntity getInfoModel(long enterpriseNumber, int informationElementId) throws UnknownInfoModelException {
        InfoModelEntity infoModelEntity = infoModelEntityTable.get(enterpriseNumber, informationElementId);
        if (infoModelEntity == null) {
            String message = String.format("unknown info model for enterprise number = %d and id = %d", enterpriseNumber, informationElementId);
            throw new UnknownInfoModelException(message);
        }
        return infoModelEntity;
    }

    @Override
    public ImportDataTemplate findByInfoModelEntities(List<InfoModelEntity> infoModelEntities) throws UnknownTemplateTypeException {
        ImportDataTemplate dataTemplate = dataTemplateMap.get(infoModelEntities);
        if (dataTemplate == null) {
            throw new UnknownTemplateTypeException("unknown template detected");
        }
        return dataTemplate;
    }

    private void load() {
        //TODO сделать загрузку из XML-файла 
        SCATDataTemplateEntities.DATA_TEMPLATE_LIST.forEach(template -> {
            template.getSpecifiers().forEach(infoModelEntity -> {
                infoModelEntityTable.put(infoModelEntity.getEnterpriseNumber(),
                        infoModelEntity.getInformationElementId(),
                        infoModelEntity);
            });
            
            dataTemplateMap.put(template.getSpecifiers(), template);
        });
    }
}
