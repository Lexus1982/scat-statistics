package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.utils.exceptions.TemplatesNotFound;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author asidorov84@gmail.com
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-app.xml")
public class TemplateRepositoryTests {
    private static final String FILENAME = "classpath:scat-templates.xml";

    @Autowired
    private TemplateRepository repository;

    @Test
    public void testGetAllSCATTemplates() throws TemplatesNotFound {
        List<DataTemplate> templates = repository.getAll(FILENAME);
        assertNotNull(templates);
        assertFalse(templates.isEmpty());
    }
}