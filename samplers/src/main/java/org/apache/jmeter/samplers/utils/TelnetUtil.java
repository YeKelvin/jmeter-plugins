package org.apache.jmeter.samplers.utils;


import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * @author KelvinYe
 */
public class TelnetUtil {

    private static final Logger logger = LogUtil.getLogger(TelnetUtil.class);

    public static final String WINDOWS = "VT220";
    public static final String UNIX = "VT100";
    public static final String LINUX = "VT100";

    private final TelnetClient telnet = new TelnetClient(WINDOWS);
    private InputStreamReader in;
    private PrintStream out;
    // shell响应等待时间
    private int timeout;

    public TelnetUtil(String host, String port) throws IOException {
        initTelnet(host, port, StandardCharsets.UTF_8.name(), 5000);
    }

    public TelnetUtil(String host, String port, String charset) throws IOException {
        initTelnet(host, port, charset, 5000);
    }

    public TelnetUtil(String host, String port, String charset, int timeout) throws IOException {
        initTelnet(host, port, charset, timeout);
    }

    private void initTelnet(String host, String port, String charset, int timeout) throws IOException {
        this.timeout = timeout;
        // 设置连接超时时间ms
        telnet.setConnectTimeout(2000);
        telnet.connect(host, Integer.parseInt(port));
        in = new InputStreamReader(telnet.getInputStream(), Charset.forName(charset));
        out = new PrintStream(telnet.getOutputStream(), true, charset);
    }

    /**
     * 调用dubbo接口
     *
     * @param interfaceName 接口名
     * @param request       请求报文
     * @return 响应报文
     */
    public String invokeDubbo(String interfaceName, String request) throws IOException {
        String result = sendCommand("invoke " + interfaceName + "(" + request + ")");
        logger.debug("invoke result={}", result);
        return result;
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }

        try {
            telnet.disconnect();
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 发送命令并返回结果
     *
     * @param command 命令值
     * @return 响应
     */
    private String sendCommand(String command) throws IOException {
        write(command);
        return readUntil(">");
    }

    /**
     * 写命令并发送
     *
     * @param value 命令值
     */
    private void write(String value) {
        //写命令
        out.println(value);
        //发送命令
        out.flush();
    }

    /**
     * 读消息，直到读到指定字符串中的其中一个才返回，超时则直接返回
     *
     * @param pattern 匹配到该字符串时返回结果
     * @return 返回筛选后的结果
     */
    private String readUntil(String pattern) throws IOException {
        StringBuffer sb = new StringBuffer();
        boolean flag = pattern != null && pattern.length() > 0;
        char lastChar = (char) -1;
        if (flag) {
            lastChar = pattern.charAt(pattern.length() - 1);
        }
        int charCode = -1;
        long startTime = System.currentTimeMillis();
        // read()返回-1时表示input stream已无数据
        while ((charCode = in.read()) != -1) {
            // 超时判断
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > timeout) {
                logger.debug("readUntil 等待超时");
                break;
            }
            char ch = (char) charCode;
            sb.append(ch);
            if (flag) {
                if (ch == lastChar && sb.toString().endsWith(pattern)) {
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
}

