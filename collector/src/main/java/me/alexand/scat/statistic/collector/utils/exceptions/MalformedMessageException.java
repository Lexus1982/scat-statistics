package me.alexand.scat.statistic.collector.utils.exceptions;

/**
 * @author asidorov84@gmail.com
 */

public class MalformedMessageException extends IPFIXParseException {
    public MalformedMessageException(String message) {
        super(message);
    }

    public MalformedMessageException(Throwable cause) {
        super(cause);
    }
}
