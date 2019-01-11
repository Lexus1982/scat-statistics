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

package me.alexand.scat.statistic.common.data;

import me.alexand.scat.statistic.common.entities.CollectorStatRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;

/**
 * @author asidorov84@gmail.com
 */
public interface CollectorStatRecordTestEntities {
    UUID TEST_INSTANCE_1_UUID = UUID.fromString("7fc93183-4760-4295-87d4-2f5ade5fef0b");
    String TEST_INSTANCE_1_ADDRESS = "localhost";
    int TEST_INSTANCE_1_PORT = 9996;
    LocalDateTime TEST_INSTANCE_1_STARTED = LocalDateTime.of(2019, 1, 1, 0, 0, 0);
    int TEST_INSTANCE_1_PERIOD = 30;

    UUID TEST_INSTANCE_2_UUID = UUID.fromString("7fc93183-4760-4295-87d4-2f5ade5fef0c");
    String TEST_INSTANCE_2_ADDRESS = "localhost";
    int TEST_INSTANCE_2_PORT = 9997;
    LocalDateTime TEST_INSTANCE_2_STARTED = LocalDateTime.of(2019, 1, 2, 0, 0, 0);
    int TEST_INSTANCE_2_PERIOD = 30;


    CollectorStatRecord TEST_INSTANCE_1_RECORD_FOR_SAVE = CollectorStatRecord.builder()
            .uuid(TEST_INSTANCE_1_UUID)
            .address(TEST_INSTANCE_1_ADDRESS)
            .port(TEST_INSTANCE_1_PORT)
            .started(TEST_INSTANCE_1_STARTED)
            .period(TEST_INSTANCE_1_PERIOD)
            .lastUpdated(LocalDateTime.of(2019, 1, 1, 0, 0, 30))
            .processorsThreadsCount(1)
            .packetsReceivedCount(300000)
            .packetsProcessedCount(300000)
            .packetsParseFailedCount(0)
            .inputQueueOverflowCount(0)
            .outputQueueOverflowCount(0)
            .recordsExportedCount(350000)
            .build();

    CollectorStatRecord TEST_INSTANCE_1_RECORD = CollectorStatRecord.builder()
            .uuid(TEST_INSTANCE_1_UUID)
            .address(TEST_INSTANCE_1_ADDRESS)
            .port(TEST_INSTANCE_1_PORT)
            .started(TEST_INSTANCE_1_STARTED)
            .period(TEST_INSTANCE_1_PERIOD)
            .lastUpdated(LocalDateTime.of(2019, 1, 1, 0, 1, 0))
            .processorsThreadsCount(1)
            .packetsReceivedCount(250000)
            .packetsProcessedCount(250000)
            .packetsParseFailedCount(0)
            .inputQueueOverflowCount(0)
            .outputQueueOverflowCount(0)
            .recordsExportedCount(350000)
            .build();

    CollectorStatRecord TEST_INSTANCE_2_RECORD = CollectorStatRecord.builder()
            .uuid(TEST_INSTANCE_2_UUID)
            .address(TEST_INSTANCE_2_ADDRESS)
            .port(TEST_INSTANCE_2_PORT)
            .started(TEST_INSTANCE_2_STARTED)
            .period(TEST_INSTANCE_2_PERIOD)
            .lastUpdated(LocalDateTime.of(2019, 1, 2, 0, 1, 0))
            .processorsThreadsCount(1)
            .packetsReceivedCount(250000)
            .packetsProcessedCount(250000)
            .packetsParseFailedCount(0)
            .inputQueueOverflowCount(0)
            .outputQueueOverflowCount(0)
            .recordsExportedCount(350000)
            .build();
    
    List<CollectorStatRecord> TEST_RECORDS = asList(TEST_INSTANCE_1_RECORD, TEST_INSTANCE_2_RECORD);
}
