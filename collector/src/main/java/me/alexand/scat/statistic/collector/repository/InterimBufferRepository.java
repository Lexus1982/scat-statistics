package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.common.model.TrackedResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Промежуточный буфер для хранения всех IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */
public interface InterimBufferRepository {
    /**
     * Получить количество IPFIX-записей указанного типа
     *
     * @param type тип записей (обязательный)
     * @return количество записей
     */
    long getCount(TemplateType type);

    /**
     * Сохранить IPFIX-запись
     *
     * @param record IPFIX-запись (обязательный)
     * @return true, если сохранение прошло успешно, иначе false
     */
    boolean save(IPFIXDataRecord record);

    /**
     * Удалить записи указанного типа старее указанной отметки времени, включительно
     *
     * @param type            тип записей (обязательный)
     * @param beforeEventTime отметка времени (обязательный)
     * @return суммарное количество удаленных записей
     */
    long delete(TemplateType type, LocalDateTime beforeEventTime);

    /**
     * Получить агрегированные данные об указанных посещенных доменах за указанный период
     *
     * @param domainPatterns список строк, где каждая строка является регулярным выражением (обязательный)
     * @param start          начальная отметка времени (>=) (обязательный)
     * @param end            конечная отметка времени (<) (обязательный)
     * @return список результатов об отслеженных доменах
     */
    List<TrackedResult> getTrackedDomainsStatistic(List<String> domainPatterns, LocalDateTime start, LocalDateTime end);

    /**
     * Получить минимальную отметку времени у записей данного типа, находящихся в буфере
     *
     * @param type тип записей (обязательный)
     * @return отметка времени или null, если в буфере нет ни одной записи данного типа
     */
    LocalDateTime getMinEventTime(TemplateType type);

    /**
     * Получить максимальную отметку времени у записей данного типа, находящихся в буфере
     *
     * @param type тип записей (обязательный)
     * @return отметка времени или null, если в буфере нет ни одной записи данного типа
     */
    LocalDateTime getMaxEventTime(TemplateType type);
}