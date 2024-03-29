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

package me.alexand.scat.statistic.collector.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;
import static java.lang.Thread.NORM_PRIORITY;

public final class ThreadFactoryBuilder {
    private String name = "thread-%d";
    private boolean isDaemon = false;
    private int priority = NORM_PRIORITY;
    private final AtomicLong counter = new AtomicLong(0);

    public ThreadFactory build() {
        return runnable -> {
            Thread thread = new Thread(runnable);

            if (name != null && !name.isEmpty()) {
                thread.setName(String.format(name, counter.incrementAndGet()));
            }

            if (priority >= MIN_PRIORITY && priority <= MAX_PRIORITY) {
                thread.setPriority(priority);
            }

            thread.setDaemon(isDaemon);
            return thread;
        };
    }

    public ThreadFactoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ThreadFactoryBuilder daemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
        return this;
    }

    public ThreadFactoryBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }
}
