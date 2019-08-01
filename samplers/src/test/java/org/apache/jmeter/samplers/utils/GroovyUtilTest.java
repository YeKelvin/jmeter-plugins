package org.apache.jmeter.samplers.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class GroovyUtilTest {

    @Test
    public void testTransformExpression2() {
        System.out.println(GroovyUtil.transformExpression2("\"success\":true"));
        System.out.println(GroovyUtil.transformExpression2("!\"success\":true"));
        System.out.println(GroovyUtil.transformExpression2("!\"success\":true || !\"errorMsg\":\"no error\""));
        System.out.println(GroovyUtil.transformExpression2("\"success\":true && \"errorMsg\":\"no error\""));
        System.out.println(GroovyUtil.transformExpression2("\"success\":true || \"errorMsg\":\"no error\""));
        System.out.println(GroovyUtil.transformExpression2("!\"success\":true && \"errorMsg\":\"no error\" || \"errorCode\":\"000000\""));
    }
}