package pers.kelvin.util.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import org.slf4j.Logger;
import pers.kelvin.util.GoogleAuthenticator;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class GoogleAuthUserInfo implements UserInfo, UIKeyboardInteractive {
    private static final Logger logger = LogUtil.getLogger(GoogleAuthUserInfo.class);
    private String password;
    private String googleSecretKey;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGoogleSecretKey(String secretKey) {
        this.googleSecretKey = secretKey;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptYesNo(String str) {
        return true;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return false;
    }

    @Override
    public boolean promptPassword(String message) {
        return false;
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo) {
        logger.debug("destination=" + destination);
        logger.debug("name=" + name);
        logger.debug("instruction=" + instruction);
        logger.debug("prompt=" + Arrays.toString(prompt));
        logger.debug("echo=" + Arrays.toString(echo));

        String[] response = new String[prompt.length];
        if (prompt[0].contains("Verification code:")) {
            response[0] = getGoogleCode();
        } else if (prompt[0].contains("Password:")) {
            response[0] = getPassword();
        } else {
            logger.error("超出预期的密码校验");
            response[0] = "";
        }
        return response;
    }

    /**
     * 获取谷歌动态验证码
     */
    private String getGoogleCode() {
        String code = "";
        try {
            code = GoogleAuthenticator.getCode(googleSecretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return code;
    }
}

