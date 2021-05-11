package org.apache.jmeter.common.utils.json;


import org.apache.jmeter.common.utils.JsonConfigUtil;
import org.apache.jmeter.common.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author KelvinYe
 */
public class JsonFileUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JsonFileUtil.class);

    /**
     * 读取json文件内容
     *
     * @param configFilePath 配置文件路径
     * @param interfaceName  json文件名
     * @return json模版
     */
    public static String readJsonFile(String configFilePath, String interfaceName) throws IOException {
        // 获取配置文件中的json模版存放目录
        String templateJsonDir = JsonConfigUtil.get(configFilePath).get("templateJsonDir");
        // 根据入參interfaceName去templateJsonDir递归搜索获取绝对路径
        String interfacePath = JsonFileUtil.findInterfacePathByKeywords(templateJsonDir, interfaceName);
        if (interfacePath == null) {
            throw new ServiceException(String.format("\"%s\" json模版不存在", interfaceName));
        }
        // 根据绝对路径获取json模版内容
        return JsonFileUtil.readJsonFileToString(interfacePath);
    }

    /**
     * 在系统名称的目录下查找json模版
     *
     * @param configFilePath 配置文件路径
     * @param systemName     接口所属系统的目录名
     * @param interfaceName  json文件名
     * @return json模版
     */
    public static String readJsonFile(String configFilePath, String systemName, String interfaceName) throws IOException {
        // 获取配置文件中的json模版存放目录
        String templateJsonDir = JsonConfigUtil.get(configFilePath).get("templateJsonDir");
        // 根据入參interfaceName去templateJsonDir递归搜索获取绝对路径
        String interfacePath = JsonFileUtil.findInterfacePathByKeywords(
                templateJsonDir + File.separator + systemName, interfaceName);
        if (interfacePath == null) {
            throw new ServiceException(String.format("\"%s\" json模版不存在", interfaceName));
        }
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
        log.debug("接口搜索路径={}", rootDir);
        ArrayList<File> fileList = getJsonFileList(rootDir);
        for (File file : fileList) {
            if (file.getName().contains(interfaceName + ".json")) {
                return file.getAbsolutePath();
            }
        }
        log.warn(String.format(
                "%s%s...%s%s.json 接口模板不存在", rootDir, File.separator, File.separator, interfaceName));
        // 搜索不到路径时返回null
        return null;
    }


    public static String readJsonFileToString(String filePath) throws IOException {
        StringBuffer content = new StringBuffer();
        File file = new File(filePath);
        if (file.exists()) {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                content.append(lineTxt);
            }
            reader.close();
            return content.toString();
        }
        // 文件不存在时返回null
        return null;
    }

}
