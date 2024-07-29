package org.example.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Converter utils.
 */
@Slf4j
public class ConverterUtils {

    private ConverterUtils() {}

    /**
     * Escapes JavaScript string excluding double quotes.
     * @param javaScript The JavaScript string.
     * @return The escaped JavaScript string.
     */
    public static String escapeJavaScriptExceptDoubleQuote(String javaScript) {
        DataValidationUtils.validateNotNull(javaScript, "javaScript");

        String escapedJson = escapeJavaScriptExcept(javaScript, '"');
        log.debug("JavaScript {} after escape: {}.", javaScript, escapedJson);
        return escapedJson;
    }

    /**
     * Escapes JavaScript string excluding single quotes.
     * @param javaScript The JavaScript string.
     * @return The escaped JavaScript string.
     */
    public static String escapeJavaScriptExceptSingleQuotes(String javaScript) {
        DataValidationUtils.validateNotNull(javaScript, "javaScript");

        String escapedJson = escapeJavaScriptExcept(javaScript, '\'');
        log.debug("JavaScript {} after escape: {}.", javaScript, escapedJson);
        return escapedJson;
    }

    /**
     * Escapes JavaScript.
     * @param input The input to escape.
     * @return The escaped script.
     */
    public static String escapeJavaScriptExcept(String input) {
        return escapeJavaScriptExcept(input, 'a');
    }

    private static String escapeJavaScriptExcept(String input, char exceptChar) {
        if (input == null) {
            return null;
        }
        StringBuilder escapedString = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == exceptChar) {
                escapedString.append(c);
                continue;
            }
            switch (c) {
                case '\'' -> escapedString.append("\\'");
                case '\"' -> escapedString.append("\\\"");
                case '\\' -> escapedString.append("\\\\");
                case '\n' -> escapedString.append("\\n");
                case '\r' -> escapedString.append("\\r");
                case '\t' -> escapedString.append("\\t");
                case '\b' -> escapedString.append("\\b");
                case '\f' -> escapedString.append("\\f");
                case '<' -> escapedString.append("\\u003C");
                case '>' -> escapedString.append("\\u003E");
                case '&' -> escapedString.append("\\u0026");
                case '=' -> escapedString.append("\\u003D");
                case '-' -> escapedString.append("\\u002D");
                default -> {
                    if (c < 32 || c > 126) {
                        escapedString.append(String.format("\\u%04x", (int) c));
                    } else {
                        escapedString.append(c);
                    }
                }
            }
        }
        return escapedString.toString();
    }
}
