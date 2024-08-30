package org.example.utils;

public final class TextUtils {

    /**
     * Returns the number of keyword in the string.
     * Therows exception if string is null or keyword is null or empty.
     * @param string The string.
     * @param keyword The keyword.
     * @return The number of keywords.
     */
    public static int getNumberOfKeywordsInString(String string, String keyword) {
        DataValidationUtils.validateNotNull(string, "string");
        DataValidationUtils.validateNotEmpty(keyword, "keyword");

        if (keyword == null || string == null || string.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;

        while ((index = string.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
}
