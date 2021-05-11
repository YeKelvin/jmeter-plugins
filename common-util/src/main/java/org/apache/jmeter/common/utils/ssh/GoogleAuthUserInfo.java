package org.apache.jmeter.common.utils.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.GoogleAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * @author Kaiwen.Ye
 */
public class GoogleAuthUserInfo implements UserInfo, UIKeyboardInteractive {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthUserInfo.class);

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
        log.debug("destination={}", destination);
        log.debug("name={}", name);
        log.debug("instruction={}", instruction);
        log.debug("prompt={}", Arrays.toString(prompt));
        log.debug("echo={}", Arrays.toString(echo));

        String[] response = new String[prompt.length];
        if (prompt[0].contains("Verification code:")) {
            response[0] = getGoogleCode();
        } else if (prompt[0].contains("Password:")) {
            response[0] = getPassword();
        } else {
            log.error("超出预期的密码校验");
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
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return code;
    }
}

