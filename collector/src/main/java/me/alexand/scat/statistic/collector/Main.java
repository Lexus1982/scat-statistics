package me.alexand.scat.statistic.collector;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Точка входа в приложение
 * @author asidorov84@gmail.com
 */
public class Main {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:spring-app.xml").registerShutdownHook();
    }
}
