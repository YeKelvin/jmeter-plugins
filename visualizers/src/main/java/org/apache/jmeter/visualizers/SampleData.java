package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-01
 * Time     17:09
 */
@Setter
@Getter
@ToString
public class SampleData {

    private TestSuite testSuite;
    private TestCase testCase;
    private TestCaseStep testCaseStep;

    public SampleData() {
        testSuite = new TestSuite();
        testCase = new TestCase();
        testCaseStep = new TestCaseStep();
    }


    @Setter
    @Getter
    @ToString
    private class TestSuite {
        private String title;
        private String startTime;
        private String endTime;
    }

    @Setter
    @Getter
    @ToString
    private class TestCase {
        private String id;
        private String title;
        private String startTime;
        private String endTime;
    }

    @Setter
    @Getter
    @ToString
    private class TestCaseStep {
        private String id;
        private String title;
        private String startTime;
        private String endTime;
        private boolean status;
        private String request;
        private String response;
    }

}
