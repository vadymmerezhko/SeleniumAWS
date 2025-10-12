package com.smarte2e.interfaces;

import com.smarte2e.data.SmartValue;

/**
 * Readable interface for smart objects with value to read.
 */
public interface ReadableObject extends ValuableObject {

    /**
     * Gets smart value.
     * @return The smart value.
     */
    SmartValue getValue();
}
