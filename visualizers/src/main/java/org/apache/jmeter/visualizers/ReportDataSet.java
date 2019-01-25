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
 * Time     16:38
 */
@Setter
@Getter
@ToString
public class ReportDataSet {

    private int startID;

    private ArrayList<TestSuiteData> testSuiteList;

    public void add(TestSuiteData testSuite) {
        testSuiteList.add(testSuite);
    }

}
