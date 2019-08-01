package org.apache.jmeter.visualizers.data;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OverviewInfo {

    private int testSuiteTotal;

    private int testCaseTotal;

    private int testCaseStepTotal;

    private int errorTestCaseTotal;

    private int errorTestCaseStepTotal;

    public void add(OverviewInfo overviewInfo) {
        testSuiteTotal += overviewInfo.getTestSuiteTotal();
        testCaseTotal += overviewInfo.getTestCaseTotal();
        testCaseStepTotal += overviewInfo.getTestCaseStepTotal();
        errorTestCaseTotal += overviewInfo.getErrorTestCaseTotal();
        errorTestCaseStepTotal += overviewInfo.getErrorTestCaseStepTotal();
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

    public synchronized void errorTestCaseAddOne() {
        errorTestCaseTotal++;
    }

    public void errorTestCaseStepAddOne() {
        errorTestCaseStepTotal++;
    }

}
