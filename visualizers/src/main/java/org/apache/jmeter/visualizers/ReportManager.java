package org.apache.jmeter.visualizers;

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

    public static void updateHTML(String reportPath, Object appendObject) throws IOException {
        // 解析html
        Document doc = JsoupUtil.getDocument(reportPath);
        // 提取script标签表列
        Elements scripts = JsoupUtil.extractScriptTabList(doc);
        // 提取js脚本内容（最后一个script标签为Vue的app脚本）
        Element vueAppJs = scripts.last();
        String jsContent = vueAppJs.data();
        // 提取 js中 testSuiteList的值
        String testSuiteListValue = JavaScriptContentUtil.extractTestSuiteList(jsContent);
        // 向数组最后添加新数据
        testSuiteListValue = JavaScriptContentUtil.appendTestSuiteList(testSuiteListValue, appendObject);
        // 更新js脚本内容
        jsContent = JavaScriptContentUtil.updateTestSuiteList(jsContent, testSuiteListValue);

        vueAppJs.text(jsContent);

        JsoupUtil.documentToFile(doc, reportPath);
    }

    public static void clearReportDataSet() {
        reportDataSet = null;
    }

}
