package org.apache.jmeter.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 *
 * @author  Kelvin.Ye
 * @date    2018-09-30 10:25
 */
public class ExceptionUtil {
    /**
     * 获取异常堆栈信息
     * @param throwable e
     * @return 异常堆栈信息
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();

        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
