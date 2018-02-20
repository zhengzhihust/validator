/*
 * Copyright (c) 2018. All Rights Reserved.
 *
 */

package com.welink.hub.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhengzhi
 */
public class ByteUtils {

    private static final char[] BASE16 = {'0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] HBASE16 = {'0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final String BASE64_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    private static final char[] BASE64 = BASE64_STRING
            .toCharArray();

    private static final String URLSAFE_BASE64_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    private static final char[] URLSAFE_BASE64 = URLSAFE_BASE64_STRING
            .toCharArray();

    private static final int MASK4 = 0x0f, MASK6 = 0x3f,
            MASK8 = 0xff;

    private static final Map<String, byte[]> BASE64_DECODE_TABLE_MAP = new ConcurrentHashMap<String, byte[]>();

    // rc4
    private static final int RC4_SBOX_LEN = 256;

    // uuid
    private static final int PREFIX;

    private static final _Random RANDOM;

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    // md
    private static final ThreadLocal<MessageDigest> MD = new ThreadLocal<MessageDigest>();
    private static final int SUM_SECURE_RANDOM = 64;
    private static final String UTF8 = "UTF-8";
    private static SecureRandom[] secureRandoms;
    private static Random random = new Random();

    static {
        secureRandoms = new SecureRandom[SUM_SECURE_RANDOM];
        for (int i = 0; i < SUM_SECURE_RANDOM; i++) {
            secureRandoms[i] = new SecureRandom();
        }
    }

    static {
        decodeTable(BASE64_STRING);
        decodeTable(URLSAFE_BASE64_STRING);

        long seed = System.currentTimeMillis();
        InetAddress ad = localAddress();

        if (ad == null) {
            PREFIX = (int) seed;
        } else {
            PREFIX = ByteUtils.bytes2int(ad.getAddress());
            seed ^= PREFIX;
        }
        RANDOM = new _Random(seed);
    }
    private ByteUtils() {
    }

    public static byte[] long2ComparableBytes(Long l) {
        if (l == null) {
            return null;
        }
        long v = l;
        byte[] b = new byte[8];
        b[0] = (byte) ((v >> 56) ^ 0x80);
        b[1] = (byte) (v >> 48);
        b[2] = (byte) (v >> 40);
        b[3] = (byte) (v >> 32);
        b[4] = (byte) (v >> 24);
        b[5] = (byte) (v >> 16);
        b[6] = (byte) (v >> 8);
        b[7] = (byte) v;
        return b;
    }

    public static byte[] maxComparableBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = -1;
        }
        return bytes;
    }

    public static byte[] concat(byte[]... args) {
        int length = 0;
        for (byte[] bytes : args) {
            if (bytes == null) {
                continue;
            }
            length += bytes.length;
        }
        byte[] result = new byte[length];
        int i = 0;
        for (byte[] bytes : args) {
            if (bytes == null) {
                continue;
            }
            for (byte b : bytes) {
                result[i] = b;
                i++;
            }
        }
        return result;
    }

    public static byte[] sub(byte[] bytes, int start, int end) {
        int length = end - start;
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = bytes[start + i];
        }
        return result;
    }

    public static String randomUuid() {
        return bytes2hex(randomBytesUuid());
    }

    public static byte[] randomBytesUuid() {
        int id = random.nextInt() & (SUM_SECURE_RANDOM - 1);
        SecureRandom numberGenerator = secureRandoms[id];
        byte[] randomBytes = new byte[16];
        numberGenerator.nextBytes(randomBytes);
        return randomBytes;
    }

    /**
     * uuid.
     *
     * @return uuid string.
     */
    public static String uuid() {
        return randomUuid();
    }

    /**
     * compare byte array.
     *
     * @param b1 byte-array1
     * @param b2 byte-array2
     * @return
     */
    public static int compare(byte[] b1, byte[] b2) {
        return compare(b1, 0, b1.length, b2, 0, b2.length);
    }

    /**
     * @param b1   byte-array1
     * @param off1 offset1
     * @param len1 length1
     * @param b2   byte-array1
     * @param off2 offset2
     * @param len2 length2
     * @return
     */
    public static int compare(byte[] b1, int off1, int len1, byte[] b2, int off2, int len2) {
        if (b1 == b2 && off1 == off2 && len1 == len2) {
            return 0;
        }

        int l1 = off1 + len1, l2 = off2 + len2;
        for (int i1 = off1, i2 = off2; i1 < l1 && i2 < l2; i1++, i2++) {
            int a = b1[i1], b = b2[i2];
            if (a != b) {
                return a - b;
            }
        }
        return l1 - l2;
    }

    /**
     * hash byte array.
     *
     * @param bytes byte array.
     * @return hash code.
     */
    public static int hash(byte[] bytes) {
        return hash(bytes, 0, bytes.length);
    }

    /**
     * hash byte array.
     *
     * @param bytes  byte array.
     * @param offset offset.
     * @param length length.
     * @return hash code.
     */
    public static int hash(byte[] bytes, int offset, int length) {
        int hash = 1;
        for (int i = offset; i < offset + length; i++) {
            hash = (31 * hash) + (int) bytes[i];
        }
        return hash;
    }

    /**
     * byte array copy.
     *
     * @param src    src.
     * @param length new length.
     * @return new byte array.
     */
    public static byte[] copyOf(byte[] src, int length) {
        byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
        return dest;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] short2bytes(short v) {
        byte[] b = {0, 0};
        short2bytes(v, b, 0);
        return b;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void short2bytes(short v, byte[] b) {
        short2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void short2bytes(short v, byte[] b, int off) {
        b[off + 1] = (byte) v;
        b[off] = (byte) (v >>> 8);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] int2bytes(int v) {
        byte[] b = {0, 0, 0, 0};
        int2bytes(v, b, 0);
        return b;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void int2bytes(int v, byte[] b) {
        int2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void int2bytes(int v, byte[] b, int off) {
        b[off + 3] = (byte) v;
        b[off + 2] = (byte) (v >>> 8);
        b[off + 1] = (byte) (v >>> 16);
        b[off] = (byte) (v >>> 24);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] float2bytes(float v) {
        byte[] b = {0, 0, 0, 0};
        float2bytes(v, b, 0);
        return b;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void float2bytes(float v, byte[] b) {
        float2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void float2bytes(float v, byte[] b, int off) {
        int i = Float.floatToIntBits(v);
        b[off + 3] = (byte) i;
        b[off + 2] = (byte) (i >>> 8);
        b[off + 1] = (byte) (i >>> 16);
        b[off] = (byte) (i >>> 24);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] long2bytes(Long v) {
        if (v == null) {
            return null;
        }
        byte[] b = {0, 0, 0, 0, 0, 0, 0, 0};
        long2bytes(v, b, 0);
        return b;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void long2bytes(long v, byte[] b) {
        long2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void long2bytes(long v, byte[] b, int off) {
        b[off + 7] = (byte) v;
        b[off + 6] = (byte) (v >>> 8);
        b[off + 5] = (byte) (v >>> 16);
        b[off + 4] = (byte) (v >>> 24);
        b[off + 3] = (byte) (v >>> 32);
        b[off + 2] = (byte) (v >>> 40);
        b[off + 1] = (byte) (v >>> 48);
        b[off] = (byte) (v >>> 56);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @return byte[].
     */
    public static byte[] double2bytes(double v) {
        byte[] b = {0, 0, 0, 0, 0, 0, 0, 0};
        double2bytes(v, b, 0);
        return b;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void double2bytes(double v, byte[] b) {
        double2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v   value.
     * @param b   byte array.
     * @param off array offset.
     */
    public static void double2bytes(double v, byte[] b, int off) {
        long j = Double.doubleToLongBits(v);
        b[off + 7] = (byte) j;
        b[off + 6] = (byte) (j >>> 8);
        b[off + 5] = (byte) (j >>> 16);
        b[off + 4] = (byte) (j >>> 24);
        b[off + 3] = (byte) (j >>> 32);
        b[off + 2] = (byte) (j >>> 40);
        b[off + 1] = (byte) (j >>> 48);
        b[off] = (byte) (j >>> 56);
    }

    /**
     * to short.
     *
     * @param b byte array.
     * @return short.
     */
    public static short bytes2short(byte[] b) {
        return bytes2short(b, 0);
    }

    /**
     * to short.
     *
     * @param b   byte array.
     * @param off offset.
     * @return short.
     */
    public static short bytes2short(byte[] b, int off) {
        return (short) ((b[off + 1] & 0xff) | ((b[off + 0]) << 8));
    }

    /**
     * to int.
     *
     * @param b byte array.
     * @return int.
     */
    public static int bytes2int(byte[] b) {
        return bytes2int(b, 0);
    }

    /**
     * to int.
     *
     * @param b   byte array.
     * @param off offset.
     * @return int.
     */
    public static int bytes2int(byte[] b, int off) {
        return (b[off + 3] & 0xff) | ((b[off + 2] & 0xff) << 8) | ((b[off + 1] & 0xff) << 16)
                | ((b[off + 0]) << 24);
    }

    /**
     * to int.
     *
     * @param b byte array.
     * @return int.
     */
    public static float bytes2float(byte[] b) {
        return bytes2float(b, 0);
    }

    /**
     * to int.
     *
     * @param b   byte array.
     * @param off offset.
     * @return int.
     */
    public static float bytes2float(byte[] b, int off) {
        return Float.intBitsToFloat(bytes2int(b, off));
    }

    /**
     * to long.
     *
     * @param b byte array.
     * @return long.
     */
    public static long bytes2long(byte[] b) {
        return bytes2long(b, 0);
    }

    /**
     * to long.
     *
     * @param b   byte array.
     * @param off offset.
     * @return long.
     */
    public static long bytes2long(byte[] b, int off) {
        return (b[off + 7] & 0xff) | ((b[off + 6] & 0xff) << 8) | ((b[off + 5] & 0xff) << 16)
                | (((long) (b[off + 4] & 0xff)) << 24) | (((long) (b[off + 3] & 0xff)) << 32)
                | (((long) (b[off + 2] & 0xff)) << 40) | (((long) (b[off + 1] & 0xff)) << 48)
                | (((long) (b[off + 0])) << 56);
    }

    /**
     * to long.
     *
     * @param b byte array.
     * @return double.
     */
    public static double bytes2double(byte[] b) {
        return bytes2double(b, 0);
    }

    /**
     * to long.
     *
     * @param b   byte array.
     * @param off offset.
     * @return double.
     */
    public static double bytes2double(byte[] b, int off) {
        return Double.longBitsToDouble(bytes2long(b, off));
    }

    /**
     * to hex string.
     *
     * @param bs byte array.
     * @return hex string.
     */
    public static String bytes2hex(byte[] bs) {
        return bytes2hex(bs, 0, bs.length);
    }

    /**
     * to hex string.
     *
     * @param s
     * @return hex string.
     */
    public static String string2Md5HexStr(String s) {
        return bytes2hex(ByteUtils.md5(s));
    }

    public static String bytes2HbaseHex(byte[] bs) {
        return bytes2HbaseHex(bs, 0, bs.length);
    }

    public static byte[] str2UTF8Bytes(String s) {
        try {
            return s.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bytes2UTF8Str(byte[] bytes) {
        try {
            return new String(bytes, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * to hex string.
     *
     * @param bs  byte array.
     * @param off offset.
     * @param len length.
     * @return hex string.
     */
    public static String bytes2hex(byte[] bs, int off, int len) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: length < 0, length is " + len);
        }
        if (off + len > bs.length) {
            throw new IndexOutOfBoundsException("bytes2hex: offset + length > array length.");
        }

        byte b;
        int r = off, w = 0;
        char[] cs = new char[len * 2];
        for (int i = 0; i < len; i++) {
            b = bs[r++];
            cs[w++] = BASE16[b >> 4 & MASK4];
            cs[w++] = BASE16[b & MASK4];
        }
        return new String(cs);
    }

    public static String bytes2HbaseHex(byte[] bs, int off, int len) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("bytes2hex: length < 0, length is " + len);
        }
        if (off + len > bs.length) {
            throw new IndexOutOfBoundsException("bytes2hex: offset + length > array length.");
        }

        byte b;
        int r = off, w = 0;
        char[] cs = new char[len * 4];
        for (int i = 0; i < len; i++) {
            b = bs[r++];
            cs[w++] = '\\';
            cs[w++] = 'x';
            cs[w++] = HBASE16[b >> 4 & MASK4];
            cs[w++] = HBASE16[b & MASK4];
        }
        return new String(cs);
    }

    /**
     * from hex string.
     *
     * @param str hex string.
     * @return byte array.
     */
    public static byte[] hex2bytes(String str) {
        return hex2bytes(str, 0, str.length());
    }

    /**
     * from hex string.
     *
     * @param str hex string.
     * @param off offset.
     * @param len length.
     * @return byte array.
     */
    public static byte[] hex2bytes(final String str, final int off, int len) {
        if ((len & 1) == 1) {
            throw new IllegalArgumentException("hex2bytes: ( len & 1 ) == 1.");
        }
        if (off < 0) {
            throw new IndexOutOfBoundsException("hex2bytes: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("hex2bytes: length < 0, length is " + len);
        }
        if (off + len > str.length()) {
            throw new IndexOutOfBoundsException("hex2bytes: offset + length > array length.");
        }

        int num = len / 2, r = off, w = 0;
        byte[] b = new byte[num];
        for (int i = 0; i < num; i++) {
            b[w++] = (byte) (hex(str.charAt(r++)) << 4 | hex(str.charAt(r++)));
        }
        return b;
    }

    /**
     * to base64 string.
     *
     * @param b byte array.
     * @return base64 string.
     */
    public static String bytes2base64(byte[] b) {
        return bytes2base64(b, 0, b.length);
    }

    /**
     * to base64 string.
     *
     * @param b byte array.
     * @return base64 string.
     */
    public static String bytes2base64(byte[] b, int offset, int length) {
        return bytes2base64(b, offset, length, BASE64, true);
    }

    /**
     * to base64 string.
     *
     * @param b   byte array.
     * @param pad has pad.
     * @return base64 string.
     */
    public static String bytes2base64(byte[] b, boolean pad) {
        return bytes2base64(b, 0, b.length, pad);
    }

    /**
     * to base64 string.
     *
     * @param b      byte array.
     * @param offset offset.
     * @param length length.
     * @param pad    has pad.
     * @return base64 string.
     */
    public static String bytes2base64(byte[] b, int offset, int length, boolean pad) {
        return bytes2base64(b, offset, length, BASE64, pad);
    }

    /**
     * to base64 string.
     *
     * @param b byte array.
     * @return base64 string.
     */
    public static String bytes2URLSafeBase64(byte[] b) {
        return bytes2URLSafeBase64(b, 0, b.length);
    }

    /**
     * to base64 string.
     *
     * @param b byte array.
     * @return base64 string.
     */
    public static String bytes2URLSafeBase64(byte[] b, int offset, int length) {
        return bytes2base64(b, offset, length, URLSAFE_BASE64, false);
    }

    /**
     * to base64 string.
     *
     * @param b    byte array.
     * @param code base64 code string(0-63 is base64 char, 64 is pad char).
     * @return base64 string.
     */
    public static String bytes2base64(byte[] b, String code) {
        return bytes2base64(b, 0, b.length, code);
    }

    /**
     * to base64 string.
     *
     * @param b    byte array.
     * @param code base64 code string(0-63 is base64 char, 64 is pad char).
     * @return base64 string.
     */
    public static String bytes2base64(byte[] b, int offset, int length, String code) {
        int cl = code.length();
        if (cl < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }

        return bytes2base64(b, offset, length, code.toCharArray(), cl > 64);
    }

    /**
     * to base64 string.
     *
     * @param bs   byte array.
     * @param off  offset.
     * @param len  length.
     * @param code base64 code(0-63 is base64 char,64 is pad char).
     * @return base64 string.
     */
    private static String bytes2base64(byte[] bs, int off, int len, char[] code, boolean pad) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("bytes2base64: offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("bytes2base64: length < 0, length is " + len);
        }
        if (off + len > bs.length) {
            throw new IndexOutOfBoundsException("bytes2base64: offset + length > array length.");
        }

        int num = len / 3, rem = len % 3, r = off, w = 0;
        char[] cs = new char[num * 4 + (rem == 0 ? 0 : pad ? 4 : rem + 1)];

        for (int i = 0; i < num; i++) {
            int b1 = bs[r++] & MASK8, b2 = bs[r++] & MASK8, b3 = bs[r++] & MASK8;

            cs[w++] = code[b1 >> 2];
            cs[w++] = code[(b1 << 4) & MASK6 | (b2 >> 4)];
            cs[w++] = code[(b2 << 2) & MASK6 | (b3 >> 6)];
            cs[w++] = code[b3 & MASK6];
        }

        if (rem == 1) {
            int b1 = bs[r++] & MASK8;
            cs[w++] = code[b1 >> 2];
            cs[w++] = code[(b1 << 4) & MASK6];
            if (pad) {
                cs[w++] = code[64];
                cs[w++] = code[64];
            }
        } else if (rem == 2) {
            int b1 = bs[r++] & MASK8, b2 = bs[r++] & MASK8;
            cs[w++] = code[b1 >> 2];
            cs[w++] = code[(b1 << 4) & MASK6 | (b2 >> 4)];
            cs[w++] = code[(b2 << 2) & MASK6];
            if (pad) {
                cs[w++] = code[64];
            }
        }
        return new String(cs);
    }

    /**
     * from base64 string.
     *
     * @param str base64 string.
     * @return byte array.
     */
    public static byte[] base642bytes(String str) {
        return base642bytes(str, 0, str.length());
    }

    /**
     * from base64 string.
     *
     * @param str    base64 string.
     * @param offset offset.
     * @param length length.
     * @return byte array.
     */
    public static byte[] base642bytes(String str, int offset, int length) {
        return base642bytes(str, offset, length, BASE64_STRING);
    }

    /**
     * from base64 string.
     *
     * @param str base64 string.
     * @return byte array.
     */
    public static byte[] urlSafeBase642Bytes(String str) {
        return urlSafeBase642Bytes(str, 0, str.length());
    }

    /**
     * from base64 string.
     *
     * @param str    base64 string.
     * @param offset offset.
     * @param length length.
     * @return byte array.
     */
    public static byte[] urlSafeBase642Bytes(String str, int offset, int length) {
        return base642bytes(str, offset, length, URLSAFE_BASE64_STRING);
    }

    /**
     * from base64 string.
     *
     * @param str  base64 string.
     * @param code base64 code(0-63 is base64 char,64 is pad char).
     * @return byte array.
     */
    public static byte[] base642bytes(String str, String code) {
        return base642bytes(str, 0, str.length(), code);
    }

    /**
     * from base64 string.
     *
     * @param str  base64 string.
     * @param off  offset.
     * @param len  length.
     * @param code base64 code(0-63 is base64 char,64 is pad char).
     * @return byte array.
     */
    public static byte[] base642bytes(final String str, final int off, final int len,
                                      final String code) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("offset < 0, offset is " + off);
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("length < 0, length is " + len);
        }
        if (off + len > str.length()) {
            throw new IndexOutOfBoundsException("offset + length > string length.");
        }

        int cl = code.length();
        if (cl < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }

        int rem = len % 4, num = len / 4, size = num * 3;
        switch (rem) {
            case 0:
                if (cl > 64) // check padding string.
                {
                    char pc = code.charAt(64);
                    if (str.charAt(off + len - 2) == pc) {
                        size -= 2;
                        --num;
                        rem = 2;
                    } else if (str.charAt(off + len - 1) == pc) {
                        size--;
                        --num;
                        rem = 3;
                    }
                }
                break;
            case 1:
                throw new IllegalArgumentException("Illegal base64 string, length % 4 == 1.");
            case 2:
                size++;
                break;
            case 3:
                size += 2;
                break;
            default:
                break;
        }

        int r = off, w = 0;
        byte[] b = new byte[size], t = decodeTable(code);
        for (int i = 0; i < num; i++) {
            int c1 = t[str.charAt(r++)], c2 = t[str.charAt(r++)], c3 = t[str.charAt(r++)], c4 = t[str
                    .charAt(r++)];

            b[w++] = (byte) ((c1 << 2) | (c2 >> 4));
            b[w++] = (byte) ((c2 << 4) | (c3 >> 2));
            b[w++] = (byte) ((c3 << 6) | c4);
        }

        if (rem == 2) {
            int c1 = t[str.charAt(r++)], c2 = t[str.charAt(r++)];

            b[w++] = (byte) ((c1 << 2) | (c2 >> 4));
        } else if (rem == 3) {
            int c1 = t[str.charAt(r++)], c2 = t[str.charAt(r++)], c3 = t[str.charAt(r++)];

            b[w++] = (byte) ((c1 << 2) | (c2 >> 4));
            b[w++] = (byte) ((c2 << 4) | (c3 >> 2));
        }
        return b;
    }

    /**
     * md5.
     *
     * @param str input string.
     * @return MD5 byte array.
     */
    public static byte[] md5(String str) {
        return md5(str2UTF8Bytes(str));
    }

    public static String native2Ascii(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(char2Ascii(str.charAt(i)));
        }
        return sb.toString();
    }

    private static String char2Ascii(char c) {
        if (c > 255) {
            StringBuilder sb = new StringBuilder();
            sb.append("\\u");
            int code = (c >> 8);
            String tmp = Integer.toHexString(code);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
            code = (c & 0xFF);
            tmp = Integer.toHexString(code);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
            return sb.toString();
        } else {
            return Character.toString(c);
        }
    }

    /**
     * md5.
     *
     * @param source byte array source.
     * @return MD5 byte array.
     */
    public static byte[] md5(byte[] source) {
        MessageDigest md = getMessageDigest();
        return md.digest(source);
    }

    /**
     * md5.
     *
     * @param file file source.
     * @return MD5 byte array.
     */
    public static byte[] md5(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            return md5(is);
        } finally {
            is.close();
        }
    }

    /**
     * get md5.
     *
     * @param is input stream.
     * @return MD5 byte array.
     */
    public static byte[] md5(InputStream is) throws IOException {
        return md5(is, 1024 * 8);
    }

    /**
     * rc4.
     *
     * @param v value.
     * @param k key string.
     * @return result.
     */
    public static byte[] rc4(byte[] v, String k) {
        return rc4(v, k.getBytes());
    }

    public static String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * rc4.
     *
     * @param v value.
     * @param k key.
     * @return result.
     */
    public static byte[] rc4(byte[] v, byte[] k) {
        int i, j, s[] = new int[RC4_SBOX_LEN], vl = v.length, kl = k.length;

        for (i = 0; i < RC4_SBOX_LEN; i++) {
            s[i] = i;
        }

        j = 0;
        for (i = 0; i < RC4_SBOX_LEN; i++) {
            j = (j + s[i] + (kl == 0 ? 0 : k[i % kl] & 0xff)) % RC4_SBOX_LEN;
            int t = s[i];
            s[i] = s[j];
            s[j] = t;
        }

        i = j = 0;
        byte[] ret = new byte[vl];
        for (int x = 0; x < vl; x++) {
            i = (i + 1) % RC4_SBOX_LEN;
            j = (j + s[i]) % RC4_SBOX_LEN;
            int t = s[i];
            s[i] = s[j];
            s[j] = t;
            ret[x] = (byte) (v[x] ^ s[(s[i] + s[j]) % RC4_SBOX_LEN]);
        }
        return ret;
    }

    private static byte hex(char c) {
        if (c <= '9') {
            return (byte) (c - '0');
        }
        if (c >= 'a' && c <= 'f') {
            return (byte) (c - 'a' + 10);
        }
        if (c >= 'A' && c <= 'F') {
            return (byte) (c - 'A' + 10);
        }
        throw new IllegalArgumentException("hex string format error [" + c + "].");
    }

    private static byte[] decodeTable(String code) {
        if (code.length() < 64) {
            throw new IllegalArgumentException("Base64 code length < 64.");
        }

        byte[] ret = BASE64_DECODE_TABLE_MAP.get(code);
        if (ret == null) {
            ret = new byte[128];
            for (int i = 0; i < 128; i++) {
                ret[i] = -1;
            }
            for (int i = 0; i < 64; i++) {
                ret[code.charAt(i)] = (byte) i;
            }
            BASE64_DECODE_TABLE_MAP.put(code, ret);
        }
        return ret;
    }

    private static byte[] md5(InputStream is, int bs) throws IOException {
        MessageDigest md = getMessageDigest();
        byte[] buf = new byte[bs];
        while (is.available() > 0) {
            int read, total = 0;
            do {
                if ((read = is.read(buf, total, bs - total)) <= 0) {
                    break;
                }
                total += read;
            } while (total < bs);
            md.update(buf);
        }
        return md.digest();
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest ret = MD.get();
        if (ret == null) {
            try {
                ret = MessageDigest.getInstance("MD5");
                MD.set(ret);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    private static InetAddress localAddress() {
        try {
            Enumeration<NetworkInterface> itfs = NetworkInterface.getNetworkInterfaces();
            if (itfs != null) {
                while (itfs.hasMoreElements()) {
                    NetworkInterface itf = itfs.nextElement();
                    for (Enumeration<InetAddress> ads = itf.getInetAddresses(); ads
                            .hasMoreElements(); ) {
                        InetAddress ad = ads.nextElement();
                        if (ad == null || ad.isLoopbackAddress()) {
                            continue;
                        }

                        String ip = ad.getHostAddress();
                        if ("0.0.0.0".equals(ip) || "127.0.0.1".equals(ip)) {
                            continue;
                        }

                        return ad;
                    }
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }

    private static class _Random extends Random {

        _Random(long seed) {
            super(seed);
        }

        @Override
        protected int next(int bits) {
            return super.next(bits);
        }
    }
}
