package org.apache.jmeter.visualizers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-29
 * Time     11:27
 */
public class JsoupUtil {
    /**
     * 解析 HTML文件，获取最后一个 <script>标签的 js脚本
     *
     * @param htmlFilePath html文件路径
     * @return js脚本内容
     * @throws IOException html文件不存在
     */
    public static String getScriptData(String htmlFilePath) throws IOException {
        Document doc = Jsoup.parse(new File(htmlFilePath), StandardCharsets.UTF_8.name());
        Elements scripts = doc.getElementsByTag("script");
        return scripts.last().data();
    }


}
