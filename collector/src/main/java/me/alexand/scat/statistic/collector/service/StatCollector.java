package me.alexand.scat.statistic.collector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class StatCollector {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatCollector.class);
    private final Map<CounterName, AtomicLong> counters = new ConcurrentHashMap<>();
    private final LocalDateTime applicationStart = LocalDateTime.now();

    private LocalDateTime lastReport;

    public StatCollector() {
        for (CounterName counterName : CounterName.values()) {
            counters.put(counterName, new AtomicLong());
        }

        lastReport = applicationStart;
    }

    public void registerReceivedPacket() {
        counters.get(CounterName.RECEIVED_PACKETS).incrementAndGet();
    }

    public void registerTemplateRecord() {
        counters.get(CounterName.TEMPLATE_RECORDS).incrementAndGet();
    }

    public void registerInputBufferOverflow() {
        counters.get(CounterName.INPUT_BUFFER_OVERFLOWS).incrementAndGet();
    }

    public void registerProcessorThread() {
        counters.get(CounterName.PROCESSOR_THREAD).incrementAndGet();
    }

    public void unregisterProcessorThread() {
        counters.get(CounterName.PROCESSOR_THREAD).decrementAndGet();
    }

    public void registerProcessedPacket() {
        counters.get(CounterName.PROCESSED_PACKETS).incrementAndGet();
    }

    public void registerDataRecord() {
        counters.get(CounterName.DATA_RECORDS).incrementAndGet();
    }

    public void registerUnknownDataFormatPacket() {
        counters.get(CounterName.UNKNOWN_FORMAT_DATA_RECORDS).incrementAndGet();
        counters.get(CounterName.FAILED_PACKETS).incrementAndGet();
    }


    public void registerUnknownInfoModel() {
        counters.get(CounterName.UNKNOWN_INFO_MODEL).incrementAndGet();
        counters.get(CounterName.FAILED_PACKETS).incrementAndGet();
    }


    public void registerMalformedPacket() {
        counters.get(CounterName.MALFORMED_PACKETS).incrementAndGet();
        counters.get(CounterName.FAILED_PACKETS).incrementAndGet();
    }

    public void registerUnknownProtocolPacket() {
        counters.get(CounterName.UNKNOWN_PROTOCOL_PACKETS).incrementAndGet();
        counters.get(CounterName.FAILED_PACKETS).incrementAndGet();
    }

    public void registerFailedPacket() {
        counters.get(CounterName.FAILED_PACKETS).incrementAndGet();
    }

    public void registerOptionalTemplateRecord() {
        counters.get(CounterName.OPTIONAL_TEMPLATE_RECORDS).incrementAndGet();
    }

    public void registerExportedRecords(int insertedCount) {
        counters.get(CounterName.EXPORTED_RECORDS).updateAndGet(operand -> operand + insertedCount);
    }

    public void registerDeletedRecordsCount(int deletedCount) {
        counters.get(CounterName.DELETED_RECORDS).updateAndGet(operand -> operand + deletedCount);
    }

    @Scheduled(fixedDelay = 10_000)
    public void report() {
        long secondsSinceLastReport = ChronoUnit.SECONDS.between(lastReport, LocalDateTime.now());

        if (secondsSinceLastReport == 0) return;

        lastReport = LocalDateTime.now();
        Duration uptime = Duration.between(applicationStart, LocalDateTime.now());

        StringBuilder sb = new StringBuilder();

        sb.append("\n\n\tuptime: ")
                .append(uptime.toMinutes()).append(" minutes\n");

        sb.append("\tparser threads: ")
                .append(counters.get(CounterName.PROCESSOR_THREAD).get())
                .append("\n");

        long receivedPackets = counters.get(CounterName.RECEIVED_PACKETS).getAndSet(0);

        sb.append("\n\tpackets received: ")
                .append(receivedPackets)
                .append(", rate: ")
                .append(receivedPackets / secondsSinceLastReport)
                .append(" packets/sec").append("\n");

        sb.append("\tinput buffer overflows: ")
                .append(counters.get(CounterName.INPUT_BUFFER_OVERFLOWS).getAndSet(0))
                .append("\n");

        long processedPackets = counters.get(CounterName.PROCESSED_PACKETS).getAndSet(0);

        sb.append("\tpackets processed: ")
                .append(processedPackets)
                .append(", rate: ")
                .append(processedPackets / secondsSinceLastReport)
                .append(" packets/sec").append("\n");

        sb.append("\tfailed packets (malformed/unknown info model/unknown protocol/unknown data record format/total): ")
                .append(counters.get(CounterName.MALFORMED_PACKETS).getAndSet(0)).append("/")
                .append(counters.get(CounterName.UNKNOWN_INFO_MODEL).getAndSet(0)).append("/")
                .append(counters.get(CounterName.UNKNOWN_PROTOCOL_PACKETS).getAndSet(0)).append("/")
                .append(counters.get(CounterName.UNKNOWN_FORMAT_DATA_RECORDS).getAndSet(0)).append("/")
                .append(counters.get(CounterName.FAILED_PACKETS).getAndSet(0)).append("\n");

        long dataRecords = counters.get(CounterName.DATA_RECORDS).getAndSet(0);

        sb.append("\trecords types (template/optional template/data): ")
                .append(counters.get(CounterName.TEMPLATE_RECORDS).getAndSet(0)).append("/")
                .append(counters.get(CounterName.OPTIONAL_TEMPLATE_RECORDS).getAndSet(0)).append("/")
                .append(dataRecords).append("\n");

        sb.append("\tdata records rate: ")
                .append(dataRecords / secondsSinceLastReport)
                .append(" records/sec").append("\n");

        sb.append("\n\texported records to buffer: ")
                .append(counters.get(CounterName.EXPORTED_RECORDS).getAndSet(0)).append("\n");

        sb.append("\n\tdeleted records from buffer: ")
                .append(counters.get(CounterName.DELETED_RECORDS).getAndSet(0)).append("\n");

        LOGGER.info(sb.toString());
    }

    private enum CounterName {
        PROCESSOR_THREAD,
        RECEIVED_PACKETS,
        INPUT_BUFFER_OVERFLOWS,
        PROCESSED_PACKETS,
        FAILED_PACKETS,
        MALFORMED_PACKETS,
        UNKNOWN_PROTOCOL_PACKETS,
        UNKNOWN_INFO_MODEL,
        UNKNOWN_FORMAT_DATA_RECORDS,
        TEMPLATE_RECORDS,
        OPTIONAL_TEMPLATE_RECORDS,
        DATA_RECORDS,
        DELETED_RECORDS,
        EXPORTED_RECORDS
    }
}
