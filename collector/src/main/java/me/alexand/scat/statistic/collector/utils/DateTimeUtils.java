package me.alexand.scat.statistic.collector.utils;

import java.time.LocalDateTime;

import static me.alexand.scat.statistic.collector.utils.Constants.DATE_TIME_FORMATTER;

/**
 * @author asidorov84@gmail.com
 */
public interface DateTimeUtils {
    static String getFormattedDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "N/A";
    }
}
