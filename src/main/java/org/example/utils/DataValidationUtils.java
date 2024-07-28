package org.example.utils;

import org.example.exceptions.SmartValidationException;

import java.nio.file.Paths;

public final class DataValidationUtils {

    private DataValidationUtils() {}

    /**
     * Validates that data value is not null.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotNull(Object value, String dataName) {
        if (value == null) {
            handleError(String.format("%s has NULL value.", dataName));
        }
    }

    /**
     * Validates that data value is not empty.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotEmpty(String value, String dataName) {
        validateNotNull(value, dataName);
        if (value.isEmpty()) {
            handleError(String.format("%s has empty value.", dataName));
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotBlank(String value, String dataName) {
        validateNotNull(value, dataName);
        if (value.trim().isEmpty()) {
            handleError(String.format("%s has blank value: '%s'", dataName, value));
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateNotMultiline(String value, String dataName) {
        validateNotNull(value, dataName);
        if (value.trim().contains("\n")) {
            handleError(String.format("%s has multiline value: '%s'", dataName, value));
        }
    }

    /**
     * Validates that data value has correct color format like '#FF0088'.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateColorFormat(String value, String dataName) {
        validateNotNull(value, dataName);
        if (!value.matches("^#(?:[0-9a-fA-F]{3}){1,2}$")) {
            handleError(String.format("%s has invalid color format: '%s'", dataName, value));
        }
    }

    /**
     * Validates that data value has correct date format like '05/23/1970'.
     * @param value The data value.
     * @param dataName The data name.
     */
    public static void validateMmDdYyyyDateValue(String value, String dataName) {
        validateNotNull(value, dataName);
        if (!value.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
            handleError(String.format("%s has invalid date format: '%s'", dataName, value));
        }
    }

    /**
     * Validates that numeric range is correct.
     * @param value The data value.
     * @param from The range beginning.
     * @param to The range ending.
     * @param dataName The data name.
     */
    public static void validateRange(long value, long from, long to, String dataName) {
        if (value < from || value > to) {
            handleError(String.format("%s has invalid [%d:%d] range value: %d",
                    dataName, from, to, value));
        }
    }

    /**
     * Validates that value is not less than MIN value.
     * @param value The value.
     * @param min The MIN value.
     * @param dataName The data name.
     */
    public static void validateMin(long value, long min, String dataName) {
        if (value < min) {
            handleError(String.format("%s has value less than MIN=%d: %d",
                    dataName, min, value));
        }
    }

    /**
     * Validates that value is not bigger than MAX value.
     * @param value The value.
     * @param max The MAX value.
     * @param dataName The data name.
     */
    public static void validateMax(long value, long max, String dataName) {
        if (value > max) {
            handleError(String.format("%s has value less than MIN=%d: %d",
                    dataName, max, value));
        }
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param filePath Yhe file  path.
     */
    public static void validateFilePath(String filePath, String dataName) {
        validateNotBlank(filePath, dataName);
        try {
            Paths.get(filePath);
        } catch (Exception e){
            handleError(String.format("Invalid file path: %s", filePath));
        }
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param folderPath Yhe file  path.
     */
    public static void validateFolderPath(String folderPath, String dataName) {
        validateNotNull(folderPath, dataName);
        try {
            Paths.get(folderPath);
        } catch (Exception e){
            handleError(String.format("Invalid folder path: %s", folderPath));
        }
    }

    private static void handleError(String errorMessage) {
        // Make wait to get time to highlight the failed element.
        WaiterUtils.waitMilliSeconds(500);
        throw new SmartValidationException(errorMessage);
    }
}
