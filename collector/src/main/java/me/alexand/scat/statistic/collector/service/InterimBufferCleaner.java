package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static me.alexand.scat.statistic.collector.model.TemplateType.UNKNOWN;
import static me.alexand.scat.statistic.collector.utils.Constants.*;
import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.getFormattedDateTime;

/**
 * Класс для очистки буфера от старых IPFIX-записей
 * Единственный метод
 *
 * @author asidorov84@gmail.com
 */

@Service
public class InterimBufferCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterimBufferCleaner.class);

    private InterimBufferRepository interimBufferRepository;

    @Autowired
    public InterimBufferCleaner(InterimBufferRepository interimBufferRepository) {
        this.interimBufferRepository = interimBufferRepository;

    }

    @Scheduled(fixedRate = INTERIM_BUFFER_CLEANER_RUN_FREQUENCY, initialDelay = INTERIM_BUFFER_CLEANER_RUN_FREQUENCY)
    public void clean() {
        LOGGER.info("Start cleaner...");
        LocalDateTime beforeEventTime = LocalDateTime.now().minusMinutes(INTERIM_BUFFER_DEPTH);

        LOGGER.info("\tdeleting all records of all types in buffer before {}", beforeEventTime.format(DATE_TIME_FORMATTER));

        long totalRecordsDeleted = 0;

        for (TemplateType type : TemplateType.values()) {
            if (type.equals(UNKNOWN)) continue;

            long recordsDeleted = interimBufferRepository.delete(type, beforeEventTime);
            totalRecordsDeleted += recordsDeleted;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("\t-------------------------------------------------------");
                LOGGER.debug("\tdeleted records from {}: {}", type, recordsDeleted);
                LOGGER.debug("\trecords in {} is now: {}", type, interimBufferRepository.getCount(type));
                LOGGER.debug("\tminimum event time in {}: {}", type, getFormattedDateTime(interimBufferRepository.getMinEventTime(type)));
                LOGGER.debug("\tmaximum event time in {}: {}", type, getFormattedDateTime(interimBufferRepository.getMaxEventTime(type)));
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\t-------------------------------------------------------");
        }
        LOGGER.info("\ttotal records deleted: {}", totalRecordsDeleted);
        LOGGER.info("Stop cleaner\n");
    }
}
