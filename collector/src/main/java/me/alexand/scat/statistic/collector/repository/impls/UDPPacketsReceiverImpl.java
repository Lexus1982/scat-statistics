package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.repository.PacketsReceiver;
import org.springframework.stereotype.Repository;

/**
 * @author asidorov84@gmail.com
 */

@Repository
public class UDPPacketsReceiverImpl implements PacketsReceiver {
    @Override
    public RawDataPacket getNextPacket() throws InterruptedException {
        return null;
    }
}