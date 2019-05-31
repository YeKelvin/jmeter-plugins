package org.apache.jmeter.samplers.utils;


import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;


/**
 * @author KelvinYe
 */
public class TelnetUtil {
    public static String WINDOWS = "VT220";
    public static String UNIX = "VT100";
    public static String LINUX = "VT100";

    private TelnetClient telnet = new TelnetClient(WINDOWS);
    private InputStreamReader in;
    private PrintStream out;

    public TelnetUtil(String ip, String port) throws IOException {
        // 设置连接超时时间ms
        telnet.setConnectTimeout(2000);
        telnet.connect(ip, Integer.parseInt(port));
        in = new InputStreamReader(telnet.getInputStream(), Charset.forName("UTF-8"));
        out = new PrintStream(telnet.getOutputStream(), true, "UTF-8");
    }

    public TelnetUtil(String ip, String port, String charset) throws IOException {
        // 设置连接超时时间ms
        telnet.setConnectTimeout(2000);
        telnet.connect(ip, Integer.parseInt(port));
        in = new InputStreamReader(telnet.getInputStream(), Charset.forName(charset));
        out = new PrintStream(telnet.getOutputStream(), true, charset);
    }

    /**
     * 调用dubbo接口
     *
     * @param method  接口名
     * @param request 请求报文
     * @return 响应报文
     */
    public String invokeDubbo(String method, String request) {
        return sendCommand("invoke " + method + "(" + request + ")");
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (telnet != null) {
                telnet.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送命令并返回结果
     *
     * @param command 命令值
     * @return 响应
     */
    private String sendCommand(String command) {
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
     * 读取分析结果
     *
     * @param pattern 匹配到该字符串时返回结果
     * @return 返回筛选后的结果
     */
    private String readUntil(String pattern) {
        StringBuffer sb = new StringBuffer();
        boolean flag = pattern != null && pattern.length() > 0;
        char lastChar = (char) -1;
        if (flag) {
            lastChar = pattern.charAt(pattern.length() - 1);
        }
        int charCode = -1;
        try {
            while ((charCode = in.read()) != -1) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

