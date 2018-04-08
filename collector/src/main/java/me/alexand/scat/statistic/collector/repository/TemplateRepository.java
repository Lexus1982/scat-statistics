package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.utils.exceptions.TemplatesNotFound;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public interface TemplateRepository {
    List<DataTemplate> getAll(String xmlFileName) throws TemplatesNotFound;
}