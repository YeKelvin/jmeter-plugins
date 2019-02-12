package org.apache.jmeter.visualizers;

import org.apache.jmeter.util.JMeterUtils;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pers.kelvin.util.TimeUtil;
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

    public static final String DATE_FORMAT_PATTERN = "yyyy.MM.dd HH:mm:ss";

    public static String REPORT_FILE_SUFFIX = ".html";

    private static ReportDataSet reportDataSet;

    public static void createReportDataSet() {
        reportDataSet = new ReportDataSet();
    }

    public static ReportDataSet getReport() {
        return reportDataSet;
    }

    /**
     * 将测试报告数据集中的map转为list，且list升序排序
     */
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

    private static ReportInfo getReportInfo() {
        ReportInfo reportInfo = new ReportInfo();
        String currentTime = TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN);
        reportInfo.setCreateTime(currentTime);
        reportInfo.setLastUpdateTime(currentTime);
        reportInfo.setToolName("Jmeter " + JMeterUtils.getJMeterVersion());
        return reportInfo;
    }

    /**
     * 组装并返回 freemarker引擎所需变量
     */
    private static Map<String, Object> getTemplateRootData() {
        Map<String, Object> root = new HashMap<>(1);
        traverseReportData();
        root.put("reportInfo", JsonUtil.toJson(getReportInfo()));
        root.put("testSuiteList", JsonUtil.toJson(reportDataSet.getTestSuiteList()));
        return root;
    }

    /**
     * 把测试数据写入html文件中
     *
     * @param reportPath 测试报告路径
     */
    public static void flush(String reportPath) {
        FreemarkerUtil.outputFile("report.ftl", getTemplateRootData(), reportPath);
    }

    /**
     * 把测试数据追加写入html文件中
     *
     * @param reportPath 测试报告路径
     */
    public static void appendDataToHtmlFile(String reportPath) {
        try {
            // 解析html
            Document doc = JsoupUtil.getDocument(reportPath);
            // 设置文档缩进距离
            doc.outputSettings().indentAmount(2);
            // 提取<script>标签（最后一个<script>标签为Vue.js）
            Element vueAppJs = JsoupUtil.extractScriptTabList(doc).last();
            // 获取js脚本内容
            String jsContent = vueAppJs.data();
            // 提取 js中 testSuiteList的值
            String testSuiteListValue = JavaScriptUtil.extractTestSuiteList(jsContent);
            // 按顺序整理测试报告数据
            traverseReportData();
            // 循环向数组添加新数据
            for (TestSuiteData testSuite : reportDataSet.getTestSuiteList()) {
                testSuiteListValue = JavaScriptUtil.appendTestSuiteList(testSuiteListValue, testSuite);
            }
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

    /**
     * 设置reportDataSet为null
     */
    public static void clearReportDataSet() {
        reportDataSet = null;
    }

}
