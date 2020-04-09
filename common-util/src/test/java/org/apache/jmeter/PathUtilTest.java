package org.apache.jmeter;

import org.apache.jmeter.common.utils.PathUtil;
import org.testng.annotations.Test;

public class PathUtilTest {

    @Test
    public void testJoin() {
        String path = "E:\\JMeter\\apache-jmeter-5.1.1";
        String path2 = "E:/JMeter/apache-jmeter-5.1.1";
        System.out.println(PathUtil.join(path,"lib","ext"));
        System.out.println(PathUtil.join(path2,"lib","ext"));
    }
}