package me.alexand.scat.statistic.collector.service.impls;

import me.alexand.scat.statistic.collector.repository.InterimBufferRepository;
import me.alexand.scat.statistic.collector.service.Sampling;
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

/**
 * @author asidorov84@gmail.com
 */

@Service
public class SamplingImpl implements Sampling {
    private static final Logger LOGGER = LoggerFactory.getLogger(SamplingImpl.class);
    private final TrackedDomainRepository trackedDomainRepository;
    private final InterimBufferRepository interimBufferRepository;
    private final TrackedResultRepository trackedResultRepository;
    private LocalDateTime lastTime;

    @Autowired
    public SamplingImpl(TrackedDomainRepository trackedDomainRepository,
                        InterimBufferRepository interimBufferRepository,
                        TrackedResultRepository trackedResultRepository) {
        this.trackedDomainRepository = trackedDomainRepository;
        this.interimBufferRepository = interimBufferRepository;
        this.trackedResultRepository = trackedResultRepository;
        lastTime = LocalDateTime.now().minusSeconds(60);
    }

    @Override
    @Scheduled(fixedRate = 30_000)//TODO вынести периодичность в проперти
    public void trackDomains() {
        LOGGER.info("start tracking domains...");

        List<String> domainRegexPatterns = trackedDomainRepository.getAll().stream()
                .filter(TrackedDomain::isActive)
                .map(TrackedDomain::getRegexPattern)
                .collect(toList());

        LOGGER.info("time period is between {} and {}", lastTime, lastTime.plusSeconds(30));
        LOGGER.info("list of domain regex patterns which must be tracked: {}", domainRegexPatterns);

        List<TrackedResult> results = interimBufferRepository.getTrackedDomainsStatistic(domainRegexPatterns,
                lastTime,
                lastTime.plusSeconds(30));

        LOGGER.info("number of results: {}", results.size());

        lastTime = lastTime.plusSeconds(30);
        trackedResultRepository.saveAll(results);

        LOGGER.info("...stop tracking domains.");
    }
}
