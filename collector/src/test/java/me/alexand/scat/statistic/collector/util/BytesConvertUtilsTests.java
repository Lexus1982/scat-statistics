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

package me.alexand.scat.statistic.collector.util;

import org.junit.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.*;
import static org.junit.Assert.*;

/**
 * Тесты конвертеров
 *
 * @author asidorov84@gmail.com
 */

public class BytesConvertUtilsTests {
    private static final int UNSIGNED_TWO_BYTES_DATA = 0x7F_FF;
    private static final int SIGNED_TWO_BYTES_DATA = 0xFF_FF;

    private static final long UNSIGNED_FOUR_BYTES_DATA = 0x7F_FF_FF_FFL;
    private static final long SIGNED_FOUR_BYTES_DATA = 0xFF_FF_FF_FFL;

    private static final String TEST_STRING = "Test string";

    @Test
    public void testConvertOneByteToInt() {
        byte[] testBytesSet = {(byte) 0xff, (byte) 0x7f, (byte) 0x3f, (byte) 0x00};
        int[] expectedValues = {255, 127, 63, 0};

        for (int i = 0; i < testBytesSet.length; i++) {
            assertEquals(expectedValues[i], oneByteToInt(testBytesSet[i]));
        }

        for (int i = 0; i < testBytesSet.length; i++) {
            assertEquals(expectedValues[i], oneByteToInt(testBytesSet, i));
        }
    }

    @Test
    public void testConvertTwoBytesToInt() {
        byte[] payload = ByteBuffer
                .allocate(Short.BYTES)
                .putShort((short) UNSIGNED_TWO_BYTES_DATA)
                .array();

        assertEquals(UNSIGNED_TWO_BYTES_DATA, twoBytesToInt(payload));
        assertEquals(UNSIGNED_TWO_BYTES_DATA, twoBytesToInt(payload, 0));

        payload = ByteBuffer
                .allocate(Short.BYTES)
                .putShort((short) SIGNED_TWO_BYTES_DATA)
                .array();

        assertEquals(SIGNED_TWO_BYTES_DATA, twoBytesToInt(payload));
        assertEquals(SIGNED_TWO_BYTES_DATA, twoBytesToInt(payload, 0));
    }

    @Test
    public void testConvertFourBytesToLong() {
        byte[] payload = ByteBuffer
                .allocate(Integer.BYTES)
                .putInt((int) UNSIGNED_FOUR_BYTES_DATA)
                .array();

        assertEquals(UNSIGNED_FOUR_BYTES_DATA, fourBytesToLong(payload));
        assertEquals(UNSIGNED_FOUR_BYTES_DATA, fourBytesToLong(payload, 0));

        payload = ByteBuffer
                .allocate(Integer.BYTES)
                .putInt((int) SIGNED_FOUR_BYTES_DATA)
                .array();

        assertEquals(SIGNED_FOUR_BYTES_DATA, fourBytesToLong(payload));
        assertEquals(SIGNED_FOUR_BYTES_DATA, fourBytesToLong(payload, 0));
    }

    @Test
    public void testConvertEightBytesToBigInteger() {
        byte[] bytes = {0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};

        assertEquals(new BigInteger("9223372036854775807"), eightBytesToBigInteger(bytes));
        assertEquals(new BigInteger("9223372036854775807"), eightBytesToBigInteger(bytes, 0));
    }

    @Test
    public void testIsHighBitSet() {
        assertTrue(isHighBitSet((byte) 0xff));
        assertFalse(isHighBitSet((byte) 0x7f));
    }

    @Test
    public void testConvertBytesToString() {
        byte[] array = TEST_STRING.getBytes();
        assertEquals(TEST_STRING, bytesToString(array));
        assertEquals(TEST_STRING, bytesToString(array, 0, array.length));
        assertEquals("", bytesToString(array, 2, 0));
        assertEquals("", bytesToString(array, 2, -1));
        assertEquals("string", bytesToString(array, 5, 6));
        assertEquals("string", bytesToString(array, 5, 100));
    }

    @Test
    public void testConvertFourBytesToIPv4() {
        byte[] bytes = {0x1f, (byte) 0xaa, (byte) 0xa8, (byte) 0xab};
        assertEquals("31.170.168.171", fourBytesToIPv4(bytes, 0));
        assertEquals("31.170.168.171", fourBytesToIPv4(bytes));
    }
}