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
import java.util.*;

import static java.sql.Types.BIGINT;

/**
 * Реализация хранилища сущностей TrackedDomainRequests на основе JDBC
 *
 * @author asidorov84@gmail.com
 * @see TrackedDomainRequests
 * @see TrackedDomainRequestsRepository
 */
public class TrackedDomainRequestsRepositoryJDBCImpl implements TrackedDomainRequestsRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackedDomainRequestsRepositoryJDBCImpl.class);

    private static final String INSERT_QUERY = "INSERT INTO reports.tracked_domain_requests AS tdr (date, domain_id, address, login, first_time, last_time, count) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (date, domain_id, address, login) DO UPDATE SET " +
            "last_time = EXCLUDED.last_time, " +
            "count = tdr.count + EXCLUDED.count";

    private final JdbcTemplate jdbcTemplate;

    public TrackedDomainRequestsRepositoryJDBCImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("persistenceTM")
    public int saveAll(List<TrackedDomainRequests> entities) {
        Objects.requireNonNull(entities);
        int result = 0;

        try {
            LOGGER.debug("executing query: [{}]", INSERT_QUERY);

            result = Arrays.stream(jdbcTemplate.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
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
            })).sum();

            LOGGER.debug("entities saved: {}", result);
        } catch (DataAccessException e) {
            LOGGER.error("exception while saving: {}", e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional("persistenceTM")
    public boolean save(TrackedDomainRequests entity) {
        boolean result = false;

        try {
            LOGGER.debug("executing query: [{}]", INSERT_QUERY);

            result = jdbcTemplate.update(INSERT_QUERY, ps -> {
                ps.setObject(1, entity.getDate());
                ps.setObject(2, entity.getDomainRegex().getId());
                ps.setString(3, entity.getAddress());
                ps.setString(4, entity.getLogin());
                ps.setObject(5, entity.getFirstTime());
                ps.setObject(6, entity.getLastTime());
                ps.setObject(7, entity.getCount(), BIGINT);
            }) == 1;

            LOGGER.debug("entity saved: {}", result);
        } catch (DataAccessException e) {
            LOGGER.error("exception while saving: {}", e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<TrackedDomainRequests> findBetween(LocalDate from, LocalDate to) {
        return findBetween(from, to, null, null);
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<TrackedDomainRequests> findBetween(LocalDate from, LocalDate to, Map<String, String> filters) {
        return findBetween(from, to, filters, null);
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<TrackedDomainRequests> findBetween(LocalDate from, LocalDate to, SortingAndPagination sortingAndPagination) {
        return findBetween(from, to, null, sortingAndPagination);
    }

    @Override
    @Transactional(value = "persistenceTM", readOnly = true)
    public List<TrackedDomainRequests> findBetween(LocalDate from,
                                                   LocalDate to,
                                                   Map<String, String> filters,
                                                   SortingAndPagination sortingAndPagination) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        String suffix = sortingAndPagination != null ? sortingAndPagination.formSQLSuffix() : "";
        StringBuilder sqlFilters = new StringBuilder();

        if (filters != null) {
            String domainId = filters.get("domain_id");

            if (domainId != null) {
                sqlFilters.append(String.format(" AND domain_id = %s", domainId));
            }

            String address = filters.get("address");

            if (address != null) {
                sqlFilters.append(String.format(" AND address LIKE '%s'", "%" + address + "%"));
            }

            String login = filters.get("login");

            if (login != null) {
                sqlFilters.append(String.format(" AND login LIKE '%s'", "%" + login + "%"));
            }
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
                "FROM reports.tracked_domain_requests tdr INNER JOIN reports.domain_regex dr ON tdr.domain_id = dr.id " +
                "WHERE date BETWEEN ? AND ? " +
                "%s %s", sqlFilters, suffix);

        List<TrackedDomainRequests> result = null;

        try {
            LOGGER.debug("executing query: [{}]", query);

            result = jdbcTemplate.query(query,
                    ps -> {
                        ps.setObject(1, from);
                        ps.setObject(2, to);
                    },
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

            LOGGER.debug("entities found: {}", result.size());
        } catch (DataAccessException e) {
            LOGGER.error("exception while finding: {}", e.getMessage());
        }

        return result != null ? result : new ArrayList<>();
    }
}
