package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.network.PacketsReceiver;
import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
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
public class DataRecordsProcessor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordsProcessor.class);
    private static volatile int processorsCounter = 0;
    private int processorId;
    private final IPFIXParser parser;
    private final PacketsReceiver receiver;
    private final InterimBufferRepository interimBufferRepository;
    private final StatCollector statCollector;

    public DataRecordsProcessor(IPFIXParser parser,
                                PacketsReceiver receiver,
                                InterimBufferRepository interimBufferRepository,
                                StatCollector statCollector) {
        synchronized (DataRecordsProcessor.class) {
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
        statCollector.registerProcessorThread(processorId);

        while (!currentThread.isInterrupted()) {
            byte[] rawPacket;

            try {
                rawPacket = receiver.getNextPacket();
                statCollector.registerReceivedPacket(processorId);
            } catch (InterruptedException e) {
                break;
            }

            try {
                long t0 = System.nanoTime();
                IPFIXMessage message = parser.parse(rawPacket);
                long t1 = System.nanoTime();

                statCollector.registerProcessedPacket(processorId, t1 - t0);

//                for (IPFIXSet set : message.getSets()) {
//                    int setID = set.getSetID();
//
//                    if (setID >= 256 && setID <= 65535) {
//                        set.getRecords().forEach(record -> {
//                            boolean savingStatus = interimBufferRepository.save((IPFIXDataRecord) record);
////                            if (savingStatus) {
////                                statCollector.registerExportedRecords(1);
////                            }
//                        });
//                    }
//                }
            } catch (MalformedMessageException |
                    UnknownProtocolException |
                    UnknownDataRecordFormatException |
                    UnknownInfoModelException e) {
            } catch (IPFIXParseException e) {
                //statCollector.registerFailedPacket();
            }
        }

        LOGGER.info("...shutdown of processor with id = {} complete", processorId);
        statCollector.unregisterProcessorThread();
    }
}