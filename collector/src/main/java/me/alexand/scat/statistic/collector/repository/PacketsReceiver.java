package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.RawDataPacket;

/**
 * @author asidorov84@gmail.com
 */
public interface PacketsReceiver {
    RawDataPacket getNextPacket() throws InterruptedException;
}