package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author  Kelvin.Ye
 * @date     2019-01-24 16:38
 */
@Setter
@Getter
@ToString(exclude = "testSuiteMap")
public class ReportDataSet {

    private ArrayList<TestSuiteVO> testSuiteList;

    private transient HashMap<String, TestSuiteVO> testSuiteMap;

    public ReportDataSet() {
        testSuiteMap = new HashMap<>(16);
    }

    public void createTestSuite(String title) {
        TestSuiteVO testSuite = new TestSuiteVO();
        testSuite.setTitle(title);
        testSuiteMap.put(title, testSuite);
    }

    public void putTestSuite(TestSuiteVO testSuite) {
        testSuiteMap.put(testSuite.getTitle(), testSuite);
    }

    public TestSuiteVO getTestSuite(String title) {
        return testSuiteMap.get(title);
    }

    /**
     * map转list
     */
    public void testSuiteMapConvertToList() {
        testSuiteList = new ArrayList<>();
        testSuiteMap.keySet().forEach(key->testSuiteList.add(testSuiteMap.get(key)));
    }

}
