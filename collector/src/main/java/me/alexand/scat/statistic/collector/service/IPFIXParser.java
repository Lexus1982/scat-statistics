package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.utils.exceptions.IPFIXParseException;

/**
 * @author asidorov84@gmail.com
 */
public interface IPFIXParser {
    IPFIXMessage parse(byte[] pdu) throws IPFIXParseException;
}