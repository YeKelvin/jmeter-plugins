package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Kelvin.Ye
 * @date 2019-01-24 16:39
 */
@Setter
@Getter
@ToString
public class TestStepVO {

    private String id;
    private Boolean status;
    private String tile;
    private String description;
    private String request;
    private String response;
    private String elapsedTime;

    private transient Long startTimestamp;

    public TestStepVO() {
        status = true;
    }

    /**
     * 设置TestCaseStep为测试通过
     */
    public void pass() {
        status = true;
    }

    /**
     * 设置TestCaseStep为测试失败
     */
    public void fail() {
        status = false;
    }

}
