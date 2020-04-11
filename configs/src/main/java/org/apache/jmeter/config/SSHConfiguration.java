package org.apache.jmeter.config;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.apache.jmeter.common.utils.JMeterVarsUtil;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;

/**
 * SSH配置器
 *
 * @author Kelvin.Ye
 */
public class SSHConfiguration extends ConfigTestElement implements TestStateListener, Interruptible {

    private static final Logger logger = LogUtil.getLogger(SSHConfiguration.class);

    public static final String SSH_ADDRESS = "SSHConfiguration.address";
    public static final String SSH_USER_NAME = "SSHConfiguration.userName";
    public static final String SSH_PASSWORD = "SSHConfiguration.password";
    public static final String SSH_PORT_FORWARDING = "SSHConfiguration.sshPortForwarding";
    public static final String LOCAL_FORWARDING_PORT = "SSHConfiguration.localForwardingPort";
    public static final String REMOTE_ADDRESS = "SSHConfiguration.remoteAddress";


    private Session session;

    @Override
    public void testStarted() {
        testStarted("local");
    }

    /**
     * 测试开始时，ssh连接跳板机做本地端口转发
     */
    @Override
    public void testStarted(String s) {
        try {
            if (isSSHPortForwarding()) {
                logger.info("开始端口转发");
                String username = getSSHUserName();
                String password = getSSHPassword();
                int localForwardingPort = getLocalForwardingPort();

                // 拆分ssh address
                String[] sshAddres = getSSHAddress().split(":");
                String sshHost = sshAddres[0];
                int sshPort = Integer.parseInt(sshAddres.length == 1 ? "22" : sshAddres[1]);

                // 拆分remote address
                String[] remoteAddres = getRemoteAddress().split(":");
                String remoteHost = remoteAddres[0];
                int remotePort = Integer.parseInt(remoteAddres.length == 1 ? "22" : remoteAddres[1]);

                // ssh连接
                JSch jsch = new JSch();
                session = jsch.getSession(username, sshHost, sshPort);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");

                // 本地端口转发
                logger.info("本地转发端口={}", localForwardingPort);
                session.setPortForwardingL(localForwardingPort, remoteHost, remotePort);
                session.connect();
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }

    }

    @Override
    public void testEnded() {
        testEnded("localhost");
    }

    /**
     * 测试结束前删除本地转发的端口
     */
    @Override
    public void testEnded(String s) {
        if (isSSHPortForwarding() && session != null) {
            disconnect();
        }
    }

    private void disconnect() {
        try {
            int localForwardingPort = getLocalForwardingPort();
            logger.info("停用端口转发，端口号={}", localForwardingPort);
            session.delPortForwardingL(localForwardingPort);
        } catch (JSchException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
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

    private int getLocalForwardingPort() {
        return getPropertyAsInt(LOCAL_FORWARDING_PORT, 22);
    }

    private String getRemoteAddress() {
        return getPropertyAsString(REMOTE_ADDRESS);
    }

    private boolean isSSHPortForwarding() {
        return JMeterUtils.getPropDefault(
                "sshPortForwarding", JMeterVarsUtil.getDefaultAsBoolean(SSH_PORT_FORWARDING, false));
    }

    @Override
    public boolean interrupt() {
        testEnded();
        return false;
    }
}
