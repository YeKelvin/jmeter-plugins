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

    public static void exec(String command, String dirPath) throws IOException {
        exec(command, new File(dirPath));
    }

    public static void exec(String command, File dir) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains(WINDOWS)) {
            exec4Windows(command, dir);
        } else {
            exec4LinuxOrUnix(command, dir);
        }
    }

    private static void exec4Windows(String command, File dir) throws IOException {
        String[] fullCommand = new String[]{"cmd", "/c", "start " + command};
        log.info("workspace:[ {} ] execute:[ {} ]", dir.getPath(), Joiner.on(" ").join(fullCommand));
        Runtime.getRuntime().exec(fullCommand, null, dir);
    }

    private static void exec4LinuxOrUnix(String command, File dir) throws IOException {
        String[] fullCommand = new String[]{"/bin/sh", "-c", "xterm -e " + command};
        log.info("workspace:[ {} ] execute:[ {} ]", dir.getPath(), Joiner.on(" ").join(fullCommand));
        Runtime.getRuntime().exec(fullCommand, null, dir);
    }

}
