package org.example.utils;

import org.example.data.SmartValue;
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
            handleError(String.format("%s has null value.", valueName));
        }
    }

    /**
     * Validates that two data objects are not the same.
     * @param expected The data value 1.
     * @param actual The data value 2.
     * @param expectedName The value 1 name.
     * @param actualName The value 2 name.
     */
    public static void validateNotTheSame(Object expected, Object actual,
                                          String expectedName, String actualName) {
        validateNotBlank(expectedName, "expectedName");
        validateNotBlank(actualName, "actualName");

        if (expected == actual) {
            handleError(String.format("""
            Actual value object is the same as expected value object.
            Expected:
            %s
            Actual:
            %s
            """.stripIndent(),
            expected, actual));

        }
    }

    /**
     * Validates that two data objects have the same type.
     * @param expected The object value1.
     * @param actual The object value2.
     * @param expectedName The value name.
     * @param actualName The actual name.
     */
    public static void validateTheSameType(Object expected, Object actual,
                                           String expectedName, String actualName) {
        validateNotBlank(expectedName, "expectedName");
        validateNotBlank(actualName, "actualName");
        validateNotNull(expected, expectedName);
        validateNotNull(actual, actualName);

        String actualClassName = actual.getClass().getName();
        String expectedClassName = expected.getClass().getName();

        if (!expectedClassName.equals(actualClassName)) {
            handleError(String.format("""
                    Actual value class does not equal expected value class.
                    Expected: %s
                    Actual: %s
                    """.stripIndent(),
                    expectedClassName,
                    actualClassName));
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
            handleError(String.format("%s is empty.", valueName));
        }
    }

    /**
     * Validates that data value is not empty.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotEmpty(SmartValue value, String valueName) {
        validateNotEmpty(value.toString(), valueName);
    }

    /**
     * Validates that data values are not equal.
     * @param value1 The first data value.
     * @param value2 The second data value.
     * @param valueName1 The first value name.
     * @param valueName2 The second value name.
     */
    public static void validateNotEqual(Object value1, Object value2, String valueName1, String valueName2) {
        validateNotNull(value1, valueName1);
        validateNotNull(value2, valueName2);

        if (value1.equals(value2)) {
            handleError(String.format("%s equals %s.", valueName1, value2));
        }
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
    public static void validateNotBlank(SmartValue value, String valueName) {
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
            handleError(String.format("%s has multiline value:\n'%s'", valueName, value));
        }
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
     * Validates that numeric range is correct.
     * @param value The data value.
     * @param from The range beginning.
     * @param to The range ending.
     * @param valueName The value name.
     */
    public static void validateRange(long value, long from, long to, String valueName) {
        validateNotBlank(valueName, valueName);

        if (value < from || value > to) {
            handleError(String.format("%s has invalid [%d:%d] range value: %d",
                    valueName, from, to, value));
        }
    }

    /**
     * Validates that double range is correct.
     * @param value The data value.
     * @param from The range beginning.
     * @param to The range ending.
     * @param dataName The data name.
     */
    public static void validateRange(double value, double from, double to, String dataName) {
        if (value < from || value > to) {
            handleError(String.format("%s has invalid [%f:%f] range value: %f",
                    dataName, from, to, value));
        }
    }

    /**
     * Validates that value is not less than MIN value.
     * @param value The value.
     * @param min The MIN value.
     * @param valueName The value name.
     */
    public static void validateMin(long value, long min, String valueName) {
        validateNotBlank(valueName, valueName);

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
        validateNotBlank(valueName, valueName);

        if (value > max) {
            handleError(String.format("%s has value bigger than MAX=%d: %d",
                    valueName, max, value));
        }
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param filePath Yhe file  path.
     */
    public static void validateFilePathFormat(String filePath, String valueName) {
        validateNotBlank(filePath, "filePath");
        validateNotBlank(filePath, valueName);

        try {
            Paths.get(filePath);
        }
        catch (Exception e){
            handleError(String.format("Invalid file path: %s", filePath));
        }
    }

    /**
     * Validates file path.
     * Throws exception if path is invalid.
     * @param folderPath Yhe file  path.
     */
    public static void validateFolderPath(String folderPath, String valueName) {
        validateNotBlank(folderPath, valueName);
        validateNotBlank(valueName, "valueName");

        try {
            Paths.get(folderPath);
        }
        catch (Exception e) {
            handleError(String.format("Invalid folder path: %s", folderPath));
        }
    }

    /**
     * Validated that value object is instance of exact type.
     * @param value The value object.
     * @param type The type class.
     * @param valueName The
     */
    public static void validateInstanceOf(Object value, Class<?> type, String valueName) {
        validateNotNull(value, valueName);
        validateNotNull(type, "type");
        validateNotBlank(valueName, "valueName");

        if (!type.isInstance(value)) {
            handleError(String.format("Value type %s is not instance of %s.",
                    value.getClass().getName(), type.getName()));
        }
    }

    private static void handleError(String errorMessage) {
        // Make wait to get time to highlight the failed element.
        WaiterUtils.waitMilliSeconds(500);
        throw new SmartValidationException(errorMessage);
    }
}
