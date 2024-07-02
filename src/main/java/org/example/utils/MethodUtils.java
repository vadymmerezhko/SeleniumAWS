package org.example.utils;

/**
 * The method manger class.
 */
public final class MethodUtils {

    private MethodUtils() {}

    /**
     * Throws "Method not implemented" exception by method name.
     * @param methodName The method name.
     */
    public static void throwMethodNotImplementedException(String methodName) {
        throw new RuntimeException(String.format("Method %s is not implemented.", methodName));
    }
}
