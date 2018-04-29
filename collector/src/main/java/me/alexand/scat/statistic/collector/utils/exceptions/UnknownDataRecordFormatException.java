package me.alexand.scat.statistic.collector.utils.exceptions;

/**
 * @author asidorov84@gmail.com
 */

public class UnknownDataRecordFormatException extends IPFIXParseException {
    public UnknownDataRecordFormatException(String message) {
        super(message);
    }
}
