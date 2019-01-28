package org.apache.jmeter.visualizers;

import pers.kelvin.util.json.JsonUtil;

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
            for (TestCaseData testCase : testSuite.getTestCaseList()) {
                testCase.testCaseStepMapConvertToList();
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

    public static void clearReportDataSet() {
        reportDataSet = null;
    }

    //public static void main(String[] args) {
    //    ReportManager reportManager = new ReportManager("C:\\Users\\Administrator\\Desktop\\test_test.html");
    //    reportManager.getReport().createTestSuite("scriptName");
    //    reportManager.getReport().getTestSuite("scriptName").createTestCase("threadName");
    //
    //    TestSuiteData testSuite = reportManager.getReport().getTestSuite("scriptName");
    //    TestCaseData testCase = testSuite.getTestCase("threadName");
    //    TestCaseStepData testCaseStep = new TestCaseStepData();
    //    testCaseStep.setTestCaseStepTile("sampleLabel");
    //    testCaseStep.setTestCaseRequest("samplerData");
    //    testCaseStep.setTestCaseResponse("responseDataAsString");
    //    testCaseStep.pass();
    //    testCase.pass();
    //    testSuite.pass();
    //    testCase.putTestCaseStep(testCaseStep);
    //    reportManager.flush();
    //}

}
