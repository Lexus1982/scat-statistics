package me.alexand.scat.statistic.collector.service;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

/**
 * @author asidorov84@gmail.com
 */

@Component
public abstract class IPFIXMessageProcessorFactory {
    @Lookup
    public abstract IPFIXMessageProcessor getProcessor();
}