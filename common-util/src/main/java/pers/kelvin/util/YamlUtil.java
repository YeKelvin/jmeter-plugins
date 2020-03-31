package pers.kelvin.util;


import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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

    public static void writerYaml(File file, Map<String, String> map) {
        try {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
            dumperOptions.setPrettyFlow(false);
            Yaml yaml = new Yaml(dumperOptions);
            yaml.dump(map, new OutputStreamWriter((new FileOutputStream(file)), StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
