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

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.common.model.ClickCount;
import me.alexand.scat.statistic.common.model.TrackedDomainRequests;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Промежуточный буфер для хранения всех IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */
public interface TransitionalBufferRepository {
    /**
     * Получить количество всех IPFIX-записей указанного типа
     *
     * @param type тип записей (обязательный)
     * @return количество записей
     */
    long getCount(TemplateType type);

    /**
     * Метод для получения количества IPFIX-записей типа CS_REQ за указанный период времени
     *
     * @param start начальная отметка времени (>=) (обязательный)
     * @param end   конечная отметка времени (<) (обязательный)
     * @return список экземпляров класса ClickCount, каждый из которых фактически содержит количество веб-запросов,
     * совершенных абонентами за данную дату
     */
    List<ClickCount> getClickCount(LocalDateTime start, LocalDateTime end);

    /**
     * Сохранить IPFIX-запись
     *
     * @param record IPFIX-запись (обязательный)
     * @return true, если сохранение прошло успешно, иначе false
     */
    boolean save(IPFIXDataRecord record);

    /**
     * Удалить записи указанного типа старее указанной отметки времени, включительно
     *
     * @param type            тип записей (обязательный)
     * @param beforeEventTime отметка времени (обязательный)
     * @return суммарное количество удаленных записей
     */
    long delete(TemplateType type, LocalDateTime beforeEventTime);

    /**
     * Получить агрегированные данные об указанных посещенных доменах за указанный период
     *
     * @param domainPatterns список строк, где каждая строка является регулярным выражением (обязательный)
     * @param start          начальная отметка времени (>=) (обязательный)
     * @param end            конечная отметка времени (<) (обязательный)
     * @return список результатов об отслеженных доменах
     */
    List<TrackedDomainRequests> getTrackedDomainRequests(List<String> domainPatterns, LocalDateTime start, LocalDateTime end);

    /**
     * Получить минимальную отметку времени у записей данного типа, находящихся в буфере
     *
     * @param type тип записей (обязательный)
     * @return отметка времени или null, если в буфере нет ни одной записи данного типа
     */
    LocalDateTime getMinEventTime(TemplateType type);

    /**
     * Получить максимальную отметку времени у записей данного типа, находящихся в буфере
     *
     * @param type тип записей (обязательный)
     * @return отметка времени или null, если в буфере нет ни одной записи данного типа
     */
    LocalDateTime getMaxEventTime(TemplateType type);

    /**
     * Сохранить список IPFIX-записей в виде одного BATCH INSERT
     *
     * @param records список IPFIX-записей
     * @return количество успешно сохраненных IPFIX-записей
     */
    int save(TemplateType type, List<IPFIXDataRecord> records);
}