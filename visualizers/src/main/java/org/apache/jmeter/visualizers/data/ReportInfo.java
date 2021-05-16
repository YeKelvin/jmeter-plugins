package org.apache.jmeter.visualizers.data;

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
public class ReportInfo {

    private String createTime;

    private String lastUpdateTime;

    private String toolName;

}
