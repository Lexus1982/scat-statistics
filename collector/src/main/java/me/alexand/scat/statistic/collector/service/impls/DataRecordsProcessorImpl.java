package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.repository.DataRecordsRepository;
import me.alexand.scat.statistic.collector.repository.PacketsReceiver;
import me.alexand.scat.statistic.collector.service.DataRecordsProcessor;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author asidorov84@gmail.com
 */

@Component
@Scope("prototype")
public class DataRecordsProcessorImpl implements DataRecordsProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordsProcessorImpl.class);
    private static volatile int processorsCounter = 0;
    private int processorId;
    private final IPFIXParser parser;
    private final PacketsReceiver receiver;
    private final DataRecordsRepository recordsRepository;

    public DataRecordsProcessorImpl(IPFIXParser parser, PacketsReceiver receiver, DataRecordsRepository recordsRepository) {
        synchronized (DataRecordsProcessorImpl.class) {
            processorId = ++processorsCounter;
        }

        this.parser = parser;
        this.receiver = receiver;
        this.recordsRepository = recordsRepository;
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        LOGGER.info("...start new processor with id = {}", processorId);

        while (!currentThread.isInterrupted()) {
            RawDataPacket rawDataPacket;

            try {
                rawDataPacket = receiver.getNextPacket();
            } catch (InterruptedException e) {
                break;
            }

            IPFIXDataRecord dataRecord = parser.parse(rawDataPacket);
            recordsRepository.save(dataRecord);
        }

        LOGGER.info("...shutdown of processor with id = {} complete", processorId);
    }
}