package me.alexand.scat.statistic.collector.utils.exceptions;

/**
 * @author asidorov84@gmail.com
 */

public class IPFIXParseException extends Exception {
    public IPFIXParseException(String message) {
        super(message);
    }

    public IPFIXParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public IPFIXParseException(Throwable cause) {
        super(cause);
    }
}
