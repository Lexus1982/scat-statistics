package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
import me.alexand.scat.statistic.common.model.TrackedDomain;
import me.alexand.scat.statistic.common.model.TrackedResult;
import me.alexand.scat.statistic.common.repository.TrackedDomainRepository;
import me.alexand.scat.statistic.common.repository.TrackedResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.getFormattedDateTime;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class Sampling {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sampling.class);
    private final TrackedDomainRepository trackedDomainRepository;
    private final InterimBufferRepository interimBufferRepository;
    private final TrackedResultRepository trackedResultRepository;
    private LocalDateTime lastTime;

    @Autowired
    public Sampling(TrackedDomainRepository trackedDomainRepository,
                    InterimBufferRepository interimBufferRepository,
                    TrackedResultRepository trackedResultRepository) {
        this.trackedDomainRepository = trackedDomainRepository;
        this.interimBufferRepository = interimBufferRepository;
        this.trackedResultRepository = trackedResultRepository;
        lastTime = LocalDateTime.now().minusSeconds(60);
    }

    @Scheduled(fixedRate = 30_000, initialDelay = 30_000)//TODO вынести периодичность в проперти
    public void trackDomains() {
        LOGGER.info("start tracking domains...");

        List<String> domainRegexPatterns = trackedDomainRepository.getAll().stream()
                .filter(TrackedDomain::isActive)
                .map(TrackedDomain::getRegexPattern)
                .collect(toList());

        LOGGER.info("\ttime period is between {} and {}",
                getFormattedDateTime(lastTime),
                getFormattedDateTime(lastTime.plusSeconds(30)));
        LOGGER.info("\tlist of domain regex patterns which must be tracked: {}", domainRegexPatterns);

        List<TrackedResult> results = interimBufferRepository.getTrackedDomainsStatistic(domainRegexPatterns,
                lastTime,
                lastTime.plusSeconds(30));

        LOGGER.info("\tnumber of results: {}", results.size());

        lastTime = lastTime.plusSeconds(30);
        trackedResultRepository.saveAll(results);

        LOGGER.info("...stop tracking domains.\n");
    }
}
