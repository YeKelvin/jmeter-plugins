package org.apache.jmeter.visualizers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-30
 * Time     17:45
 */
public class JavaScriptContentUtil {

    private static String TEST_SUITE_LIST_NAME = "testSuiteList: ";

    private static String TEST_SUITE_LIST_VALUE_PATTERN = "testSuiteList: .*";

    private static Pattern regex = Pattern.compile(TEST_SUITE_LIST_VALUE_PATTERN);

    /**
     * 提取js脚本中 testSuiteList的值
     *
     * @param jsContent js脚本
     * @return testSuiteList值
     */
    public static String extractTestSuiteList(String jsContent) {
        Matcher matcher = regex.matcher(jsContent);
        if (matcher.find()) {
            return matcher.group(0).substring(15);
        }
        return null;
    }

    /**
     * 以替换文本的方式更新 js脚本中的 testSuiteList的值
     *
     * @param jsContent js脚本
     * @param newValue  新值
     * @return 更新后的值
     */
    public static String updateTestSuiteList(String jsContent, String newValue) {
        Matcher matcher = regex.matcher(jsContent);
        return matcher.replaceAll(Matcher.quoteReplacement(TEST_SUITE_LIST_NAME + newValue));
    }

    /**
     * 向 js脚本中的 testSuiteList的添加数据
     *
     * @param testSuiteList testSuiteList值
     * @param appendValue   新值数据
     * @return 添加后的完整的 testSuiteList值
     */
    public static String appendTestSuiteList(String testSuiteList, Object appendValue) {
        Configuration conf = Configuration.defaultConfiguration();
        DocumentContext ctx = JsonPath.using(conf).parse(testSuiteList);
        ctx.add("$", appendValue);
        return ctx.jsonString();
    }

    public static void main(String[] args) throws IOException {
        String path = "F:\\Jmeter\\apache-jmeter-3.1\\htmlreport\\repor_test_testt.html";
        Document doc = JsoupUtil.getDocument(path);
        Elements scripts = JsoupUtil.extractScriptTabList(doc);
        Element vueAppJs = scripts.last();
        String jsContent = vueAppJs.data();

        ReportDataSet dataSet = new ReportDataSet();
        dataSet.createTestSuite("testSuite");
        TestSuiteData testSuite = dataSet.getTestSuite("testSuite");
        testSuite.createTestCase("testCase");
        TestCaseData testCase = testSuite.getTestCase("testCase");
        testCase.createTestCaseStep("testCaseStep");
        TestCaseStepData testCaseStep = testCase.getTestCaseStep("testCaseStep");
        testCaseStep.setId("1");
        testCaseStep.setRequest("request");
        testCaseStep.setResponse("response");
        dataSet.testSuiteMapConvertToList();

        String testSuiteListValue = JavaScriptContentUtil.extractTestSuiteList(jsContent);
        testSuiteListValue = JavaScriptContentUtil.appendTestSuiteList(testSuiteListValue, dataSet.getTestSuiteList());

        jsContent = JavaScriptContentUtil.updateTestSuiteList(jsContent, testSuiteListValue);
        System.out.println(jsContent);


    }
}

