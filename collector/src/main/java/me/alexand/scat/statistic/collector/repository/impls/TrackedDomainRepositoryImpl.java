package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.common.model.TrackedDomain;
import me.alexand.scat.statistic.common.repository.TrackedDomainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
@Repository
public class TrackedDomainRepositoryImpl implements TrackedDomainRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackedDomainRepositoryImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TrackedDomainRepositoryImpl(@Qualifier("postgresqlJDBCTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(value = "postgresqlTM", readOnly = true)
    public List<TrackedDomain> getAll() {
        String query = "select regex_pattern, is_active, date_added from tracked_domains";

        try {
            return jdbcTemplate.query(query,
                    (rs, rowNum) -> TrackedDomain.builder()
                            .regexPattern(rs.getString(1))
                            .active(rs.getBoolean(2))
                            .dateAdded(rs.getTimestamp(3).toLocalDateTime())
                            .build());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    @Transactional("postgresqlTM")
    public void saveAll(List<TrackedDomain> entities) {
        entities.forEach(this::save);
    }

    @Override
    @Transactional("postgresqlTM")
    public void save(TrackedDomain entity) {
        String query = "INSERT INTO tracked_domains AS td (regex_pattern, is_active, date_added) VALUES (?, ?, ?)" +
                " ON CONFLICT (regex_pattern) DO UPDATE SET is_active = EXCLUDED.is_active";

        try {
            int rowsCount = jdbcTemplate.update(query, entity.getRegexPattern(), entity.isActive(), entity.getDateAdded());
            if (rowsCount == 1) {
                LOGGER.info("{} saved successfully", entity);
            } else {
                LOGGER.info("TrackedDomain: pattern {} already present", entity.getRegexPattern());
            }
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    @Transactional(value = "postgresqlTM", readOnly = true)
    public long getCount() {
        String query = "SELECT count(*) AS cnt FROM tracked_domains";

        try {
            return jdbcTemplate.queryForObject(query, Long.class);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }

        return 0;
    }
}