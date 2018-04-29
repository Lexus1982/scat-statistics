package me.alexand.scat.statistic.collector.service;

/**
 * Сервис, производящий периодическую очистку
 * внутреннего буфера IPFIX-записей
 * @author asidorov84@gmail.com
 */
public interface DataRecordsCleaner {
    //TODO сделать периодическую очистку таблиц HSQLDB
    void clean();
}