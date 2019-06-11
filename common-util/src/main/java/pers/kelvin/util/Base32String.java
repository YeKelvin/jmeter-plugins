package pers.kelvin.util;

import com.google.common.collect.Maps;

import java.util.Locale;
import java.util.Map;


public class Base32String {
    private static final String SEPARATOR = "-";
    private static final char[] DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
    private static final int MASK = DIGITS.length - 1;
    private static final int SHIFT = Integer.numberOfTrailingZeros(DIGITS.length);
    private static final Map<Character, Integer> CHAR_MAP =
            Maps.newHashMapWithExpectedSize(DIGITS.length);

    static {
        for (int i = 0; i < DIGITS.length; i++) {
            CHAR_MAP.put(DIGITS[i], i);
        }
    }

    public static byte[] decode(String encoded) throws DecodingException {
        encoded = encoded.trim().replaceAll(SEPARATOR, "").replaceAll(" ", "");

        encoded = encoded.replaceFirst("[=]*$", "");

        encoded = encoded.toUpperCase(Locale.US);

        if (encoded.length() == 0) {
            return new byte[0];
        }
        int encodedLength = encoded.length();
        int outLength = encodedLength * SHIFT / 8;
        byte[] result = new byte[outLength];
        int buffer = 0;
        int next = 0;
        int bitsLeft = 0;
        for (char c : encoded.toCharArray()) {
            if (!CHAR_MAP.containsKey(c)) {
                throw new DecodingException("Illegal character: " + c);
            }
            buffer <<= SHIFT;
            buffer |= CHAR_MAP.get(c) & MASK;
            bitsLeft += SHIFT;
            if (bitsLeft >= 8) {
                result[next++] = (byte) (buffer >> (bitsLeft - 8));
                bitsLeft -= 8;
            }
        }
        return result;
    }

    public static String encode(byte[] data) {
        int dataLength = data.length;

        if (dataLength == 0) {
            return "";
        }

        if (dataLength >= (1 << 28)) {
            throw new IllegalArgumentException();
        }

        int outputLength = (dataLength * 8 + SHIFT - 1) / SHIFT;
        StringBuilder result = new StringBuilder(outputLength);

        int buffer = data[0];
        int next = 1;
        int bitsLeft = 8;
        while (bitsLeft > 0 || next < dataLength) {
            if (bitsLeft < SHIFT) {
                if (next < dataLength) {
                    buffer <<= 8;
                    buffer |= (data[next++] & 0xff);
                    bitsLeft += 8;
                } else {
                    int pad = SHIFT - bitsLeft;
                    buffer <<= pad;
                    bitsLeft += pad;
                }
            }
            int index = MASK & (buffer >> (bitsLeft - SHIFT));
            bitsLeft -= SHIFT;
            result.append(DIGITS[index]);
        }
        return result.toString();
    }

    public static class DecodingException extends Exception {
        public DecodingException(String message) {
            super(message);
        }
    }
}