package pers.kelvin.util;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class StringUtilTest {

    @Test
    public void testReplace() {
        String source = "{\"mobile\":\"${mobile}\"}";
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("mobile", "159");
        String result = StringUtil.replace(source, valuesMap);
        System.out.println(result);
        assertEquals(result, "{\"mobile\":\"159\"}");
    }
}