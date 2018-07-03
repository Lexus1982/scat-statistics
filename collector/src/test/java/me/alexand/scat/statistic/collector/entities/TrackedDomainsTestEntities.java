package me.alexand.scat.statistic.collector.entities;

import me.alexand.scat.statistic.common.model.TrackedDomain;

import java.time.LocalDateTime;

/**
 * @author asidorov84@gmail.com
 */
public interface TrackedDomainsTestEntities {
    TrackedDomain TEST_VK_COM = TrackedDomain.builder()
            .regexPattern(".*vk\\.com$")
            .active(true)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    TrackedDomain TEST_MAIL_RU = TrackedDomain.builder()
            .regexPattern(".*mail\\.ru$")
            .active(true)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    TrackedDomain TEST_UPDATED_MAIL_RU = TrackedDomain.builder()
            .regexPattern(".*mail\\.ru$")
            .active(false)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();

    TrackedDomain TEST_OK_RU = TrackedDomain.builder()
            .regexPattern(".*ok\\.ru$")
            .active(true)
            .dateAdded(LocalDateTime.of(2018, 1, 1, 1, 1, 1))
            .build();
}
