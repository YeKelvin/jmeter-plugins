package org.apache.jmeter.config;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.JMeterVarsUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

/**
 * SSH本地端口转发插件
 *
 * @author Kelvin.Ye
 */
public class SSHPortForwarding extends ConfigTestElement implements TestStateListener {

    private static final Logger logger = LogUtil.getLogger(SSHPortForwarding.class);

    public static final String SSH_ADDRESS = "SSHPortForwarding.address";
    public static final String SSH_USER_NAME = "SSHPortForwarding.userName";
    public static final String SSH_PASSWORD = "SSHPortForwarding.password";
    public static final String LOCAL_FORWARDING_PORT = "SSHPortForwarding.localForwardingPort";
    public static final String REMOTE_ADDRESS = "SSHPortForwarding.remoteAddress";
    public static final String IS_SSH_PORT_FORWARDING = "SSHPortForwarding.isSSHPortForwarding";

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
        boolean isSSHPortForwarding = isSSHPortForwarding();
        logger.debug("isSSHPortForwarding=" + isSSHPortForwarding);

        if (isSSHPortForwarding) {
            String username = getSSHUserName();
            String password = getSSHPassword();
            int localForwardingPort = getLocalForwardingPort();
            // ssh地址解析
            String[] sshAddres = getSSHAddress().split(":");
            String sshHost = sshAddres[0];
            int sshPort = Integer.valueOf(sshAddres.length == 1 ? "22" : sshAddres[1]);

            // 远程服务器地址解析
            String[] remoteAddres = getRemoteAddress().split(":");
            String remoteHost = remoteAddres[0];
            int remotePort = Integer.valueOf(remoteAddres.length == 1 ? "22" : remoteAddres[1]);

            logger.debug("username=" + username);
            logger.debug("password=" + password);
            logger.debug("sshHost=" + sshHost);
            logger.debug("sshPort=" + sshPort);
            logger.debug("remoteHost=" + remoteHost);
            logger.debug("remotePort=" + remotePort);
            logger.debug("localForwardingPort=" + localForwardingPort);

            try {
                // ssh连接
                JSch jsch = new JSch();
                session = jsch.getSession(username, sshHost, sshPort);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                // 本地端口转发
                session.setPortForwardingL(localForwardingPort, remoteHost, remotePort);
                session.connect();
                logger.info("本地转发端口=" + localForwardingPort);
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        } else {
            logger.info("用户设置不需要进行端口转发");
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
            try {
                int localForwardingPort = getLocalForwardingPort();
                session.delPortForwardingL(localForwardingPort);
                logger.info("删除转发的端口=" + localForwardingPort);
            } catch (JSchException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            } finally {
                if (session != null) {
                    session.disconnect();
                }
                logger.debug("session连接关闭");
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
                "isSSHPortForwarding", JMeterVarsUtil.getDefaultAsBoolean(IS_SSH_PORT_FORWARDING, true));
    }
}
