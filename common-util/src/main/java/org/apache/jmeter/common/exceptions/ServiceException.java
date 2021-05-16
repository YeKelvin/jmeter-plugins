package org.apache.jmeter.common.exceptions;

/**
 * @author  Kelvin.Ye
 * @date    2019-02-28 14:53
 */
public class ServiceException extends RuntimeException {

    private String errorMessage;

    public ServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
