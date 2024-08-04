package org.example.exceptions;

/**
 * Data validation exception class.
 */
public class SmartValidationException extends BaseSmartException {

    public SmartValidationException(String message) {
        super(message);
    }

    public SmartValidationException(String message, Throwable e) {
        super(message, e);
    }

}
