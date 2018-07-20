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

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.arraycopy;

/**
 * @author asidorov84@gmail.com
 */

public interface BytesConvertUtils {
    //TODO убрать лишние

    static int oneByteToInt(byte[] buffer, int offset) {
        return buffer[offset] & 0xff;
    }

    static int oneByteToInt(byte bytes) {
        return bytes & 0xff;
    }

    static int twoBytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
    }

    static long fourBytesToLong(byte[] bytes) {
        long result = 0;

        for (int i = 0; i < 4; i++) {
            result |= ((bytes[i] & 0xff) << 8 * (3 - i));
        }

        return result & 0xffffffffL;
    }

    static BigInteger eightBytesToBigInteger(byte[] buffer, int offset) {
        byte[] bigIntegerBuf = new byte[8];
        arraycopy(buffer, offset, bigIntegerBuf, 0, 8);
        return new BigInteger(1, bigIntegerBuf);
    }

    static BigInteger eightBytesToBigInteger(byte[] bytes) {
        return new BigInteger(1, bytes);
    }

    static boolean isHighBitSet(byte octet) {
        return (octet & 0xff) >>> 7 == 1;
    }

    static String bytesToString(byte[] buffer, int offset, int length) {
        if (length == 0) {
            return "";
        }

        byte[] strBuffer = new byte[length];
        arraycopy(buffer, offset, strBuffer, 0, length);
        return new String(strBuffer);
    }

    static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }

    static InetAddress fourBytesToIPv4(byte[] buffer, int offset) throws UnknownHostException {
        byte[] rawOctets = new byte[4];
        System.arraycopy(buffer, offset, rawOctets, 0, 4);
        return InetAddress.getByAddress(rawOctets);
    }

    static InetAddress fourBytesToIPv4(byte[] bytes) throws UnknownHostException {
        return InetAddress.getByAddress(bytes);
    }
}