package pers.kelvin.util.random;

import java.util.ArrayList;
import java.util.Random;

/**
 * 柬埔寨手机号随机数
 *
 * @author KelvinYe
 */
public class CambodiaMobilePhone {
    private static final ArrayList<String> PHONE_CODE = new ArrayList<>();
    private static final ArrayList<Integer> PHONE_LENGTH = new ArrayList<>();

    static {
        // PHONE_CODE
        PHONE_CODE.add("10");
        PHONE_CODE.add("15");
        PHONE_CODE.add("16");
        PHONE_CODE.add("69");
        PHONE_CODE.add("70");
        PHONE_CODE.add("81");
        PHONE_CODE.add("86");
        PHONE_CODE.add("87");
        PHONE_CODE.add("93");
        PHONE_CODE.add("98");
        PHONE_CODE.add("96");
        PHONE_CODE.add("88");
        PHONE_CODE.add("97");
        PHONE_CODE.add("71");
        PHONE_CODE.add("60");
        PHONE_CODE.add("66");
        PHONE_CODE.add("67");
        PHONE_CODE.add("68");
        PHONE_CODE.add("90");
        PHONE_CODE.add("31");
        PHONE_CODE.add("91");
        PHONE_CODE.add("11");
        PHONE_CODE.add("12");
        PHONE_CODE.add("14");
        PHONE_CODE.add("17");
        PHONE_CODE.add("61");
        PHONE_CODE.add("76");
        PHONE_CODE.add("77");
        PHONE_CODE.add("78");
        PHONE_CODE.add("85");
        PHONE_CODE.add("89");
        PHONE_CODE.add("92");
        PHONE_CODE.add("95");
        PHONE_CODE.add("99");
        PHONE_CODE.add("18");
        PHONE_CODE.add("38");
        PHONE_CODE.add("13");
        PHONE_CODE.add("80");
        PHONE_CODE.add("83");
        PHONE_CODE.add("84");
        PHONE_CODE.add("79");

        // PHONE_LENGTH
        PHONE_LENGTH.add(6);
        PHONE_LENGTH.add(7);
    }

    /**
     * 随机生成柬埔寨手机号前缀
     */
    public static String getRandomPhoneCode() {
        return PHONE_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    /**
     * 随机生成柬埔寨手机号长度
     */
    public static int getRandomPhoneLength() {
        return PHONE_LENGTH.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }


}
