package pers.kelvin.util.ssh;


import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Properties;

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
     * @throws JSchException
     */
    public static Session getSession(String host, int port, String userName, String password) throws JSchException {
        Session session = new JSch().getSession(userName, host, port);
        session.setPassword(password);
        // 第一次访问服务器时不用输入yes
        session.setConfig("StrictHostKeyChecking", "no");
        // 超时等待时间，单位毫秒
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
     * @throws IOException
     * @throws JSchException
     */
    public static String executeCommand(Session session, String command) throws IOException, JSchException {
        return executeCommand(session, command, "UTF-8");
    }

    /**
     * 执行命令
     *
     * @param session        ssh会话对象
     * @param command        命令
     * @param resultEncoding 结果文本编码
     * @return 执行结果
     * @throws IOException
     * @throws JSchException
     */
    public static String executeCommand(Session session, String command, String resultEncoding)
            throws IOException, JSchException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String result = IOUtils.toString(in, Charset.forName(resultEncoding));
        channelExec.disconnect();
        return result;
    }

    public static void disConnect(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

}
