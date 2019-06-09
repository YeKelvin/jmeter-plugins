package pers.kelvin.util.ssh;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.log.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class SSHTelnetClient {
    private static final Logger logger = LogUtil.getLogger(SSHTelnetClient.class);

    private Session session;
    private InputStream in;
    private OutputStream out;
    private PrintStream printStream;
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
        channelShell = (ChannelShell) session.openChannel("shell");
        //从远端到达的数据  从这个流读取
        in = channelShell.getInputStream();
        channelShell.setPty(true);
        channelShell.connect();
        //写入该流的数据  都将发送到远端
        out = channelShell.getOutputStream();
        //使用PrintStream 是为了使用println 这个方法，不需要每次手动给命令加\n
        printStream = new PrintStream(out, true, charsetName);
//        System.out.println(readContainUntil("[" + userName));
        // 读取一次，减少输入流的消息
        readContainUntil("[" + userName);
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
        session.setConfig("PreferredAuthentications", "keyboard-interactive,password,publickey");
        session.setConfig("ForwardAgent", "yes");
        // 超时等待时间，单位毫秒
        session.setTimeout(timeout);
        session.setUserInfo(ui);
        session.connect();

        channelShell = (ChannelShell) session.openChannel("shell");
        //从远端到达的数据  从这个流读取
        in = channelShell.getInputStream();
        channelShell.setPty(true);
        channelShell.connect();
        //写入该流的数据  都将发送到远端
        out = channelShell.getOutputStream();
        //使用PrintStream 是为了使用println 这个方法，不需要每次手动给命令加\n
        printStream = new PrintStream(out, true, charsetName);
//        System.out.println(readContainUntil("[" + userName));
        // 读取一次，减少输入流的消息
        readContainUntil("[" + userName);
    }

    /**
     * telnet连接
     *
     * @param host 服务器地址
     * @param port 端口号
     */
    public void telnet(String host, String port) throws IOException {
        write("telnet " + host + " " + port);
        String telnetResult = readContainUntil("Escape character is '^]'.");
//        System.out.println(telnetResult);
        if (!telnetResult.contains("Escape character is '^]'.")) {
            throw new ServiceException("telnet连接失败\n" + telnetResult);
        }
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
//        System.out.println(readContainUntil("\n"));
//        System.out.println(dubboResponseDataFormat(readContainUntil("elapsed:")));
        readContainUntil("\n");
//        return dubboResponseDataFormat(readContainUntil("elapsed:"));
        return dubboResponseDataFormat(readContainUntil("elapsed:", "Failed to invoke"));
    }

    /**
     * 对telnet dubbo接口返回的响应报文做字符过滤，包含去掉头尾的dubbo>表示和elapsed:耗时，
     * 尽量只保留响应报文数据本身
     */
    private String dubboResponseDataFormat(String responseData) {
        if (StringUtil.isBlank(responseData)) {
            return responseData;
        }
        int startIndex = 0;
        int endIndex = responseData.length() - 1;
        if (responseData.startsWith("dubbo>")) {
            startIndex = 6;
        }
        if (responseData.endsWith("dubbo>")) {
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
    public void write(String command) {
        //写命令
        printStream.println(command);
        //发送命令
        printStream.flush();
    }

    /**
     * 去读消息，直到消息包含指定的字符串，或超时
     */
    public String readContainUntil(String... containStrArray) throws IOException {
        StringBuffer sb = new StringBuffer();
        byte[] tmp = new byte[10240];
        long startTime = System.currentTimeMillis();
        loopRead:
        while (true) {
            // 超时判断
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > timeout) break;
            if (in.available() > 0) {
                int i = in.read(tmp, 0, 10240);
                // read()返回-1时表示input stream已无数据
                if (i < 0) break;
                String result = new String(tmp, 0, i, charsetName);
                sb.append(result);
                for (String containStr : containStrArray) {
                    if (result.contains(containStr)) {
                        break loopRead;
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 读消息，直到消息包含指定的字符串，或超时
     */
    public String readContainUntil(String containStr) throws IOException {
        StringBuffer sb = new StringBuffer();
        byte[] tmp = new byte[10240];
        long startTime = System.currentTimeMillis();
        while (true) {
            // 超时判断
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > timeout) break;
            if (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                // read()返回-1时表示input stream已无数据
                if (i < 0) break;
                String result = new String(tmp, 0, i, charsetName);
                sb.append(result);
                if (result.contains(containStr)) {
                    break;
                }
            }
        }
        return sb.toString();
    }

    private String readUntil(String endStr) throws IOException {
        StringBuffer sb = new StringBuffer();
        boolean flag = endStr != null && endStr.length() > 0;
        char lastChar = (char) -1;
        if (flag) {
            lastChar = endStr.charAt(endStr.length() - 1);
        }
        int charCode = -1;

        // read()返回-1时表示input stream已无数据
        while ((charCode = in.read()) != -1) {
            char ch = (char) charCode;
            sb.append(ch);
            if (flag) {
                if (ch == lastChar && sb.toString().endsWith(endStr)) {
                    return sb.substring(0, sb.length() - 7);
                }
            } else {
                //如果没指定结束标识,匹配到默认结束标识字符时返回结果
                if (ch == '>') {
                    return sb.toString();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
