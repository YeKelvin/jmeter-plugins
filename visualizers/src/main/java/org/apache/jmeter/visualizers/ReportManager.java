package org.apache.jmeter.visualizers;

import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pers.kelvin.util.json.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-24
 * Time     16:51
 */
public class ReportManager {

    public static String REPORT_FILE_SUFFIX = ".html";

    private static ReportDataSet reportDataSet;

    public static void createReportDataSet() {
        reportDataSet = new ReportDataSet();
    }

    public static ReportDataSet getReport() {
        return reportDataSet;
    }

    private static void traverseReportData() {
        reportDataSet.testSuiteMapConvertToList();
        for (TestSuiteData testSuite : reportDataSet.getTestSuiteList()) {
            testSuite.testCaseMapConvertToList();
            testSuite.sort();
            for (TestCaseData testCase : testSuite.getTestCaseList()) {
                testCase.testCaseStepMapConvertToList();
                testCase.sort();
            }
        }
    }

    private static Map<String, Object> getTemplateRootData() {
        Map<String, Object> root = new HashMap<>(1);
        traverseReportData();
        root.put("testSuiteList", JsonUtil.toJson(reportDataSet.getTestSuiteList()));
        return root;
    }

    public static void flush(String reportPath) {
        FreemarkerUtil.outputFile("report.ftl", getTemplateRootData(), reportPath);
    }

    public static void appendDataToHtmlFile(String reportPath) {
        try {
            // 解析html
            Document doc = JsoupUtil.getDocument(reportPath);
            // 设置缩进距离
            doc.outputSettings().indentAmount(2);
            // 提取<script>标签表列
            Elements scripts = JsoupUtil.extractScriptTabList(doc);
            // 提取<script>标签（最后一个script标签为Vue.js）
            Element vueAppJs = scripts.last();
            // 获取js脚本内容
            String jsContent = vueAppJs.data();
            // 提取 js中 testSuiteList的值
            String testSuiteListValue = JavaScriptUtil.extractTestSuiteList(jsContent);
            // 按顺序整理测试报告数据
            traverseReportData();
            // 向数组最后添加新数据
            testSuiteListValue = JavaScriptUtil.appendTestSuiteList(testSuiteListValue, reportDataSet.getTestSuiteList());
            // 更新js脚本内容
            jsContent = JavaScriptUtil.updateTestSuiteList(jsContent, testSuiteListValue);
            // 将更新后的js写入doc
            ((DataNode) vueAppJs.childNode(0)).setWholeData(jsContent);
            // 将更新后的 html写入文件
            JsoupUtil.documentToFile(doc, reportPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearReportDataSet() {
        reportDataSet = null;
    }

}
