package pers.kelvin.util;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PathUtilTest {

    @Test
    public void testJoin() {
        String path = "E:\\JMeter\\apache-jmeter-5.1.1";
        String path2 = "E:/JMeter/apache-jmeter-5.1.1";
        System.out.println(PathUtil.join(path,"lib","ext"));
        System.out.println(PathUtil.join(path2,"lib","ext"));
    }
}