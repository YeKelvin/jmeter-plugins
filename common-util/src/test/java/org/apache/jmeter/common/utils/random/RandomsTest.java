package org.apache.jmeter.common.utils.random;

import org.apache.jmeter.common.utils.Randoms;
import org.junit.Test;

public class RandomsTest {
    @Test
    public void getStringTest() {
        System.out.println(Randoms.getString(6));
    }

}