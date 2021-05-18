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

    public void testSuiteAddOne() {
        testSuiteTotal++;
    }

    public void testCaseAddOne() {
        testCaseTotal++;
    }

    public void testCaseStepAddOne() {
        testStepTotal++;
    }

    public synchronized void errorTestSuiteAddOne() {
        errorTestSuiteTotal++;
    }

    public synchronized void errorTestCaseAddOne() {
        errorTestCaseTotal++;
    }

    public void errorTestCaseStepAddOne() {
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
            long testSuiteAverageElapsedTimeAsLong = TimeUtil.hmsElapsedTimeToLong(testSuiteAverageElapsedTime);
            long addedElapsedTimeAsLong = TimeUtil.hmsElapsedTimeToLong(elapsedTime);
            long averageElapsedTime = (testSuiteAverageElapsedTimeAsLong + addedElapsedTimeAsLong) / 2;
            testSuiteAverageElapsedTime = TimeUtil.formatElapsedTimeAsHMS(averageElapsedTime);
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
            long testCaseAverageElapsedTimeAsLong = TimeUtil.msElapsedTimeToLong(testCaseAverageElapsedTime);
            long addedElapsedTimeAsLong = TimeUtil.msElapsedTimeToLong(elapsedTime);
            long averageElapsedTime = (testCaseAverageElapsedTimeAsLong + addedElapsedTimeAsLong) / 2;
            testCaseAverageElapsedTime = TimeUtil.formatElapsedTimeAsMS(averageElapsedTime);
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
            long testStepAverageElapsedTimeAsLong = Long.parseLong(
                    testStepAverageElapsedTime.substring(0, testStepAverageElapsedTime.length() - 2));
            long elapsedTimeAsLong = Long.parseLong(elapsedTime.substring(0, elapsedTime.length() - 2));
            long averageElapsedTime = (testStepAverageElapsedTimeAsLong + elapsedTimeAsLong) / 2;
            testStepAverageElapsedTime = averageElapsedTime + "ms";
        }
    }

}
