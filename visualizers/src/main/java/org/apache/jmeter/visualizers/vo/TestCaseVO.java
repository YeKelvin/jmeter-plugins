package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Kelvin.Ye
 * @date 2019-01-24 16:39
 */
@Setter
@Getter
@ToString
public class TestCaseVO {

    private String id;
    private Boolean status;
    private String title;
    private String startTime;
    private String endTime;
    private String elapsedTime;

    private ArrayList<TestStepVO> testCaseStepList;
    private transient String prefixId;
    private transient int startId;

    public TestCaseVO() {
        status = true;
        startId = 1;
        testCaseStepList = new ArrayList<>();
    }

    public TestCaseVO(String prefixId) {
        this();
        this.prefixId = prefixId + "-";
    }

    public void addTestCaseStep(TestStepVO testCaseStep) {
        testCaseStep.setId(prefixId + startId++);
        testCaseStepList.add(testCaseStep);
    }

    /**
     * list升序排序
     */
    public void sort() {
        testCaseStepList.sort(Comparator.comparingInt(obj -> Integer.parseInt(obj.getId())));
    }

    /**
     * 设置TestCase为测试通过
     */
    public void pass() {
        status = true;
    }

    /**
     * 设置TestCase为测试失败
     */
    public void fail() {
        if (!status) {
            return;
        }
        status = false;
    }

}
