package pers.kelvin.util.random;

import java.util.ArrayList;
import java.util.Random;

/**
 * 柬埔寨手机号随机数
 *
 * @author KelvinYe
 */
public class CambodiaMobilePhone {
    private static final ArrayList<String[]> PHONE_CODE = new ArrayList<>();
    private static final ArrayList<String[]> CELLCARD_CODE = new ArrayList<>();
    private static final ArrayList<String[]> SMART_CODE = new ArrayList<>();
    private static final ArrayList<String[]> METFENE_CODE = new ArrayList<>();
    private static final ArrayList<String[]> QB_CODE = new ArrayList<>();
    private static final ArrayList<String[]> EXCELL_CODE = new ArrayList<>();
    private static final ArrayList<String[]> COOTEL_CODE = new ArrayList<>();

    static {
        // CELLCARD
        CELLCARD_CODE.add(new String[]{"11", "6"});
        CELLCARD_CODE.add(new String[]{"12", "6"});
        CELLCARD_CODE.add(new String[]{"14", "6"});
        CELLCARD_CODE.add(new String[]{"17", "6"});
        CELLCARD_CODE.add(new String[]{"61", "6"});
        CELLCARD_CODE.add(new String[]{"76", "7"});
        CELLCARD_CODE.add(new String[]{"77", "6"});
        CELLCARD_CODE.add(new String[]{"78", "6"});
        CELLCARD_CODE.add(new String[]{"85", "6"});
        CELLCARD_CODE.add(new String[]{"89", "6"});
        CELLCARD_CODE.add(new String[]{"92", "6"});
        CELLCARD_CODE.add(new String[]{"95", "6"});
        CELLCARD_CODE.add(new String[]{"99", "6"});

        // SMART
        SMART_CODE.add(new String[]{"10", "6"});
        SMART_CODE.add(new String[]{"15", "6"});
        SMART_CODE.add(new String[]{"16", "6"});
        SMART_CODE.add(new String[]{"69", "6"});
        SMART_CODE.add(new String[]{"70", "6"});
        SMART_CODE.add(new String[]{"81", "6"});
        SMART_CODE.add(new String[]{"86", "6"});
        SMART_CODE.add(new String[]{"87", "6"});
        SMART_CODE.add(new String[]{"93", "6"});
        SMART_CODE.add(new String[]{"96", "6"});
        SMART_CODE.add(new String[]{"98", "6"});

        // METFENE
        METFENE_CODE.add(new String[]{"88","7"});
        METFENE_CODE.add(new String[]{"97","7"});
        METFENE_CODE.add(new String[]{"71","7"});
        METFENE_CODE.add(new String[]{"31","7"});
        METFENE_CODE.add(new String[]{"60","6"});
        METFENE_CODE.add(new String[]{"65","6"});
        METFENE_CODE.add(new String[]{"67","6"});
        METFENE_CODE.add(new String[]{"68","6"});
        METFENE_CODE.add(new String[]{"90","6"});

        // QB
        QB_CODE.add(new String[]{"13", "6"});
        QB_CODE.add(new String[]{"80", "6"});
        QB_CODE.add(new String[]{"83", "6"});
        QB_CODE.add(new String[]{"84", "6"});

        // EXCELL
        EXCELL_CODE.add(new String[]{"18", "6"});

        // COOTEL
        COOTEL_CODE.add(new String[]{"38", "6"});

        PHONE_CODE.addAll(CELLCARD_CODE);
        PHONE_CODE.addAll(SMART_CODE);
        PHONE_CODE.addAll(METFENE_CODE);
        PHONE_CODE.addAll(QB_CODE);
        PHONE_CODE.addAll(EXCELL_CODE);
        PHONE_CODE.addAll(COOTEL_CODE);
    }

    /**
     * 随机生成柬埔寨手机号前缀
     */
    public static String[] getRandomPhoneCode() {
        return PHONE_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    public static String[] getRandomCellcardCode() {
        return CELLCARD_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    public static String[] getRandomSmartCode() {
        return SMART_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    public static String[] getRandomMetfeneCode() {
        return METFENE_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    public static String[] getRandomQbCode() {
        return QB_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    public static String[] getRandomExcellCode() {
        return EXCELL_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }

    public static String[] getRandomCootelCode() {
        return COOTEL_CODE.get(new Random().nextInt(PHONE_CODE.size() - 1));
    }


}
