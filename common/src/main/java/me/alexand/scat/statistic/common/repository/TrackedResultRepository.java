package me.alexand.scat.statistic.common.repository;

import me.alexand.scat.statistic.common.model.TrackedResult;

import java.util.List;

/**
 * @author asidorov84@gmail.com
 */
public interface TrackedResultRepository {
    int saveAll(List<TrackedResult> results);

    int save(TrackedResult result);
}
