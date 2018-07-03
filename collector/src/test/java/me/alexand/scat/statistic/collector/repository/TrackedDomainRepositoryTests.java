package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.common.model.TrackedDomain;
import me.alexand.scat.statistic.common.repository.TrackedDomainRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static me.alexand.scat.statistic.collector.entities.TrackedDomainsTestEntities.*;
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
                scripts = "classpath:db/postgres/populate.sql",
                executionPhase = BEFORE_TEST_METHOD,
                config = @SqlConfig(
                        transactionManager = "postgresqlTM",
                        transactionMode = ISOLATED,
                        dataSource = "postgresqlTestDataSource")
        ),
        @Sql(
                scripts = "classpath:db/postgres/clear.sql",
                executionPhase = AFTER_TEST_METHOD,
                config = @SqlConfig(
                        transactionManager = "postgresqlTM",
                        transactionMode = ISOLATED,
                        dataSource = "postgresqlTestDataSource")
        )
})
public class TrackedDomainRepositoryTests {
    private static final long POPULATED_DOMAINS_COUNT = 2;

    @Autowired
    private TrackedDomainRepository repository;

    @Test
    public void testGetAll() {
        List<TrackedDomain> actual = repository.getAll();
        assertNotNull(actual);
        assertTrue(actual.size() == POPULATED_DOMAINS_COUNT);
        assertTrue(actual.contains(TEST_MAIL_RU));
        assertTrue(actual.contains(TEST_VK_COM));
    }

    @Test
    public void testGetCount() {
        assertEquals(POPULATED_DOMAINS_COUNT, repository.getCount());
    }

    @Test
    public void testSaveDuplicate() {
        repository.save(TEST_UPDATED_MAIL_RU);
        assertEquals(POPULATED_DOMAINS_COUNT, repository.getCount());

        List<TrackedDomain> actual = repository.getAll();
        assertNotNull(actual);
        assertTrue(actual.contains(TEST_UPDATED_MAIL_RU));
    }

    @Test
    public void testSave() {
        repository.save(TEST_OK_RU);
        assertEquals(POPULATED_DOMAINS_COUNT + 1, repository.getCount());

        List<TrackedDomain> actual = repository.getAll();
        assertNotNull(actual);
        assertTrue(actual.contains(TEST_OK_RU));
    }
}