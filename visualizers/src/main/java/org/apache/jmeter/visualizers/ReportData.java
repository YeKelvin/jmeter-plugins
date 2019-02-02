package org.apache.jmeter.visualizers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-01
 * Time     17:09
 */
@Setter
@Getter
@ToString
public class ReportData {

    private String testSuiteTile;
    private String testCaseTile;
    private String testCaseStepTile;
    private String id;
    private boolean status;
    private String request;
    private String response;


}
