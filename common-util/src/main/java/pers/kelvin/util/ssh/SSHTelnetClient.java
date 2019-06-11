package pers.kelvin.util.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.log.LogUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

public class SSHTelnetClient {
    private static final Logger logger = LogUtil.getLogger(SSHTelnetClient.class);
    private static final String DUBBO_FLAG = "dubbo>";

    private Session session;
    private InputStreamReader in;
    private PrintStream out;
    private ChannelShell channelShell;
    private String charsetName;
    private int timeout;

    public SSHTelnetClient(String host, int port,
                           String userName, String password,
                           String charsetName, int timeout) throws JSchException, IOException {
        this.charsetName = charsetName;
        this.timeout = timeout;
        JSch jsch = new JSch();
        session = jsch.getSession(userName, host, port);
        session.setPassword(password);
        // 第一次访问服务器时不用输入yes
        session.setConfig("StrictHostKeyChecking", "no");
        // 超时等待时间，单位毫秒
        session.setTimeout(timeout);
        session.connect();
        openChannelByShell();

    }

    public SSHTelnetClient(String host, int port,
                           String userName, String password,
                           String charsetName, int timeout,
                           String secretKey) throws JSchException, IOException {
        this.charsetName = charsetName;
        this.timeout = timeout;
        JSch jsch = new JSch();
        UserInfo ui = new SSHUserInfo();
        session = jsch.getSession(userName, host, port);
        session.setPassword(password);
        // 第一次访问服务器时不用输入yes
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "keyboard-interactive,password");
        session.setConfig("ForwardAgent", "yes");
        // 超时等待时间，单位毫秒
        session.setTimeout(timeout);
        session.setUserInfo(ui);
        session.connect();
        openChannelByShell();
    }

    private void openChannelByShell() throws JSchException, IOException {
        channelShell = (ChannelShell) session.openChannel("shell");
        //从远端到达的数据  从这个流读取
        in = new InputStreamReader(channelShell.getInputStream(), Charset.forName(charsetName));
//        in = channelShell.getInputStream();
        channelShell.setPty(true);
        channelShell.connect();
        //写入数据流，都将发送到远端使用PrintStream 是为了使用println 这个方法，不需要每次手动给命令加\n
        out = new PrintStream(channelShell.getOutputStream(), true, charsetName);
        // 读取一次，减少输入流的消息
        String connectMsg = readUntil("]$");
        logger.debug("connectMsg=" + connectMsg);
    }

    /**
     * telnet连接
     *
     * @param host 服务器地址
     * @param port 端口号
     */
    public void telnetDubbo(String host, String port) throws IOException {
        write("telnet " + host + " " + port);
        String telnetResult = readUntil("Escape character is '^]'.", "]$");
        logger.debug(telnetResult);
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
        // 读取invoke的命令消息，降低后续消息的解析难度
        String invokeCommand = readUntil("\n");
        logger.debug("invokeCommand=" + invokeCommand);
        // 读取空行，减少无效数据
        readUntil("\n");
        String result = readUntil(DUBBO_FLAG);
        logger.debug(result);
        // 判断第一次读取是否只读到dubbo>标识符，如是则再读取一次
        if (result.equals("dubbo>")) {
            logger.debug("再读一次dubbo响应");
            result = readUntil(DUBBO_FLAG);
            logger.debug(result);
        }
        return extractResponse(result);
    }

    /**
     * 提取响应内容本身，去掉尾部的dubbo>标识符和elapsed:耗时
     */
    private String extractResponse(String responseData) {
        if (StringUtil.isBlank(responseData) || responseData.length() < 7) {
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

        if (responseData.contains("elapsed:")) {
            String[] responseDatas = responseData.split("\n");
            responseData = responseDatas[0];
        }
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
            if (currentTime - startTime > timeout) break;
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
            e.printStackTrace();
        }
        if (channelShell != null) {
            channelShell.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

}
