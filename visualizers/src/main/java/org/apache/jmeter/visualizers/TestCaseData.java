package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-24
 * Time     16:39
 */
@Setter
@Getter
@ToString(exclude = "testCaseStepMap")
public class TestCaseData {

    private String testCaseID;

    private boolean status;

    private String testCaseTitle;

    private ArrayList<TestCaseStepData> testCaseStepList;

    private transient HashMap<String, TestCaseStepData> testCaseStepMap;

    public void createTestCaseStep(String title) {
        if (testCaseStepMap == null) {
            testCaseStepMap = new HashMap<>(16);
        }
        TestCaseStepData testCaseStep = new TestCaseStepData();
        testCaseStep.setTestCaseStepTile(title);
        testCaseStepMap.put(title, testCaseStep);
    }

    public void putTestCaseStep(TestCaseStepData testCaseStep) {
        if (testCaseStepMap == null) {
            testCaseStepMap = new HashMap<>(16);
        }
        testCaseStepMap.put(testCaseStep.getTestCaseStepTile(), testCaseStep);
    }

    public TestCaseStepData getTestCaseStep(String title) {
        return testCaseStepMap.get(title);
    }

    public void testCaseStepMapConvertToList() {
        testCaseStepList = new ArrayList<>();
        for (String key : testCaseStepMap.keySet()) {
            testCaseStepList.add(testCaseStepMap.get(key));
        }
    }

    public void pass() {
        if (status) {
            return;
        }
        status = true;
    }

    public void fail() {
        if (!status) {
            return;
        }
        status = false;
    }

}
