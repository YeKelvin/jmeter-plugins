package pers.kelvin.util;


import jodd.util.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GoogleAuthenticator {
    /**
     * 获取谷歌动态认证码
     */
    public static String getCode(String secretkey) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] key = Base32.decode(secretkey);
        long timeMsec = System.currentTimeMillis();
        long t = (timeMsec / 1000L) / 30L;
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

        String code = String.valueOf(truncatedHash);
        int strLen = code.length();
        while (strLen < 6) {
            StringBuffer sb = new StringBuffer();
            sb.append("0").append(code);//左补0
            code = sb.toString();
            strLen = code.length();
        }
        return code;
    }

}
