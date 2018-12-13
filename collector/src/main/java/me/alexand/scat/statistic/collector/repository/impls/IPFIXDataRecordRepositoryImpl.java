package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXFieldValue;
import me.alexand.scat.statistic.collector.model.ImportDataTemplate;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.repository.IPFIXDataRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sidorov Alexander. 27.11.18
 */

@Repository
public class IPFIXDataRecordRepositoryImpl implements IPFIXDataRecordRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXDataRecordRepositoryImpl.class);
    private static final String DATABASE_SCHEMA_NAME = "ipfix_data"; 

    private final JdbcTemplate jdbcTemplate;
    private final Map<ImportDataTemplate, String> insertStatementsCache = new ConcurrentHashMap<>();

    public IPFIXDataRecordRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public int save(ImportDataTemplate dataTemplate, List<IPFIXDataRecord> records) {
        try {
            int[] rows = jdbcTemplate.batchUpdate(getInsertStatement(dataTemplate), new BatchPreparedStatementSetter() {
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

    private String getInsertStatement(ImportDataTemplate dataTemplate) {
        String statement = insertStatementsCache.get(dataTemplate);
        if (statement == null) {
            StringBuilder sql = new StringBuilder();
            
            sql.append("INSERT INTO ")
                    .append(DATABASE_SCHEMA_NAME)
                    .append(".")
                    .append(dataTemplate.getName())
                    .append(" (")
                    .append(joinTemplateSpecifierNames(dataTemplate.getSpecifiers()))
                    .append(") VALUES (")
                    .append(joinValues(dataTemplate.getSpecifiers().size()))
                    .append(")");

            statement = sql.toString();
            LOGGER.debug(statement);
            insertStatementsCache.put(dataTemplate, statement);
        }
        return statement;
    }

    private String joinValues(int size) {
        return Stream.generate(() -> "?")
                .limit(size)
                .collect(Collectors.joining(", "));
    }

    private String joinTemplateSpecifierNames(List<InfoModelEntity> specifiers) {
        return specifiers.stream()
                .map(InfoModelEntity::getName)
                .collect(Collectors.joining(", "));
    }
}
