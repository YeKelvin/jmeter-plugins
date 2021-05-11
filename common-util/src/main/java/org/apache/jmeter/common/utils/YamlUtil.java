package org.apache.jmeter.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Kaiwen.Ye
 */
public class YamlUtil {

    private static final Logger log = LoggerFactory.getLogger(YamlUtil.class);

    private static final Yaml yaml = new Yaml();

    public static Object parseYaml(File file) {
        try (
                FileInputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8.name())
        ) {
            return yaml.load(reader);
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
            return null;
        }
    }
}
