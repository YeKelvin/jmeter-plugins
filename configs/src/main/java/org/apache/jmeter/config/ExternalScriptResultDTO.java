package org.apache.jmeter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Map;

@Setter
@Getter
@ToString
public class ExternalScriptResultDTO {

    private boolean isExecuteSuccess;

    private Map<String, Object> externalScriptData;

    private transient SampleResult errorSampleResult;

}
