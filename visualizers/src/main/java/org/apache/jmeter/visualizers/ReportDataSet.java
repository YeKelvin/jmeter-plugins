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

    private ArrayList<TestSuiteData> testSuiteList;

    private transient HashMap<String, TestSuiteData> testSuiteMap;

    public ReportDataSet() {
        testSuiteMap = new HashMap<>(16);
    }

    public void createTestSuite(String title) {
        TestSuiteData testSuite = new TestSuiteData();
        testSuite.setTitle(title);
        testSuiteMap.put(title, testSuite);
    }

    public void putTestSuite(TestSuiteData testSuite) {
        testSuiteMap.put(testSuite.getTitle(), testSuite);
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
