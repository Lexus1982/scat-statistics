package me.alexand.scat.statistic.collector.model;

/**
 * Типы данных согласно классификации IANA
 *
 * @author asidorov84@gmail.com
 */

public enum IANAAbstractDataTypes {
    UNSIGNED8,
    UNSIGNED16,
    UNSIGNED32,
    UNSIGNED64,
    SIGNED8,
    SIGNED16,
    SIGNED32,
    SIGNED64,
    FLOAT32,
    FLOAT64,
    BOOLEAN,
    MAC_ADDRESS,
    OCTET_ARRAY,
    STRING,
    DATE_TIME_SECONDS,
    DATE_TIME_MILLISECONDS,
    DATE_TIME_MICROSECONDS,
    DATE_TIME_NANOSECONDS,
    IPV4_ADDRESS,
    IPV6_ADDRESS
}