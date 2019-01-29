package org.apache.jmeter.visualizers;


import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestIterationListener;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;

import java.io.File;


public class ReportCollector2 extends AbstractTestElement implements TestStateListener,
        ThreadListener, SampleListener, TestIterationListener {
    public static final String REPORT_NAME = "ReportName";
    public static final String IS_APPEND = "IsAppend";
    public static final String JSON_OUTPUT = "JsonOutput";

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
     * 测试计划（TestPlan）开始时创建 testSuite
     */
    @Override
    public void testStarted(String host) {
        ReportManager.createReportDataSet();
        ReportManager.getReport().createTestSuite(getScriptName());

    }

    @Override
    public void testEnded() {
        testEnded("local");
    }

    /**
     * 测试计划（TestPlan）执行结束后把数据写入 HTML文件中
     */
    @Override
    public void testEnded(String host) {
        ReportManager.flush(getReportPath());
        ReportManager.clearReportDataSet();
    }

    /**
     * 线程(TestCase)开始时创建 testCase
     */
    @Override
    public void threadStarted() {
        ReportManager.getReport().getTestSuite(getScriptName()).createTestCase(getThreadName());
    }

    @Override
    public void threadFinished() {
        // pass
    }

    /**
     * 将测试样本（TestSample）的标题、请求和响应数据写入 HTML
     */
    @Override
    public void sampleOccurred(SampleEvent sampleEvent) {
        TestSuiteData testSuite = ReportManager.getReport().getTestSuite(getScriptName());
        TestCaseData testCase = testSuite.getTestCase(getThreadName());
        TestCaseStepData testCaseStep = new TestCaseStepData();

        SampleResult result = sampleEvent.getResult();
        testCaseStep.setTile(result.getSampleLabel());
        testCaseStep.setRequest(result.getSamplerData());
        testCaseStep.setResponse(result.getResponseDataAsString());

        if (result.isSuccessful()) {
            testCaseStep.pass();
            testCase.pass();
            testSuite.pass();
        } else {
            testCaseStep.fail();
            testCase.fail();
            testSuite.fail();
        }
        testCase.putTestCaseStep(testCaseStep);

        // 另外把 sample 执行结果打印到控制台
        printStatusToConsole(result.isSuccessful(), getThreadName());
    }

    @Override
    public void sampleStarted(SampleEvent sampleEvent) {
        // pass
    }

    @Override
    public void sampleStopped(SampleEvent sampleEvent) {
        // pass
    }

    @Override
    public void testIterationStart(LoopIterationEvent loopIterationEvent) {
        // pass
    }

    /**
     * 获取脚本名称（去文件后缀）
     */
    private String getScriptName() {
        String scriptName = FileServer.getFileServer().getScriptName();
        return scriptName.substring(0, scriptName.length() - 4);
    }

    @Override
    public String getThreadName(){
        return JMeterContextService.getContext().getThread().getThreadName();
    }

    private String getReportName() {
        // Non-Gui下，命令行存在 -JreportName 参数时，优先读取 reportName
        return JMeterUtils.getPropDefault("reportName", getPropertyAsString(REPORT_NAME));
    }

    private String getIsAppend() {
        // Non-Gui下，命令行存在 -JisAppend 参数时，优先读取 isAppend
        return JMeterUtils.getPropDefault("isAppend", getPropertyAsString(IS_APPEND));
    }

    private String getJsonOutput() {
        // Non-Gui下，命令行存在 -JjsonOutput 参数时，优先读取 jsonOutput
        return JMeterUtils.getPropDefault("jsonOutput", getPropertyAsString(JSON_OUTPUT));
    }

    private String getReportPath() {
        return JMeterUtils.getJMeterHome() + File.separator +
                "htmlreport" + File.separator +
                appendHTMLSuffix(getReportName());
    }

    /**
     * 为测试报告名称添加.html后缀
     */
    private String appendHTMLSuffix(String name) {
        if (name.endsWith(ReportManager.REPORT_FILE_SUFFIX)) {
            return name;
        } else {
            return name + ReportManager.REPORT_FILE_SUFFIX;
        }
    }

    /**
     * 控制台打印执行状态信息
     */
    private void printStatusToConsole(boolean isSuccessful, String message) {
        if (isSuccessful) {
            System.out.println("[true] - " + message);
        } else {
            System.err.println("[false]- " + message);
        }
    }
}
