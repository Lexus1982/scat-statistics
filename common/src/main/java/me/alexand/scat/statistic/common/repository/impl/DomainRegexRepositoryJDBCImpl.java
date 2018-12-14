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

import me.alexand.scat.statistic.common.entities.DomainRegex;
import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import me.alexand.scat.statistic.common.utils.SortingAndPagination;
import me.alexand.scat.statistic.common.utils.exceptions.DomainPatternAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static me.alexand.scat.statistic.common.utils.ColumnOrder.DESC;

/**
 * Реализация хранилища шаблонов доменных имен на основе JDBC
 *
 * @author asidorov84@gmail.com
 * @see DomainRegex
 * @see DomainRegexRepository
 */
public class DomainRegexRepositoryJDBCImpl implements DomainRegexRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRegexRepositoryJDBCImpl.class);
    private static final SortingAndPagination DEFAULT_SORTING_PARAM = SortingAndPagination.builder()
            .orderingColumn("date_added", DESC)
            .build();

    private final JdbcTemplate jdbcTemplate;

    public DomainRegexRepositoryJDBCImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public DomainRegex add(String pattern) {
        Objects.requireNonNull(pattern);
        if (pattern.isEmpty()) {
            throw new PatternSyntaxException("pattern is empty", pattern, 0);
        }
        //проверка синтаксиса регулярного выражения
        Pattern.compile(pattern);

        String query = "INSERT INTO reports.domain_regex AS td (pattern, date_added, is_active) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            LocalDateTime dateAdded = LocalDateTime.now();

            LOGGER.debug("executing update query: [{}]", query);

            int rowsCount = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
                ps.setString(1, pattern.trim());
                ps.setTimestamp(2, Timestamp.valueOf(dateAdded));
                ps.setBoolean(3, true);
                return ps;
            }, keyHolder);

            if (rowsCount == 1) {
                DomainRegex result = DomainRegex.builder()
                        .id(Objects.requireNonNull(keyHolder.getKey()).longValue())
                        .pattern(pattern)
                        .dateAdded(dateAdded)
                        .build();

                LOGGER.info("{} saved successfully", result);
                return result;
            }
        } catch (DuplicateKeyException e) {
            LOGGER.info("pattern '{}' already present", pattern);
            throw new DomainPatternAlreadyExistsException(pattern);
        } catch (DataAccessException e) {
            LOGGER.error("exception while adding domain pattern: {}", e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        boolean result = false;

        try {
            String query = "DELETE FROM reports.domain_regex WHERE id = ?";
            LOGGER.debug("executing delete query: [{}]", query);
            result = jdbcTemplate.update(query, id) == 1;
            LOGGER.debug("pattern deleted: {}", result);
        } catch (DataAccessException e) {
            LOGGER.error("exception while deleting domain pattern: {}", e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainRegex> findAll() {
        return findAll(DEFAULT_SORTING_PARAM);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainRegex> findAll(SortingAndPagination sortingAndPagination) {
        String suffix = sortingAndPagination != null ? sortingAndPagination.formSQLSuffix() : "";
        String query = String.format("SELECT id, pattern, date_added, is_active FROM reports.domain_regex %s", suffix);
        List<DomainRegex> result = null;

        try {
            LOGGER.debug("executing query: [{}]", query);
            result = jdbcTemplate.query(query,
                    (rs, rowNum) -> DomainRegex.builder()
                            .id(rs.getLong(1))
                            .pattern(rs.getString(2))
                            .dateAdded(rs.getTimestamp(3).toLocalDateTime())
                            .active(rs.getBoolean(4))
                            .build());
            LOGGER.debug("entities found: {}", result.size());
        } catch (DataAccessException e) {
            LOGGER.error("exception while finding DomainRegex: {}", e.getMessage());
        }

        return result != null ? result : new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public long getCount() {
        long result = 0;

        try {
            String query = "SELECT count(*) AS cnt FROM reports.domain_regex";
            LOGGER.debug("executing query: [{}]", query);
            Long count = jdbcTemplate.queryForObject(query, Long.class);
            result = count != null ? count : 0;
            LOGGER.debug("patterns count: {}", count);
        } catch (DataAccessException e) {
            LOGGER.error("exception while get count: {}", e.getMessage());
        }
        return result;
    }
}