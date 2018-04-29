package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXFieldValue;
import me.alexand.scat.statistic.collector.repository.DataRecordsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SQL Databases реализация репозитория для IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class DataRecordsRepositoryImpl implements DataRecordsRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordsRepositoryImpl.class);

    private static final String CS_REQ_INSERT = "INSERT INTO cs_req(event_time, login, ip_src, ip_dst, hostname," +
            " path, refer, user_agent, cookie, session_id, locked, host_type, method)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String CS_RESP_INSERT = "INSERT INTO cs_resp(event_time, login, ip_src, ip_dst," +
            " result_code, content_length, content_type, session_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    private static final String GENERIC_INSERT = "INSERT INTO generic(octet_delta_count, packet_delta_count," +
            " protocol_identifier, ip_class_of_service, source_transport_port, source_ipv4_address," +
            " destination_transport_port, destination_ipv4_address, bgp_source_as_number, bgp_destination_as_number," +
            " flow_start_millisecond, flow_end_millisecond, input_snmp, output_snmp, ip_version, session_id," +
            " http_host, dpi_protocol, login, post_nat_source_ipv4_address, post_nat_source_transport_port) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DataRecordsRepositoryImpl(@Qualifier("bufferJDBCTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int save(IPFIXDataRecord record) {
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

        return insertedRecords;
    }

    @Override
    public int deleteBetween(LocalDateTime start, LocalDateTime end) {
        return 0;
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
}