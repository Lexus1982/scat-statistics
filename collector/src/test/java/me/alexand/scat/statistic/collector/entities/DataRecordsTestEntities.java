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

package me.alexand.scat.statistic.collector.entities;

import me.alexand.scat.statistic.collector.model.IPFIXDataRecord;
import me.alexand.scat.statistic.collector.model.IPFIXFieldValue;

import java.math.BigInteger;

import static java.util.Arrays.asList;
import static me.alexand.scat.statistic.collector.model.IANAAbstractDataTypes.*;
import static me.alexand.scat.statistic.collector.model.TemplateType.CS_REQ;

/**
 * @author asidorov84@gmail.com
 */
public interface DataRecordsTestEntities {
    IPFIXDataRecord CS_REQ_DATA_RECORD_1 = IPFIXDataRecord.builder()
            .type(CS_REQ)
            .fieldValues(asList(
                    IPFIXFieldValue.builder()
                            .name("timestamp")
                            .value("2018-04-01 17:06:09.000000")
                            .type(DATE_TIME_SECONDS)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("login")
                            .value("begov_ra@setka.ru")
                            .type(STRING)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("ipSrc")
                            .value("92.246.155.49")
                            .type(IPV4_ADDRESS)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("ipDst")
                            .value("217.69.139.42")
                            .type(IPV4_ADDRESS)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("domain")
                            .value("rs.mail.ru")
                            .type(STRING)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("path")
                            .value("/d28935394.gif?sz=6&rnd=809692830&ts=1522591507&sz=6")
                            .type(STRING)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("refer")
                            .value("http://my.mail.ru/apps/652162?ref=lmnu%20%20%D0%B0%D0%B2%D0%B8%D1%82%D0%B0")
                            .type(STRING)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("userAgent")
                            .value("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36 u01-04")
                            .type(STRING)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("cookie")
                            .value("p=f10AACdXHQAA; mrcu=F097597D7EDB5F54B15D2E03DDB0; yandexuid=3625663611505809386; _ym_uid=1506853011562024413; searchuid=2824119071501366878; t_0=1; i=AQCxpcBaEAATAAgQBVoIAWYKAW4KAXILAVoMARQACEwZAAABIQABdgABgAABggABhwABjgABFgEBJAEBLAEBLgEBOQEBPAEBQwEBRgEBSAEBWwEBdQEBdwEBfgEBhAEBiAEBvwEBwgEBb04BfwAIDQQOAAEUAAEWAAExAAGAAAkBAYEACgQXCr4HxgAIBAEBAAHJAAUCAQC7AQgEAQQAAU4CCDEQ4wAB6AABBwEBMQEBWwEBXQEBYgEBiwEBlQEBFAIBGAIBuAMBnQQBkAUBCAYBIgcBYwIIlzLaAAHdAAHiAAHjAAHlAAHoAAHpAAH8AAEHAQExAQFBAQFEAQFGAQFXAQFaAQFbAQFcAQFdAQFiAQF9AQGEAQGHAQGLAQGMAQGVAQGjAQGpAQGqAQG+AQHxAQHzAQH3AQH6AQH7AQEUAgEYAgG6AgG4AwEpBAGdBAG2BAG6BAHDBAHGBAGQBQEIBgFHBgGEBgHrBgEiBwFkAggNBEsAAmEABBMIBIAIAosCCCILUQABVAABXQABYAABYwABbwABeAABhwABjQABrQAB+wABjQIIIgtRAAFUAAFdAAFgAAFjAAFvAAF4AAGHAAGNAAGtAAH7AAGRAgglDAEAAQQAAQcAAQkAAQ8AAREAARIAARMAARQAARoAASAAAS8AAb0HCAQBrRUBKQkIKA3ZAgGoAwG9AwG+AwG/AwHAAwHBAwH6AwGBBAGFBAGNBAGRBAG7BAE=; b=1UQZAJCYfVwAdrOaBlnZI4NclFWB+9nWACl7ZFBYJjOAWwQnvJXJDNQP3IYo2yNC+YwWobuADBGvXEMoy69BLCjCMP5kjgiP7lGE5y0tQnwdEqIWSw1x5BeDOGfZQQQAADA6TYYQQx8FiIKCI20oSW1qBBEkB0MYRXIwBAAA; c=w6zAWgIAAEpwAQAiAAAAAwC8PIIA; t=obLD1AAAAAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAABdABcKvgcA; _ga=GA1.2.867135752.1501396700; _gid=GA1.2.71776309.1522393619; s=fver=18; VID=2MoTa405ykXh0000070E14nh:544703798729::; Mpop=1522591408:7b70410f6502587f190502190805001b031a01034966535c465d0d09050a1e0b08051e5f5856584a470f0d1658505d5b174345:fanizv88@mail.ru:")
                            .type(STRING)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("sessionID")
                            .value(new BigInteger("701101205089978138"))
                            .type(UNSIGNED64)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("locked")
                            .value(0L)
                            .type(UNSIGNED64)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("hostType")
                            .value(1)
                            .type(UNSIGNED8)
                            .build(),
                    IPFIXFieldValue.builder()
                            .name("method")
                            .value(1)
                            .type(UNSIGNED8)
                            .build()
            ))
            .build();
}
