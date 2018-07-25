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

import static java.lang.System.arraycopy;

/**
 * Вспомогательные методы для конвертирования наборов байт,
 * интерпретируемые как unsigned, в другие типы данных
 *
 * @author asidorov84@gmail.com
 */

public interface BytesConvertUtils {
    static int oneByteToInt(byte[] array, int offset) {
        return ((int) array[offset]) & 0xFF;
    }

    static int oneByteToInt(byte value) {
        return ((int) value) & 0xFF;
    }

    static int twoBytesToInt(byte[] array) {
        return twoBytesToInt(array, 0);
    }

    static int twoBytesToInt(byte[] array, int offset) {
        int result = 0;
        result |= (((int) array[offset]) & 0xFF) << 8;
        result |= ((int) array[offset + 1]) & 0xFF;
        return result;
    }

    static long fourBytesToLong(byte[] array) {
        return fourBytesToLong(array, 0);
    }

    static long fourBytesToLong(byte[] array, int offset) {
        long result = 0;
        result |= (((int) array[offset]) & 0xFF) << 24;
        result |= (((int) array[offset + 1]) & 0xFF) << 16;
        result |= (((int) array[offset + 2]) & 0xFF) << 8;
        result |= ((int) array[offset + 3]) & 0xFF;
        return result & 0xffffffffL;
    }

    static BigInteger eightBytesToBigInteger(byte[] array) {
        return new BigInteger(1, array);
    }

    static BigInteger eightBytesToBigInteger(byte[] array, int offset) {
        byte[] bigIntegerBuf = new byte[8];
        arraycopy(array, offset, bigIntegerBuf, 0, 8);
        return new BigInteger(1, bigIntegerBuf);
    }

    static boolean isHighBitSet(byte octet) {
        return (octet & 0xff) >>> 7 == 1;
    }

    static String bytesToString(byte[] array, int offset, int length) {
        if (length <= 0) {
            return "";
        }

        if (offset + length > array.length) {
            length = array.length - offset;
        }

        byte[] strBuffer = new byte[length];
        arraycopy(array, offset, strBuffer, 0, length);
        return new String(strBuffer);
    }

    static String bytesToString(byte[] array) {
        return new String(array);
    }

    static String fourBytesToIPv4(byte[] array, int offset) {
        return String.format("%d.%d.%d.%d",
                oneByteToInt(array[offset]),
                oneByteToInt(array[offset + 1]),
                oneByteToInt(array[offset + 2]),
                oneByteToInt(array[offset + 3]));
    }

    static String fourBytesToIPv4(byte[] array) {
        return fourBytesToIPv4(array, 0);
    }
}