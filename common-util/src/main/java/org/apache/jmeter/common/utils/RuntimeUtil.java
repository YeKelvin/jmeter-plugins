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
    private static final String MAC = "mac";
    private static final String LINUX = "linux";

    private static final String WIN_CMD = "c:\\Windows\\System32\\cmd.exe";
    private static final String MAC_TERMINAL = "/Applications/Utilities/Terminal.app/Contents/MacOS/Terminal";
    private static final String MAC_ITERM2 = "/Applications/iTerm.app/Contents/MacOS/iTerm2";


    public static void exec(String command, String dirPath) throws IOException {
        exec(command, new File(dirPath));
    }

    public static void exec(String command, File dir) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains(WINDOWS)) {
            exec4Windows(command, dir);
        } else if (osName.toLowerCase().contains(MAC)) {
            exec4Mac(command, dir);
        }else if (osName.toLowerCase().contains(LINUX)) {
            exec4Linux(command, dir);
        }
    }

    /**
     * cmd /c dir 是执行完dir命令后关闭命令窗口
     * cmd /k dir 是执行完dir命令后不关闭命令窗口
     * cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭
     * cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭
     */
    private static void exec4Windows(String command, File dir) throws IOException {
        String[] fullCommand = new String[]{"cmd", "/c", "start " + command};
        log.info("workspace:[ {} ] execute:[ {} ]", dir.getPath(), Joiner.on(" ").join(fullCommand));
        Runtime.getRuntime().exec(fullCommand, null, dir);
    }

    private static void exec4Mac(String command, File dir) throws IOException {
        String[] fullCommand = new String[]{MAC_ITERM2, command};
        log.info("workspace:[ {} ] execute:[ {} ]", dir.getPath(), Joiner.on(" ").join(fullCommand));
        Runtime.getRuntime().exec(fullCommand, null, dir);
    }

    private static void exec4Linux(String command, File dir) throws IOException {
        String[] fullCommand = new String[]{"/bin/sh", "-c", "xterm -e " + command};
        log.info("workspace:[ {} ] execute:[ {} ]", dir.getPath(), Joiner.on(" ").join(fullCommand));
        Runtime.getRuntime().exec(fullCommand, null, dir);
    }

}
