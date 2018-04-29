package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXFieldSpecifier;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public interface DataTemplateService {
    void loadFromXML(String filename);

    TemplateType getTypeByIPFIXSpecifiers(List<IPFIXFieldSpecifier> specifiers) throws UnknownInfoModelException;
}
