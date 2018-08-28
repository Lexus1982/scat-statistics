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

package me.alexand.scat.statistic.common.repository;

import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для работы с хранилищем сущностей TrackedDomainRequests
 *
 * @author asidorov84@gmail.com
 * @see TrackedDomainRequests
 */
public interface TrackedDomainRequestsRepository {
    //TODO добавить javadoc
    int saveAll(List<TrackedDomainRequests> results);

    //TODO добавить javadoc
    int save(TrackedDomainRequests result);

    /**
     * Метод для получения результатов трекинга доменов.
     *
     * @param from начальная дата (обязательный)
     * @param to   конечная дата (обязательный)
     * @return список экземпляров класса TrackedDomainRequests
     */
    List<TrackedDomainRequests> findBetween(LocalDate from,
                                            LocalDate to);

    /**
     * Метод для получения результатов трекинга доменов.
     * Фильтры представляют собой список пар вида: имя_фильтруемого_поля -> значение
     * Возможные варианты фильтров:
     * <ul>
     * <li>domain_id равен значению</li>
     * <li>address like значение</li>
     * <li>login like значение</li>
     * </ul>
     *
     * @param from    начальная дата (обязательный)
     * @param to      конечная дата (обязательный)
     * @param filters фильтры
     * @return список экземпляров класса TrackedDomainRequests
     */
    List<TrackedDomainRequests> findBetween(LocalDate from,
                                            LocalDate to,
                                            Map<String, String> filters);

    /**
     * Метод для получения результатов трекинга доменов.
     *
     * @param from                 начальная дата (обязательный)
     * @param to                   конечная дата (обязательный)
     * @param sortingAndPagination параметры сортировки и пейджинга
     * @return список экземпляров класса TrackedDomainRequests
     */
    List<TrackedDomainRequests> findBetween(LocalDate from,
                                            LocalDate to,
                                            SortingAndPagination sortingAndPagination);

    /**
     * Метод для получения результатов трекинга доменов.<br>
     * Фильтры представляют собой список пар вида: имя_фильтруемого_поля -> значение
     * Возможные варианты фильтров:
     * <ul>
     * <li>domain_id равен значению</li>
     * <li>address like значение</li>
     * <li>login like значение</li>
     * </ul>
     *
     * @param from                 начальная дата (обязательный)
     * @param to                   конечная дата (обязательный)
     * @param filters              фильтры
     * @param sortingAndPagination параметры сортировки и пейджинга
     * @return список экземпляров класса TrackedDomainRequests
     */
    List<TrackedDomainRequests> findBetween(LocalDate from,
                                            LocalDate to,
                                            Map<String, String> filters,
                                            SortingAndPagination sortingAndPagination);
}