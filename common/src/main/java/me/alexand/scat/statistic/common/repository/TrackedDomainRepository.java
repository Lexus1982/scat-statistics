package me.alexand.scat.statistic.common.repository;

import me.alexand.scat.statistic.common.model.TrackedDomain;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public interface TrackedDomainRepository {
    List<TrackedDomain> getAll();

    void saveAll(List<TrackedDomain> entities);

    void save(TrackedDomain entity);

    long getCount();
}
