package com.smarte2e.exceptions;

public class SmartRuntimeException extends SmartException {

    public SmartRuntimeException() {
        super();
    }

    public SmartRuntimeException(Throwable e) {
        super(e);
    }
    public SmartRuntimeException(String message) {
        super(message);
    }

    public SmartRuntimeException(String message, Throwable e) {
        super(message, e);
    }
}
