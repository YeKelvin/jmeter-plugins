package org.apache.jmeter;

import org.apache.jmeter.common.utils.JsonConfigUtil;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * User: KelvinYe
 * Date: 2018-03-28
 * Time: 14:46
 */
public class JsonConfigUtilTest {

    @Test
    public void testGet() throws Exception {
        String configFilePath = System.getProperty("user.dir")+"\\src\\test\\java\\pers\\kelvin\\util\\config.json";
        HashMap<String, String> configMap = JsonConfigUtil.get(configFilePath);
        System.out.println(configMap);
    }
}