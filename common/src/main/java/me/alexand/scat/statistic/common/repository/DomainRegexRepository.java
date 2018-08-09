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

import me.alexand.scat.statistic.common.entities.DomainRegex;
import me.alexand.scat.statistic.common.utils.exceptions.DomainRegexAlreadyExistsException;

import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Интерфейс для работы с хранилищем шаблонов доменных имен
 *
 * @author asidorov84@gmail.com
 * @see DomainRegex
 */
public interface DomainRegexRepository {
    /**
     * Метод для добавления шаблона доменного имени.<br>
     * В качестве параметра ожидается корректное регулярное выражение.
     * Перед добавлением у параметра удаляются все начальные и конечные пробельные символы
     *
     * @param pattern корректное регулярное выражение
     * @return экземпляр класса DomainRegex
     * @throws PatternSyntaxException            если регулярное выражение содержит синтаксическую ошибку
     * @throws NullPointerException              если параметр null
     * @throws DomainRegexAlreadyExistsException если такой шаблон уже присутствует
     * @see java.util.regex.Pattern
     */
    DomainRegex add(String pattern);

    /**
     * Метод для удаления шаблона из хранилища
     *
     * @param id идентификатор шаблона
     * @return true, если шаблон был удален успешно, иначе false
     */
    boolean delete(long id);

    /**
     * Метод для получения списка всех шаблонов доменных имен
     *
     * @return список шаблонов
     */
    List<DomainRegex> getAll();

    /**
     * Метод для получения количества шаблонов доменных имен
     *
     * @return количество шаблонов
     */
    long getCount();

    //TODO добавить метод get c пейджингом
}
