package org.example.exception;

/**
 * Timeout exception class.
 */
public class TimeOutException extends RuntimeException {

    /**
     * Timeout exception constructor.
     */
    public TimeOutException(String message) {
        super(message);
    }
}
