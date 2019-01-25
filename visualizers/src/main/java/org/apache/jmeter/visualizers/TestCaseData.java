package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

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
public class TestCaseData {

    private String testCaseID;

    private String status;

    private ArrayList<TestCaseStepData> testCaseStepList;

}
