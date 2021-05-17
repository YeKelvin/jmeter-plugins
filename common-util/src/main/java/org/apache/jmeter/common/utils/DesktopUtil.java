package org.apache.jmeter.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author  Kelvin.Ye
 * @date    2021-05-15 13:30
 */
public class DesktopUtil {

    private static final Logger log = LoggerFactory.getLogger(DesktopUtil.class);

    public static void openFile(String filePath) {
        if (StringUtils.isBlank(filePath)){
            log.warn("打开文件或目录失败，路径为空");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            log.warn("打开文件或目录失败，路径不存在，路径:[ {} ]", filePath);
        }
        openFile(file);
    }

    public static void openFile(File file){
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            log.warn(ex.getMessage());
            log.debug(ExceptionUtil.getStackTrace(ex));
        }
    }
}
