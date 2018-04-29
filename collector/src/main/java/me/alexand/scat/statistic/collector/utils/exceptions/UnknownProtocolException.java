package me.alexand.scat.statistic.collector.utils.exceptions;

/**
 * @author asidorov84@gmail.com
 */
public class UnknownProtocolException extends IPFIXParseException {
    public UnknownProtocolException(String message) {
        super(message);
    }
}
