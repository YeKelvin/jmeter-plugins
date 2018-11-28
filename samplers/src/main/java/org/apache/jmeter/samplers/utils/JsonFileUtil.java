package org.apache.jmeter.samplers.utils;


import org.slf4j.Logger;
import pers.kelvin.util.Config;
import pers.kelvin.util.log.LogUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @author KelvinYe
 */
public class JsonFileUtil {
    private static final Logger logger = LogUtil.getLogger(JsonFileUtil.class);

    /**
     * 读取json文件内容
     *
     * @param configFilePath 配置文件路径
     * @param interfaceName  json文件名
     * @return jsonContent
     */
    public static String readJsonFile(String configFilePath, String interfaceName) throws IOException {
        // 获取配置文件中的json模版存放目录
        String rootDir = Config.get(configFilePath).get("templateJsonDir");
        // 根据入參interfaceName去templateJsonDir递归搜索获取绝对路径
        String interfacePath = JsonFileUtil.findInterfacePathByKeywords(rootDir, interfaceName);
        // 根据绝对路径获取json模版内容
        return JsonFileUtil.readJsonFileToString(interfacePath);
    }


    public static ArrayList<File> getJsonFileList(String rootDir) {
        ArrayList<File> fileList = new ArrayList<>();
        File dir = new File(rootDir);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getJsonFileList(file.getAbsolutePath()));
                } else if (file.getName().endsWith("json")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }


    public static String findInterfacePathByKeywords(String rootDir, String interfaceName) {
        logger.debug("templateJsonDir={}", rootDir);
        ArrayList<File> fileList = getJsonFileList(rootDir);
        for (File file : fileList) {
            if (file.getName().contains(interfaceName + ".json")) {
                return file.getAbsolutePath();
            }
        }
        return String.format("%s%s...%s%s.json template file not found.", rootDir, File.separator, File.separator, interfaceName);
    }


    public static String readJsonFileToString(String filePath) throws IOException {
        String content = "";
        File file = new File(filePath);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(reader);
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            content += lineTxt;
        }
        reader.close();
        return content;
    }

}
