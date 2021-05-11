package org.apache.jmeter.visualizers;

import org.apache.jmeter.common.utils.TimeUtil;
import org.apache.jmeter.common.utils.json.JsonUtil;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.data.OverviewInfo;
import org.apache.jmeter.visualizers.data.ReportDataSet;
import org.apache.jmeter.visualizers.data.ReportInfo;
import org.apache.jmeter.visualizers.data.TestCaseData;
import org.apache.jmeter.visualizers.data.TestCaseStepData;
import org.apache.jmeter.visualizers.data.TestSuiteData;
import org.apache.jmeter.visualizers.utils.FreemarkerUtil;
import org.apache.jmeter.visualizers.utils.JavaScriptUtil;
import org.apache.jmeter.visualizers.utils.JsoupUtil;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    public static final String HTML_SUFFIX = ".html";

    private static ReportDataSet reportDataSet;

    public static ReportDataSet getReport() {
        if (reportDataSet == null) {
            reportDataSet = new ReportDataSet();
        }
        return reportDataSet;
    }

    /**
     * 将测试报告数据集中的map转为list，且list升序排序
     */
    public static void traverseReportData() {
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

    /**
     * 获取报告创建时间、最后更新时间和jmeter版本信息
     */
    private static ReportInfo createReportInfo() {
        ReportInfo reportInfo = new ReportInfo();
        String currentTime = TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN);
        reportInfo.setCreateTime(currentTime);
        reportInfo.setLastUpdateTime(currentTime);
        reportInfo.setToolName("Jmeter " + JMeterUtils.getJMeterVersion());
        return reportInfo;
    }

    /**
     * 获取报告分析的基础数据，包括总数、成功数、失败数和平均耗时
     */
    private static OverviewInfo createOverviewInfo() {
        OverviewInfo overviewInfo = new OverviewInfo();
        // 添加 TestPlan的数据
        overviewInfo.testSuiteAddOne();
        TestSuiteData testSuite = reportDataSet.getTestSuiteList().get(0);
        overviewInfo.setTestSuiteAverageElapsedTime(testSuite.getElapsedTime());
        if (!testSuite.isStatus()) {
            overviewInfo.errorTestSuiteAddOne();
        }
        // 遍历添加 ThreadGroup的数据
        for (TestCaseData testCase : testSuite.getTestCaseList()) {
            overviewInfo.testCaseAddOne();
            if (!testCase.isStatus()) {
                overviewInfo.errorTestCaseAddOne();
            }
            overviewInfo.setTestCaseAverageElapsedTime(testCase.getElapsedTime());
            // 遍历添加 Sampler的数据
            for (TestCaseStepData testCaseStep : testCase.getTestCaseStepList()) {
                overviewInfo.testCaseStepAddOne();
                if (!testCaseStep.isStatus()) {
                    overviewInfo.errorTestCaseStepAddOne();
                }
                overviewInfo.setTestCaseStepAverageElapsedTime(testCaseStep.getElapsedTime());
            }
        }
        return overviewInfo;
    }

    /**
     * 组装并返回 freemarker引擎所需变量
     */
    private static Map<String, Object> getTemplateRootData() {
        Map<String, Object> root = new HashMap<>(1);
        traverseReportData();
        root.put("reportInfo", JsonUtil.toJson(createReportInfo()));
        root.put("overviewInfo", JsonUtil.toJson(createOverviewInfo()));
        root.put("testSuiteList", JsonUtil.toJson(reportDataSet.getTestSuiteList()));
        return root;
    }

    /**
     * 把测试数据写入html文件中
     *
     * @param reportPath 测试报告路径
     */
    public synchronized static void flush(String reportPath) {
        FreemarkerUtil.outputFile("report.ftl", getTemplateRootData(), reportPath);
    }

    /**
     * 把测试数据追加写入html文件中
     *
     * @param reportPath 测试报告路径
     */
    public synchronized static void appendDataToHtmlFile(String reportPath) {
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
            // 提取 js中 reportInfo的值
            String reportInfoValue = JavaScriptUtil.extractReportInfo(jsContent);
            // 提取 js中 overviewInfo的值
            String overviewInfoValue = JavaScriptUtil.extractOverviewInfo(jsContent);
            // 按顺序整理测试报告数据
            traverseReportData();
            // 循环向数组添加新数据
            for (TestSuiteData testSuite : reportDataSet.getTestSuiteList()) {
                testSuiteListValue = JavaScriptUtil.appendTestSuiteList(testSuiteListValue, testSuite);
            }
            // 更新js脚本内容
            jsContent = JavaScriptUtil.updateOverviewInfo(jsContent, overviewInfoValue, createOverviewInfo());
            jsContent = JavaScriptUtil.updateReportInfo(jsContent, reportInfoValue, TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN));
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
