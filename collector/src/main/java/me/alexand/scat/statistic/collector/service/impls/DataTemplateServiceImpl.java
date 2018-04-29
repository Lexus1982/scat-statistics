package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.model.IPFIXFieldSpecifier;
import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.DataTemplateRepository;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.service.DataTemplateService;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static me.alexand.scat.statistic.collector.model.TemplateType.UNKNOWN;
import static me.alexand.scat.statistic.collector.utils.DataTemplateEntities.DATA_TEMPLATE_LIST;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class DataTemplateServiceImpl implements DataTemplateService {
    private DataTemplateRepository dataTemplateRepository;
    private InfoModelRepository infoModelRepository;

    @Autowired
    public DataTemplateServiceImpl(DataTemplateRepository dataTemplateRepository,
                                   InfoModelRepository infoModelRepository) {
        this.dataTemplateRepository = dataTemplateRepository;
        this.infoModelRepository = infoModelRepository;
    }

    @Override
    public void loadFromXML(String filename) {
        //TODO распарсить и сохранить в репозитории шаблоны, в перспективе

        DATA_TEMPLATE_LIST.forEach(dataTemplate -> {
            dataTemplateRepository.save(dataTemplate);
            dataTemplate.getSpecifiers().forEach(infoModelRepository::save);
        });
    }

    @Override
    public TemplateType getTypeByIPFIXSpecifiers(List<IPFIXFieldSpecifier> fieldSpecifiers) throws UnknownInfoModelException {
        List<InfoModelEntity> infoModelEntities;

        try {
            infoModelEntities = fieldSpecifiers.stream()
                    .map(specifier -> {
                        try {
                            return infoModelRepository.getByEnterpriseNumberAndInformationElementIdentifier(
                                    specifier.getEnterpriseNumber(),
                                    specifier.getInformationElementIdentifier()
                            );
                        } catch (UnknownInfoModelException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(toList());
        } catch (RuntimeException e) {
            throw new UnknownInfoModelException(e.getMessage());
        }

        return dataTemplateRepository.getAll().stream()
                .filter(template -> Objects.equals(template.getSpecifiers(), infoModelEntities))
                .map(DataTemplate::getType)
                .findFirst()
                .orElse(UNKNOWN);
    }
}