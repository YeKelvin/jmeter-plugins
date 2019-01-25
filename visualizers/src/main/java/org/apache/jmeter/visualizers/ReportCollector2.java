package org.apache.jmeter.visualizers;


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


public class ReportCollector2 extends AbstractTestElement implements TestStateListener, ThreadListener, SampleListener {
    public static final String REPORT_NAME = "ReportName";
    public static final String IS_APPEND = "IsAppend";
    public static final String JSON_OUTPUT = "JsonOutput";

    private ReportManager reportManager;

    public ReportCollector2() {
        super();
    }

    public ReportCollector2(String name) {
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
        reportManager = new ReportManager(reportPath);
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
        reportManager.flush();
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
        // Non-Gui模式下，命令行存在 -JreportName 参数时，优先读取 reportName
        return JMeterUtils.getPropDefault("reportName", getPropertyAsString(REPORT_NAME));
    }

    private String getIsAppend() {
        // Non-Gui模式下，命令行存在 -JisAppend 参数时，优先读取 isAppend
        return JMeterUtils.getPropDefault("isAppend", getPropertyAsString(IS_APPEND));
    }

    private String getJsonOutput() {
        // Non-Gui模式下，命令行存在 -JjsonOutput 参数时，优先读取 jsonOutput
        return JMeterUtils.getPropDefault("jsonOutput", getPropertyAsString(JSON_OUTPUT));
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
