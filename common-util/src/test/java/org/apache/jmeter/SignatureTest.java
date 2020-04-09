package org.apache.jmeter;

import org.apache.jmeter.common.utils.Signature;
import org.testng.annotations.Test;

public class SignatureTest {

    @Test
    public void testSign() {
        String testJson = "{\"zz\":\"vzz\",\"bb\":22,\"cc\":true,\"dd\":null,\"ee\":[\"ee1\",\"ee2\"],\"ff\":{\"ff1\":\"vff1\",\"ff2\":\"vff2\"},\"gg\":[{\"gg1\":\"vgg1\",\"gg2\":\"vgg2\"},{\"gg3\":\"vgg3\",\"gg4\":\"vgg4\"}]}";
        System.out.println(Signature.sign(testJson, ""));
    }
}