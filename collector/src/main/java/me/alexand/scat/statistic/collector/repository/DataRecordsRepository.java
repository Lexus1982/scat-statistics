package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;

import java.time.LocalDateTime;

/**
 * @author asidorov84@gmail.com
 */
public interface DataRecordsRepository {
    IPFIXDataRecord save(IPFIXDataRecord record);

    boolean deleteBetween(LocalDateTime start, LocalDateTime end);
}