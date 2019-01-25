package org.apache.jmeter.visualizers;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-23
 * Time     14:47
 */
public class FreemarkerUtil {

    private static final String TEMPLATE_LOCATION = "template";

    public static Template getTemplate(String name) {
        try {
            // 通过Freemaker的Configuration读取相应的ftl
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
            // 设定去哪里读取相应的ftl模板文件
            cfg.setClassForTemplateLoading(FreemarkerUtil.class, TEMPLATE_LOCATION);
            // 在模板文件目录中找到名称为name的文件
            return cfg.getTemplate(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 输出HTML文件
     */
    public static void fprint(String name, Map<String, Object> root, String outFile) {
        FileWriter out = null;
        try {
            // 通过一个文件输出流写到相应的文件中
            File file = new File("C:\\Users\\Administrator\\Desktop\\" + outFile);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, true), StandardCharsets.UTF_8));
            Template temp = getTemplate(name);
            temp.process(root, bw);
            bw.flush();
            bw.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, Object> root = new HashMap<>(1);
        root.put("testSuiteList", "KelvinYe");
        FreemarkerUtil.fprint("test.ftl", root, "testtest.html");
    }
}
