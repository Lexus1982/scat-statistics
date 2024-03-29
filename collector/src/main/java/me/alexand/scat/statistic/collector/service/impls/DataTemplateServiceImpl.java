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

package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.model.IPFIXFieldSpecifier;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.model.SCATDataTemplate;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.repository.SCATDataTemplateRepository;
import me.alexand.scat.statistic.collector.service.DataTemplateService;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownTemplateTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static me.alexand.scat.statistic.collector.utils.SCATDataTemplateEntities.DATA_TEMPLATE_LIST;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class DataTemplateServiceImpl implements DataTemplateService {
    private SCATDataTemplateRepository SCATDataTemplateRepository;
    private InfoModelRepository infoModelRepository;

    @Autowired
    public DataTemplateServiceImpl(SCATDataTemplateRepository SCATDataTemplateRepository,
                                   InfoModelRepository infoModelRepository) {
        this.SCATDataTemplateRepository = SCATDataTemplateRepository;
        this.infoModelRepository = infoModelRepository;
    }

    @Override
    public void load() {
        DATA_TEMPLATE_LIST.forEach(dataTemplate -> {
            SCATDataTemplateRepository.save(dataTemplate);
            dataTemplate.getSpecifiers().forEach(infoModelRepository::save);
        });
    }

    @Override
    public TemplateType getTypeByIPFIXSpecifiers(List<IPFIXFieldSpecifier> fieldSpecifiers)
            throws UnknownInfoModelException, UnknownTemplateTypeException {
        List<InfoModelEntity> infoModelEntities;

        try {
            infoModelEntities = fieldSpecifiers.stream()
                    .map(specifier -> {
                        InfoModelEntity entity = infoModelRepository.getByEnterpriseNumberAndInformationElementIdentifier(
                                specifier.getEnterpriseNumber(),
                                specifier.getInformationElementIdentifier()
                        );

                        if (entity == null) {
                            throw new RuntimeException();
                        }

                        return entity;
                    })
                    .collect(toList());
        } catch (RuntimeException e) {
            throw new UnknownInfoModelException(e.getMessage());
        }

        return SCATDataTemplateRepository.getAll().stream()
                .filter(template -> Objects.equals(template.getSpecifiers(), infoModelEntities))
                .map(SCATDataTemplate::getType)
                .findFirst()
                .orElseThrow(() -> new UnknownTemplateTypeException("Unknown template type"));
    }
}
