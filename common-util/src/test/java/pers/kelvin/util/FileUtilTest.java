package pers.kelvin.util;

import org.apache.jmeter.util.JMeterUtils;
import org.testng.annotations.Test;
import pers.kelvin.util.json.JsonUtil;

import java.io.File;

import static org.testng.Assert.*;

public class FileUtilTest {

    @Test
    public void testReadEnvFile() {
        String path = "E:\\JMeter\\apache-jmeter-5.1.1" + File.separator + "config" + File.separator + "uat.env";
        String jsonStr = FileUtil.readEnvFile(path);
        Object json = JsonUtil.fromJson(jsonStr, JsonUtil.mapType);
        System.out.println(json.toString());

    }
}