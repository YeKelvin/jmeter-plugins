package org.apache.jmeter;

import org.apache.jmeter.common.utils.GoogleAuthenticator;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GoogleAuthenticatorTest {
    @Test
    public void checkCodeTest() throws InvalidKeyException, NoSuchAlgorithmException {
        String secret = "";
        System.out.println(GoogleAuthenticator.getCode(secret));
    }
}