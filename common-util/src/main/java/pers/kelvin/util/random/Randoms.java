package pers.kelvin.util.random;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author KelvinYe
 */
public class Randoms {
    /**
     * 获取随机数
     *
     * @param length 随机数的长度
     */
    public static String getNumber(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(9));
        }
        return sb.toString();
    }

    /**
     * 获取字符串 + 随机数的组合
     */
    public static String getNumber(String str, int length) {
        return str + getNumber(length);
    }

    /**
     * 获取随机数 + 字符串的组合
     */
    public static String getNumber(int length, String str) {
        return getNumber(length) + str;
    }

    /**
     * 获取字符串 + 随机数 + 字符串的组合
     */
    public static String getNumber(int length1, String str, int length2) {
        return getNumber(length1) + str + getNumber(length2);
    }

    /**
     * 获取字符串 + 随机数 + 字符串的组合
     */
    public static String getNumber(String str1, int length, String str2) {
        return str1 + getNumber(length) + str2;
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 获取日志ID
     */
    public static String getTraceLogId() {
        return "traceLogId" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + getNumber(6);
    }

    /**
     * 获取身份证ID
     */
    public static String getIDCard() {
        return IDCard.generate();
    }

    /**
     * 获取15位身份证ID
     */
    public static String getIDCard15() {
        String idCard = IDCard.generate();
        return idCard.substring(0, 6) + idCard.substring(8, 17);
    }

    /**
     * 根据卡bin和卡长度随机生成银行卡卡号，卡号无需减去卡bin长度（代码自动扣减）
     *
     * @param cardBin    卡bin
     * @param cardLength 卡号长度
     * @return 随机银行卡卡号
     */
    public static String getBankCard(String cardBin, int cardLength) {
        return getNumber(cardBin, cardLength - cardBin.length());
    }

    /**
     * 获取 移动/联通/电信 手机号码
     */
    public static String getMobileNumber() {
        return MobilePhone.getRandomPhoneCode() + getNumber(8);
    }

    /**
     * 获取移动手机号码
     */
    public static String getCMCCMobileNumber() {
        return MobilePhone.getRandomCMCCCode() + getNumber(8);
    }

    /**
     * 获取联通手机号码
     */
    public static String getCUCCMobileNumber() {
        return MobilePhone.getRandomCUCCCode() + getNumber(8);
    }

    /**
     * 获取电信手机号码
     */
    public static String getTelecomMobileNumber() {
        return MobilePhone.getRandomTelecomCode() + getNumber(8);
    }

}
