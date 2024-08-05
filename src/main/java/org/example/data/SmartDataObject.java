package org.example.data;

import lombok.extern.slf4j.Slf4j;

/**
 * Smart data object class.
 */
@Slf4j
public abstract class SmartDataObject {
    private final String name = getClass().getSimpleName();

    public String getName() {
        return name;
    }
}
