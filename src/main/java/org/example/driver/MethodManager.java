package org.example.driver;

public class MethodManager {

    public static void throwMethodNotImplementedException(String methodName) {
        throw new RuntimeException(String.format("Method %s is not implemented.", methodName));
    }
}
