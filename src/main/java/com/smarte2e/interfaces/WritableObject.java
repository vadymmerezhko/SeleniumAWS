package com.smarte2e.interfaces;


/**
 * Writable interface for smart objects with value to write.
 */
public interface WritableObject extends ValuableObject {

    /**
     * Sets value.
     * @param value The value.
     * @param <T> The value type.
     */
    <T> void setValue(T value);
}
