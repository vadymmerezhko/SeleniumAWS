package org.example.utils;

public class ConverterUtils {
    private ConverterUtils() {}

    public static String convertJsonStringToRemoteInput(String jsonString) {
        return String.format("\"%s\"",
                jsonString.replace("\\", "\\\\")
                        .replace("\"", "\\\""));
    }

    public static String convertRemoteOutputToJsonString(String lambdaOutput) {
        return lambdaOutput.substring(1, lambdaOutput.length() - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
