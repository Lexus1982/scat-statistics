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

import me.alexand.scat.statistic.common.model.TrackedDomainRequests;
import me.alexand.scat.statistic.common.repository.TrackedDomainRequestsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author asidorov84@gmail.com
 */
@Repository
public class TrackedDomainRequestsRepositoryImpl implements TrackedDomainRequestsRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackedDomainRequestsRepositoryImpl.class);

    private static final String INSERT_QUERY = "INSERT INTO tracked_domain_requests AS tdr (date, pattern, address, login, first_time, last_time, count) " +
            " VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (date, pattern, address, login) DO UPDATE SET " +
            "last_time = EXCLUDED.last_time, " +
            "count = tdr.count + EXCLUDED.count";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TrackedDomainRequestsRepositoryImpl(@Qualifier("postgresqlJDBCTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("postgresqlTM")
    public int saveAll(List<TrackedDomainRequests> entities) {
        Objects.requireNonNull(entities);
        
        try {
            int[] rows = jdbcTemplate.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, entities.get(i).getDate());
                    ps.setString(2, entities.get(i).getPattern());
                    ps.setString(3, entities.get(i).getAddress());
                    ps.setString(4, entities.get(i).getLogin());
                    ps.setObject(5, entities.get(i).getFirstTime());
                    ps.setObject(6, entities.get(i).getLastTime());
                    ps.setBigDecimal(7, new BigDecimal(entities.get(i).getCount()));
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
    @Transactional("postgresqlTM")
    public int save(TrackedDomainRequests entity) {
        try {
            return jdbcTemplate.update(INSERT_QUERY, ps -> {
                ps.setObject(1, entity.getDate());
                ps.setString(2, entity.getPattern());
                ps.setString(3, entity.getAddress());
                ps.setString(4, entity.getLogin());
                ps.setObject(5, entity.getFirstTime());
                ps.setObject(6, entity.getLastTime());
                ps.setBigDecimal(7, new BigDecimal(entity.getCount()));
            });
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }
}
