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

package me.alexand.scat.statistic.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.alexand.scat.statistic.api.utils.JacksonObjectMapper;
import me.alexand.scat.statistic.api.utils.SortingAndPaginationArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web-контекст приложения
 *
 * @author asidorov84@gmail.com
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "me.alexand.scat.statistic.api.controllers")
public class WebConfig implements WebMvcConfigurer {
    @Bean
    ObjectMapper objectMapper() {
        return JacksonObjectMapper.getObjectMapper();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SortingAndPaginationArgumentResolver());
    }
}
