package org.apache.jmeter.common.random;

import java.util.ArrayList;
import java.util.Random;

/**
 * 柬埔寨手机号随机数
 *
 * @author KelvinYe
 */
public class CambodiaMobilePhone {
    private static final ArrayList<String[]> PHONE_RULE = new ArrayList<>();

    static {
        // cellcard
        PHONE_RULE.add(new String[]{"11","6"});
        PHONE_RULE.add(new String[]{"12","6"});
        PHONE_RULE.add(new String[]{"12","7"});
        PHONE_RULE.add(new String[]{"14","6"});
        PHONE_RULE.add(new String[]{"17","6"});
        PHONE_RULE.add(new String[]{"61","6"});
        PHONE_RULE.add(new String[]{"76","7"});
        PHONE_RULE.add(new String[]{"77","6"});
        PHONE_RULE.add(new String[]{"78","6"});
        PHONE_RULE.add(new String[]{"79","6"});
        PHONE_RULE.add(new String[]{"85","6"});
        PHONE_RULE.add(new String[]{"89","6"});
        PHONE_RULE.add(new String[]{"92","6"});
        PHONE_RULE.add(new String[]{"95","6"});
        PHONE_RULE.add(new String[]{"99","6"});

        // smart
        PHONE_RULE.add(new String[]{"10","6"});
        PHONE_RULE.add(new String[]{"15","6"});
        PHONE_RULE.add(new String[]{"16","6"});
        PHONE_RULE.add(new String[]{"69","6"});
        PHONE_RULE.add(new String[]{"70","6"});
        PHONE_RULE.add(new String[]{"81","6"});
        PHONE_RULE.add(new String[]{"86","6"});
        PHONE_RULE.add(new String[]{"87","6"});
        PHONE_RULE.add(new String[]{"93","6"});
        PHONE_RULE.add(new String[]{"96","7"});
        PHONE_RULE.add(new String[]{"98","6"});

        // metfone
        PHONE_RULE.add(new String[]{"31","7"});
        PHONE_RULE.add(new String[]{"60","6"});
        PHONE_RULE.add(new String[]{"66","6"});
        PHONE_RULE.add(new String[]{"67","6"});
        PHONE_RULE.add(new String[]{"68","6"});
        PHONE_RULE.add(new String[]{"71","7"});
        PHONE_RULE.add(new String[]{"88","7"});
        PHONE_RULE.add(new String[]{"90","6"});
        PHONE_RULE.add(new String[]{"97","7"});

        // qb
        PHONE_RULE.add(new String[]{"13","6"});
        PHONE_RULE.add(new String[]{"80","6"});
        PHONE_RULE.add(new String[]{"83","6"});
        PHONE_RULE.add(new String[]{"84","6"});

        // cootel
        PHONE_RULE.add(new String[]{"38","7"});

        // seatel
        PHONE_RULE.add(new String[]{"18","7"});

    }

    /**
     * 随机生成柬埔寨手机号规则（前缀 + 长度）
     */
    public static String[] getRandomPhoneCode() {
        return PHONE_RULE.get(new Random().nextInt(PHONE_RULE.size()));
    }

}
