package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
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

    private boolean status;

    private String title;

    private ArrayList<TestCaseData> testCaseList;

    private transient int testCaseStartID = 1;

    private transient HashMap<String, TestCaseData> testCaseMap;

    public TestSuiteData() {
        testCaseMap = new HashMap<>(16);
    }

    public void createTestCase(String title) {
        TestCaseData testCase = new TestCaseData(String.valueOf(testCaseStartID));
        testCase.setTitle(title);
        testCase.setId(String.valueOf(testCaseStartID++));
        testCaseMap.put(title, testCase);
    }

    public void putTestCase(TestCaseData testCase) {
        testCase.setTestCaseStepPrefixID(String.valueOf(testCaseStartID));
        testCase.setId(String.valueOf(testCaseStartID++));
        testCaseMap.put(testCase.getTitle(), testCase);
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

    public void reverse() {
        Collections.reverse(testCaseList);
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
