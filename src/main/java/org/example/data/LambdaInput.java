package org.example.data;

public record LambdaInput(
        long threadId,
        String methodName,
        String paramClassName,
        String inputData) {
}
