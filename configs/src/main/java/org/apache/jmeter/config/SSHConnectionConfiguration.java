package org.apache.jmeter.config;


import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

/**
 * SSH连接信息配置插件
 *
 * @author Kelvin.Ye
 */
public class SSHConnectionConfiguration extends ConfigTestElement implements TestStateListener {

    private static final Logger logger = LogUtil.getLogger(SSHConnectionConfiguration.class);

    public static final String SSH_ADDRESS = "SSHConnectionConfiguration.address";
    public static final String SSH_USER_NAME = "SSHConnectionConfiguration.userName";
    public static final String SSH_PASSWORD = "SSHConnectionConfiguration.password";
    public static final String IS_SSH_CONNECT = "SSHConnectionConfiguration.isSSHConnect";

    @Override
    public void testStarted() {
        testStarted("localhost");
    }

    /**
     * 测试开始时把ssh连接信息写入JMeter变量中
     */
    @Override
    public void testStarted(String s) {
        boolean isSSHConnect = isSSHConnect();
        getThreadContext().getVariables().put("isSSHConnect", String.valueOf(isSSHConnect));
        if (isSSHConnect) {
            getThreadContext().getVariables().put("sshAddress", getSSHAddress());
            getThreadContext().getVariables().put("sshUserName", getSSHUserName());
            getThreadContext().getVariables().put("sshPassword", getSSHPassword());
        }
    }

    @Override
    public void testEnded() {
        testEnded("localhost");
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
                "isSSHConnect", getThreadVariablesAsBooleanDefault(IS_SSH_CONNECT, true));
    }

    private boolean getThreadVariablesAsBooleanDefault(String keyName, boolean DefaultVar) {
        String var = getThreadContext().getVariables().get(keyName);
        return var != null ? Boolean.valueOf(var) : DefaultVar;
    }
}
