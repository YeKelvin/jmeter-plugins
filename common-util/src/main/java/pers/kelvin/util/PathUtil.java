package pers.kelvin.util;

import java.io.File;

public class PathUtil {
    private static final String WIN_SEP = "\\";
    private static final String UNIX_SEP = "/";

    /**
     * 目录路径拼接
     *
     * @param parentPath 父路径
     * @param childPath  子路径
     * @return 拼接路径
     */
    public static String pathJoin(String parentPath, String childPath) {
        if (parentPath.endsWith(WIN_SEP) || parentPath.endsWith(UNIX_SEP)) {
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        if (childPath.startsWith(WIN_SEP) || childPath.startsWith(UNIX_SEP)) {
            childPath = childPath.substring(1);
        }
        if (childPath.endsWith(WIN_SEP) || childPath.endsWith(UNIX_SEP)) {
            childPath = childPath.substring(0, childPath.length() - 1);
        }
        return parentPath + File.separator + childPath;
    }
}
