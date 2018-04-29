package me.alexand.scat.statistic.collector.util;

import org.junit.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import static me.alexand.scat.statistic.collector.utils.BytesConvertUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * @author asidorov84@gmail.com
 */

public class BytesConvertUtilsTests {
    private static final int UNSIGNED_TWO_BYTES_DATA = 0x7F_FF;
    private static final int SIGNED_TWO_BYTES_DATA = 0xFF_FF;

    private static final long UNSIGNED_FOUR_BYTES_DATA = 0x7F_FF_FF_FFL;
    private static final long SIGNED_FOUR_BYTES_DATA = 0xFF_FF_FF_FFL;

    @Test
    public void testConvertTwoBytesToInt() {
        byte[] payload = ByteBuffer
                .allocate(Short.BYTES)
                .putShort((short) UNSIGNED_TWO_BYTES_DATA)
                .array();

        assertEquals(UNSIGNED_TWO_BYTES_DATA, twoBytesToInt(payload));

        payload = ByteBuffer
                .allocate(Short.BYTES)
                .putShort((short) SIGNED_TWO_BYTES_DATA)
                .array();

        assertEquals(SIGNED_TWO_BYTES_DATA, twoBytesToInt(payload));
    }

    @Test
    public void testConvertFourBytesToLong() {
        byte[] payload = ByteBuffer
                .allocate(Integer.BYTES)
                .putInt((int) UNSIGNED_FOUR_BYTES_DATA)
                .array();

        assertEquals(UNSIGNED_FOUR_BYTES_DATA, fourBytesToLong(payload));

        payload = ByteBuffer
                .allocate(Integer.BYTES)
                .putInt((int) SIGNED_FOUR_BYTES_DATA)
                .array();

        assertEquals(SIGNED_FOUR_BYTES_DATA, fourBytesToLong(payload));
    }

    @Test
    public void testConvertEightBytesToBigInteger() {
        byte[] bytes = {0x7f, (byte) 0xff, (byte) 0xff, (byte) 0xff,
                (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};

        assertEquals(new BigInteger("9223372036854775807"), eightBytesToBigInteger(bytes));
    }

    //TODO сделать тесты оставшихся методов
}