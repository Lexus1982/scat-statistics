package me.alexand.scat.statistic.collector.repository;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.common.model.TrackedResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Промежуточный буфер для всех IPFIX-записей
 *
 * @author asidorov84@gmail.com
 */
public interface InterimBufferRepository {
    /**
     * Получить количество IPFIX-записей указанного типа
     *
     * @param type тип записей
     * @return количество записей
     */
    long getCount(TemplateType type);

    /**
     * Сохранить IPFIX-запись
     *
     * @param record IPFIX-запись
     * @return true, если сохранение прошло успешно, иначе false
     */
    boolean save(IPFIXDataRecord record);

    /**
     * Удалить записи любых типов до указанной отметки времени, включительно
     *
     * @param before отметка времени
     * @return суммарное количество удаленных записей
     */
    long delete(LocalDateTime before);

    /**
     * Получить агрегированные данные об указанных посещенных доменах за указанный период
     *
     * @param domainPatterns список строк, где каждая строка является регулярным выражением
     * @param start          начальная отметка времени (>=)
     * @param end            конечная отметка времени (<)
     * @return список результатов об отслеженных доменах
     */
    List<TrackedResult> getTrackedDomainsStatistic(List<String> domainPatterns, LocalDateTime start, LocalDateTime end);
}