package org.example.utils;

public class Converter {
    private Converter() {}

    public static String convertJsonStringToLambdaInput(String jsonString) {
        return String.format("\"%s\"",
                jsonString.replace("\\", "\\\\")
                        .replace("\"", "\\\""));
    }

    public static String convertLambdaOutputToJsonString(String lambdaOutput) {
        return lambdaOutput.substring(1, lambdaOutput.length() - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
