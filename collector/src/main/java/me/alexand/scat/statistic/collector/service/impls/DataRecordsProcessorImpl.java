package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.model.IPFIXSet;
import me.alexand.scat.statistic.collector.model.RawDataPacket;
import me.alexand.scat.statistic.collector.network.PacketsReceiver;
import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
import me.alexand.scat.statistic.collector.service.DataRecordsProcessor;
import me.alexand.scat.statistic.collector.service.IPFIXParser;
import me.alexand.scat.statistic.collector.service.StatCollector;
import me.alexand.scat.statistic.collector.utils.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Процессор, управляющий логикой получения пакета по сети,
 * преобразования его в IPFIX-сообщение и сохранения декодированных
 * данных во внутреннем буфере
 *
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
    private final InterimBufferRepository interimBufferRepository;
    private final StatCollector statCollector;

    public DataRecordsProcessorImpl(IPFIXParser parser,
                                    PacketsReceiver receiver,
                                    InterimBufferRepository interimBufferRepository,
                                    StatCollector statCollector) {
        synchronized (DataRecordsProcessorImpl.class) {
            processorId = ++processorsCounter;
        }

        this.parser = parser;
        this.receiver = receiver;
        this.interimBufferRepository = interimBufferRepository;
        this.statCollector = statCollector;
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        LOGGER.info("...start new processor with id = {}", processorId);
        statCollector.registerProcessorThread();

        while (!currentThread.isInterrupted()) {
            RawDataPacket rawDataPacket;

            try {
                rawDataPacket = receiver.getNextPacket();
            } catch (InterruptedException e) {
                break;
            }

            try {
                IPFIXMessage message = parser.parse(rawDataPacket.getPdu());
                statCollector.registerProcessedPacket();

                for (IPFIXSet set : message.getSets()) {
                    int setID = set.getSetID();

                    if (setID >= 256 && setID <= 65535) {
                        set.getRecords().forEach(record -> {
                            boolean savingStatus = interimBufferRepository.save((IPFIXDataRecord) record);
                            if (savingStatus) {
                                statCollector.registerExportedRecords(1);
                            }
                        });
                    }
                }
            } catch (MalformedMessageException |
                    UnknownProtocolException |
                    UnknownDataRecordFormatException |
                    UnknownInfoModelException e) {
            } catch (IPFIXParseException e) {
                statCollector.registerFailedPacket();
            }
        }

        LOGGER.info("...shutdown of processor with id = {} complete", processorId);
        statCollector.unregisterProcessorThread();
    }
}