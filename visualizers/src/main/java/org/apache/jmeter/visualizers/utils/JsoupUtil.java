package org.apache.jmeter.visualizers.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author  Kelvin.Ye
 * @date    2019-01-29 11:27
 */
public class JsoupUtil {

    /**
     * 获取 html文档对象
     *
     * @param htmlFilePath 文件路径
     * @return Document对象
     * @throws IOException 文件不存在
     */
    public static Document getDocument(String htmlFilePath) throws IOException {
        return Jsoup.parse(new File(htmlFilePath), StandardCharsets.UTF_8.name());
    }

    /**
     * 解析 HTML文件，获取最后一个 <script>标签的 js脚本
     *
     * @param doc Document对象
     * @return js脚本内容
     */
    public static Elements extractScriptTabList(Document doc) {
        return doc.getElementsByTag("script");
    }

    /**
     * 写 html到文件
     *
     * @param doc      Document对象
     * @param filePath 输出文件的路径
     * @throws IOException 异常
     */
    public static void documentToFile(Document doc, String filePath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(
                        new File(filePath), false), StandardCharsets.UTF_8));
        bw.write(doc.html());
        bw.close();
    }

}
