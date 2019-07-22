package org.apache.jmeter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Setter
@Getter
@ToString
public class ExternalScriptResultDTO {

    private boolean isExecuteSuccess;

    private Map<String, Object> externalScriptData;

}
