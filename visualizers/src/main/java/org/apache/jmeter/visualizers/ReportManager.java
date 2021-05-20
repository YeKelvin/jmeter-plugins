package org.apache.jmeter.visualizers;

import org.apache.jmeter.common.json.JsonUtil;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.TimeUtil;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.utils.FreemarkerUtil;
import org.apache.jmeter.visualizers.utils.JavaScriptUtil;
import org.apache.jmeter.visualizers.utils.JsoupUtil;
import org.apache.jmeter.visualizers.vo.OverviewInfoVO;
import org.apache.jmeter.visualizers.vo.ReportInfoVO;
import org.apache.jmeter.visualizers.vo.TestCaseVO;
import org.apache.jmeter.visualizers.vo.TestDataSet;
import org.apache.jmeter.visualizers.vo.TestStepVO;
import org.apache.jmeter.visualizers.vo.TestSuiteVO;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kelvin.Ye
 * @date 2019-01-24 16:51
 */
public class ReportManager {

    private static final Logger log = LoggerFactory.getLogger(ReportManager.class);

    public static final String DATE_FORMAT_PATTERN = "yyyy.MM.dd HH:mm:ss";
    public static final String HTML_SUFFIX = ".html";

    private static volatile TestDataSet testDataSet;

    public synchronized static TestDataSet getReport() {
        if (testDataSet == null) {
            testDataSet = new TestDataSet();
        }
        return testDataSet;
    }

    /**
     * 将测试报告数据集中的map转为list，且list升序排序
     */
    public synchronized static void traverseReportData() {
        testDataSet.setTestSuiteList();

        for (TestSuiteVO testSuite : testDataSet.getTestSuiteList()) {
            testSuite.setTestCaseList();
            testSuite.sort();

            for (TestCaseVO testCase : testSuite.getTestCaseList()) {
                testCase.sort();
            }
        }
    }

    /**
     * 获取报告创建时间、最后更新时间和jmeter版本信息
     */
    private synchronized static ReportInfoVO createReportInfo() {
        ReportInfoVO reportInfo = new ReportInfoVO();
        String currentTime = TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN);

        reportInfo.setCreateTime(currentTime);
        reportInfo.setLastUpdateTime(currentTime);
        reportInfo.setToolName("JMeter " + JMeterUtils.getJMeterVersion());

        return reportInfo;
    }

    /**
     * 获取报告分析的基础数据，包括总数、成功数、失败数和平均耗时
     */
    private synchronized static OverviewInfoVO createOverviewInfo() {
        OverviewInfoVO overviewInfo = new OverviewInfoVO();

        // 添加 TestPlan的数据
        overviewInfo.increaseTestSuiteTotal();
        TestSuiteVO testSuite = testDataSet.getTestSuiteList().get(0);
        overviewInfo.setTestSuiteAverageElapsedTime(testSuite.getElapsedTime());
        if (!testSuite.getStatus()) {
            overviewInfo.increaseErrorTestSuiteTotal();
        }

        // 遍历添加 ThreadGroup的数据
        for (TestCaseVO testCase : testSuite.getTestCaseList()) {
            overviewInfo.increaseTestCaseTotal();
            if (!testCase.getStatus()) {
                overviewInfo.increaseErrorTestCaseTotal();
            }
            overviewInfo.setTestCaseAverageElapsedTime(testCase.getElapsedTime());

            // 遍历添加 Sampler的数据
            for (TestStepVO testStep : testCase.getTestStepList()) {
                overviewInfo.increaseTestStepTotal();
                if (!testStep.getStatus()) {
                    overviewInfo.increaseErrorTestStepTotal();
                }
                overviewInfo.setTestStepAverageElapsedTime(testStep.getElapsedTime());
            }
        }

        return overviewInfo;
    }

    /**
     * 组装并返回 freemarker引擎所需变量
     */
    private synchronized static Map<String, Object> getTemplateRootData() {
        Map<String, Object> root = new HashMap<>(1);
        traverseReportData();

        root.put("reportInfo", JsonUtil.toJson(createReportInfo()));
        root.put("overviewInfo", JsonUtil.toJson(createOverviewInfo()));
        root.put("testSuiteList", JsonUtil.toJson(testDataSet.getTestSuiteList()));

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

    public synchronized static void appendHtml(File reportFile) throws IOException {
        // 解析html
        Document doc = JsoupUtil.getDocument(reportFile);
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
        OverviewInfoVO previousOverview = JsonUtil.fromJson(overviewInfoValue, OverviewInfoVO.class);
        int previousTestSuiteTotal = previousOverview.getTestSuiteTotal();
        // 按顺序整理测试报告数据
        traverseReportData();
        // 循环向数组添加新数据
        for (TestSuiteVO testSuite : testDataSet.getTestSuiteList()) {
            testSuite.setTitle((++previousTestSuiteTotal) + ReportCollector.LINKER_SYMBOL + testSuite.getTitle().substring(2));
            testSuiteListValue = JavaScriptUtil.appendTestSuiteList(testSuiteListValue, testSuite);
        }
        // 更新js脚本内容
        jsContent = JavaScriptUtil.updateOverviewInfo(jsContent, previousOverview, createOverviewInfo());
        jsContent = JavaScriptUtil.updateReportInfo(jsContent, reportInfoValue, TimeUtil.currentTimeAsString(DATE_FORMAT_PATTERN));
        jsContent = JavaScriptUtil.updateTestSuiteList(jsContent, testSuiteListValue);
        // 将更新后的js写入doc
        ((DataNode) vueAppJs.childNode(0)).setWholeData(jsContent);
        // 将更新后的 html写入文件
        JsoupUtil.documentToFile(doc, reportFile);
    }

    public static void appendHtmlWithLock(String reportPath) {
        FileOutputStream out = null;
        FileLock lock = null;

        try {
            out = new FileOutputStream(getLockFile());
            FileChannel channel = out.getChannel();

            // 进程锁，阻塞方法，当文件锁不可用时，当前进程会被挂起
            log.info("lock file:[ .lock ]");
            lock = channel.lock();
            appendHtml(new File(reportPath));

        } catch (IOException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        } finally {
            releaseLock(lock, out);
        }
    }

    private static File getLockFile() {
        String lockFilePath = JMeterUtils.getJMeterHome() + File.separator + "htmlreport" + File.separator + ".lock";
        File file = new File(lockFilePath);
        if (file.exists()) {
            return file;
        }

        try {
            if (!file.createNewFile()) {
                log.debug(".lock file already exists");
            }
        } catch (IOException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }

        return file;
    }

    private static void releaseLock(FileLock lock, FileOutputStream out) {
        try {
            log.info("release lock:[ .lock ]");
            if (null != lock) {
                lock.release();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * 设置reportDataSet为null
     */
    public synchronized static void clearDataSet() {
        testDataSet = null;
    }
}
