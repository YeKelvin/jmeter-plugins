package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-24
 * Time     16:39
 */
@Setter
@Getter
@ToString
public class TestCaseStepData {

    private String testCaseStepID;

    private boolean status;

    private String testCaseStepTile;

    private String testCaseStepDescription;

    private String testCaseRequest;

    private String testCaseResponse;

    public void pass() {
        status = true;
    }

    public void fail() {
        status = false;
    }

}
