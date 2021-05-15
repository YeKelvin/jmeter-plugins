package org.apache.jmeter.common.ssh;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * @author Kaiwen.Ye
 */
public class SSHTelnetClient {
    private static final Logger log = LoggerFactory.getLogger(SSHTelnetClient.class);
    private static final String DUBBO_FLAG = "dubbo>";

    private Session session;
    private InputStreamReader in;
    private PrintStream out;
    private ChannelShell channelShell;
    private String charsetName;
    private int timeout;

    /**
     * ssh连接
     *
     * @param host        地址
     * @param port        端口
     * @param userName    用户名称
     * @param password    密码
     * @param charsetName 编码名称
     * @param timeout     超时等待时间
     * @throws JSchException
     * @throws IOException
     */
    public SSHTelnetClient(String host, int port,
                           String userName, String password,
                           String charsetName, int timeout) throws JSchException, IOException {
        this.charsetName = charsetName;
        this.timeout = timeout;
        JSch jsch = new JSch();
        session = jsch.getSession(userName, host, port);
        session.setPassword(password);
        // SSH客户端是否接受SSH服务端的hostkey
        session.setConfig("StrictHostKeyChecking", "no");
        session.setTimeout(timeout);
        session.connect();
        openChannelByShell();

    }

    /**
     * google二次认证 ssh连接
     *
     * @param host        地址
     * @param port        端口
     * @param userName    用户名称
     * @param password    密码
     * @param secretKey   google动态码秘钥
     * @param charsetName 编码名称
     * @param timeout     超时等待时间
     * @throws JSchException
     * @throws IOException
     */
    public SSHTelnetClient(String host, int port,
                           String userName, String password, String secretKey,
                           String charsetName, int timeout) throws JSchException, IOException {
        this.charsetName = charsetName;
        this.timeout = timeout;
        JSch jsch = new JSch();
        GoogleAuthUserInfo ui = new GoogleAuthUserInfo();
        ui.setPassword(password);
        ui.setGoogleSecretKey(secretKey);
        session = jsch.getSession(userName, host, port);
        session.setTimeout(timeout);
        session.setConfig("StrictHostKeyChecking", "no");
        // keyboard-interactive,password,publickey,gssapi-keyex,gssapi-with-mic
        session.setConfig("PreferredAuthentications", "keyboard-interactive,password");
        session.setUserInfo(ui);
        session.connect();
        openChannelByShell();
    }

    private void openChannelByShell() throws JSchException, IOException {
        channelShell = (ChannelShell) session.openChannel("shell");
        in = new InputStreamReader(channelShell.getInputStream(), Charset.forName(charsetName));
        channelShell.setPty(true);
        channelShell.connect();
        out = new PrintStream(channelShell.getOutputStream(), true, charsetName);
        // 及时读取消息
        String connectMsg = readUntil("]$");
        log.debug("connectMsg={}", connectMsg);
    }

    /**
     * telnet连接
     *
     * @param dubboHost 服务器地址
     * @param dubboPort 端口号
     */
    public void telnetDubbo(String dubboHost, String dubboPort) throws IOException {
        write("telnet " + dubboHost + " " + dubboPort);
        String telnetResult = readUntil("Escape character is '^]'.", "]$");
        log.debug("telnetResult={}", telnetResult);
        if (!telnetResult.contains("Escape character is '^]'.")) {
            throw new ServiceException("telnetDubbo 连接失败\n" + telnetResult);
        }
        readUntil("\n");
    }

    /**
     * 调用dubbo接口
     *
     * @param interfaceName 接口名称
     * @param requestData   请求报文
     * @return 响应报文
     */
    public String invokeDubbo(String interfaceName, String requestData) throws IOException {
        write("invoke " + interfaceName + "(" + requestData + ")");
        // 读取invoke的命令消息
        String invokeCommand = readUntil("\n");
        log.debug("invokeCommand={}", invokeCommand);
        // 读取空行
        readUntil("\n");
        String result = readUntil(DUBBO_FLAG);
        log.debug("result={}", result);
        // 第一次invoke命令后会返回一个dubbo>标识符，接收响应后还会再返回一个dubbo>标识符
        // 判断第一次读取是否只读到一个dubbo>标识符，如果是则再读取一次
        if (result.equals("dubbo>")) {
            log.debug("再读一次dubbo响应");
            result = readUntil(DUBBO_FLAG);
            log.debug("result={}", result);
        }
        return extractResponse(result);
    }

    /**
     * 提取响应内容本身，去掉尾部的dubbo>标识符和elapsed:耗时
     */
    private String extractResponse(String responseData) {
        if (StringUtils.isBlank(responseData) || responseData.length() < 7) {
            return responseData;
        }
        responseData = responseData.trim();
        int startIndex = 0;
        int endIndex = responseData.length() - 1;
        if (responseData.startsWith(DUBBO_FLAG)) {
            startIndex = 6;
        }
        if (responseData.endsWith(DUBBO_FLAG)) {
            endIndex = endIndex - 6;
        }
        responseData = responseData.substring(startIndex, endIndex);

//        if (responseData.contains("elapsed:")) {
//            String[] responseDatas = responseData.split("\n");
//            responseData = responseDatas[0];
//        }
        return responseData;
    }

    /**
     * 写命令并发送
     *
     * @param command 命令值
     */
    private void write(String command) {
        //写命令
        out.println(command);
        //发送命令
        out.flush();
    }

    /**
     * 发送exit命令
     */
    private void exit() {
        write("exit");
    }

    /**
     * 发送logout命令
     */
    public void logout() {
        write("logout");
    }


    /**
     * 读消息，直到读到指定字符串中的其中一个才返回，超时则直接返回
     */
    private String readUntil(String... endStrs) throws IOException {
        StringBuffer sb = new StringBuffer();
        boolean flag = endStrs != null && endStrs.length > 0 && !endStrs[0].isEmpty();
        char[] lastChars = null;
        if (flag) {
            lastChars = new char[endStrs.length];
            for (int i = 0; i < endStrs.length; i++) {
                lastChars[i] = endStrs[i].charAt(endStrs[i].length() - 1);
            }
        }
        int charCode = -1;
        long startTime = System.currentTimeMillis();
        // read()返回-1时表示input stream已无数据
        while ((charCode = in.read()) != -1) {
            // 超时判断
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > timeout) {
                log.debug("readUntil 等待超时");
                break;
            }
            char ch = (char) charCode;
            sb.append(ch);
            if (flag) {
                if (isBreak(sb, ch, lastChars, endStrs)) {
                    break;
                }
            } else {
                //如果没指定结束标识,匹配到默认结束标识字符时返回结果
                if (ch == '>') {
                    break;
                }
            }
        }
        return sb.toString();
    }

    private boolean isBreak(StringBuffer sb, char currentChar, char[] lastChars, String[] endStrs) {
        boolean isBreak = false;
        for (int i = 0; i < lastChars.length; i++) {
            if (currentChar == lastChars[i] && sb.toString().endsWith(endStrs[i])) {
                isBreak = true;
                break;
            }
        }
        return isBreak;
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (out != null) {
            out.close();
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        if (channelShell != null) {
            channelShell.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

}
