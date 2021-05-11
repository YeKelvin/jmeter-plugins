package org.apache.jmeter.common.exceptions;


/**
 * @author KelvinYe
 */
public class JBDCException extends RuntimeException {

    private String errorMessage;

    public JBDCException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
