package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
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

    private InterimBufferRepository recordsRepository;
    private StatCollector statCollector;

    @Autowired
    public DataRecordsCleanerImpl(InterimBufferRepository recordsRepository, StatCollector statCollector) {
        this.recordsRepository = recordsRepository;
        this.statCollector = statCollector;
    }

    @Override
    @Scheduled(fixedRate = 300_000)//TODO вынести периодичность в проперти
    public void clean() {
        LocalDateTime before = LocalDateTime.now().minusMinutes(5);
        LOGGER.info("start cleaner: delete all records before {}", before);//TODO выводить с помощью паттерна
        long deletedRecords = recordsRepository.delete(before);
        statCollector.registerDeletedRecordsCount(deletedRecords);
        LOGGER.info("...stop cleaner: deleted {} records", deletedRecords);
    }
}
