package pers.kelvin.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 *
 * @author KelvinYe
 * Date     2018-09-30
 * Time     10:25
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
