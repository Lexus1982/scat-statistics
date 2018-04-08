package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.repository.TemplateRepository;
import me.alexand.scat.statistic.collector.utils.exceptions.TemplatesNotFound;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author asidorov84@gmail.com
 */

@Repository
public class XMLTemplateRepositoryImpl implements TemplateRepository {
    @Override
    public List<DataTemplate> getAll(String xmlFileName) throws TemplatesNotFound {
        List<DataTemplate> result = new ArrayList<>();
        return result;
    }
}