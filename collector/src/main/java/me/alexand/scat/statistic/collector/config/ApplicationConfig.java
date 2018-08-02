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

package me.alexand.scat.statistic.collector.config;

import me.alexand.scat.statistic.common.config.CommonConfig;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static me.alexand.scat.statistic.common.utils.DBUtils.databasePopulator;


/**
 * Конфигурация контейнера Spring Framework
 *
 * @author asidorov84@gmail.com
 */
@Configuration
@PropertySource("app.properties")
@PropertySource(value = "file:${conf.dir}/collector.cfg", ignoreResourceNotFound = true)
@Import(CommonConfig.class)
@ComponentScan("me.alexand.scat.statistic.collector")
@EnableTransactionManagement
@EnableScheduling
public class ApplicationConfig {
    private static final Resource BUFFER_INIT_SCRIPT = new ClassPathResource("initBuffer.sql");

    private final Environment env;

    @Autowired
    public ApplicationConfig(Environment env) {
        this.env = env;
    }

    @Bean
    BasicDataSource hsqldbDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("db.hsqldb.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("db.hsqldb.url"));
        dataSource.setUsername(env.getRequiredProperty("db.hsqldb.username"));
        dataSource.setPassword(env.getRequiredProperty("db.hsqldb.password"));
        dataSource.setInitialSize(Integer.parseInt(env.getRequiredProperty("processors.count")));
        dataSource.setMaxTotal(Integer.parseInt(env.getRequiredProperty("processors.count")));

        DatabasePopulatorUtils.execute(databasePopulator(BUFFER_INIT_SCRIPT), dataSource);

        return dataSource;
    }

    @Bean("bufferTM")
    DataSourceTransactionManager hsqldbTransactionManager() {
        return new DataSourceTransactionManager(hsqldbDataSource());
    }

    @Bean("bufferJDBCTemplate")
    JdbcTemplate hsqldbJdbcTemplate() {
        return new JdbcTemplate(hsqldbDataSource());
    }

    //Scheduler

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(4);
        threadPoolTaskScheduler.setThreadNamePrefix("periodical-scheduler");
        return threadPoolTaskScheduler;
    }
}