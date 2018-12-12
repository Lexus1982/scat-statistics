package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXFieldValue;
import me.alexand.scat.statistic.collector.model.TemplateType;
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

/**
 * Created by Sidorov Alexander. 27.11.18
 */

@Repository
public class IPFIXDataRecordRepositoryImpl implements IPFIXDataRecordRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXDataRecordRepositoryImpl.class);
    
    private JdbcTemplate jdbcTemplate;

    public IPFIXDataRecordRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public int save(TemplateType type, List<IPFIXDataRecord> records) {
        try {
            int[] rows = jdbcTemplate.batchUpdate(type.getInsertStatement(), new BatchPreparedStatementSetter() {
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
}
