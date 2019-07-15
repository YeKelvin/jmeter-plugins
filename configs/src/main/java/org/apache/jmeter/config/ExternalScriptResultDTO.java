package org.apache.jmeter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@Setter
@Getter
@ToString
public class ExternalScriptResultDTO {

    private boolean isExecuteSuccess;

    private HashMap<String, Object> externalScriptData;

}
