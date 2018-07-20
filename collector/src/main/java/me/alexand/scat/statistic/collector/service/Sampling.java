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
    private final TransitionalBufferRepository transitionalBufferRepository;
    private final TrackedResultRepository trackedResultRepository;
    private LocalDateTime lastTime;

    @Autowired
    public Sampling(TrackedDomainRepository trackedDomainRepository,
                    TransitionalBufferRepository transitionalBufferRepository,
                    TrackedResultRepository trackedResultRepository) {
        this.trackedDomainRepository = trackedDomainRepository;
        this.transitionalBufferRepository = transitionalBufferRepository;
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

        List<TrackedResult> results = transitionalBufferRepository.getTrackedDomainsStatistic(domainRegexPatterns,
                lastTime,
                lastTime.plusSeconds(30));

        LOGGER.info("\tnumber of results: {}", results.size());

        lastTime = lastTime.plusSeconds(30);
        trackedResultRepository.saveAll(results);

        LOGGER.info("...stop tracking domains.\n");
    }
}
