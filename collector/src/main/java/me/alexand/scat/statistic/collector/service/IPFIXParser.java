package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.RawDataPacket;

/**
 * @author asidorov84@gmail.com
 */
public interface IPFIXParser {
    IPFIXDataRecord parse(RawDataPacket pdu);
}