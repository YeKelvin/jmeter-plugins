package pers.kelvin.util.ssh;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @author KelvinYe
 */
public class SSHUtil {
    private Session session;

    public SSHUtil(String host, int port, String userName, String password) {
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        try {
            session = new JSch().getSession(userName, host, port);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            System.err.println("Connection failed");
        }
    }

    public String exeCommand(String command) {
        StringBuffer sb = new StringBuffer();
        try {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.setCommand(command);
            channelExec.connect();
            InputStream in = channelExec.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String buf = "";
            while ((buf = reader.readLine()) != null) {
                sb.append(buf);
            }
            reader.close();
            channelExec.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    public void disconnect() {
        if (session.isConnected() && session != null) {
            session.disconnect();
        }
    }
}
