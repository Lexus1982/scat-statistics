package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.repository.DataRecordsRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author asidorov84@gmail.com
 */

@Repository
public class DataRecordsRepositoryImpl implements DataRecordsRepository {
    @Override
    public IPFIXDataRecord save(IPFIXDataRecord record) {
        return null;
    }

    @Override
    public boolean deleteBetween(LocalDateTime start, LocalDateTime end) {
        return false;
    }
}