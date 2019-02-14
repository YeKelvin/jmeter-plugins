package pers.kelvin.util.log;


import org.apache.jmeter.util.JMeterUtils;
import pers.kelvin.util.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author KelvinYe
 */
public class JLog {
    private static File logFile;

    public static void error(String className, String methodName, String request, String response, long elapsed) {
        String content = String.format("【%s.%s】-【elapsed %s ms】 ", className, methodName, elapsed) + FileUtil.LINE_SEPARATOR +
                request + FileUtil.LINE_SEPARATOR +
                response + FileUtil.LINE_SEPARATOR + FileUtil.LINE_SEPARATOR;
        write(getLogFile(), content);
    }

    public static File getLogFile() {
        if (logFile == null) {
            logFile = new File(getLogName());
            FileUtil.createParentDir(logFile);
        }
        return logFile;
    }

    private static void write(File file, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, true), StandardCharsets.UTF_8));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLogName() {
        return JMeterUtils.getJMeterHome() + File.separator +
                "log" + File.separator +
                "error-" + (new SimpleDateFormat("MMdd-HHmmss")).format(new Date()) + ".log";
    }
}
