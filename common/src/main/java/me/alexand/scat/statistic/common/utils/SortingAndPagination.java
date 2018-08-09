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

package me.alexand.scat.statistic.common.utils;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static me.alexand.scat.statistic.common.utils.ColumnOrder.ASC;

/**
 * Параметры сортировки и пейджинга.
 * <p>
 * Класс инкапсулирует такие параметры SQL-запроса как LIMIT, OFFSET и ORDER BY.
 * Для получения экземпляра класса необходимо использовать вложенный статический класс Builder.
 * Параметры LIMIT и OFFSET должны быть неотрицательными. Иначе исключение IllegalArgumentException.
 * Если LIMIT = 0, это равносильно отсутствию данного параметра. Параметры сортировки задаются путем
 * передачи имени столбца (корректность имени не проверяется), по которому необходимо произвести сортировку,
 * а также направления - по возрастанию (ASC) или по убыванию (DESC). Параметры сортировки необязательны,
 * но в этом случае результаты SQL-выборки недетерминированы.
 *
 * @author asidorov84@gmail.com
 * @see ColumnOrder
 */
public final class SortingAndPagination {
    private final long offset;
    private final long limit;
    private final List<Pair<String, ColumnOrder>> orderingColumns;

    private SortingAndPagination(SortingAndPagination.Builder builder) {
        this.offset = builder.offset;
        this.limit = builder.limit;
        this.orderingColumns = builder.orderingColumns;
    }

    public static SortingAndPagination.Builder builder() {
        return new SortingAndPagination.Builder();
    }


    /**
     * Метод для формирования суффикса SQL-запроса на основе значений полей класса
     *
     * @return суффикс SQL-запроса, например: 'ORDER BY column1 DESC, column2 ASC, column3 ASC OFFSET 0 LIMIT 10'
     */
    public String formSQLSuffix() {
        StringBuilder sb = new StringBuilder();

        if (orderingColumns.size() > 0) {
            sb.append("ORDER BY ").append(orderingColumns.stream()
                    .map(oc -> String.format("%s %s", oc.getKey().toLowerCase(), oc.getValue()))
                    .collect(Collectors.joining(", ")));
        }

        sb.append(" OFFSET ").append(offset);
        sb.append(" LIMIT ").append(limit == 0 ? "ALL" : limit);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SortingAndPagination that = (SortingAndPagination) o;
        return offset == that.offset &&
                limit == that.limit &&
                Objects.equals(orderingColumns, that.orderingColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, limit, orderingColumns);
    }

    @Override
    public String toString() {
        return "SortingAndPagination{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", orderingColumns=" + orderingColumns +
                '}';
    }

    public final static class Builder {
        private long offset = 0;
        private long limit = 0;
        private List<Pair<String, ColumnOrder>> orderingColumns = new ArrayList<>();

        private Builder() {
        }

        public Builder offset(long offset) {
            if (offset < 0) {
                throw new IllegalArgumentException("offset must have non-negative value");
            }
            this.offset = offset;
            return this;
        }

        public Builder limit(long limit) {
            if (limit < 0) {
                throw new IllegalArgumentException("limit must have non-negative value");
            }
            this.limit = limit;
            return this;
        }

        public Builder orderingColumn(String columnName, ColumnOrder order) {
            Objects.requireNonNull(columnName);
            Objects.requireNonNull(order);
            orderingColumns.add(new Pair<>(columnName, order));
            return this;
        }

        public Builder orderingColumn(String columnName) {
            Objects.requireNonNull(columnName);
            orderingColumns.add(new Pair<>(columnName, ASC));
            return this;
        }

        public SortingAndPagination build() {
            return new SortingAndPagination(this);
        }
    }
}