package org.example.utils;

import org.example.pages.BasePage;

import java.lang.reflect.Field;

/**
 * The method manger class.
 */
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
            return null;
        }

        try {
            Field[] fields = parentObject.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldReference = field.get(parentObject);

                if (fieldReference == fieldObject) {
                    return field.getName();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot get class field name.\n%s", e.getMessage()));
        }
        return null;
    }
}
