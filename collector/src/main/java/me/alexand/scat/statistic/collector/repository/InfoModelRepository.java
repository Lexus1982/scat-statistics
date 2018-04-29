package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public interface InfoModelRepository {

    InfoModelEntity save(InfoModelEntity entity);

    boolean delete(int id);

    List<InfoModelEntity> getAll();

    InfoModelEntity getById(int id);

    InfoModelEntity getByEnterpriseNumberAndInformationElementIdentifier(
            long enterpriseNumber,
            int informationElementId) throws UnknownInfoModelException;

    long getCount();

    void clear();
}
