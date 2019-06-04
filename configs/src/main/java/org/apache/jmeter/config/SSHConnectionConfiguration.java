package org.apache.jmeter.config;


import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

/**
 * @author Kelvin.Ye
 */
public class SSHConnectionConfiguration extends ConfigTestElement implements TestStateListener {
    private static final Logger logger = LogUtil.getLogger(ENVDataSet.class);

    public static final String SSH_ADDRESS = "SSHConnectionConfiguration.Address";
    public static final String SSH_USER_NAME = "SSHConnectionConfiguration.UserName";
    public static final String SSH_PASSWORD = "SSHConnectionConfiguration.Password";
    public static final String IS_SSH_CONNECT = "SSHConnectionConfiguration.IsSSHConnect";


    @Override
    public void testStarted() {
        testStarted("local");
    }

    /**
     * 测试开始时把ssh连接信息写入JMeter变量中
     */
    @Override
    public void testStarted(String s) {
        if (isSSHConnect()) {
            getThreadContext().getVariables().put("sshAddress", getSSHAddress());
            getThreadContext().getVariables().put("sshUserName", getSSHUserName());
            getThreadContext().getVariables().put("sshPassword", getSSHPassword());
        }
    }

    @Override
    public void testEnded() {
        testEnded("local");
    }

    @Override
    public void testEnded(String s) {
        // not used
    }

    private String getSSHAddress() {
        return getPropertyAsString(SSH_ADDRESS);
    }

    private String getSSHUserName() {
        return getPropertyAsString(SSH_USER_NAME);
    }

    private String getSSHPassword() {
        return getPropertyAsString(SSH_PASSWORD);
    }

    public boolean isSSHConnect() {
        return JMeterUtils.getPropDefault(
                "isSSHConnect", getPropertyAsBoolean(IS_SSH_CONNECT, true));
    }
}
