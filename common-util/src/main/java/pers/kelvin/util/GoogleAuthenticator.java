package pers.kelvin.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GoogleAuthenticator {
    // 最多可偏移的时间，default 3 - max 17
    private static int window_size = 5;

    public static void setWindowSize(int s) {
        if (s >= 1 && s <= 17)
            window_size = s;
    }

    public static String getCode(String secret, long timeMsec) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        long t = (timeMsec / 1000L) / 30L;
        long hash = 0;
        for (int i = -window_size; i <= window_size; ++i) {
            try {
                hash = verifyCode(decodedKey, t + i);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        return Long.toString(hash);
    }

    private static int verifyCode(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

}
