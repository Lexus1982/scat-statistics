package me.alexand.scat.statistic.collector.controller;

import me.alexand.scat.statistic.collector.service.DataRecordsProcessorFactory;
import me.alexand.scat.statistic.collector.service.DataTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Контроллер коллектора.
 * <p>
 * Загружает шаблоны СКАТ из XML-файла
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
                               final DataRecordsProcessorFactory dataRecordsProcessorFactory,
                               final DataTemplateService dataTemplateService) {
        LOGGER.info("Loading SCAT templates");
        dataTemplateService.loadFromXML("");

        LOGGER.info("Initializing processors thread pool with fixed thread count: {}", processorsCount);
        processorsPool = Executors.newFixedThreadPool(processorsCount);

        LOGGER.info("Starting processors");
        for (int i = 0; i < processorsCount; i++) {
            processorsPool.submit(dataRecordsProcessorFactory.getProcessor());
        }
    }

    @PreDestroy
    private void shutdown() {
        LOGGER.info("Shutdown begin...");

        try {
            processorsPool.shutdownNow();

            LOGGER.info("...waiting until all processors stopped");

            while (!processorsPool.isTerminated()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            LOGGER.info("Normal shutdown failed");
            return;
        }

        LOGGER.info("Shutdown complete successfully");
    }
}