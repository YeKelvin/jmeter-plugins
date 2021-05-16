package org.apache.jmeter.visualizers.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author  Kelvin.Ye
 * @date    2019-01-24 16:39
 */
@Setter
@Getter
@ToString
public class TestCaseStepData {

    private String id;

    private boolean status;

    private String elapsedTime;

    private String tile;

    private String description;

    private String request;

    private String response;

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
