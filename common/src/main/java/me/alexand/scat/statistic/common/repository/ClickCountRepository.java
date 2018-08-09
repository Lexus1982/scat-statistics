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

import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для работы с хранилищем сущностей ClickCount
 *
 * @author asidorov84@gmail.com
 * @see ClickCount
 */
public interface ClickCountRepository {

    /**
     * Метод для сохранения общего количества веб-запросов в базе данных.
     * Если данные для указанного дня уже имеются, то производится суммирование старого значения с новым.
     * Иначе создается новая запись на указанный день с соответствующим количеством запросов.
     *
     * @param entities список сущностей типа ClickCount (обязательный)
     * @return количество успешно обновленных записей
     */
    int saveAll(List<ClickCount> entities);

    /**
     * Метод для получения всех ClickCount из базы данных.
     * Список отсортирован по дате по убыванию.
     *
     * @return список ClickCount
     */
    List<ClickCount> findAll();

    /**
     * Метод для получения ClickCount из базы данных c параметрами сортировки и пейджинга.
     *
     * @param sortingAndPagination параметры сортировки и пейджинга (необязательный)
     * @return список ClickCount
     */
    List<ClickCount> findAll(SortingAndPagination sortingAndPagination);

    /**
     * Метод для получения ClickCount из базы данных за указанный период
     *
     * @param start начальная дата (необязательный, включительно)
     * @param end   конечная дата (необязательный, включительно)
     * @return список ClickCount
     */
    List<ClickCount> findBetween(LocalDate start, LocalDate end);

    /**
     * Метод для получения ClickCount из базы данных за указанный период с параметрами сортировки и пейджинга
     *
     * @param start                начальная дата (необязательный, включительно)
     * @param end                  конечная дата (необязательный, включительно)
     * @param sortingAndPagination параметры сортировки и пейджинга (необязательный)
     * @return список ClickCount
     */
    List<ClickCount> findBetween(LocalDate start, LocalDate end, SortingAndPagination sortingAndPagination);

    /**
     * Метод для получения ClickCount за указанную дату
     *
     * @param date дата (обязательный)
     * @return экземпляр класса ClickCount, либо null - если за указанную дату нет данных
     */
    ClickCount findByDate(LocalDate date);
}
