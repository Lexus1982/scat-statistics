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

package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.common.model.DomainRegex;
import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * SQL-реализация хранилища шаблонов доменных имен
 *
 * @author asidorov84@gmail.com
 * @see DomainRegexRepository
 */
@Repository
public class DomainRegexRepositoryImpl implements DomainRegexRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRegexRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DomainRegexRepositoryImpl(@Qualifier("postgresqlJDBCTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("postgresqlTM")
    public DomainRegex add(String pattern) throws PatternSyntaxException {
        Objects.requireNonNull(pattern);
        if (pattern.isEmpty()) {
            throw new PatternSyntaxException("pattern is empty", pattern, 0);
        }
        //проверка синтаксиса регулярного выражения
        Pattern.compile(pattern);

        String query = "INSERT INTO domain_regex AS td (pattern, date_added) VALUES (?, ?)" +
                " ON CONFLICT (pattern) DO NOTHING ";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            LocalDateTime dateAdded = LocalDateTime.now();
            int rowsCount = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(query, new String[]{"id"});
                ps.setString(1, pattern.trim());
                ps.setTimestamp(2, Timestamp.valueOf(dateAdded));
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
            } else {
                LOGGER.info("pattern '{}' already present", pattern);
            }
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional("postgresqlTM")
    public boolean delete(long id) {
        try {
            return jdbcTemplate.update("DELETE FROM domain_regex WHERE id = ?", id) == 1;
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return false;
    }

    @Override
    @Transactional(value = "postgresqlTM", readOnly = true)
    public List<DomainRegex> getAll() {
        try {
            return jdbcTemplate.query("SELECT id, pattern, date_added FROM domain_regex",
                    (rs, rowNum) -> DomainRegex.builder()
                            .id(rs.getLong(1))
                            .pattern(rs.getString(2))
                            .dateAdded(rs.getTimestamp(3).toLocalDateTime())
                            .build());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional(value = "postgresqlTM", readOnly = true)
    public long getCount() {
        try {
            Long count = jdbcTemplate.queryForObject("SELECT count(*) AS cnt FROM domain_regex", Long.class);
            return count != null ? count : 0;
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }
}