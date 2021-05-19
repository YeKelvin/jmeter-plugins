package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.ToString;
import org.apache.jmeter.common.utils.TimeUtil;

/**
 * @author Kaiwen.Ye
 */
@Getter
@ToString
public class OverviewInfoVO {

    private Integer testSuiteTotal;
    private Integer testCaseTotal;
    private Integer testStepTotal;

    private Integer errorTestSuiteTotal;
    private Integer errorTestCaseTotal;
    private Integer errorTestStepTotal;

    private String testSuiteAverageElapsedTime;
    private String testCaseAverageElapsedTime;
    private String testStepAverageElapsedTime;

    public OverviewInfoVO() {
        testSuiteTotal = 0;
        testCaseTotal = 0;
        testStepTotal = 0;
        errorTestSuiteTotal = 0;
        errorTestCaseTotal = 0;
        errorTestStepTotal = 0;
    }

    public void add(OverviewInfoVO overviewInfo) {
        testSuiteTotal += overviewInfo.getTestSuiteTotal();
        testCaseTotal += overviewInfo.getTestCaseTotal();
        testStepTotal += overviewInfo.getTestStepTotal();

        errorTestSuiteTotal += overviewInfo.getErrorTestSuiteTotal();
        errorTestCaseTotal += overviewInfo.getErrorTestCaseTotal();
        errorTestStepTotal += overviewInfo.getErrorTestStepTotal();

        setTestSuiteAverageElapsedTime(overviewInfo.getTestSuiteAverageElapsedTime());
        setTestCaseAverageElapsedTime(overviewInfo.getTestCaseAverageElapsedTime());
        setTestStepAverageElapsedTime(overviewInfo.getTestStepAverageElapsedTime());
    }

    public void increaseTestSuiteTotal() {
        testSuiteTotal++;
    }

    public void increaseTestCaseTotal() {
        testCaseTotal++;
    }

    public void increaseTestStepTotal() {
        testStepTotal++;
    }

    public synchronized void increaseErrorTestSuiteTotal() {
        errorTestSuiteTotal++;
    }

    public synchronized void increaseErrorTestCaseTotal() {
        errorTestCaseTotal++;
    }

    public void increaseErrorTestStepTotal() {
        errorTestStepTotal++;
    }

    /**
     * 设置 TestPlan的平均响应时间，如果已经设置则重新计算平均响应时间后再设置
     *
     * @param elapsedTime 当前 TestPlan的耗时
     */
    public void setTestSuiteAverageElapsedTime(String elapsedTime) {
        if (testSuiteAverageElapsedTime == null) {
            testSuiteAverageElapsedTime = elapsedTime;
        } else {
            long testSuiteAverageElapsedTimestamp = TimeUtil.hmsElapsedTimeToLong(testSuiteAverageElapsedTime);
            long addedElapsedTimestamp = TimeUtil.hmsElapsedTimeToLong(elapsedTime);
            long averageElapsedTimestamp = (testSuiteAverageElapsedTimestamp + addedElapsedTimestamp) / 2;
            testSuiteAverageElapsedTime = TimeUtil.formatElapsedTimeAsHMS(averageElapsedTimestamp);
        }
    }

    /**
     * 设置 ThreadGroup的平均响应时间，如果已经设置则重新计算平均响应时间后再设置
     *
     * @param elapsedTime 当前 ThreadGroup的耗时
     */
    public void setTestCaseAverageElapsedTime(String elapsedTime) {
        if (testCaseAverageElapsedTime == null) {
            testCaseAverageElapsedTime = elapsedTime;
        } else {
            long testCaseAverageElapsedTimestamp = TimeUtil.msElapsedTimeToLong(testCaseAverageElapsedTime);
            long addedElapsedTimestamp = TimeUtil.msElapsedTimeToLong(elapsedTime);
            long averageElapsedTimestamp = (testCaseAverageElapsedTimestamp + addedElapsedTimestamp) / 2;
            testCaseAverageElapsedTime = TimeUtil.formatElapsedTimeAsMS(averageElapsedTimestamp);
        }
    }

    /**
     * 设置 Sampler的平均响应时间，如果已经设置则重新计算平均响应时间后再设置
     *
     * @param elapsedTime 当前 sampler的耗时
     */
    public void setTestStepAverageElapsedTime(String elapsedTime) {
        if (testStepAverageElapsedTime == null) {
            testStepAverageElapsedTime = elapsedTime;
        } else {
            long testStepAverageElapsedTimestamp = getTestStepAverageElapsedTimestamp();
            long elapsedTimestamp = Long.parseLong(elapsedTime.substring(0, elapsedTime.length() - 2));
            long averageElapsedTimestamp = (testStepAverageElapsedTimestamp + elapsedTimestamp) / 2;
            testStepAverageElapsedTime = averageElapsedTimestamp + "ms";
        }
    }

    private long getTestStepAverageElapsedTimestamp() {
        return Long.parseLong(testStepAverageElapsedTime.substring(0, testStepAverageElapsedTime.length() - 2));
    }

}
