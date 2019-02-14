package org.apache.jmeter.visualizers;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import pers.kelvin.util.json.JsonPathUtil;

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
public class JavaScriptUtil {

    private static String TEST_SUITE_LIST_NAME = "testSuiteList: ";

    private static String TEST_SUITE_LIST_VALUE_PATTERN = "testSuiteList: .*";

    private static String REPORT_INFO_NAME = "reportInfo: ";

    private static String REPORT_INFO_VALUE_PATTERN = "reportInfo: .*";

    private static Pattern testSuiteListRegex = Pattern.compile(TEST_SUITE_LIST_VALUE_PATTERN);

    private static Pattern reportInfoRegex = Pattern.compile(REPORT_INFO_VALUE_PATTERN);

    /**
     * 提取js脚本中 testSuiteList的值
     *
     * @param jsContent js脚本
     * @return testSuiteList值
     */
    public static String extractTestSuiteList(String jsContent) {
        Matcher matcher = testSuiteListRegex.matcher(jsContent);
        if (matcher.find()) {
            return matcher.group(0).substring(15);
        }
        return null;
    }

    /**
     * 提取js脚本中 reportInfo的值
     *
     * @param jsContent js脚本
     * @return testSuiteList值
     */
    public static String extractReportInfo(String jsContent) {
        Matcher matcher = reportInfoRegex.matcher(jsContent);
        if (matcher.find()) {
            String result = matcher.group(0);
            return result.substring(12, result.length() - 1);
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
        Matcher matcher = testSuiteListRegex.matcher(jsContent);
        return matcher.replaceAll(
                Matcher.quoteReplacement(TEST_SUITE_LIST_NAME + newValue));
    }

    /**
     * 以替换文本的方式更新 js脚本中的 reportInfo的值
     *
     * @param jsContent js脚本
     * @param newValue  新值
     * @return 更新后的值
     */
    public static String updateReportInfo(String jsContent, String newValue) {
        Matcher matcher = reportInfoRegex.matcher(jsContent);
        return matcher.replaceAll(
                Matcher.quoteReplacement(REPORT_INFO_NAME + newValue + ","));
    }

    /**
     * 更新 reportInfo中的lastUpdateTime的值
     *
     * @param reportInfo     reportInfo的json串
     * @param lastUpdateTime String型的时间
     * @return 更新后的reportInfo的json串
     */
    public static String updateLastUpdateTime(String reportInfo, Object lastUpdateTime) {
        DocumentContext ctx = JsonPathUtil.jsonParse(reportInfo);
        ctx.set("$.lastUpdateTime", lastUpdateTime);
        return ctx.jsonString();
    }

    /**
     * 向 js脚本中的 testSuiteList的添加数据
     *
     * @param testSuiteList testSuiteList值
     * @param appendValue   新值数据
     * @return 添加后的完整的 testSuiteList值
     */
    public static String appendTestSuiteList(String testSuiteList, Object appendValue) {
        DocumentContext ctx = JsonPathUtil.jsonParse(testSuiteList);
        ctx.add("$", appendValue);
        return ctx.jsonString();
    }

    public static void main(String[] args) throws IOException {
        String path = "F:\\Jmeter\\apache-jmeter-3.1\\htmlreport\\repor_test_testt.html";
        //Document doc = JsoupUtil.getDocument(path);
        //Elements scripts = JsoupUtil.extractScriptTabList(doc);
        //Element vueAppJs = scripts.last();
        //String jsContent = vueAppJs.data();

        //ReportDataSet dataSet = new ReportDataSet();
        //dataSet.createTestSuite("testSuite");
        //TestSuiteData testSuite = dataSet.getTestSuite("testSuite");
        //testSuite.createTestCase("testCase");
        //TestCaseData testCase = testSuite.getTestCase("testCase");
        //testCase.createTestCaseStep("testCaseStep");
        //TestCaseStepData testCaseStep = testCase.getTestCaseStep("testCaseStep");
        //testCaseStep.setId("1");
        //testCaseStep.setRequest("request");
        //testCaseStep.setResponse("response");
        //dataSet.testSuiteMapConvertToList();

        //String testSuiteListValue = JavaScriptUtil.extractTestSuiteList(jsContent);
        //testSuiteListValue = JavaScriptUtil.appendTestSuiteList(testSuiteListValue, dataSet.getTestSuiteList());
        //jsContent = JavaScriptUtil.updateTestSuiteList(jsContent, testSuiteListValue);

        //String reportInfoValue = JavaScriptUtil.extractReportInfo(jsContent);
        //JavaScriptUtil.updateLastUpdateTime(reportInfoValue,"2019.02.13 99:99:99");
        //System.out.println(reportInfoValue);


        //DataNode data = (DataNode)vueAppJs.childNode(0);
        //data.setWholeData(jsContent);
        //
        //System.out.println(doc.html());
    }
}

