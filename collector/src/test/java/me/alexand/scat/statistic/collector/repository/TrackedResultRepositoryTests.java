package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.common.repository.TrackedResultRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.alexand.scat.statistic.collector.entities.TrackedResultsTestEntities.TEST;
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
public class TrackedResultRepositoryTests {
    @Autowired
    private TrackedResultRepository repository;

    @Test
    public void testSave() {
        repository.save(TEST);
    }

    @Test
    public void testSaveDuplicate() {
        repository.save(TEST);
        repository.save(TEST);
    }
}
