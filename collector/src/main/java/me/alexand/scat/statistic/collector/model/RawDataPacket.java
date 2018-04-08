package me.alexand.scat.statistic.collector.model;

/**
 * Необработанный пакет данных
 *
 * @author asidorov84@gmail.com
 */
public class RawDataPacket {
    private byte[] rawPacket;

    public RawDataPacket(byte[] rawPacket) {
        this.rawPacket = rawPacket;
    }

    public byte[] getRawPacket() {
        return rawPacket;
    }
}