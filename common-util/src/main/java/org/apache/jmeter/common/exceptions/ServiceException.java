package org.apache.jmeter.common.exceptions;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-28
 * Time     14:53
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
