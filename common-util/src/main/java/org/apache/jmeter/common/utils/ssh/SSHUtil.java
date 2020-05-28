package org.apache.jmeter.common.utils.ssh;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author KelvinYe
 */
public class SSHUtil {
    /**
     * ssh远程连接
     *
     * @param host     地址
     * @param port     端口号
     * @param userName 登录用户名
     * @param password 登录密码
     * @return ssh会话对象
     */
    public static Session getSession(String host, int port, String userName, String password) throws JSchException {
        Session session = new JSch().getSession(userName, host, port);
        session.setPassword(password);
        // SSH客户端是否接受SSH服务端的hostkey
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(5000);
        session.connect();
        return session;
    }

    /**
     * 执行命令
     *
     * @param session ssh会话对象
     * @param command 命令
     * @return 执行结果
     */
    public static String executeCommand(Session session, String command) throws IOException, JSchException {
        return executeCommand(session, command, StandardCharsets.UTF_8.name());
    }

    /**
     * 执行命令
     *
     * @param session        ssh会话对象
     * @param command        命令
     * @param resultEncoding 结果文本编码
     * @return 执行结果
     */
    public static String executeCommand(Session session, String command, String resultEncoding)
            throws IOException, JSchException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String result = IOUtils.toString(new InputStreamReader(in, Charset.forName(resultEncoding)));
        channelExec.disconnect();
        return result;
    }

    public static void disConnect(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

}
