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

import me.alexand.scat.statistic.common.entities.CollectorStatRecord;
import me.alexand.scat.statistic.common.repository.CollectorStatRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author asidorov84@gmail.com
 */
@Repository
public class CollectorStatRecordRepositoryImpl implements CollectorStatRecordRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorStatRecordRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public CollectorStatRecordRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public boolean save(CollectorStatRecord record) {
        Objects.requireNonNull(record);

        try {
            boolean result = jdbcTemplate.update(con -> {
                String query = "INSERT INTO log.collectors_history (uuid, address, port, started, last_update, period, " +
                        " processors_threads_count, packets_received_count, packets_processed_count, " +
                        " packets_parse_failed_count, input_queue_overflow_count, output_queue_overflow_count, " +
                        " records_exported_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        " ON CONFLICT (uuid) DO UPDATE SET " +
                        "   last_update = EXCLUDED.last_update, " +
                        "   period = EXCLUDED.period, " +
                        "   processors_threads_count = EXCLUDED.processors_threads_count, " +
                        "   packets_received_count = EXCLUDED.packets_received_count, " +
                        "   packets_processed_count = EXCLUDED.packets_processed_count, " +
                        "   packets_parse_failed_count = EXCLUDED.packets_parse_failed_count, " +
                        "   input_queue_overflow_count = EXCLUDED.input_queue_overflow_count, " +
                        "   output_queue_overflow_count = EXCLUDED.output_queue_overflow_count, " +
                        "   records_exported_count = EXCLUDED.records_exported_count ";

                LOGGER.debug("executing query: [{}]", query);

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, record.getUuid().toString());
                ps.setString(2, record.getAddress());
                ps.setInt(3, record.getPort());
                ps.setTimestamp(4, Timestamp.valueOf(record.getStarted()));
                ps.setTimestamp(5, Timestamp.valueOf(record.getLastUpdated()));
                ps.setInt(6, record.getPeriod());
                ps.setInt(7, record.getProcessorsThreadsCount());
                ps.setInt(8, record.getPacketsReceivedCount());
                ps.setInt(9, record.getPacketsProcessedCount());
                ps.setInt(10, record.getPacketsParseFailedCount());
                ps.setInt(11, record.getInputQueueOverflowCount());
                ps.setInt(12, record.getOutputQueueOverflowCount());
                ps.setInt(13, record.getRecordsExportedCount());
                return ps;
            }) == 1;

            if (result) {
                LOGGER.debug("{} saved successfully", record);
            }

            return result;
        } catch (DataAccessException e) {
            LOGGER.error("exception while inserting collectors stat record: {}", e.getMessage());
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectorStatRecord> findAll() {
        String query = "SELECT * FROM log.collectors_history";
        List<CollectorStatRecord> result = null;

        try {
            LOGGER.debug("executing query: [{}]", query);

            result = jdbcTemplate.query(query,
                    (rs, rowNum) -> CollectorStatRecord.builder()
                            .uuid(UUID.fromString(rs.getString("uuid")))
                            .address(rs.getString("address"))
                            .port(rs.getInt("port"))
                            .started(rs.getTimestamp("started").toLocalDateTime())
                            .lastUpdated(rs.getTimestamp("last_update").toLocalDateTime())
                            .period(rs.getInt("period"))
                            .processorsThreadsCount(rs.getInt("processors_threads_count"))
                            .packetsReceivedCount(rs.getInt("packets_received_count"))
                            .packetsProcessedCount(rs.getInt("packets_processed_count"))
                            .packetsParseFailedCount(rs.getInt("packets_parse_failed_count"))
                            .inputQueueOverflowCount(rs.getInt("input_queue_overflow_count"))
                            .outputQueueOverflowCount(rs.getInt("output_queue_overflow_count"))
                            .recordsExportedCount(rs.getInt("records_exported_count"))
                            .build());

            LOGGER.debug("entities found: {}", result.size());
        } catch (DataAccessException e) {
            LOGGER.error("exception while getting collectors stat records: {}", e.getMessage());
        }

        return result != null ? result : new ArrayList<>();
    }
}
