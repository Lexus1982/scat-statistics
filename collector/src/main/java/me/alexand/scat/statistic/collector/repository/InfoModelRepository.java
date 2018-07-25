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

package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.InfoModelEntity;

/**
 * Хранилище InfoModelEntity
 *
 * @author asidorov84@gmail.com
 */
public interface InfoModelRepository {

    /**
     * Метод для сохранения информационного элемента {@code InfoModelEntity} в хранилище
     *
     * @param entity информационный элемент (обязательный параметр)
     * @throws NullPointerException если {@code InfoModelEntity} равен {@code null}
     */
    void save(InfoModelEntity entity);

    /**
     * Метод для получения информационного элемента по идентификатору организации и идентификатору элемента
     * внутри данной организации
     *
     * @param enterpriseNumber     уникальный идентификатор организации, согласно IANA (обязательный параметр)
     * @param informationElementId уникальный идентификатор элемента внутри организации (обязательный параметр)
     * @return информационный элемент или {@code null}, если для данных параметров элемент не существует
     */
    InfoModelEntity getByEnterpriseNumberAndInformationElementIdentifier(
            long enterpriseNumber,
            int informationElementId);
}
