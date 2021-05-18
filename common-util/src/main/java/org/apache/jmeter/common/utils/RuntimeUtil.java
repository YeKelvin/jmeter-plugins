package org.apache.jmeter.common.utils;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Kelvin.Ye
 * @date 2021-05-16 21:30
 */
public class RuntimeUtil {

    private static final Logger log = LoggerFactory.getLogger(RuntimeUtil.class);

    private static final String WINDOWS = "windows";

    public static void exec(String[] cmdarray, File dir) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains(WINDOWS)) {
            exec4Windows(cmdarray, dir);
        } else {
            exec4LinuxOrUnix(cmdarray, dir);
        }
    }

    public static void exec(String command, File dir) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains(WINDOWS)) {
            exec4Windows(command, dir);
        } else {
            exec4LinuxOrUnix(command, dir);
        }
    }

    /**
     * Runtime.getRuntime().exec(
     * "cmd /c start file.bat",
     * null,
     * new File("C:\\Users\\User\\Desktop\\"));
     */
    private static void exec4Windows(String[] cmdarray, File dir) throws IOException {
        String command = Joiner.on(" ").join(cmdarray);
        exec4Windows(command, dir);
    }

    private static void exec4Windows(String command, File dir) throws IOException {
        log.info("exec cmd:[ cmd /c start {} ]", command);
        log.info("dir:[ {} ]", dir.getPath());
        Runtime.getRuntime().exec("cmd /c start " + command, null, dir);
    }

    private static void exec4LinuxOrUnix(String[] cmdarray, File dir) throws IOException {
        String command = Joiner.on(" ").join(cmdarray);
        exec4LinuxOrUnix(command, dir);
    }

    private static void exec4LinuxOrUnix(String command, File dir) throws IOException {
        log.info("exec cmd :[ /bin/bash -c {} ]", command);
        log.info("dir:[ {} ]", dir.getPath());
        Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, dir);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(System.getProperty("os.name"));
//        String jmeterHome = "JMeter/apache-jmeter-5.1.1/bin/";
//        String scriptPath = "JMeter/scripts/jmeter脚本取样器.jmx";
//        String cmd = "jmeter ";
//        Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd}, null, new File(jmeterHome));
//        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
//        String line = "";
//        while ((line = input.readLine()) != null) {
//            System.out.println(line);
//        }
//        process.waitFor();
//        Thread.sleep(10000);
    }

}
