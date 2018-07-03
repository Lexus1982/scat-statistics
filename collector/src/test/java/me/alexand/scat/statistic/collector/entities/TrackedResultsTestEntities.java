package me.alexand.scat.statistic.collector.entities;

import me.alexand.scat.statistic.common.model.TrackedResult;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author asidorov84@gmail.com
 */
public interface TrackedResultsTestEntities {
    TrackedResult TEST = TrackedResult.builder()
            .regexPattern(".*vk\\.com$")
            .address("176.221.0.224")
            .login("polyakov_al@setka.ru")
            .firstTime(LocalDateTime.of(2018, 4, 1, 17, 6, 17))
            .lastTime(LocalDateTime.of(2018, 4, 1, 17, 6, 19))
            .count(BigInteger.valueOf(3))
            .build();
}
