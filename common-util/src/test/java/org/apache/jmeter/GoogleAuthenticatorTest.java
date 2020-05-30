package org.apache.jmeter;

import org.apache.jmeter.common.utils.GoogleAuthenticator;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class GoogleAuthenticatorTest {
    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
        String secret = "";
        System.out.println(GoogleAuthenticator.getCode(secret));
    }
}