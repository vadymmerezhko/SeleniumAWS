package org.example.utils;

import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;

public final class DataValidationUtils {
    protected static final String FULL_CLASS_NAME_REGEX =
            "^([a-zA-Z_][a-zA-Z0-9_]*)(\\.[a-zA-Z_][a-zA-Z0-9_]*)*\\.[A-Z][a-zA-Z0-9_$]*$";
    protected static final String CLASS_PACKAGE_NAME_REGEX =
            "^([a-zA-Z_][a-zA-Z0-9_]*)(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$";
    protected static final String SIMPLE_CLASS_NAME_REGEX = "^[A-Z][a-zA-Z0-9_$]*$";
    protected static final String VARIABLE_NAME_REGEX = "\\b([a-zA-Z][a-zA-Z0-9]*)\\s*=";
    protected static final String FULL_METHOD_NAME_REGEX =
            "^([a-z][a-z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*)\\.[a-zA-Z_][a-zA-Z0-9_]*$";

    protected static final String SIMPLE_METHOD_NAME_REGEX = "^[a-z_][a-zA-Z0-9_]*$";

    private DataValidationUtils() {}

    /**
     * Validates that data value is not null.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNotNull(Object value, String valueName) {
        validateValueName(valueName);

        if (value == null || value == JSONObject.NULL) {
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
    // TODO - add unit tests
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
    // TODO: add unit tests
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
     * Validates that data value is number string.
     * @param value The data value.
     * @param valueName The value name.
     */
    public static void validateNumberString(String value, String valueName) {
        validateNotNull(value, valueName);

        try {
            ConvertUtils.stringToNumber(value);
        }
        catch (SmartRuntimeException e) {
            handleError(String.format("%s is not a number string: '%s'", valueName, value));
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

        if (value.contains("\n")) {
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
    public static void validateMin(Number value, Number min, String valueName) {
        validateNotBlank(valueName, valueName);
        BigDecimal number = new BigDecimal(String.valueOf(value));
        BigDecimal limit = new BigDecimal(String.valueOf(min));

        if (number.compareTo(limit) < 0) {
            handleError(String.format("%s has lower value than MIN=%s: %s",
                    valueName, min, value));
        }
    }

    /**
     * Validates that value is not bigger than MAX value.
     * @param value The value.
     * @param max The MAX value.
     * @param valueName The value name.
     */
    public static void validateMax(Number value, Number max, String valueName) {
        validateNotBlank(valueName, valueName);

        BigDecimal number = new BigDecimal(String.valueOf(value));
        BigDecimal limit = new BigDecimal(String.valueOf(max));

        if (number.compareTo(limit) > 0) {
            handleError(String.format("%s has bigger value than MIN=%s: %s",
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
     * @param valueName The value name.
     */
    // TODO: add unit tests
    public static void validateInstanceOf(Object value, Class<?> type, String valueName) {
        validateNotNull(value, valueName);
        validateNotNull(type, "type");
        validateNotBlank(valueName, "valueName");

        if (!type.isInstance(value)) {
            handleError(String.format("Value type %s is not instance of %s.",
                    value.getClass().getName(), type.getName()));
        }
    }

    /**
     * Validates that folder exists.
     * @param folderPath The folder path
     * @param valueName The value name.
     */
    public static void validateFolder(String folderPath, String valueName) {
        validateFilePathFormat(folderPath, valueName);
        File folder = new File(folderPath);

        // Validate the folder path
        if (!folder.exists() || !folder.isDirectory()) {
            handleError(String.format(
                    "The folder path provided is invalid or not a directory: %s",
                    folderPath));
        }
    }

    /**
     * Validates that string value matches regex.
     * @param value The value.
     * @param regex The regex.
     * @param valueName The value name.
     */
    public static void validateMatches(String value, String regex, String valueName) {
        validateNotNull(value, valueName);

        // Validate the value matches the regex
        if (!value.matches(regex)) {
            handleError(String.format(
                    "The value '%s' does not match regex: '%s'",
                    value, regex));
        }
    }

    /**
     * Validates if the provided class name is a valid full class name.
     * A valid full class name consists of a package and class name,
     * following Java naming conventions.
     * @param className The full class name to validate.
     * @param valueName The value name.
     * @throws IllegalArgumentException if the class name is invalid.
     */
    public static void validateFullClassName(String className, String valueName) {
        validateNotBlank(valueName, "valueName");
        validateNotBlank(className, valueName);
        validateNotMultiline(className, valueName);

        // match a valid full class name with regex
        if (!className.matches(FULL_CLASS_NAME_REGEX)) {
            handleError(String.format("Invalid full class name: %s", className));
        }
    }

    /**
     * Validates if the provided package name is a valid Java package name.
     * A valid package name consists of segments separated by dots, following Java naming conventions.
     * Each segment must start with a letter or underscore, followed by letters, digits, or underscores.
     * @param packageName The package name to validate.
     * @param valueName The value name.
     */
    public static void validatePackageName(String packageName, String valueName) {
        validateNotBlank(packageName, valueName);
        validateNotMultiline(packageName, valueName);

        // Match a valid package name with regex
        if (!packageName.matches(CLASS_PACKAGE_NAME_REGEX)) {
            handleError(String.format("Invalid class package name: %s", packageName));
        }
    }

    /**
     * Validates if the provided simple class name is a valid Java class name,
     * including names with inner classes (denoted by a $ sign).
     * A valid class name follows Java naming conventions, allowing for nested class names.
     * @param className The class name to validate.
     * @param valueName The value name.
     */
    public static void validatesSimpleClassName(String className, String valueName) {
        validateNotBlank(className, valueName);

        // match a valid simple class name, including nested class names with $ sign
        if (!className.matches(SIMPLE_CLASS_NAME_REGEX)) {
            handleError(String.format("Invalid class name: '%s'", className));
        }
    }

    /**
     * Validates if the provided string is a valid full Java method name.
     * A valid full method name consists of a package name, class name, and method name,
     * following Java naming conventions.
     * Example of valid method names:
     * - com.example.MyClass.myMethod
     * - org.project.service.AccountService.getAccount
     * - com.package.InnerClass$NestedClass.methodName
     * @param fullMethodName The full method name to validate.
     * @param valueName The value name.
     */
    public static void validateFullMethodName(String fullMethodName, String valueName) {
        validateNotBlank(fullMethodName, valueName);

        // Validate full method name with regex: package, class, and method
        if (!fullMethodName.matches(FULL_METHOD_NAME_REGEX)) {
            throw new SmartRuntimeException(String.format("Invalid full method name: '%s'", fullMethodName));
        }
    }

    /**
     * Validates if the provided string is a valid simple Java method name.
     * A valid method name must start with a lowercase letter or underscore,
     * and can contain letters, digits, or underscores.
     * @param methodName The method name to validate.
     * @param valueName The value name.
     * @throws IllegalArgumentException if the method name is invalid.
     */
    public static void validateSimpleMethodName(String methodName, String valueName) {
        DataValidationUtils.validateNotBlank(methodName, valueName);
        DataValidationUtils.validateNotMultiline(methodName, valueName);

        // validate a simple method name with regex
        if (!methodName.matches(SIMPLE_METHOD_NAME_REGEX)) {
            handleError(String.format("Invalid method name: '%s'", methodName));
        }
    }

    private static void handleError(String errorMessage) {
        // Make wait to get time to highlight the failed element.
        TimerUtils.waitMilliSeconds(500);
        throw new SmartValidationException(errorMessage);
    }

    private static void validateValueName(String valueName) {

        if (valueName == null || valueName.isEmpty()) {
            handleError(String.format("Invalid value name: %s", valueName));
        }
    }
}
