package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.model.TemplateType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static me.alexand.scat.statistic.collector.model.TemplateType.GENERIC;
import static me.alexand.scat.statistic.collector.utils.DataTemplateEntities.DATA_TEMPLATE_LIST;
import static me.alexand.scat.statistic.collector.utils.InfoModelEntities.INFO_MODEL_DATA_LIST;
import static org.junit.Assert.*;

/**
 * @author asidorov84@gmail.com
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class DataTemplateRepositoryTests {

    @Autowired
    private DataTemplateRepository repository;

    @Before
    public void before() {
        repository.clear();
        DATA_TEMPLATE_LIST.forEach(repository::save);
    }

    @Test
    public void testGetAll() {
        assertEquals(DATA_TEMPLATE_LIST, repository.getAll());
    }

    @Test
    public void testGetByType() {
        for (DataTemplate expected : DATA_TEMPLATE_LIST) {
            DataTemplate actual = repository.getByType(expected.getType());
            assertNotNull(actual);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testSave() {
        DataTemplate expected = DataTemplate.builder()
                .type(TemplateType.UNKNOWN)
                .specifiers(INFO_MODEL_DATA_LIST)
                .build();

        DataTemplate actual = repository.save(expected);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testDelete() {
        assertTrue(repository.delete(GENERIC));
        assertNull(repository.getByType(GENERIC));
    }

    @Test
    public void testGetCount() {
        assertEquals(DATA_TEMPLATE_LIST.size(), repository.getCount());
    }

    @Test
    public void testClear() {
        repository.clear();
        assertTrue(repository.getCount() == 0);
    }
}