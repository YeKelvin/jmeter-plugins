package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
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

    private String id;

    private boolean status;

    private String title;

    private String startTime;

    private String endTime;

    private String elapsedTime;

    private ArrayList<TestCaseStepData> testCaseStepList;

    private transient String testCaseStepPrefixID;

    private transient int testCaseStepstartID = 1;

    private transient HashMap<String, TestCaseStepData> testCaseStepMap;

    public TestCaseData() {
        testCaseStepMap = new HashMap<>(16);
    }

    public TestCaseData(String prefixID) {
        testCaseStepPrefixID = prefixID + "-";
        testCaseStepMap = new HashMap<>(16);
    }

    public void createTestCaseStep(String title) {
        TestCaseStepData testCaseStep = new TestCaseStepData();
        testCaseStep.setTile(title);
        testCaseStep.setId(testCaseStepPrefixID + testCaseStepstartID++);
        testCaseStepMap.put(title, testCaseStep);
    }

    public void putTestCaseStep(TestCaseStepData testCaseStep) {
        testCaseStep.setId(testCaseStepPrefixID + testCaseStepstartID++);
        testCaseStepMap.put(testCaseStep.getTile(), testCaseStep);
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

    public void sort() {
        testCaseStepList.sort(Comparator.comparing(TestCaseStepData::getId));
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
