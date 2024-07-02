package org.example.exceptions;

/**
 * Data validation exception class.
 */
public class DataValidationException extends RuntimeException {

    /**
     * Data validation exception constructor.
     */
    public DataValidationException(String message) {
        super(message);
    }
}
