package org.apache.jmeter.visualizers.utils;

import com.jayway.jsonpath.DocumentContext;
import org.apache.jmeter.common.json.JsonPathUtil;
import org.apache.jmeter.common.json.JsonUtil;
import org.apache.jmeter.visualizers.vo.OverviewInfoVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kelvin.Ye
 * @date 2019-01-30 17:45
 */
public class JavaScriptUtil {

    private static final String TEST_SUITE_LIST_NAME = "testSuiteList: ";
    private static final String REPORT_INFO_NAME = "reportInfo: ";
    private static final String OVERVIEW_INFO_NAME = "overviewInfo: ";

    private static final String TEST_SUITE_LIST_VALUE_PATTERN = "testSuiteList: .*";
    private static final String REPORT_INFO_VALUE_PATTERN = "reportInfo: .*";
    private static final String OVERVIEW_INFO_VALUE_PATTERN = "overviewInfo: .*";

    private static final Pattern TEST_SUITE_LIST_REGEX = Pattern.compile(TEST_SUITE_LIST_VALUE_PATTERN);
    private static final Pattern REPORT_INFO_REGEX = Pattern.compile(REPORT_INFO_VALUE_PATTERN);
    private static final Pattern OVERVIEW_INFO_REGEX = Pattern.compile(OVERVIEW_INFO_VALUE_PATTERN);

    /**
     * 提取js脚本中 testSuiteList的值
     *
     * @param jsContent js脚本
     * @return str
     */
    public static String extractTestSuiteList(String jsContent) {
        Matcher matcher = TEST_SUITE_LIST_REGEX.matcher(jsContent);
        if (matcher.find()) {
            return matcher.group(0).substring(TEST_SUITE_LIST_NAME.length());
        }
        return null;
    }

    /**
     * 提取js脚本中 reportInfo的值
     *
     * @param jsContent js脚本
     * @return str
     */
    public static String extractReportInfo(String jsContent) {
        Matcher matcher = REPORT_INFO_REGEX.matcher(jsContent);
        if (matcher.find()) {
            String result = matcher.group(0);
            return result.substring(REPORT_INFO_NAME.length(), result.length() - 1);
        }
        return null;
    }

    /**
     * 提取js脚本中 overviewInfo的值
     *
     * @param jsContent js脚本
     * @return str
     */
    public static String extractOverviewInfo(String jsContent) {
        Matcher matcher = OVERVIEW_INFO_REGEX.matcher(jsContent);
        if (matcher.find()) {
            String result = matcher.group(0);
            return result.substring(OVERVIEW_INFO_NAME.length(), result.length() - 1);
        }
        return null;
    }

    /**
     * 以替换文本的方式更新 js脚本中的 testSuiteList的值
     *
     * @param jsContent     js脚本
     * @param testSuiteJson 新值
     * @return str
     */
    public static String updateTestSuiteList(String jsContent, String testSuiteJson) {
        Matcher matcher = TEST_SUITE_LIST_REGEX.matcher(jsContent);
        return matcher.replaceAll(Matcher.quoteReplacement(TEST_SUITE_LIST_NAME + testSuiteJson));
    }

    /**
     * 以替换文本的方式更新 js脚本中的 reportInfo的值
     *
     * @param jsContent      js脚本
     * @param previousValue  旧值
     * @param lastUpdateTime 最后更新时间
     * @return str
     */
    public static String updateReportInfo(String jsContent, String previousValue, Object lastUpdateTime) {
        String newValue = updateLastUpdateTime(previousValue, lastUpdateTime);
        Matcher matcher = REPORT_INFO_REGEX.matcher(jsContent);
        return matcher.replaceAll(Matcher.quoteReplacement(REPORT_INFO_NAME + newValue + ","));
    }

    /**
     * 以替换文本的方式更新 js脚本中的 overviewInfo的值
     *
     * @param jsContent       js脚本
     * @param previousValue   旧值
     * @param currentOverview 当前的 OverviewInfo对象
     * @return str
     */
    public static String updateOverviewInfo(String jsContent, String previousValue, OverviewInfoVO currentOverview) {
        OverviewInfoVO overviewInfo = JsonUtil.fromJson(previousValue, OverviewInfoVO.class);
        overviewInfo.add(currentOverview);
        Matcher matcher = OVERVIEW_INFO_REGEX.matcher(jsContent);
        return matcher.replaceAll(Matcher.quoteReplacement(OVERVIEW_INFO_NAME + JsonUtil.toJson(overviewInfo) + ","));
    }

    public static String updateOverviewInfo(String jsContent, OverviewInfoVO previousOverview, OverviewInfoVO currentOverview) {
        previousOverview.add(currentOverview);
        Matcher matcher = OVERVIEW_INFO_REGEX.matcher(jsContent);
        return matcher.replaceAll(Matcher.quoteReplacement(OVERVIEW_INFO_NAME + JsonUtil.toJson(previousOverview) + ","));
    }

    /**
     * 更新 reportInfo中的lastUpdateTime的值
     *
     * @param reportInfo     reportInfo的json串
     * @param lastUpdateTime String型的时间
     * @return str
     */
    private static String updateLastUpdateTime(String reportInfo, Object lastUpdateTime) {
        DocumentContext ctx = JsonPathUtil.jsonParse(reportInfo);
        ctx.set("$.lastUpdateTime", lastUpdateTime);
        return ctx.jsonString();
    }

    /**
     * 向 js脚本中的 testSuiteList的添加数据
     *
     * @param testSuiteList testSuiteList值
     * @param testSuite     新testSuite
     * @return str
     */
    public static String appendTestSuiteList(String testSuiteList, Object testSuite) {
        DocumentContext ctx = JsonPathUtil.jsonParse(testSuiteList);
        ctx.add("$", testSuite);
        return ctx.jsonString();
    }

}

