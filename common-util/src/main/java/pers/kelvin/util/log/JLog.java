package pers.kelvin.util.log;


import org.apache.jmeter.util.JMeterUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author KelvinYe
 */
public class JLog {
    /**
     * 获取当前系统换行符
     */
    private static String lineSep = System.getProperty("line.separator");
    private static String logName;

    public static String getLogName() {
        if (logName == null) {
            logName = JMeterUtils.getJMeterHome() + File.separator +
                    "log" + File.separator +
                    "error-" + (new SimpleDateFormat("MMdd-HHmmss")).format(new Date()) + ".log";
        }
        return logName;
    }

    public static void error(String className, String methodName, String request, String response, long elapsed) {
        String content = String.format("【%s.%s】-【elapsed %s ms】 ", className, methodName, elapsed) + lineSep +
                request + lineSep +
                response + lineSep + lineSep;
        write(getLogName(), content);
    }

    private static void write(String filePath, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
