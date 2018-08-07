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

package me.alexand.scat.statistic.common.repository.impl;

import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.repository.ClickCountRepository;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.BIGINT;
import static me.alexand.scat.statistic.common.utils.ColumnOrder.DESC;

/**
 * Реализация хранилища сущностей ClickCount на основе JDBC
 *
 * @author asidorov84@gmail.com
 * @see ClickCount
 * @see ClickCountRepository
 */
public class ClickCountRepositoryImpl implements ClickCountRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickCountRepositoryImpl.class);
    private static final SortingAndPagination DEFAULT_SORTING_PARAM = SortingAndPagination.builder()
            .orderingColumn("date", DESC)
            .build();

    private final JdbcTemplate jdbcTemplate;

    public ClickCountRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("persistenceTM")
    public int saveAll(List<ClickCount> entities) {
        Objects.requireNonNull(entities);
        if (entities.isEmpty()) {
            return 0;
        }

        String query = "INSERT INTO click_count AS cc (date, count) " +
                " VALUES (?, ?) ON CONFLICT (date) DO UPDATE SET " +
                "count = cc.count + EXCLUDED.count";

        try {
            int[] rows = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, entities.get(i).getDate());
                    ps.setObject(2, entities.get(i).getCount(), BIGINT);
                }

                @Override
                public int getBatchSize() {
                    return entities.size();
                }
            });

            return Arrays.stream(rows).sum();
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<ClickCount> findAll() {
        return findBetween(null, null, DEFAULT_SORTING_PARAM);
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<ClickCount> findAll(SortingAndPagination sortingAndPagination) {
        return findBetween(null, null, sortingAndPagination);
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<ClickCount> findBetween(LocalDate start, LocalDate end) {
        return findBetween(start, end, DEFAULT_SORTING_PARAM);
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<ClickCount> findBetween(LocalDate start, LocalDate end, SortingAndPagination sortingAndPagination) {
        String suffix = sortingAndPagination != null ? sortingAndPagination.formSQLSuffix() : "";
        String filters = "";

        if (start != null && end != null) {
            filters = " WHERE date BETWEEN ? AND ? ";
        }

        if (start != null && end == null) {
            filters = " WHERE date >= ? ";
        }

        if (start == null && end != null) {
            filters = " WHERE date <= ? ";
        }

        try {
            return jdbcTemplate.query(String.format("SELECT date, count FROM click_count %s %s", filters, suffix),
                    ps -> {
                        int paramNumber = 1;
                        if (start != null) ps.setObject(paramNumber++, start);
                        if (end != null) ps.setObject(paramNumber, end);
                    },
                    (rs, rowNum) -> ClickCount.builder()
                            .date(rs.getTimestamp(1).toLocalDateTime().toLocalDate())
                            .count(rs.getBigDecimal(2).toBigInteger())
                            .build());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public ClickCount findByDate(LocalDate date) {
        Objects.requireNonNull(date);
        List<ClickCount> list = findBetween(date, date);
        return !list.isEmpty() ? list.get(0) : null;
    }
}
