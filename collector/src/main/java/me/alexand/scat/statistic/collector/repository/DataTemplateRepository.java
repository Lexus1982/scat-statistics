package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.model.TemplateType;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public interface DataTemplateRepository {
    List<DataTemplate> getAll();

    DataTemplate save(DataTemplate template);

    DataTemplate getByType(TemplateType type);

    boolean delete(TemplateType type);

    long getCount();

    void clear();
}