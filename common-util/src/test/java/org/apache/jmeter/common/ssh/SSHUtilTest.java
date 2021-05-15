package org.apache.jmeter.common.ssh;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.testng.annotations.Test;

public class SSHUtilTest {

    @Test
    public void testTelnet() throws JSchException {
        String host = "";
        int port = 22;
        String userName = "";
        String password = "";
        String remoteHost = "";
        String remotePort = "";
        Session session = SSHUtil.getSession(host, port, userName, password);
    }
}