package pers.kelvin.util;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.testng.Assert.*;

public class GoogleAuthenticatorTest {
    @Test
    public void checkCodeTest() throws InvalidKeyException, NoSuchAlgorithmException {
        String secret = "";
        System.out.println(GoogleAuthenticator.getCode(secret));
    }
}