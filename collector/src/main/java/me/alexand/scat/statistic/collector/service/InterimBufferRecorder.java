package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author asidorov84@gmail.com
 */

@Component
public class InterimBufferRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterimBufferRecorder.class);
    private static final int OUTPUT_BUFFER_SIZE = 100000;

    private final Map<TemplateType, Thread> recorderThreads = new HashMap<>();
    private final Map<TemplateType, BlockingQueue<List<IPFIXDataRecord>>> recordsBuffers = new HashMap<>();
    private final InterimBufferRepository interimBufferRepository;

    @Autowired
    public InterimBufferRecorder(InterimBufferRepository interimBufferRepository) {
        LOGGER.info("initializing recorders...");
        this.interimBufferRepository = interimBufferRepository;

        for (TemplateType type : TemplateType.values()) {
            recordsBuffers.put(type, new ArrayBlockingQueue<>(OUTPUT_BUFFER_SIZE));
        }

        for (TemplateType templateType : TemplateType.values()) {
            String threadName = String.format("%s-recorder-thread", templateType.getName().toLowerCase());
            Thread recorderThread = new Thread(new Recorder(templateType), threadName);
            recorderThreads.put(templateType, recorderThread);
            recorderThread.start();
        }

    }

    public void transfer(TemplateType type, List<IPFIXDataRecord> records) {
        recordsBuffers.get(type).offer(records);
    }

    private class Recorder implements Runnable {

        private final TemplateType templateType;

        public Recorder(TemplateType templateType) {
            this.templateType = templateType;
        }

        @Override
        public void run() {
            BlockingQueue<List<IPFIXDataRecord>> buffer = recordsBuffers.get(templateType);
            LOGGER.info("start recorder with template type: {}", templateType);

            try {
                while (!recorderThreads.get(templateType).isInterrupted()) {
                    List<IPFIXDataRecord> records = buffer.take();
                    interimBufferRepository.save(templateType, records);
                }

            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
