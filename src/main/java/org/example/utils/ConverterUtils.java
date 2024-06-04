package org.example.utils;

/**
 * Converter utils.
 */
public class ConverterUtils {
    private ConverterUtils() {}

    /**
     * Converts remote string to RMI input string.
     * @param jsonString The JSON string.
     * @return The remote input string.
     */
    public static String convertJsonStringToRemoteInput(String jsonString) {
        return String.format("\"%s\"",
                jsonString.replace("\\", "\\\\")
                        .replace("\"", "\\\""));
    }

    /**
     * Converts remote output to JSON string.
     * @param remoteOutput The remote output string.
     * @return The JSON string.
     */
    public static String convertRemoteOutputToJsonString(String remoteOutput) {
        return remoteOutput.substring(1, remoteOutput.length() - 1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
