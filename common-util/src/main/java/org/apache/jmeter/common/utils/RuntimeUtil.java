package org.apache.jmeter.common.utils;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Kelvin.Ye
 * @date 2021-05-16 21:30
 */
public class RuntimeUtil {

    private static final Logger log = LoggerFactory.getLogger(RuntimeUtil.class);

    public static void exec(String[] commands) throws IOException {
        String osName = System.getProperty("os.name");
        if ("Windows".equals(osName)) {
            exec4Windows(commands);
        } else {
            exec4LinuxOrUnix(commands);
        }
    }

    public static void exec(String command) throws IOException {
        String osName = System.getProperty("os.name");
        if ("Windows".equals(osName)) {
            exec4Windows(command);
        } else {
            exec4LinuxOrUnix(command);
        }
    }

    /**
     * Runtime.getRuntime().exec(
     * "cmd /c start file.bat",
     * null,
     * new File("C:\\Users\\User\\Desktop\\"));
     */
    private static void exec4Windows(String[] commands) throws IOException {
        String cmd = Joiner.on(" ").join(commands);
        exec4Windows(cmd);
    }

    private static void exec4Windows(String command) throws IOException {
        log.info("exec cmd:[ cmd /c start {} ]", command);
        Runtime.getRuntime().exec("cmd /c start " + command);
    }

    private static void exec4LinuxOrUnix(String[] commands) throws IOException {
        String cmd = Joiner.on(" ").join(commands);
        exec4LinuxOrUnix(cmd);
    }

    private static void exec4LinuxOrUnix(String command) throws IOException {
        log.info("exec cmd :[ /bin/bash -c {} ]", command);
        Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(System.getProperty("os.name"));
        String jmeterHome = "JMeter/apache-jmeter-5.1.1/bin/";
        String scriptPath = "JMeter/scripts/jmeter脚本取样器.jmx";
        String cmd = "jmeter ";
        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd}, null, new File(jmeterHome));
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
        String line = "";
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
        Thread.sleep(10000);
    }

}
