package me.alexand.scat.statistic.collector.model;

/**
 * Необработанный пакет данных
 *
 * @author asidorov84@gmail.com
 */
public class RawDataPacket {
    private byte[] pdu;

    public RawDataPacket(byte[] pdu) {
        this.pdu = pdu;
    }

    public byte[] getPdu() {
        return pdu;
    }

    public int getLength() {
        return pdu.length;
    }
}