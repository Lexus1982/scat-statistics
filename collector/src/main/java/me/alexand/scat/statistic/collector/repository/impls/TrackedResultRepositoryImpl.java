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

import me.alexand.scat.statistic.common.model.TrackedResult;
import me.alexand.scat.statistic.common.repository.TrackedResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
@Repository
public class TrackedResultRepositoryImpl implements TrackedResultRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackedResultRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TrackedResultRepositoryImpl(@Qualifier("postgresqlJDBCTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("postgresqlTM")
    public int saveAll(List<TrackedResult> results) {
        return results.stream()
                .mapToInt(this::save)
                .sum();
    }

    @Override
    @Transactional("postgresqlTM")
    public int save(TrackedResult entity) {
        String query = "INSERT INTO tracked_results AS tr (regex_pattern, address, login, first_time, last_time, count) " +
                " VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (regex_pattern, address, login) DO UPDATE SET " +
                "last_time = EXCLUDED.last_time, " +
                "count = tr.count + EXCLUDED.count";

        try {
            return jdbcTemplate.update(query, ps -> {
                ps.setString(1, entity.getRegexPattern());
                ps.setString(2, entity.getAddress());
                ps.setString(3, entity.getLogin());
                ps.setObject(4, entity.getFirstTime());
                ps.setObject(5, entity.getLastTime());
                ps.setBigDecimal(6, new BigDecimal(entity.getCount()));
            });
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }
}
