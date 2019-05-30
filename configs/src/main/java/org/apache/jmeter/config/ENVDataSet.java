package org.apache.jmeter.config;

import com.google.gson.reflect.TypeToken;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * User: KelvinYe
 * Date: 2018-04-08
 * Time: 17:11
 */
public class ENVDataSet extends ConfigTestElement implements TestStateListener {
    private static final Logger logger = LogUtil.getLogger(ENVDataSet.class);
    public static final String CONFIG_NAME = "ConfigName";

    public ENVDataSet() {
        super();
    }

    /**
     * 获取环境变量配置文件名称
     */
    public String getFileName() {
        return JMeterUtils.getPropDefault("configName", getPropertyAsString(ENVDataSet.CONFIG_NAME));
    }

    /**
     * 获取环境变量配置文件路径
     */
    public String getFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "config" + File.separator + getFileName();
    }


    /**
     * 读取json文件转换为HashMap
     *
     * @param filePath 文件路径
     */
    public HashMap<String, String> getEnvMap(String filePath) {
        HashMap<String, String> envMap = new HashMap<>();
        FileInputStream input = null;
        InputStreamReader reader = null;
        try {
            if (isEnvFile(filePath)) {
                input = new FileInputStream(filePath);
                reader = new InputStreamReader(input, Charset.forName("UTF-8"));
                Type hashMap = new TypeToken<HashMap<String, String>>() {
                }.getType();
                envMap = JsonUtil.getGsonInstance().fromJson(reader, hashMap);
            } else {
                logger.error("{}非\".env\"后缀配置文件", filePath);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        } finally {
            // 关闭流
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        }
        return envMap;
    }

    /**
     * 判断文件后缀是否为env
     *
     * @param filePath 文件路径
     */
    private Boolean isEnvFile(String filePath) {
        File file = new File(filePath);
        return file.isFile() && filePath.endsWith("env");
    }

    @Override
    public void testStarted() {
        testStarted("local");
    }

    @Override
    public void testStarted(String s) {
        // 把测试环境配置文件名添加到jmeter线程变量中
        JMeterContextService.getContext().getVariables().put(CONFIG_NAME, getFileName());
        // 将配置文件中的所有属性逐一添加到jmeter线程变量中
        HashMap<String, String> envMap = getEnvMap(getFilePath());
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            JMeterContextService.getContext().getVariables().put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void testEnded() {
        testEnded("local");
    }

    @Override
    public void testEnded(String s) {

    }
}
