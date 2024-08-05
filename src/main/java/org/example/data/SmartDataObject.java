package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;

import java.lang.reflect.Field;

/**
 * Smart data object class.
 */
@Slf4j
public abstract class SmartDataObject {
    private final String name = getClass().getSimpleName();

    public String getName() {
        return name;
    }

    public void initialize() {
        try {
            Field[] fields = getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(this);

                if (fieldObject instanceof SmartType) {
                    ((SmartType) fieldObject).setParent(this);
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot initialize %s data object fields.");
        }
    }
}
