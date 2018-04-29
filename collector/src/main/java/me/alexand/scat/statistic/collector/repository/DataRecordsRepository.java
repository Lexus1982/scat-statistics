package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;

import java.time.LocalDateTime;

/**
 * Репозиторий для хранения всех IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */
public interface DataRecordsRepository {
    int save(IPFIXDataRecord record);

    int deleteOld(LocalDateTime before);
}