package org.apache.jmeter.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kaiwen.Ye
 */
public class YamlUtil {

    private static final Logger log = LoggerFactory.getLogger(YamlUtil.class);

    private static final Yaml YAML = new Yaml();

    public static final String YAML_SUFFIX = ".yaml";

    public static Object parseYaml(File file) {
        try (
                FileInputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8.name())
        ) {
            return YAML.load(reader);
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
            return null;
        }
    }

    public static Map<String, Object> parseYamlAsMap(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && filePath.endsWith(YAML_SUFFIX)) {
            try (
                    FileInputStream input = new FileInputStream(file);
                    InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8.name())
            ) {
                return YAML.loadAs(reader, Map.class);
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTrace(e));
            }
        } else {
            log.error("非yaml文件，路径:[ {} ]", filePath);
        }
        return new HashMap<>();
    }
}
