package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXFieldValue;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
import me.alexand.scat.statistic.common.model.TrackedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SQL Databases реализация репозитория для IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class InterimBufferRepositoryImpl implements InterimBufferRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterimBufferRepositoryImpl.class);

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
    public InterimBufferRepositoryImpl(@Qualifier("bufferJDBCTemplate") JdbcTemplate jdbcTemplate) {
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
    public List<TrackedResult> getTrackedDomainsStatistic(List<String> domainPatterns, LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(domainPatterns);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);

        if (domainPatterns.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder querySB = new StringBuilder();

        querySB.append("SELECT ")
                .append(" td.regex_pattern, ")
                .append(" cs.ip_src, ")
                .append(" cs.login, ")
                .append(" min(cs.event_time) AS first_time, ")
                .append(" max(cs.event_time) AS last_time, ")
                .append(" count(*) AS cnt ")
                .append("FROM cs_req AS cs INNER JOIN (VALUES ");

        String values = domainPatterns.stream()
                .map(pattern -> String.format("('%s')", pattern.toLowerCase()))
                .collect(Collectors.joining(", "));

        querySB.append(values)
                .append(") as td (regex_pattern) ")
                .append("ON REGEXP_MATCHES(lower(cs.hostname), trim(BOTH FROM td.regex_pattern)) ")
                .append("WHERE cs.event_time >= ? AND cs.event_time < ? ")
                .append("GROUP BY td.regex_pattern, cs.ip_src, cs.login");

        LOGGER.debug("Query: {}", querySB);

        try {
            return jdbcTemplate.query(querySB.toString(),
                    ps -> {
                        ps.setObject(1, start);
                        ps.setObject(2, end);
                    },
                    (rs, rowNum) -> TrackedResult.builder()
                            .regexPattern(rs.getString(1))
                            .address(rs.getString(2))
                            .login(rs.getString(3))
                            .firstTime(rs.getTimestamp(4).toLocalDateTime())
                            .lastTime(rs.getTimestamp(5).toLocalDateTime())
                            .count(rs.getBigDecimal(6).toBigInteger())
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