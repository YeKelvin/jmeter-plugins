package org.apache.jmeter.config;

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
public class ExternalScriptResultDTO {

    private Boolean success;

    private Map<String, Object> externalData;

    private transient SampleResult errorSampleResult;

}
