package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXMessage;
import me.alexand.scat.statistic.collector.model.IPFIXSet;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.network.PacketsReceiver;
import me.alexand.scat.statistic.collector.utils.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Процессор, управляющий логикой получения пакета по сети,
 * преобразования его в IPFIX-сообщение и сохранения декодированных
 * данных во внутреннем буфере
 *
 * @author asidorov84@gmail.com
 */

@Component
@Scope("prototype")
public class IPFIXMessageProcessor implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPFIXMessageProcessor.class);

    private static final int BATCH_SIZE = 5000;

    private static volatile int processorsCounter = 0;
    private int processorId;

    private final Map<TemplateType, List<IPFIXDataRecord>> recordsCache = new HashMap<>();
    private final IPFIXParser parser;
    private final PacketsReceiver receiver;
    private final InterimBufferRecorder interimBufferRecorder;

    private final StatCollector statCollector;

    public IPFIXMessageProcessor(IPFIXParser parser,
                                 PacketsReceiver receiver,
                                 InterimBufferRecorder interimBufferRecorder,
                                 StatCollector statCollector) {
        synchronized (IPFIXMessageProcessor.class) {
            processorId = ++processorsCounter;
        }

        this.parser = parser;
        this.receiver = receiver;
        this.interimBufferRecorder = interimBufferRecorder;
        this.statCollector = statCollector;

        for (TemplateType type : TemplateType.values()) {
            recordsCache.put(type, new ArrayList<>(BATCH_SIZE));
        }
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

                for (IPFIXSet set : message.getSets()) {
                    int setID = set.getSetID();

                    if (setID >= 256 && setID <= 65535) {
                        set.getRecords().forEach(record -> {
                            if (record instanceof IPFIXDataRecord) {
                                IPFIXDataRecord dataRecord = (IPFIXDataRecord) record;
                                TemplateType dataRecordType = dataRecord.getType();
                                List<IPFIXDataRecord> batchList = recordsCache.get(dataRecordType);
                                batchList.add(dataRecord);

                                if (batchList.size() == BATCH_SIZE) {
                                    interimBufferRecorder.transfer(dataRecordType, batchList);
                                    recordsCache.put(dataRecordType, new ArrayList<>(BATCH_SIZE));
                                }
                            }
                        });
                    }
                }
            } catch (MalformedMessageException |
                    UnknownProtocolException |
                    UnknownDataRecordFormatException |
                    UnknownInfoModelException e) {
                //TODO
            } catch (IPFIXParseException e) {
                //TODO
            }
        }

        LOGGER.info("...shutdown of processor with id = {} complete", processorId);
        statCollector.unregisterProcessorThread();
    }
}