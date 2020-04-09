package org.apache.jmeter.common.utils;


import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class YamlUtil {

    private static final Logger logger = LogUtil.getLogger(YamlUtil.class);

    private static Yaml yaml = new Yaml();

    public static Object parseYaml(File file) {
        try (
                FileInputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8.name())
        ) {
            return yaml.load(reader);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
            return null;
        }
    }
}
