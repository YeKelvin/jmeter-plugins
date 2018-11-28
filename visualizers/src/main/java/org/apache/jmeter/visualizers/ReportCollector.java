package org.apache.jmeter.visualizers;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;

import java.io.File;


public class ReportCollector extends AbstractTestElement implements TestStateListener, ThreadListener, SampleListener {
    public static final String REPORTNAME = "ReportName";
    public static final String ISAPPEND = "IsAppend";

    private ExtentReports extent;

    public ReportCollector() {
        super();
    }

    public ReportCollector(String name) {
        this();
        setName(name);
    }

    @Override
    public void testStarted() {
        testStarted("local");
    }

    /**
     * 测试计划开始时创建testSuite
     */
    @Override
    public void testStarted(String host) {
        // 测试报告输出到jmeterHome/testreport/reportName.html
        String reportPath = getReportPath();
        extent = ExtentManager.getExtentReports(reportPath, getIsAppend());
        ExtentTest testSuite = extent.createTest(getScriptName());
        ExtentManager.setTestSuite(testSuite);
    }

    @Override
    public void testEnded() {
        testEnded("local");
    }

    /**
     * 整个测试计划结束时将ExtendReport收集的所有数据写入html文件
     */
    @Override
    public void testEnded(String host) {
        extent.flush();
    }

    /**
     * 线程开始时创建ExtendReport.Node
     */
    @Override
    public void threadStarted() {
        String threadName = JMeterContextService.getContext().getThread().getThreadName();
        ExtentManager.putTestCase(threadName, ExtentManager.getTestSuite().createNode(threadName));
    }

    @Override
    public void threadFinished() {
        // not used
    }

    /**
     * 将测试请求和响应写入Html
     */
    @Override
    public void sampleOccurred(SampleEvent sampleEvent) {
        String threadName = JMeterContextService.getContext().getThread().getThreadName();
        SampleResult result = sampleEvent.getResult();
        // 表格化测试数据
        String[][] sampleInfo = {{result.getSampleLabel()}, {result.getSamplerData()}, {result.getResponseDataAsString()}};
        Markup m = MarkupHelper.createTable(sampleInfo);

        if (result.isSuccessful()) {
            ExtentManager.getTestCase(threadName).pass(m);
        } else {
            ExtentManager.getTestCase(threadName).fail(m);
        }
        // 另外把 sample 执行结果打印到控制台
        printConsoleInfo(result.isSuccessful(), threadName);
    }

    @Override
    public void sampleStarted(SampleEvent sampleEvent) {
        // not used
    }

    @Override
    public void sampleStopped(SampleEvent sampleEvent) {
        // not used
    }

    private String getScriptName() {
        String scriptName = FileServer.getFileServer().getScriptName();
        return scriptName.substring(0, scriptName.length() - 4);
    }

    private String getReportName() {
        // 隐藏功能，Non-Gui模式，命令行存在 -JreportName 参数时，优先读取 reportName
        return JMeterUtils.getPropDefault("reportName", getPropertyAsString(REPORTNAME));
    }

    private String getIsAppend() {
        // 隐藏功能，Non-Gui模式，命令行存在 -JisAppend 参数时，优先读取 isAppend
        return JMeterUtils.getPropDefault("isAppend", getPropertyAsString(ISAPPEND));
    }

    private String getReportPath() {
        return JMeterUtils.getJMeterHome() + File.separator +
                "htmlreport" + File.separator +
                suffixHTMLAppend(getReportName());
    }

    private String suffixHTMLAppend(String name) {
        if (name.endsWith(".html")) {
            return name;
        } else {
            return name + ".html";
        }
    }

    private void printConsoleInfo(boolean isSuccessful, String message) {
        if (isSuccessful) {
            System.out.println("[true] - " + message);
        } else {
            System.err.println("[false]- " + message);
        }
    }
}
