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

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static me.alexand.scat.statistic.common.utils.DBUtils.databasePopulator;

/**
 * @author asidorov84@gmail.com
 */
@Configuration
@EnableTransactionManagement
public class TransitionalBufferConfig {
    private static final Resource BUFFER_INIT_SCRIPT = new ClassPathResource("initBuffer.sql");

    private final Environment env;

    public TransitionalBufferConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public BasicDataSource bufferDataSource() {
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

    @Bean("bufferJDBCTemplate")
    public JdbcTemplate bufferJdbcTemplate() {
        return new JdbcTemplate(bufferDataSource());
    }

    @Bean("bufferTM")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(bufferDataSource());
    }
}