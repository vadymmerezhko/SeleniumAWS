package org.example.exceptions;

/**
 * Timeout exception class.
 */
public class SmartTimeOutException extends RuntimeException {

    public SmartTimeOutException(Throwable e) {
        super(e);
    }

    public SmartTimeOutException(String message) {
        super(message);
    }

    public SmartTimeOutException(String message, Throwable e) {
        super(message, e);
    }
}
