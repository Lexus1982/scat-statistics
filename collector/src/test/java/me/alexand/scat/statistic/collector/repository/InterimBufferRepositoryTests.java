package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.common.model.TrackedResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.collector.entities.DataRecordsTestEntities.CS_REQ_DATA_RECORD_1;
import static me.alexand.scat.statistic.collector.model.TemplateType.*;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

/**
 * @author asidorov84@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
@SqlGroup({
        @Sql(
                scripts = "classpath:db/hsqldb/populate.sql",
                executionPhase = BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        transactionManager = "bufferTM",
                        transactionMode = ISOLATED,
                        dataSource = "hsqlTestDataSource"
                )
        ),
        @Sql(
                scripts = "classpath:db/hsqldb/clear.sql",
                executionPhase = AFTER_TEST_METHOD,
                config = @SqlConfig(
                        transactionManager = "bufferTM",
                        transactionMode = ISOLATED,
                        dataSource = "hsqlTestDataSource"
                )
        )
})
public class InterimBufferRepositoryTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterimBufferRepositoryTests.class);
    private static final long EXPECTED_CS_REQ_RECORDS_COUNT = 22;
    private static final LocalDateTime DELETE_BEFORE_DATE_TIME = LocalDateTime.parse("2018-04-01T17:06:10");
    private static final long EXPECTED_DELETED_RECORDS_COUNT = 6;

    @Autowired
    private TransitionalBufferRepository repository;

    @Test
    public void testGetCountCSReq() {
        assertEquals(EXPECTED_CS_REQ_RECORDS_COUNT, repository.getCount(CS_REQ));
    }

    @Test
    public void testSaveCSReq() {
        assertTrue(repository.save(CS_REQ_DATA_RECORD_1));
    }

    @Test
    public void testDeleteBetween() {
        assertEquals(EXPECTED_DELETED_RECORDS_COUNT, repository.delete(CS_REQ, DELETE_BEFORE_DATE_TIME));
        assertEquals(0, repository.delete(CS_REQ, LocalDateTime.parse("2017-04-01T17:06:10")));
    }

    @Test
    public void testGetTrackedResults() {
        List<TrackedResult> actual = repository.getTrackedDomainsStatistic(
                asList(".*mail\\.ru$", ".*vk\\.com$"),
                LocalDateTime.parse("2018-04-01T17:06:10"),
                LocalDateTime.parse("2018-04-01T17:07:09"));

        assertNotNull(actual);
        assertTrue(actual.size() == 5);
    }

    @Test
    public void testGetMinEventTime() {
        LocalDateTime actual = repository.getMinEventTime(CS_REQ);
        assertNotNull(actual);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMinEventTime(CS_RESP);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMinEventTime(GENERIC);
        LOGGER.info("actual result: {}", String.valueOf(actual));
    }

    @Test
    public void testGetMaxEventTime() {
        LocalDateTime actual = repository.getMaxEventTime(CS_REQ);
        assertNotNull(actual);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMaxEventTime(CS_RESP);
        LOGGER.info("actual result: {}", String.valueOf(actual));

        actual = repository.getMaxEventTime(GENERIC);
        LOGGER.info("actual result: {}", String.valueOf(actual));
    }
}
