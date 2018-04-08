package me.alexand.scat.statistic.collector.controller;

import me.alexand.scat.statistic.collector.service.DataRecordsProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Главный контроллер коллектора.
 * Создает и запускает в разных потоках процессоры для обработки.
 *
 * @author asidorov84@gmail.com
 */

@Component
public class ProcessorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorController.class);
    private final ExecutorService processorsPool;

    @Autowired
    public ProcessorController(@Value("${processors.count}") final int processorsCount,
                               final DataRecordsProcessorFactory dataRecordsProcessorFactory) {
        registerShutdownHook();
        processorsPool = Executors.newFixedThreadPool(processorsCount);

        LOGGER.info("starting processors...");

        for (int i = 0; i < processorsCount; i++) {
            processorsPool.submit(dataRecordsProcessorFactory.getProcessor());
        }
    }

    private void shutdown() {
        LOGGER.info("start collector shutdown...");

        try {
            processorsPool.shutdownNow();

            LOGGER.info("...wait until all processors stopped");

            while (!processorsPool.isTerminated()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            LOGGER.info("shutdown failed");
            return;
        }

        LOGGER.info("shutdown complete successfully");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }
}
