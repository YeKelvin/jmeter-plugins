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
@ToString(exclude = "testCaseMap")
public class TestSuiteData {

    private int testSuiteID;

    private boolean status;

    private String testSuiteTitle;

    private ArrayList<TestCaseData> testCaseList;

    private transient HashMap<String, TestCaseData> testCaseMap;

    public void createTestCase(String title) {
        if (testCaseMap == null) {
            testCaseMap = new HashMap<>(16);
        }
        TestCaseData testCase = new TestCaseData();
        testCase.setTestCaseTitle(title);
        testCaseMap.put(title, testCase);
    }

    public void putTestCase(TestCaseData testCase) {
        if (testCaseMap == null) {
            testCaseMap = new HashMap<>(16);
        }
        testCaseMap.put(testCase.getTestCaseTitle(), testCase);
    }

    public TestCaseData getTestCase(String title) {
        return testCaseMap.get(title);
    }

    public void testCaseMapConvertToList() {
        testCaseList = new ArrayList<>();
        for (String key : testCaseMap.keySet()) {
            testCaseList.add(testCaseMap.get(key));
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
