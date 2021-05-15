package org.apache.jmeter.common.random;

import java.util.Calendar;
import java.util.Random;
import java.util.Set;

/**
 * 生成随机身份证号
 *
 * @author KelvinYe
 */
public class IDCard {
    /**
     * 随机生成大陆身份证号码
     */
    public static String idGenerate() {
        StringBuffer idCard = new StringBuffer();
        idCard.append(randomAreaCode());
        idCard.append(randomBirthday());
        idCard.append(randomCode());
        idCard.append(calcTrailingNumber(idCard.toString().toCharArray()));
        return idCard.toString();
    }

    /**
     * 随机生成港澳台身份证号码，810000 | 820000 | 830000
     */
    public static String idGenerate(String prefix) {
        StringBuffer idCard = new StringBuffer();
        idCard.append(prefix);
        idCard.append(randomBirthday());
        idCard.append(randomCode());
        idCard.append(calcTrailingNumber(idCard.toString().toCharArray()));
        return idCard.toString();
    }

    /**
     * 随机地区码
     */
    private static int randomAreaCode() {
        Set<String> ac = AreaCode.code.keySet();
        return AreaCode.code.get(ac.toArray()[new Random().nextInt(ac.size() - 1)].toString());
    }

    /**
     * 随机出生日期
     */
    private static String randomBirthday() {
        Calendar birthday = Calendar.getInstance();
        birthday.set(Calendar.YEAR, (int) (Math.random() * 60) + 1950);
        birthday.set(Calendar.MONTH, (int) (Math.random() * 12));
        birthday.set(Calendar.DATE, (int) (Math.random() * 31));

        StringBuffer randomBirthday = new StringBuffer();
        randomBirthday.append(birthday.get(Calendar.YEAR));
        long month = birthday.get(Calendar.MONTH) + 1;
        if (month < 10) {
            randomBirthday.append("0");
        }
        randomBirthday.append(month);
        long date = birthday.get(Calendar.DATE);
        if (date < 10) {
            randomBirthday.append("0");
        }
        randomBirthday.append(date);
        return randomBirthday.toString();
    }

    /**
     * 18位身份证验证
     * 根据〖中华人民共和国国家标准 GB 11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
     * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 第十八位数字(校验码)的计算方法为：
     * 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 2.将这17位数字和系数相乘的结果相加。
     * 3.用加出来和除以11，看余数是多少？
     * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。
     * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
     */
    private static char calcTrailingNumber(char[] chars) {
        if (chars.length < 17) {
            return ' ';
        }
        int[] c = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] r = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int[] n = new int[17];
        int result = 0;
        for (int i = 0; i < n.length; i++) {
            n[i] = Integer.parseInt(chars[i] + "");
        }
        for (int i = 0; i < n.length; i++) {
            result += c[i] * n[i];
        }
        return r[result % 11];
    }

    /**
     * 随机产生3位数
     */
    private static String randomCode() {
        int code = (int) (Math.random() * 1000);
        if (code < 10) {
            return "00" + code;
        } else if (code < 100) {
            return "0" + code;
        } else {
            return "" + code;
        }
    }

}
