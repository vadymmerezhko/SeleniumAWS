package com.smarte2e.exceptions;

/**
 * Data validation exception class.
 */
public class SmartValidationException extends SmartRuntimeException {

    public SmartValidationException(String message) {
        super(message);
    }

    public SmartValidationException(String message, Throwable e) {
        super(message, e);
    }

}
