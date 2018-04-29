package me.alexand.scat.statistic.collector.model;

/**
 * Типы данных согласно классификации IANA
 *
 * @author asidorov84@gmail.com
 */

public enum IANAAbstractDataTypes {
    UNSIGNED8(1),
    UNSIGNED16(2),
    UNSIGNED32(4),
    UNSIGNED64(8),
    SIGNED8(1),
    SIGNED16(2),
    SIGNED32(4),
    SIGNED64(8),
    FLOAT32(4),
    FLOAT64(8),
    BOOLEAN(0),
    MAC_ADDRESS(0),
    OCTET_ARRAY(0),
    STRING(65535),
    DATE_TIME_SECONDS(4),
    DATE_TIME_MILLISECONDS(8),
    DATE_TIME_MICROSECONDS(0),
    DATE_TIME_NANOSECONDS(0),
    IPV4_ADDRESS(4),
    IPV6_ADDRESS(0);

    private int length;

    IANAAbstractDataTypes(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}