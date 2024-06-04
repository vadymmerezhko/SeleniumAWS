package org.example.data;

/**
 * Test method data record.
 * @param methodName The method name.
 * @param paramClassName The param class name.
 * @param inputData The input data as Json string.
 */
public record MethodInput(
        String methodName,
        String paramClassName,
        String inputData) {
}
