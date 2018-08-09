/*
 * Copyright 2018 Alexander Sidorov (asidorov84@gmail.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.alexand.scat.statistic.collector.service;

import me.alexand.scat.statistic.collector.repository.TransitionalBufferRepository;
import me.alexand.scat.statistic.common.entities.ClickCount;
import me.alexand.scat.statistic.common.entities.DomainRegex;
import me.alexand.scat.statistic.common.entities.TrackedDomainRequests;
import me.alexand.scat.statistic.common.repository.ClickCountRepository;
import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import me.alexand.scat.statistic.common.repository.TrackedDomainRequestsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static me.alexand.scat.statistic.collector.utils.Constants.SAMPLING_RUN_FREQUENCY;
import static me.alexand.scat.statistic.collector.utils.DateTimeUtils.getFormattedDateTime;

/**
 * @author asidorov84@gmail.com
 */

@Service
public class Sampling {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sampling.class);
    private final DomainRegexRepository domainRegexRepository;
    private final TransitionalBufferRepository transitionalBufferRepository;
    private final TrackedDomainRequestsRepository trackedDomainRequestsRepository;
    private final ClickCountRepository clickCountRepository;
    private LocalDateTime lastTimeTrackDomains;
    private LocalDateTime lastTimeCountClicks;

    @Autowired
    public Sampling(DomainRegexRepository domainRegexRepository,
                    TransitionalBufferRepository transitionalBufferRepository,
                    TrackedDomainRequestsRepository trackedDomainRequestsRepository,
                    ClickCountRepository clickCountRepository) {
        this.domainRegexRepository = domainRegexRepository;
        this.transitionalBufferRepository = transitionalBufferRepository;
        this.trackedDomainRequestsRepository = trackedDomainRequestsRepository;
        this.clickCountRepository = clickCountRepository;
        lastTimeTrackDomains = LocalDateTime.now().minusSeconds(60);
        lastTimeCountClicks = LocalDateTime.now().minusSeconds(60);
    }

    @Scheduled(fixedRate = SAMPLING_RUN_FREQUENCY, initialDelay = SAMPLING_RUN_FREQUENCY)
    public void trackDomains() {
        LOGGER.info("start tracking domains...");
        LocalDateTime endDateTime = lastTimeTrackDomains.plusSeconds(30);

        List<String> domainRegexPatterns = domainRegexRepository.getAll().stream()
                .map(DomainRegex::getPattern)
                .collect(toList());

        LOGGER.info("\ttime period for tracking domains is between {} and {}",
                getFormattedDateTime(lastTimeTrackDomains),
                getFormattedDateTime(endDateTime));
        LOGGER.info("\tlist of domain regex patterns which must be tracked: {}", domainRegexPatterns);

        List<TrackedDomainRequests> results = transitionalBufferRepository.getTrackedDomainRequests(domainRegexPatterns,
                lastTimeTrackDomains,
                endDateTime);

        LOGGER.info("\tnumber of tracking domain results: {}", results.size());

        lastTimeTrackDomains = endDateTime;
        trackedDomainRequestsRepository.saveAll(results);

        LOGGER.info("...stop tracking domains.\n");
    }

    @Scheduled(fixedRate = SAMPLING_RUN_FREQUENCY, initialDelay = SAMPLING_RUN_FREQUENCY + 5000)
    public void countClicks() {
        LOGGER.info("start counting clicks...");
        LocalDateTime endDateTime = lastTimeCountClicks.plusSeconds(30);

        LOGGER.info("\ttime period for counting clicks is between {} and {}",
                getFormattedDateTime(lastTimeCountClicks),
                getFormattedDateTime(endDateTime));

        List<ClickCount> clickCounts = transitionalBufferRepository.getClickCount(lastTimeCountClicks, endDateTime);
        LOGGER.info("\tnumber of counts: {}", clickCounts.size());

        lastTimeCountClicks = endDateTime;
        
        try {
            clickCountRepository.saveAll(clickCounts);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }
        

        LOGGER.info("...stop counting clicks\n");
    }
}
