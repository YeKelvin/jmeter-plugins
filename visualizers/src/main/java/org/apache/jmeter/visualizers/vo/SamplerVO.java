package org.apache.jmeter.visualizers.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author  Kelvin.Ye
 * @date    2019-02-01 17:09
 */
@Setter
@Getter
@ToString
public class SamplerVO {

    private static int startId = 0;

    private String id;
    private String testSuiteTitle;
    private String testCaseTitle;
    private String sampleTitle;
    private String startTime;
    private String endTime;
    private String elapsedTime;
    private boolean status;
    private String request;
    private String response;

    public static String getId() {
        return String.valueOf(startId++);
    }

    public static void resetId() {
        startId = 0;
    }
}
