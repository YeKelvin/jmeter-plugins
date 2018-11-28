package org.apache.jmeter.visualizers;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.jmeter.threads.JMeterContextService;

import java.util.HashMap;


/**
 * ExtentReports测试报告管理类
 *
 * @author yekaiwen
 */
public class ExtentManager {
    private static ExtentReports extent;
    private static ExtentTest testSuite;
    private static HashMap<String, ExtentTest> testCase = new HashMap<>();

    public static ExtentReports getExtentReports(String reportName, String isAppend) {
        if (extent == null) {
            createExtentReports(reportName, isAppend);
        }
        return extent;
    }

    private static void createExtentReports(String fileName, String isAppend) {
        extent = new ExtentReports();
        extent.setSystemInfo("Tool", "Jmeter");
        extent.setSystemInfo("Env", JMeterContextService.getContext().getVariables().get("ConfigName"));
        extent.attachReporter(createHtmlReporter(fileName, isAppend));
    }

    /**
     * 创建html报告
     */
    private static ExtentHtmlReporter createHtmlReporter(String fileName, String isAppend) {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
        htmlReporter.config().setChartVisibilityOnOpen(false);
        htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setEncoding("UTF-8");
        htmlReporter.config().setReportName("Jmeter Interface Testing");
        htmlReporter.config().setDocumentTitle("Interface Testing");
        htmlReporter.setAppendExisting(Boolean.valueOf(isAppend));

        return htmlReporter;
    }


    public static ExtentTest getTestSuite() {
        return testSuite;
    }

    public static void setTestSuite(ExtentTest suite) {
        testSuite = suite;
    }

    public static void putTestCase(String key, ExtentTest value) {
        testCase.put(key, value);
    }

    public static ExtentTest getTestCase(String key) {
        return testCase.get(key);
    }

}