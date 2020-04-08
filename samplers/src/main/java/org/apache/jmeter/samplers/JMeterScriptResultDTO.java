package org.apache.jmeter.samplers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Map;

/**
 * @author Kelvin.Ye
 */
@Setter
@Getter
@ToString
public class JMeterScriptResultDTO {

    private Boolean success;

    private Map<String, Object> externalData;

    private transient SampleResult errorSampleResult;

}
