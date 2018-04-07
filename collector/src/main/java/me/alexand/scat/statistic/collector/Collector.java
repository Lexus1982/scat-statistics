package me.alexand.scat.statistic.collector;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author asidorov84@gmail.com
 */
public class Collector {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-app.xml").registerShutdownHook();
    }
}
