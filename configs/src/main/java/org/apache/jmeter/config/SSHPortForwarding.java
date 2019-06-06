package org.apache.jmeter.config;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

/**
 * SSH本地端口转发插件
 *
 * @author Kelvin.Ye
 */
public class SSHPortForwarding extends ConfigTestElement implements TestStateListener {
    private static final Logger logger = LogUtil.getLogger(ENVDataSet.class);

    public static final String SSH_ADDRESS = "SSHPortForwarding.Address";
    public static final String SSH_USER_NAME = "SSHPortForwarding.UserName";
    public static final String SSH_PASSWORD = "SSHPortForwarding.Password";
    public static final String LOCAL_FORWARDING_PORT = "SSHPortForwarding.LocalForwardingPort";
    public static final String REMOTE_ADDRESS = "SSHPortForwarding.RemoteAddress";
    public static final String IS_SSH_PORT_FORWARDING = "SSHPortForwarding.IsSSHPortForwarding";

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
        if (isSSHPortForwarding()) {
            // ssh地址解析
            String[] sshAddres = getSSHAddress().split(":");
            String sshHost = sshAddres[0];
            int sshPort = Integer.valueOf(sshAddres.length == 1 ? "22" : sshAddres[1]);

            // 远程服务器地址解析
            String[] remoteAddres = getRemoteAddress().split(":");
            String remoteHost = remoteAddres[0];
            int remotePort = Integer.valueOf(remoteAddres.length == 1 ? "22" : remoteAddres[1]);

            try {
                // ssh连接
                JSch jsch = new JSch();
                session = jsch.getSession(getSSHUserName(), sshHost, sshPort);
                session.setPassword(getSSHPassword());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
                // 本地端口转发
                int assinged_port = session.setPortForwardingL(getLocalForwardingPort(), remoteHost, remotePort);
                logger.info("本地转发端口=" + assinged_port);
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        }
    }

    @Override
    public void testEnded() {
        testEnded("local");
    }

    /**
     * 测试结束前删除本地转发的端口
     */
    @Override
    public void testEnded(String s) {
        try {
            String[] ports = session.getPortForwardingL();
//            session.delPortForwardingL(ports[0]);
        } catch (JSchException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
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

    public boolean isSSHPortForwarding() {
        return JMeterUtils.getPropDefault(
                "isSSHPortForwarding", getPropertyAsBoolean(IS_SSH_PORT_FORWARDING, true));
    }
}
