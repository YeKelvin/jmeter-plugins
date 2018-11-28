package pers.kelvin.util;

import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * User: KelvinYe
 * Date: 2018-03-28
 * Time: 14:46
 */
public class ConfigTest {

    @Test
    public void testGet() throws Exception {
        String configFilePath = "F:\\Jmeter\\apache-jmeter-4.0\\config\\config.json";
        HashMap<String, String> configMap = Config.get(configFilePath);
        System.out.println(configMap);
    }
}