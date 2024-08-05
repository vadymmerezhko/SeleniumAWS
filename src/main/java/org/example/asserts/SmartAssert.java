package org.example.asserts;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.data.SmartDataObject;
import org.example.data.SmartType;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.example.utils.WebUtils;
import org.testng.Assert;

import java.lang.reflect.Field;

/**
 * Smart assertion class.
 */
@Slf4j
public abstract class SmartAssert {
    private SmartAssert() {}

    /**
     * Asserts expected smart type and actual one.
     * @param expected The expected smart type.
     * @param actual The actual one.
     */
    public static void assertDataObjects(SmartDataObject expected, SmartDataObject actual) {
        DataValidationUtils.validateNotNull(expected, "expected");
        DataValidationUtils.validateNotNull(actual, "actual");

        if (expected == actual) {
            throw new SmartRuntimeException(String.format(
                    "Expected and actual data objects refer to the same %s object.",
                    expected.getName()));
        }

        Class<?> expectedClass = expected.getClass();
        Field[] expectedFields = expectedClass.getDeclaredFields();
        Class<?> actualClass = actual.getClass();
        Field[] actualFields = actualClass.getDeclaredFields();

        if (actualFields.length != expectedFields.length) {
            throw new SmartRuntimeException(String.format(
                    "Expected data object has %d fields but actual has %d fields.",
                    expectedFields.length, actualFields.length));
        }

        try {
            for (Field expectdField : expectedFields) {
                expectdField.setAccessible(true);
                Object expectedObject = expectdField.get(expected);

                if (expectedObject instanceof SmartType expectedType) {
                    boolean typeFound = false;
                    String expectedName = expectedType.getName();
                    String expectedValue = expectedType.toString();

                    if (expectedValue == null) {
                        throw new SmartRuntimeException(String.format(
                                "Expected data object %s field value is NULL.",
                                expectedName));
                    }

                    for (Field actualField : actualFields) {
                        actualField.setAccessible(true);
                        Object actuaalObject = actualField.get(actual);

                        if (actuaalObject instanceof SmartType actualType) {
                            String actualName = actualType.getName();

                            if (expectedValue == null) {
                                SmartAssert.updateExpectedValue(expectedType, actualType);
                            }

                            if (expectedName.equals(actualName)) {
                                String actualValue = actualType.toString();

                                if (actualValue == null) {
                                    throw new SmartRuntimeException(String.format(
                                            "Actual data object %s field value is NULL.",
                                            expectedName));
                                }

                                if (Config.getInstance().getDebugMode() &&
                                        !expectedValue.equals(actualValue)) {
                                    SmartAssert.updateExpectedValue(expectedType, actualType);
                                }

                                Assert.assertEquals(expectedValue, actualValue, expectedName);
                                typeFound = true;
                                log.debug("Expected smart type {} expected '{}' equals to actual '{}'.",
                                        expectedName, actualName, expectedType);
                                break;
                            }
                        }
                        else {
                            throw new SmartRuntimeException(String.format(
                                    "Actual data object field type %s is not supported.",
                                    expectedFields.getClass().getSimpleName()));
                        }
                    }
                    if (!typeFound) {
                        throw new SmartRuntimeException(String.format(
                                "Actual data object field %s is not present.",
                                expectedName));
                    }
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Expected data object field type %s is not supported.",
                            expectedFields.getClass().getSimpleName()));
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot assert expected data object '%s' and actual one '%s'.",
                    expected.getName(), actual.getName()), e);
        }
    }

    private static void updateExpectedValue(SmartType expectedType, SmartType actualType) {
        String newValue = WebUtils.showPrompt(String.format("""
                ASSERT FAIL
                
                Actual data object '%s' field value does not equal expected one.
                Expected: '%s'
                Actual: '%s'
                
                Update expected field with actual value?.
                
                Click OK to confirm update.
                OR click CANCEL to exit the test.
                """.stripTrailing(),
                expectedType.getName(), expectedType, actualType),
                actualType.toString());

        if (newValue == null) {
            log.debug("User exited the test.");
            WebDriverFactory.hardSystemExit();
        }

        expectedType.setAndSaveValue(newValue);
        log.debug("Expected dta object {} field value was updated to '{}'",
                expectedType.getName(), newValue);
    }
}
