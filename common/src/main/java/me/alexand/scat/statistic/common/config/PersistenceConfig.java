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

package me.alexand.scat.statistic.common.config;

import me.alexand.scat.statistic.common.repository.ClickCountRepository;
import me.alexand.scat.statistic.common.repository.DomainRegexRepository;
import me.alexand.scat.statistic.common.repository.TrackedDomainRequestsRepository;
import me.alexand.scat.statistic.common.repository.impl.ClickCountRepositoryJDBCImpl;
import me.alexand.scat.statistic.common.repository.impl.DomainRegexRepositoryJDBCImpl;
import me.alexand.scat.statistic.common.repository.impl.TrackedDomainRequestsRepositoryJDBCImpl;
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
 * Конфигурация контейнера Spring для работы с базой данных
 *
 * @author asidorov84@gmail.com
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfig {
    private static final Resource DB_INIT_SCRIPT = new ClassPathResource("initDB.sql");

    private final Environment env;

    public PersistenceConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public BasicDataSource persistenceDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("db.postgresql.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("db.postgresql.url"));
        dataSource.setUsername(env.getRequiredProperty("db.postgresql.username"));
        dataSource.setPassword(env.getRequiredProperty("db.postgresql.password"));
        dataSource.setInitialSize(Integer.parseInt(env.getRequiredProperty("db.postgresql.pool.init.size")));
        dataSource.setMaxTotal(Integer.parseInt(env.getRequiredProperty("db.postgresql.pool.max.size")));

        DatabasePopulatorUtils.execute(databasePopulator(DB_INIT_SCRIPT), dataSource);

        return dataSource;
    }

    @Bean
    public JdbcTemplate persistenceJdbcTemplate() {
        return new JdbcTemplate(persistenceDataSource());
    }

    @Bean("persistenceTM")
    public PlatformTransactionManager persistenceTransactionManager() {
        return new DataSourceTransactionManager(persistenceDataSource());
    }

    @Bean
    public ClickCountRepository clickCountRepository() {
        return new ClickCountRepositoryJDBCImpl(persistenceJdbcTemplate());
    }

    @Bean
    public DomainRegexRepository domainRegexRepository() {
        return new DomainRegexRepositoryJDBCImpl(persistenceJdbcTemplate());
    }

    @Bean
    public TrackedDomainRequestsRepository trackedDomainRequestsRepository() {
        return new TrackedDomainRequestsRepositoryJDBCImpl(persistenceJdbcTemplate());
    }
}