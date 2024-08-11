package org.example.utils;

import org.example.data.SmartType;
import org.example.exceptions.SmartValidationException;

import java.nio.file.Paths;

public final class DataValidationUtils {

    private DataValidationUtils() {}

    /**
     * Validates that data value is not null.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotNull(Object value, String valueName) {
        if (value == null) {
            handleError(String.format("%s has NULL value.", valueName));
        }
    }
    
    /**
     * Validates that data value is not empty.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotEmpty(String value, String valueName) {
        validateNotNull(value, valueName);
        if (value.isEmpty()) {
            handleError(String.format("%s has empty value.", valueName));
        }
    }

    /**
     * Validates that data value is not empty.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotEmpty(SmartType value, String valueName) {
        validateNotEmpty(value.toString(), valueName);
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotBlank(String value, String valueName) {
        validateNotNull(value, valueName);
        if (value.trim().isEmpty()) {
            handleError(String.format("%s has blank value: '%s'", valueName, value));
        }
    }

    /**
     * Validates that data value is not blank.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotBlank(SmartType value, String valueName) {
        validateNotNull(value.toString(), valueName);
    }

    /**
     * Validates that data value is not multiline.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotMultiline(String value, String valueName) {
        validateNotNull(value, valueName);
        if (value.trim().contains("\n")) {
            handleError(String.format("%s has multiline value: '%s'", valueName, value));
        }
    }

    /**
     * Validates that data value is not multiline.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotMultiline(SmartType value, String valueName) {
        validateNotMultiline(value.toString(), valueName);
    }

    /**
     * Validates that data value has correct color format like '#FF0088'.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateColorFormat(String value, String valueName) {
        validateNotNull(value, valueName);
        if (!value.matches("^#(?:[0-9a-fA-F]{3}){1,2}$")) {
            handleError(String.format("%s has invalid color format: '%s'", valueName, value));
        }
    }

    /**
     * Validates that data value has correct color format like '#FF0088'.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateColorFormat(SmartType value, String valueName) {
       validateColorFormat(value.toString(), valueName);
    }

    /**
     * Validates that data value has correct date format like '05/23/1970'.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateDateValue(String value, String valueName) {
        validateNotNull(value, valueName);
        if (!value.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
            handleError(String.format("%s has invalid date format: '%s'", valueName, value));
        }
    }

    /**
     * Validates that data value has correct date format like '05/23/1970'.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateDateValue(SmartType value, String valueName) {
        validateDateValue(valueName, valueName);
    }

    /**
     * Validates that numeric range is correct.
     * @param value The data value.
     * @param from The range beginning.
     * @param to The range ending.
     * @param valueName The value name.
     */
    public static void validateRange(long value, long from, long to, String valueName) {
        if (value < from || value > to) {
            handleError(String.format("%s has invalid [%d:%d] range value: %d",
                    valueName, from, to, value));
        }
    }

    /**
     * Validates that value is not less than MIN value.
     * @param value The value.
     * @param min The MIN value.
     * @param valueName The value name.
     */
    public static void validateMin(long value, long min, String valueName) {
        if (value < min) {
            handleError(String.format("%s has value less than MIN=%d: %d",
                    valueName, min, value));
        }
    }

    /**
     * Validates that value is not bigger than MAX value.
     * @param value The value.
     * @param max The MAX value.
     * @param valueName The value name.
     */
    public static void validateMax(long value, long max, String valueName) {
        if (value > max) {
            handleError(String.format("%s has value less than MIN=%d: %d",
                    valueName, max, value));
        }
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param value Yhe file  path.
     */
    public static void validateFilePath(String value, String valueName) {
        validateNotBlank(value, valueName);
        try {
            Paths.get(value);
        } catch (Exception e){
            handleError(String.format("Invalid file path: %s", value));
        }
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param value Yhe file  path.
     */
    public static void validateFilePath(SmartType value, String valueName) {
        validateFilePath(value.toString(), valueName);
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param folderPath Yhe file  path.
     */
    public static void validateFolderPath(String folderPath, String valueName) {
        validateNotNull(folderPath, valueName);
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
