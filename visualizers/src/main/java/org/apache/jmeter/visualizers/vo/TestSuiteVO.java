package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * @author  Kelvin.Ye
 * @date    2019-01-24 16:39
 */
@Setter
@Getter
@ToString(exclude = "testCaseMap")
public class TestSuiteVO {

    private boolean status = true;

    private String startTime;

    private String endTime;

    private String elapsedTime;

    private String title;

    private ArrayList<TestCaseVO> testCaseList;

    private transient int testCaseStartID = 1;

    private transient HashMap<String, TestCaseVO> testCaseMap;

    public TestSuiteVO() {
        testCaseMap = new HashMap<>(16);
    }

    public void createTestCase(String title) {
        TestCaseVO testCase = new TestCaseVO(String.valueOf(testCaseStartID));
        testCase.setTitle(title);
        testCase.setId(String.valueOf(testCaseStartID++));
        testCaseMap.put(title, testCase);
    }

    public void putTestCase(TestCaseVO testCase) {
        testCase.setTestCaseStepPrefixID(String.valueOf(testCaseStartID));
        testCase.setId(String.valueOf(testCaseStartID++));
        testCaseMap.put(testCase.getTitle(), testCase);
    }

    public TestCaseVO getTestCase(String title) {
        return testCaseMap.get(title);
    }

    /**
     * map转list
     */
    public void testCaseMapConvertToList() {
        testCaseList = new ArrayList<>();
        for (String key : testCaseMap.keySet()) {
            testCaseList.add(testCaseMap.get(key));
        }
    }

    /**
     * list升序排序
     */
    public void sort() {
        testCaseList.sort(Comparator.comparingInt(obj -> Integer.parseInt(obj.getId())));
    }

    /**
     * 设置TestSuite为测试通过
     */
    public void pass() {
        status = true;
    }

    /**
     * 设置TestSuite为测试失败
     */
    public void fail() {
        if (!status) {
            return;
        }
        status = false;
    }

}
