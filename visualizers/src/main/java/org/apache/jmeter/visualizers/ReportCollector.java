package org.apache.jmeter.visualizers;


import com.jayway.jsonpath.DocumentContext;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.Interruptible;
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
import org.apache.jmeter.visualizers.data.TestCaseData;
import org.apache.jmeter.visualizers.data.TestCaseStepData;
import org.apache.jmeter.visualizers.data.TestSuiteData;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.TimeUtil;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;

import java.io.File;
import java.util.ArrayList;


/**
 * @author KelvinYe
 */
public class ReportCollector extends AbstractTestElement implements TestStateListener,
        ThreadListener, SampleListener, TestIterationListener, Interruptible {

    public static final String DATE_FORMAT_PATTERN = "yyyy.MM.dd HH:mm:ss";

    public static final String REPORT_NAME = "ReportCollector.reportName";
    public static final String IS_APPEND = "ReportCollector.isAppend";
    public static final String DATA_FILE_NAME = "ReportCollector.dataFileName";

    public ReportCollector() {
        super();
    }

    public ReportCollector(String name) {
        this();
        setName(name);
    }

    @Override
    public void testStarted() {
        testStarted("localhost");
    }

    /**
     * 测试计划（TestPlan）开始时创建 testSuite
     */
    @Override
    public void testStarted(String host) {
        TestSuiteData testSuiteData = new TestSuiteData();
        testSuiteData.setTitle(getScriptName());
        testSuiteData.setStartTime(TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN));
        ReportManager.getReport().putTestSuite(testSuiteData);
    }

    @Override
    public void testEnded() {
        testEnded("localhost");
    }

    /**
     * 测试计划（TestPlan）执行结束后把数据写入 HTML文件中
     */
    @Override
    public void testEnded(String host) {
        TestSuiteData testSuiteData = ReportManager.getReport().getTestSuite(getScriptName());
        testSuiteData.setEndTime(TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN));
        testSuiteData.setElapsedTime(TimeUtil.formatElapsedTimeAsHMS(
                testSuiteData.getStartTime(), testSuiteData.getEndTime(), DATE_FORMAT_PATTERN));

        // 如判断为追加模式且 html文件存在时，以追加模式写入数据，否则以新建模式写入数据
        if (Boolean.valueOf(getIsAppend()) && FileUtil.exists(getReportPath())) {
            ReportManager.appendDataToHtmlFile(getReportPath());
        } else {
            ReportManager.flush(getReportPath());
        }

        // 如命令模式有 -PdataFileName选项，则输出数据文件
        if (isOutputDataFile()) {
            outputDataFile();
        }

        // 测试结束时重置测试数据集
        ReportManager.clearReportDataSet();
    }

    /**
     * 线程(TestCase)开始时创建 testCase
     */
    @Override
    public void threadStarted() {
        TestSuiteData testSuiteData = ReportManager.getReport().getTestSuite(getScriptName());
        TestCaseData testCaseData = new TestCaseData(String.valueOf(testSuiteData.getTestCaseStartID()));
        testCaseData.setTitle(getThreadName());
        testCaseData.setStartTime(TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN));
        testSuiteData.putTestCase(testCaseData);
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

        // 设置测试数据
        SampleResult result = sampleEvent.getResult();
        testCaseStep.setTile(result.getSampleLabel());
        testCaseStep.setElapsedTime(getSampleElapsedTime(result));
        testCaseStep.setRequest(result.getSamplerData());
        testCaseStep.setResponse(result.getResponseDataAsString());

        // 设置测试结果
        if (result.isSuccessful()) {
            testCaseStep.pass();
        } else {
            testCaseStep.fail();
            testCase.fail();
            testSuite.fail();
        }

        // 每次sample执行完毕覆盖testCase的完成时间和耗时
        testCase.setEndTime(TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN));
        testCase.setElapsedTime(TimeUtil.formatElapsedTimeAsMS(
                testCase.getStartTime(), testCase.getEndTime(), DATE_FORMAT_PATTERN));

        // 把测试步骤数据添加至测试案例集中
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
        return scriptName.substring(0, scriptName.length() - 4).trim();
    }

    @Override
    public String getThreadName() {
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

    private String getDataFileName() {
        // Non-Gui下，命令行存在 -JdataFileName 参数时，优先读取 dataFileName
        return JMeterUtils.getPropDefault("dataFileName", getPropertyAsString(DATA_FILE_NAME));
    }

    /**
     * 获取测试报告路径
     */
    private String getReportPath() {
        return JMeterUtils.getJMeterHome() + File.separator +
                "htmlreport" + File.separator +
                appendHTMLSuffix(getReportName());
    }

    /**
     * 获取数据文件路径
     */
    private String getDataFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator +
                "htmlreport" + File.separator + "datafile" + File.separator + getDataFileName();
    }

    /**
     * 为测试报告名称添加.html后缀
     */
    private String appendHTMLSuffix(String name) {
        if (name.endsWith(ReportManager.HTML_SUFFIX)) {
            return name;
        } else {
            return name + ReportManager.HTML_SUFFIX;
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

    /**
     * 获取 sample的耗时
     *
     * @param result SampleResult对象
     * @return 耗时(xxms)
     */
    private String getSampleElapsedTime(SampleResult result) {
        return result.getEndTime() - result.getStartTime() + "ms";
    }


    /**
     * 判断是否输出 DataFile文件
     */
    private boolean isOutputDataFile() {
        return getDataFileName() != null && !"".equals(getDataFileName());
    }

    private void outputDataFile() {
        ReportManager.traverseReportData();
        ArrayList<TestSuiteData> testSuiteList = ReportManager.getReport().getTestSuiteList();

        // 如为append模式则追加写文件
        if (Boolean.valueOf(getIsAppend()) && FileUtil.exists(getDataFilePath())) {
            // 循环添加数据
            for (TestSuiteData testSuite : testSuiteList) {
                appendListJsonToFile(getDataFilePath(), testSuite);
            }
        } else {
            FileUtil.outputFile(getDataFilePath(), JsonUtil.toJson(testSuiteList));
        }
    }

    /**
     * 读取文件中list json串，追加list内容后回写文件
     *
     * @param filePath 文件路径
     * @param newList  列表对象 或列表json串
     */
    private static void appendListJsonToFile(String filePath, Object newList) {
        String content = FileUtil.readFile(filePath);
        DocumentContext ctx = JsonPathUtil.jsonParse(content);
        ctx.add("$", newList);
        content = ctx.jsonString();
        FileUtil.outputFile(filePath, content);
    }

    @Override
    public boolean interrupt() {
        testEnded();
        return true;
    }

}
