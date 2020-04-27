package org.apache.jmeter.visualizers.data;

import lombok.Getter;
import lombok.ToString;
import org.apache.jmeter.common.utils.TimeUtil;

@Getter
@ToString
public class OverviewInfo {

    private int testSuiteTotal;

    private int testCaseTotal;

    private int testCaseStepTotal;

    private int errorTestSuiteTotal;

    private int errorTestCaseTotal;

    private int errorTestCaseStepTotal;

    private String testSuiteAverageElapsedTime;

    private String testCaseAverageElapsedTime;

    private String testCaseStepAverageElapsedTime;

    public void add(OverviewInfo overviewInfo) {
        testSuiteTotal += overviewInfo.getTestSuiteTotal();
        testCaseTotal += overviewInfo.getTestCaseTotal();
        testCaseStepTotal += overviewInfo.getTestCaseStepTotal();
        errorTestSuiteTotal += overviewInfo.getErrorTestSuiteTotal();
        errorTestCaseTotal += overviewInfo.getErrorTestCaseTotal();
        errorTestCaseStepTotal += overviewInfo.getErrorTestCaseStepTotal();
        setTestSuiteAverageElapsedTime(overviewInfo.getTestSuiteAverageElapsedTime());
        setTestCaseAverageElapsedTime(overviewInfo.getTestCaseAverageElapsedTime());
        setTestCaseStepAverageElapsedTime(overviewInfo.getTestCaseStepAverageElapsedTime());
    }

    public void testSuiteAddOne() {
        testSuiteTotal++;
    }

    public void testCaseAddOne() {
        testCaseTotal++;
    }

    public void testCaseStepAddOne() {
        testCaseStepTotal++;
    }

    public synchronized void errorTestSuiteAddOne() {
        errorTestSuiteTotal++;
    }

    public synchronized void errorTestCaseAddOne() {
        errorTestCaseTotal++;
    }

    public void errorTestCaseStepAddOne() {
        errorTestCaseStepTotal++;
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
    public void setTestCaseStepAverageElapsedTime(String elapsedTime) {
        if (testCaseStepAverageElapsedTime == null) {
            testCaseStepAverageElapsedTime = elapsedTime;
        } else {
            long testCaseStepAverageElapsedTimeAsLong = Long.valueOf(
                    testCaseStepAverageElapsedTime.substring(0, testCaseStepAverageElapsedTime.length() - 2));
            long elapsedTimeAsLong = Long.valueOf(elapsedTime.substring(0, elapsedTime.length() - 2));
            long averageElapsedTime = (testCaseStepAverageElapsedTimeAsLong + elapsedTimeAsLong) / 2;
            testCaseStepAverageElapsedTime = averageElapsedTime + "ms";
        }
    }

}
