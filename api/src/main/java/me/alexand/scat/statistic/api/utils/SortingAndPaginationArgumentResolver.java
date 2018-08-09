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

package me.alexand.scat.statistic.api.utils;

import me.alexand.scat.statistic.common.utils.ColumnOrder;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;

/**
 * Служебный класс для маппинга http-параметров сортировки и пейджинга искомых ресурсов.
 * Данный класс распознает следующие http-параметры:
 * <p>
 * page - номер страницы (>=1),
 * size - размер страницы (>=0),
 * order - порядок сортировки: название_колонки[,{asc,desc}]
 *
 * @author asidorov84@gmail.com
 */
public class SortingAndPaginationArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(SortingAndPaginationArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SPRequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws IllegalArgumentException {
        String page = webRequest.getParameter("page");
        String size = webRequest.getParameter("size");
        String[] orders = webRequest.getParameterValues("order");

        LOGGER.debug("page = {}", page);
        LOGGER.debug("size = {}", size);
        LOGGER.debug("orders = {}", Arrays.toString(orders));

        long lSize;
        long lPage;

        try {
            lPage = page != null ? Long.valueOf(page) : 1;
            if (lPage < 1) {
                throw new IllegalArgumentException("page parameter must be greater than 1");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("page parameter must have a numeric value");
        }

        try {
            lSize = size != null ? Long.valueOf(size) : 0;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("size parameter must have a numeric value");
        }

        SortingAndPagination.Builder resultBuilder = SortingAndPagination.builder()
                .offset(lSize * (lPage - 1))
                .limit(lSize);

        if (orders != null) {
            for (String next : orders) {
                //Правильный формат (нечувствительный к регистру):
                // название_колонки (любые английские буквы, цифры или знак подчеркивания),
                // затем опционально через запятую порядок: asc или desc
                if (!next.matches("(?i:^[\\w]+([,](asc|desc))?$)")) {
                    throw new IllegalArgumentException(String.format("illegal format of order parameter: %s", next));
                }
                String[] pair = next.split(",");
                if (pair.length == 2) {
                    resultBuilder.orderingColumn(pair[0], ColumnOrder.valueOf(pair[1].toUpperCase()));
                } else {
                    resultBuilder.orderingColumn(pair[0]);
                }
            }
        }

        return resultBuilder.build();
    }
}
