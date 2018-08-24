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
import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import me.alexand.scat.statistic.common.repository.TrackedDomainRequestsRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.sql.Types.BIGINT;

/**
 * Реализация хранилища сущностей TrackedDomainRequests на основе JDBC
 *
 * @author asidorov84@gmail.com
 * @see TrackedDomainRequests
 * @see TrackedDomainRequestsRepository
 */
public class TrackedDomainRequestsRepositoryImpl implements TrackedDomainRequestsRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackedDomainRequestsRepositoryImpl.class);

    private static final String INSERT_QUERY = "INSERT INTO tracked_domain_requests AS tdr (date, domain_id, address, login, first_time, last_time, count) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (date, domain_id, address, login) DO UPDATE SET " +
            "last_time = EXCLUDED.last_time, " +
            "count = tdr.count + EXCLUDED.count";

    private final JdbcTemplate jdbcTemplate;

    public TrackedDomainRequestsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("persistenceTM")
    public int saveAll(List<TrackedDomainRequests> entities) {
        Objects.requireNonNull(entities);

        try {
            int[] rows = jdbcTemplate.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, entities.get(i).getDate());
                    ps.setObject(2, entities.get(i).getDomainRegex().getId());
                    ps.setString(3, entities.get(i).getAddress());
                    ps.setString(4, entities.get(i).getLogin());
                    ps.setObject(5, entities.get(i).getFirstTime());
                    ps.setObject(6, entities.get(i).getLastTime());
                    ps.setObject(7, entities.get(i).getCount(), BIGINT);
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
    @Transactional("persistenceTM")
    public int save(TrackedDomainRequests entity) {
        try {
            return jdbcTemplate.update(INSERT_QUERY, ps -> {
                ps.setObject(1, entity.getDate());
                ps.setObject(2, entity.getDomainRegex().getId());
                ps.setString(3, entity.getAddress());
                ps.setString(4, entity.getLogin());
                ps.setObject(5, entity.getFirstTime());
                ps.setObject(6, entity.getLastTime());
                ps.setObject(7, entity.getCount(), BIGINT);
            });
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<TrackedDomainRequests> findBetween(LocalDate from,
                                                   LocalDate to,
                                                   Map<String, String> filters,
                                                   SortingAndPagination sortingAndPagination) {
        String suffix = sortingAndPagination != null ? sortingAndPagination.formSQLSuffix() : "";
        String sqlFilters = "";

        if (from != null && to != null) {
            if (from.equals(to)) {
                sqlFilters = String.format(" WHERE date = '%s' ", from.format(DateTimeFormatter.ISO_DATE));
            } else {
                sqlFilters = String.format(" WHERE date BETWEEN '%s' AND '%s' ",
                        from.format(DateTimeFormatter.ISO_DATE),
                        to.format(DateTimeFormatter.ISO_DATE));
            }
        }

        if (from != null && to == null) {
            sqlFilters = String.format(" WHERE date >= '%s' ", from.format(DateTimeFormatter.ISO_DATE));
        }

        if (from == null && to != null) {
            sqlFilters = String.format(" WHERE date <= '%s' ", to.format(DateTimeFormatter.ISO_DATE));
        }

        String query = String.format("SELECT " +
                "  tdr.date, " +
                "  dr.id, " +
                "  dr.pattern, " +
                "  dr.date_added, " +
                "  dr.is_active, " +
                "  tdr.address, " +
                "  tdr.login, " +
                "  tdr.first_time, " +
                "  tdr.last_time, " +
                "  tdr.count " +
                "FROM tracked_domain_requests tdr INNER JOIN domain_regex dr ON tdr.domain_id = dr.id %s %s", sqlFilters, suffix);

        LOGGER.debug("executing query: [{}]", query);

        return jdbcTemplate.query(query,
                (rs, rowNum) -> TrackedDomainRequests.builder()
                        .date(rs.getTimestamp(1).toLocalDateTime().toLocalDate())
                        .domainRegex(DomainRegex.builder()
                                .id(rs.getLong(2))
                                .pattern(rs.getString(3))
                                .dateAdded(rs.getTimestamp(4).toLocalDateTime())
                                .active(rs.getBoolean(5))
                                .build())
                        .address(rs.getString(6))
                        .login(rs.getString(7))
                        .firstTime(rs.getTime(8).toLocalTime())
                        .lastTime(rs.getTime(9).toLocalTime())
                        .count(rs.getBigDecimal(10).toBigInteger())
                        .build());
    }
}
