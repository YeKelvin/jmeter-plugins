package pers.kelvin.util.random;


import java.util.ArrayList;
import java.util.Random;

/**
 * @author KelvinYe
 */
public class MobilePhone {
    private static final ArrayList<String> PHONE_CODE = new ArrayList<>();
    private static final ArrayList<String> CMCC_CODE = new ArrayList<>();
    private static final ArrayList<String> CUCC_CODE = new ArrayList<>();
    private static final ArrayList<String> TELECOM_CODE = new ArrayList<>();

    static {
        //移动
        CMCC_CODE.add("134");
        CMCC_CODE.add("135");
        CMCC_CODE.add("136");
        CMCC_CODE.add("137");
        CMCC_CODE.add("138");
        CMCC_CODE.add("139");
        CMCC_CODE.add("147");
        CMCC_CODE.add("150");
        CMCC_CODE.add("151");
        CMCC_CODE.add("152");
        CMCC_CODE.add("157");
        CMCC_CODE.add("158");
        CMCC_CODE.add("159");
        CMCC_CODE.add("170");
        CMCC_CODE.add("172");
        CMCC_CODE.add("178");
        CMCC_CODE.add("182");
        CMCC_CODE.add("183");
        CMCC_CODE.add("184");
        CMCC_CODE.add("187");
        CMCC_CODE.add("188");

        //联通
        CUCC_CODE.add("130");
        CUCC_CODE.add("131");
        CUCC_CODE.add("132");
        CUCC_CODE.add("145");
        CUCC_CODE.add("155");
        CUCC_CODE.add("156");
        CUCC_CODE.add("170");
        CUCC_CODE.add("171");
        CUCC_CODE.add("175");
        CUCC_CODE.add("176");
        CUCC_CODE.add("185");
        CUCC_CODE.add("186");

        //电信
        TELECOM_CODE.add("133");
        TELECOM_CODE.add("149");
        TELECOM_CODE.add("153");
        TELECOM_CODE.add("158");
        TELECOM_CODE.add("170");
        TELECOM_CODE.add("173");
        TELECOM_CODE.add("177");
        TELECOM_CODE.add("178");
        TELECOM_CODE.add("180");
        TELECOM_CODE.add("181");
        TELECOM_CODE.add("182");
        TELECOM_CODE.add("189");
        TELECOM_CODE.add("199");

        PHONE_CODE.addAll(CMCC_CODE);
        PHONE_CODE.addAll(CUCC_CODE);
        PHONE_CODE.addAll(TELECOM_CODE);
    }

    /**
     * 随机生成 移动/联通/电信手机号码前缀三位数
     */
    public static String getRandomPhoneCode() {
        return PHONE_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    /**
     * 随机生成 移动手机号码前缀三位数
     */
    public static String getRandomCMCCCode() {
        return CMCC_CODE.get(new Random().nextInt(CMCC_CODE.size() - 1));
    }

    /**
     * 随机生成 联通手机号码前缀三位数
     */
    public static String getRandomCUCCCode() {
        return CUCC_CODE.get(new Random().nextInt(CUCC_CODE.size() - 1));
    }

    /**
     * 随机生成 电信手机号码前缀三位数
     */
    public static String getRandomTelecomCode() {
        return TELECOM_CODE.get(new Random().nextInt(TELECOM_CODE.size() - 1));
    }

}
