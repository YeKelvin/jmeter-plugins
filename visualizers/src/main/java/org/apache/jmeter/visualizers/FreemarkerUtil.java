package org.apache.jmeter.visualizers;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-23
 * Time     14:47
 */
public class FreemarkerUtil {

    private static final Logger logger = LogUtil.getLogger(FreemarkerUtil.class);

    private static final String TEMPLATE_LOCATION = "template";

    /**
     * 获取 freemarker html模版
     *
     * @param name 模版名称
     * @return Template对象
     * @throws IOException 文件不存在
     */
    public static Template getTemplate(String name) throws IOException {
        // 通过Freemaker的Configuration读取相应的ftl
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        // 设定去哪里读取相应的ftl模板文件
        cfg.setClassForTemplateLoading(FreemarkerUtil.class, TEMPLATE_LOCATION);
        // 在模板文件目录中找到名称为name的文件
        return cfg.getTemplate(name);
    }


    /**
     * 输出HTML文件
     */
    public static void outputFile(String templateName, Map<String, Object> root, String outputFilePath) {
        try {
            File file = new File(outputFilePath);
            FileUtil.createParentDir(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false), StandardCharsets.UTF_8));
            Template temp = getTemplate(templateName);
            temp.process(root, bw);
            bw.flush();
            bw.close();
        } catch (IOException | TemplateException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

}
