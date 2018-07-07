package me.alexand.scat.statistic.collector.utils;

import java.time.format.DateTimeFormatter;

/**
 * @author asidorov84@gmail.com
 */
public interface Constants {
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
    int INTERIM_BUFFER_CLEANER_RUN_FREQUENCY = 15000;
    int INTERIM_BUFFER_DEPTH = 15;
}
