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

/**
 * @author asidorov84@gmail.com
 */
public interface RawPacketsEntities {
    byte[] PACKET_WITH_INVALID_LENGTH = {
            0x00, 0x0a, 0x00, (byte) 0xa6, 0x59, (byte) 0xe4, (byte) 0x8f, 0x51,
            0x02, (byte) 0xce, 0x58, 0x30, 0x00, 0x00, 0x00, 0x01,
            0x01, 0x00, 0x00, (byte) 0x96, 0x59, (byte) 0xe4, (byte) 0x8f, 0x51
    };

    byte[] RAW_TEMPLATES_PAYLOAD = {
            0x00, 0x0a, 0x00, (byte) 0xc8, 0x5a, (byte) 0xe5, (byte) 0xa0, 0x75,
            0x25, (byte) 0xf6, 0x56, 0x04, 0x00, 0x00, 0x00, 0x01,
            0x00, 0x02, 0x00, 0x70, 0x01, 0x01, 0x00, 0x0d,
            (byte) 0x83, (byte) 0xe9, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xea, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xeb, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xec, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xed, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xee, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xef, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf0, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf1, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x87, (byte) 0xd0, 0x00, 0x08, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf2, 0x00, 0x08, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf3, 0x00, 0x01, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf4, 0x00, 0x01, 0x00, 0x00, (byte) 0xab, 0x2f,
            0x00, 0x02, 0x00, 0x48, 0x01, 0x02, 0x00, 0x08,
            (byte) 0x83, (byte) 0xe9, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xea, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xeb, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xec, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xfc, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xfd, 0x00, 0x08, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xfe, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x87, (byte) 0xd0, 0x00, 0x08, 0x00, 0x00, (byte) 0xab, 0x2f
    };


    byte[] RAW_CS_REQ_DATA_PAYLOAD = {
            0x00, 0x0a, 0x02, 0x35, 0x59, (byte) 0xe4, (byte) 0x8f, 0x62,
            0x02, (byte) 0xcf, 0x26, 0x46, 0x00, 0x00, 0x00, 0x01,
            0x01, 0x00, 0x02, 0x25, 0x59, (byte) 0xe4, (byte) 0x8f, 0x62,
            (byte) 0xff, 0x00, 0x00, 0x1f, (byte) 0xaa, (byte) 0xa8, (byte) 0xab, (byte) 0xd9,
            0x0c, 0x0f, 0x60, (byte) 0xff, 0x00, 0x13, 0x79, 0x62,
            0x6f, 0x73, 0x73, 0x2e, 0x79, 0x61, 0x68, 0x6f,
            0x6f, 0x61, 0x70, 0x69, 0x73, 0x2e, 0x63, 0x6f,
            0x6d, (byte) 0xff, 0x01, (byte) 0xde, 0x2f, 0x79, 0x73, 0x65,
            0x61, 0x72, 0x63, 0x68, 0x2f, 0x77, 0x65, 0x62,
            0x2c, 0x69, 0x6d, 0x61, 0x67, 0x65, 0x73, 0x3f,
            0x61, 0x62, 0x73, 0x74, 0x72, 0x61, 0x63, 0x74,
            0x3d, 0x6c, 0x6f, 0x6e, 0x67, 0x26, 0x66, 0x6f,
            0x72, 0x6d, 0x61, 0x74, 0x3d, 0x6a, 0x73, 0x6f,
            0x6e, 0x26, 0x69, 0x6d, 0x61, 0x67, 0x65, 0x73,
            0x2e, 0x63, 0x6f, 0x75, 0x6e, 0x74, 0x3d, 0x35,
            0x30, 0x26, 0x69, 0x6d, 0x61, 0x67, 0x65, 0x73,
            0x2e, 0x73, 0x74, 0x61, 0x72, 0x74, 0x3d, 0x30,
            0x26, 0x6d, 0x61, 0x72, 0x6b, 0x65, 0x74, 0x3d,
            0x65, 0x6e, 0x2d, 0x75, 0x73, 0x26, 0x71, 0x3d,
            0x4b, 0x65, 0x6e, 0x6d, 0x6f, 0x72, 0x65, 0x25,
            0x32, 0x30, 0x45, 0x6c, 0x69, 0x74, 0x65, 0x25,
            0x32, 0x30, 0x55, 0x70, 0x72, 0x69, 0x67, 0x68,
            0x74, 0x25, 0x32, 0x30, 0x56, 0x61, 0x63, 0x75,
            0x75, 0x6d, 0x25, 0x32, 0x30, 0x2d, 0x74, 0x6f,
            0x72, 0x72, 0x65, 0x6e, 0x74, 0x25, 0x32, 0x30,
            0x2d, 0x25, 0x32, 0x32, 0x73, 0x65, 0x78, 0x25,
            0x32, 0x32, 0x25, 0x32, 0x30, 0x2d, 0x25, 0x32,
            0x32, 0x61, 0x6e, 0x61, 0x6c, 0x25, 0x32, 0x32,
            0x25, 0x32, 0x30, 0x2d, 0x25, 0x32, 0x32, 0x70,
            0x6f, 0x72, 0x6e, 0x25, 0x32, 0x32, 0x25, 0x32,
            0x30, 0x2d, 0x25, 0x32, 0x32, 0x62, 0x64, 0x73,
            0x6d, 0x25, 0x32, 0x32, 0x25, 0x32, 0x30, 0x2d,
            0x25, 0x32, 0x32, 0x76, 0x61, 0x67, 0x69, 0x6e,
            0x25, 0x32, 0x32, 0x25, 0x32, 0x30, 0x2d, 0x25,
            0x32, 0x32, 0x70, 0x65, 0x6e, 0x69, 0x73, 0x25,
            0x32, 0x32, 0x25, 0x32, 0x30, 0x2d, 0x25, 0x32,
            0x32, 0x61, 0x6e, 0x69, 0x6d, 0x65, 0x25, 0x32,
            0x32, 0x25, 0x32, 0x30, 0x2d, 0x25, 0x32, 0x32,
            0x63, 0x61, 0x73, 0x75, 0x61, 0x6c, 0x25, 0x32,
            0x30, 0x65, 0x6e, 0x63, 0x6f, 0x75, 0x6e, 0x74,
            0x65, 0x72, 0x73, 0x25, 0x32, 0x32, 0x25, 0x32,
            0x30, 0x2d, 0x25, 0x32, 0x32, 0x63, 0x68, 0x69,
            0x63, 0x6b, 0x73, 0x25, 0x32, 0x32, 0x25, 0x32,
            0x30, 0x2d, 0x25, 0x32, 0x32, 0x64, 0x61, 0x74,
            0x69, 0x6e, 0x67, 0x25, 0x32, 0x32, 0x25, 0x32,
            0x30, 0x2d, 0x25, 0x32, 0x32, 0x6b, 0x69, 0x6e,
            0x6b, 0x79, 0x25, 0x32, 0x32, 0x25, 0x32, 0x30,
            0x2d, 0x25, 0x32, 0x32, 0x6e, 0x61, 0x6b, 0x65,
            0x64, 0x25, 0x32, 0x32, 0x25, 0x32, 0x30, 0x2d,
            0x25, 0x32, 0x32, 0x6e, 0x75, 0x64, 0x65, 0x25,
            0x32, 0x32, 0x25, 0x32, 0x30, 0x2d, 0x25, 0x32,
            0x32, 0x70, 0x65, 0x72, 0x73, 0x6f, 0x6e, 0x61,
            0x6c, 0x73, 0x25, 0x32, 0x32, 0x25, 0x32, 0x30,
            0x2d, 0x25, 0x32, 0x32, 0x70, 0x6f, 0x72, 0x6e,
            0x25, 0x32, 0x32, 0x25, 0x32, 0x30, 0x2d, 0x25,
            0x32, 0x32, 0x70, 0x6f, 0x72, 0x6e, 0x6f, 0x25,
            0x32, 0x32, 0x25, 0x32, 0x30, 0x2d, 0x25, 0x32,
            0x32, 0x73, 0x65, 0x78, 0x25, 0x32, 0x32, 0x25,
            0x32, 0x30, 0x2d, 0x25, 0x32, 0x32, 0x78, 0x2d,
            0x72, 0x61, 0x74, 0x65, 0x64, 0x25, 0x32, 0x32,
            0x25, 0x32, 0x30, 0x2d, 0x25, 0x32, 0x32, 0x78,
            0x78, 0x78, 0x25, 0x32, 0x32, 0x26, 0x73, 0x69,
            0x74, 0x65, 0x73, 0x3d, 0x77, 0x69, 0x6b, 0x69,
            0x70, 0x65, 0x64, 0x69, 0x61, 0x2e, 0x6f, 0x72,
            0x67, 0x26, 0x77, 0x65, 0x62, 0x2e, 0x63, 0x6f,
            0x75, 0x6e, 0x74, 0x3d, 0x35, 0x30, 0x26, 0x77,
            0x65, 0x62, 0x2e, 0x73, 0x74, 0x61, 0x72, 0x74,
            0x3d, 0x30, (byte) 0xff, 0x00, 0x00, (byte) 0xff, 0x00, 0x00,
            (byte) 0xff, 0x00, 0x00, 0x04, 0x2d, (byte) 0xae, (byte) 0xdc, (byte) 0xb9,
            0x18, (byte) 0xc1, 0x22, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x01, 0x01
    };

    byte[] RAW_CS_REQ_TEMPLATE = {
            0x00, 0x0a, 0x00, (byte) 0x80, 0x59, (byte) 0xe4, (byte) 0x8f, 0x5c,
            0x02, (byte) 0xce, (byte) 0xd3, 0x3a, 0x00, 0x00, 0x00, 0x01,
            0x00, 0x02, 0x00, 0x70, 0x01, 0x00, 0x00, 0x0d,
            (byte) 0x83, (byte) 0xe9, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xea, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xeb, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xec, 0x00, 0x04, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xed, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xee, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xef, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf0, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf1, (byte) 0xff, (byte) 0xff, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x87, (byte) 0xd0, 0x00, 0x08, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf2, 0x00, 0x08, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf3, 0x00, 0x01, 0x00, 0x00, (byte) 0xab, 0x2f,
            (byte) 0x83, (byte) 0xf4, 0x00, 0x01, 0x00, 0x00, (byte) 0xab, 0x2f
    };

    byte[] RAW_GENERIC_TEMPLATE = {
            0x00, 0x0a, 0x00, 0x7c, 0x5a, (byte) 0xe6, 0x2e, (byte) 0xf1,
            (byte) 0xd1, 0x22, 0x71, 0x20, 0x00, 0x00, 0x00, 0x02,
            0x00, 0x02, 0x00, 0x6c, 0x01, 0x00, 0x00, 0x15,
            0x00, 0x01, 0x00, 0x08, 0x00, 0x02, 0x00, 0x08,
            0x00, 0x04, 0x00, 0x01, 0x00, 0x05, 0x00, 0x01,
            0x00, 0x07, 0x00, 0x02, 0x00, 0x08, 0x00, 0x04,
            0x00, 0x0b, 0x00, 0x02, 0x00, 0x0c, 0x00, 0x04,
            0x00, 0x10, 0x00, 0x04, 0x00, 0x11, 0x00, 0x04,
            0x00, (byte) 0x98, 0x00, 0x08, 0x00, (byte) 0x99, 0x00, 0x08,
            0x00, 0x0a, 0x00, 0x02, 0x00, 0x0e, 0x00, 0x02,
            0x00, 0x3c, 0x00, 0x01, (byte) 0x87, (byte) 0xd0, 0x00, 0x08,
            0x00, 0x00, (byte) 0xab, 0x2f, (byte) 0x87, (byte) 0xd1, (byte) 0xff, (byte) 0xff,
            0x00, 0x00, (byte) 0xab, 0x2f, (byte) 0x87, (byte) 0xd2, 0x00, 0x02,
            0x00, 0x00, (byte) 0xab, 0x2f, (byte) 0x87, (byte) 0xd3, (byte) 0xff, (byte) 0xff,
            0x00, 0x00, (byte) 0xab, 0x2f, 0x00, (byte) 0xe1, 0x00, 0x04,
            0x00, (byte) 0xe3, 0x00, 0x02
    };


    byte[] RAW_GENERIC_DATA = {
            0x00, 0x0a, 0x00, 0x7a, 0x5a, (byte) 0xe6, 0x2f, (byte) 0xeb,
            (byte) 0xd1, (byte) 0x80, 0x32, 0x37, 0x00, 0x00, 0x00, 0x02,
            0x01, 0x00, 0x00, 0x6a, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x02, 0x72, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x02, 0x11, 0x07, (byte) 0xe7, (byte) 0xb5,
            0x5f, 0x19, 0x12, (byte) 0xce, (byte) 0xc0, 0x0d, 0x1f, (byte) 0xaa,
            (byte) 0xaa, 0x7e, 0x00, 0x00, 0x20, (byte) 0xd2, 0x00, 0x00,
            0x7b, (byte) 0xec, 0x00, 0x00, 0x01, 0x62, 0x13, 0x2b,
            0x09, 0x2a, 0x00, 0x00, 0x01, 0x63, 0x13, 0x2b,
            0x0f, 0x22, 0x00, 0x02, 0x00, 0x01, 0x04, 0x05,
            (byte) 0x90, (byte) 0xff, 0x6c, 0x55, 0x34, (byte) 0x9f, (byte) 0xde, (byte) 0xff,
            0x00, 0x00, 0x69, (byte) 0x98, (byte) 0xff, 0x00, 0x15, 0x74,
            0x72, 0x65, 0x73, 0x6b, 0x6f, 0x76, 0x61, 0x5f,
            0x79, 0x75, 0x76, 0x40, 0x73, 0x65, 0x74, 0x6b,
            0x61, 0x2e, 0x72, 0x75, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00
    };
}
