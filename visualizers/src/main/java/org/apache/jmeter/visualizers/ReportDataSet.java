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
 * Time     16:38
 */
@Setter
@Getter()
@ToString(exclude = "testSuiteMap")
public class ReportDataSet {

    private int startID;

    private ArrayList<TestSuiteData> testSuiteList;

    private transient HashMap<String, TestSuiteData> testSuiteMap;

    public void createTestSuite(String title) {
        if (testSuiteMap == null) {
            testSuiteMap = new HashMap<>(16);
        }
        TestSuiteData testSuite = new TestSuiteData();
        testSuite.setTestSuiteTitle(title);
        testSuiteMap.put(title, testSuite);
    }

    public void putTestSuite(TestSuiteData testSuite) {
        if (testSuiteMap == null) {
            testSuiteMap = new HashMap<>(16);
        }
        testSuiteMap.put(testSuite.getTestSuiteTitle(), testSuite);
    }

    public TestSuiteData getTestSuite(String title) {
        return testSuiteMap.get(title);
    }

    public void testSuiteMapConvertToList() {
        testSuiteList = new ArrayList<>();
        for (String key : testSuiteMap.keySet()) {
            testSuiteList.add(testSuiteMap.get(key));
        }
    }

}
