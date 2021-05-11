package org.apache.jmeter.common.utils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Kaiwen.Ye
 */
public class PathUtil {
    private static final String WIN_SEP = "\\";
    private static final String UNIX_SEP = "/";

    private static Pattern separatorPattern = Pattern.compile("\\|/");

    /**
     * 目录路径拼接
     *
     * @param firstPath 父路径
     * @param secondPath  子路径
     * @return 拼接路径
     */
    public static String join(String firstPath, String secondPath) {
        if (firstPath.endsWith(WIN_SEP) || firstPath.endsWith(UNIX_SEP)) {
            firstPath = firstPath.substring(0, firstPath.length() - 1);
        }
        if (secondPath.startsWith(WIN_SEP) || secondPath.startsWith(UNIX_SEP)) {
            secondPath = secondPath.substring(1);
        }
        if (secondPath.endsWith(WIN_SEP) || secondPath.endsWith(UNIX_SEP)) {
            secondPath = secondPath.substring(0, secondPath.length() - 1);
        }
        return firstPath + File.separator + secondPath;
    }

    public static String join(String path, String... names) {
        StringBuffer sb = new StringBuffer(path);
        for (String name : names) {
            sb.append(File.separator).append(name);
        }
        path = sb.toString();
        return separatorPattern.matcher(path).replaceAll(File.separator);
    }
}
