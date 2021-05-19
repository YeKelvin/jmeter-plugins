package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kelvin.Ye
 * @date 2019-01-24 16:39
 */
@Setter
@Getter
@ToString(exclude = "testCaseMap")
public class TestSuiteVO {

    private Boolean status;
    private String title;
    private String startTime;
    private String endTime;
    private String elapsedTime;
    private ArrayList<TestCaseVO> testCaseList;

    private transient Map<Integer, TestCaseVO> testCaseMap;
    private transient volatile int startId;

    public TestSuiteVO() {
        status = true;
        startId = 0;
        testCaseMap = new HashMap<>();
    }

    public synchronized String nextId() {
        return String.valueOf(++startId);
    }

    public void putTestCase(int threadHashCode, TestCaseVO testCase) {
        testCaseMap.put(threadHashCode, testCase);
    }

    public TestCaseVO getTestCase(int threadHashCode) {
        return testCaseMap.get(threadHashCode);
    }

    /**
     * map转list
     */
    public void setTestCaseList() {
        testCaseList = new ArrayList<>();
        testCaseList.addAll(testCaseMap.values());
    }

    /**
     * list升序排序
     */
    public void sort() {
        testCaseList.sort(Comparator.comparingLong(TestCaseVO::getStartTimestamp));
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
