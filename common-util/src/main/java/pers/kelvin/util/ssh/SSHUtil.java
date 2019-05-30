package pers.kelvin.util.ssh;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    /**
     * telnet连接
     *
     * @param session ssh会话对象
     * @param host    地址
     * @param port    端口
     * @return
     * @throws IOException
     * @throws JSchException
     */
    public static String telnet(Session session, String host, String port) throws IOException, JSchException {
        return executeCommand(session, "telnet " + host + " " + port);
    }

    /**
     * 登出telnet
     *
     * @param session ssh会话对象
     * @return
     * @throws IOException
     * @throws JSchException
     */
    public static String logoutTelnet(Session session) throws IOException, JSchException {
        return executeCommand(session, "logout");
    }

    /**
     * 调用dubbo接口
     *
     * @param session       ssh会话对象
     * @param interfaceName 接口名称
     * @param requestData   请求报文
     * @return 响应报文
     */
    public String invokeDubbo(Session session, String interfaceName, String requestData)
            throws IOException, JSchException {
        return executeCommand(session, "invoke " + interfaceName + "(" + requestData + ")");
    }

}
