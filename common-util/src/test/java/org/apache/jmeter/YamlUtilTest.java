package org.apache.jmeter;

import org.apache.jmeter.common.utils.YamlUtil;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;

public class YamlUtilTest {

    @Test
    public void testParseYaml() throws Exception {
        String envPath = "E:\\JMeter\\apache-jmeter-5.1.1\\config\\env.yaml";
        File file = new File(envPath);
        @SuppressWarnings("unchecked")
        HashMap<String, String> envMap = (HashMap<String, String>) YamlUtil.parseYaml(file);
        System.out.println(envMap);
    }
}