package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import org.springframework.stereotype.Service;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class IPFIXParserImpl implements IPFIXParser {
    @Override
    public IPFIXDataRecord parse(RawDataPacket pdu) {
        return null;
    }
}