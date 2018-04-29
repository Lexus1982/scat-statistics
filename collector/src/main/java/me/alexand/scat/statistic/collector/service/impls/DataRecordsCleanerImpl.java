package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.repository.DataRecordsRepository;
import me.alexand.scat.statistic.collector.service.DataRecordsCleaner;
import me.alexand.scat.statistic.collector.service.StatCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Реализация удаления IPFIX-записей старше 5 минут
 *
 * @author asidorov84@gmail.com
 */

@Service
public class DataRecordsCleanerImpl implements DataRecordsCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordsCleanerImpl.class);

    private DataRecordsRepository recordsRepository;
    private StatCollector statCollector;

    @Autowired
    public DataRecordsCleanerImpl(DataRecordsRepository recordsRepository, StatCollector statCollector) {
        this.recordsRepository = recordsRepository;
        this.statCollector = statCollector;
    }

    @Override
    @Scheduled(fixedRate = 300_000)
    public void clean() {
        LOGGER.info("start cleaner: delete 5 minute old records...");
        int deletedRecords = recordsRepository.deleteOld(LocalDateTime.now().minusMinutes(5));
        statCollector.registerDeletedRecordsCount(deletedRecords);
        LOGGER.info("...stop cleaner: deleted {} records", deletedRecords);
    }
}
