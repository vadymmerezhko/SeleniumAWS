package org.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * The method manger class.
 */
@Slf4j
public final class ClassUtils {
    private ClassUtils() {}

    /**
     * Throws "Method not implemented" exception by method name.
     * @param methodName The method name.
     */
    public static void throwMethodNotImplementedException(String methodName) {
        throw new RuntimeException(String.format("Method %s is not implemented.", methodName));
    }

    /**
     * Returns class field name or null if not found.
     * @param parentObject The parent (class) object.
     * @param fieldObject The field object.
     * @return The field object or null.
     */
    public static String getClassFieldName(Object parentObject, Object fieldObject) {

        if (parentObject == null || fieldObject == null) {
            log.debug("Field name is null for parent object {} and field object {}.",
                    parentObject, fieldObject);
            return null;
        }
        try {
            Field[] fields = parentObject.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldReference = field.get(parentObject);

                if (fieldReference == fieldObject) {
                    String fieldName=  field.getName();
                    log.debug("Field name is {} for parent object {} and field object {}.",
                            fieldName, parentObject, fieldObject);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get class field name.\n%s", e.getMessage()));
        }
        return null;
    }
}
