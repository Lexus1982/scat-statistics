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

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXFieldValue;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.TransitionalBufferRepository;
import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.entities.DomainRegex;
import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SQL Databases реализация репозитория для IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class TransitionalBufferRepositoryImpl implements TransitionalBufferRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransitionalBufferRepositoryImpl.class);

    private static final String CS_REQ_INSERT = "INSERT INTO cs_req(event_time, login, ip_src, ip_dst, hostname," +
            " path, refer, user_agent, cookie, session_id, locked, host_type, method)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String CS_REQ_DELETE = "DELETE FROM cs_req WHERE event_time <= ?";

    private static final String CS_RESP_INSERT = "INSERT INTO cs_resp(event_time, login, ip_src, ip_dst," +
            " result_code, content_length, content_type, session_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String CS_RESP_DELETE = "DELETE FROM cs_resp WHERE event_time <= ?";

    private static final String GENERIC_INSERT = "INSERT INTO generic(octet_delta_count, packet_delta_count," +
            " protocol_identifier, ip_class_of_service, source_transport_port, source_ipv4_address," +
            " destination_transport_port, destination_ipv4_address, bgp_source_as_number, bgp_destination_as_number," +
            " flow_start_millisecond, flow_end_millisecond, input_snmp, output_snmp, ip_version, session_id," +
            " http_host, dpi_protocol, login, post_nat_source_ipv4_address, post_nat_source_transport_port) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String GENERIC_DELETE = "DELETE FROM generic WHERE flow_end_millisecond <= ?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public TransitionalBufferRepositoryImpl(@Qualifier("bufferJDBCTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional("bufferTM")
    public boolean save(IPFIXDataRecord record) {
        Objects.requireNonNull(record);
        int insertedRecords = 0;

        try {
            switch (record.getType()) {
                case CS_REQ:
                    insertedRecords = insertRecord(CS_REQ_INSERT, record);
                    break;
                case CS_RESP:
                    insertedRecords = insertRecord(CS_RESP_INSERT, record);
                    break;
                case GENERIC:
                    insertedRecords = insertRecord(GENERIC_INSERT, record);
                    break;
            }
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return insertedRecords == 1;
    }

    @Override
    @Transactional("bufferTM")
    public int save(TemplateType type, List<IPFIXDataRecord> records) {
        String sql = null;

        switch (type) {
            case CS_REQ:
                sql = CS_REQ_INSERT;
                break;
            case CS_RESP:
                sql = CS_RESP_INSERT;
                break;
            case GENERIC:
                sql = GENERIC_INSERT;
                break;
        }

        try {
            int[] rows = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    List<IPFIXFieldValue> fieldValues = records.get(i).getFieldValues();

                    for (int j = 0; j < fieldValues.size(); j++) {
                        int sqlType = fieldValues.get(j).getType().getSqlType();
                        ps.setObject(j + 1, fieldValues.get(j).getValue(), sqlType);
                    }
                }

                @Override
                public int getBatchSize() {
                    return records.size();
                }
            });

            return Arrays.stream(rows).sum();
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }

    @Override
    @Transactional(value = "bufferTM", readOnly = true)
    public long getCount(TemplateType type) {
        Objects.requireNonNull(type);

        switch (type) {
            case CS_REQ:
                return jdbcTemplate.queryForObject("SELECT count(*) FROM cs_req", Long.class);
            case CS_RESP:
                return jdbcTemplate.queryForObject("SELECT count(*) FROM cs_resp", Long.class);
            case GENERIC:
                return jdbcTemplate.queryForObject("SELECT count(*) FROM generic", Long.class);
        }

        return 0;
    }

    @Override
    @Transactional(value = "bufferTM", readOnly = true)
    public List<ClickCount> getClickCount(LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ")
                .append(" cast(cs.event_time AS DATE) AS date, ")
                .append(" count(*) AS count ")
                .append("FROM cs_req AS cs ")
                .append("WHERE cs.event_time >= ? AND cs.event_time < ? ")
                .append("GROUP BY cast(cs.event_time AS DATE)");

        try {
            return jdbcTemplate.query(sb.toString(), ps -> {
                ps.setObject(1, start);
                ps.setObject(2, end);
            }, (rs, rowNum) -> ClickCount.builder()
                    .date(rs.getObject(1, LocalDate.class))
                    .count(rs.getBigDecimal(2).toBigInteger())
                    .build());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional("bufferTM")
    public long delete(TemplateType type, LocalDateTime beforeEventTime) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(beforeEventTime);

        try {
            switch (type) {
                case CS_REQ:
                    return jdbcTemplate.update(CS_REQ_DELETE, beforeEventTime);
                case CS_RESP:
                    return jdbcTemplate.update(CS_RESP_DELETE, beforeEventTime);
                case GENERIC:
                    return jdbcTemplate.update(GENERIC_DELETE, beforeEventTime);
            }
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }

    @Override
    @Transactional(value = "bufferTM", readOnly = true)
    public List<TrackedDomainRequests> getTrackedDomainRequests(List<DomainRegex> domainRegexps, LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(domainRegexps);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        String values = domainRegexps.stream()
                .filter(DomainRegex::isActive)
                .map(domainRegex -> String.format("(cast(%d AS BIGINT), cast('%s' AS VARCHAR(8000)), TIMESTAMP '%s')",
                        domainRegex.getId(),
                        domainRegex.getPattern().toLowerCase().trim(),
                        domainRegex.getDateAdded().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .collect(Collectors.joining(", "));

        if (values.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder querySB = new StringBuilder();

        querySB.append("WITH dr AS ( ")
                .append(" SELECT")
                .append("  d.id, ")
                .append("  d.pattern, ")
                .append("  d.date_added, ")
                .append("  TRUE AS is_active ")
                .append("  FROM (VALUES ")
                .append(values)
                .append(") AS d(id, pattern, date_added) ")
                .append("), tdr AS ( ")
                .append(" SELECT ")
                .append("  cast(cs.event_time AS DATE) AS date, ")
                .append("  dr.id AS domain_id, ")
                .append("  cs.ip_src, ")
                .append("  cs.login, ")
                .append("  cast(min(cs.event_time) AS TIME) AS first_time, ")
                .append("  cast(max(cs.event_time) AS TIME) AS last_time, ")
                .append("  count(*) AS cnt ")
                .append(" FROM cs_req AS cs INNER JOIN dr ON REGEXP_MATCHES(lower(cs.hostname), dr.pattern) ")
                .append("WHERE cs.event_time >= ? AND cs.event_time < ? ")
                .append("GROUP BY cast(cs.event_time AS DATE), dr.id, cs.ip_src, cs.login ")
                .append(" ) ")
                .append(" SELECT ")
                .append("  tdr.date, ")
                .append("  dr.id, ")
                .append("  dr.pattern, ")
                .append("  dr.date_added, ")
                .append("  dr.is_active, ")
                .append("  tdr.ip_src, ")
                .append("  tdr.login, ")
                .append("  tdr.first_time, ")
                .append("  tdr.last_time, ")
                .append("  tdr.cnt ")
                .append(" FROM tdr INNER JOIN dr ON tdr.domain_id = dr.id ");

        try {
            return jdbcTemplate.query(querySB.toString(),
                    ps -> {
                        ps.setObject(1, start);
                        ps.setObject(2, end);
                    },
                    (rs, rowNum) -> TrackedDomainRequests.builder()
                            .date(rs.getObject(1, LocalDate.class))
                            .domainRegex(DomainRegex.builder()
                                    .id(rs.getLong(2))
                                    .pattern(rs.getString(3))
                                    .dateAdded(rs.getTimestamp(4).toLocalDateTime())
                                    .active(rs.getBoolean(5))
                                    .build())
                            .address(rs.getString(6))
                            .login(rs.getString(7))
                            .firstTime(rs.getObject(8, LocalTime.class))
                            .lastTime(rs.getObject(9, LocalTime.class))
                            .count(rs.getBigDecimal(10).toBigInteger())
                            .build());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional(value = "bufferTM", readOnly = true)
    public LocalDateTime getMinEventTime(TemplateType type) {
        Objects.requireNonNull(type);

        try {
            switch (type) {
                case CS_REQ:
                    return jdbcTemplate.query("SELECT min(event_time) FROM cs_req", new LocalDateTimeResultSetExtractor());
                case CS_RESP:
                    return jdbcTemplate.query("SELECT min(event_time) FROM cs_resp", new LocalDateTimeResultSetExtractor());
                case GENERIC:
                    return jdbcTemplate.query("SELECT min(flow_end_millisecond) FROM generic", new LocalDateTimeResultSetExtractor());
            }
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    @Override
    @Transactional(value = "bufferTM", readOnly = true)
    public LocalDateTime getMaxEventTime(TemplateType type) {
        Objects.requireNonNull(type);

        try {
            switch (type) {
                case CS_REQ:
                    return jdbcTemplate.query("SELECT max(event_time) FROM cs_req", new LocalDateTimeResultSetExtractor());
                case CS_RESP:
                    return jdbcTemplate.query("SELECT max(event_time) FROM cs_resp", new LocalDateTimeResultSetExtractor());
                case GENERIC:
                    return jdbcTemplate.query("SELECT max(flow_start_millisecond) FROM generic", new LocalDateTimeResultSetExtractor());
            }
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    private int insertRecord(String query, IPFIXDataRecord record) {
        return jdbcTemplate.update(query, preparedStatement -> {
            List<IPFIXFieldValue> fieldValues = record.getFieldValues();

            for (int j = 0; j < fieldValues.size(); j++) {
                int sqlType = fieldValues.get(j).getType().getSqlType();
                preparedStatement.setObject(j + 1, fieldValues.get(j).getValue(), sqlType);
            }
        });
    }

    private class LocalDateTimeResultSetExtractor implements ResultSetExtractor<LocalDateTime> {
        @Override
        public LocalDateTime extractData(ResultSet rs) throws SQLException, DataAccessException {
            rs.next();
            Timestamp timestamp = rs.getTimestamp(1);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        }
    }
}