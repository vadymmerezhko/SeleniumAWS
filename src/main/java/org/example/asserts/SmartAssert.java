package org.example.asserts;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.enums.ValueType;
import org.example.utils.ConverterUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.example.utils.WebUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.lang.reflect.Field;

import static org.testng.AssertJUnit.assertFalse;

/**
 * Smart assertion class.
 */
@Slf4j
public abstract class SmartAssert {
    private SmartAssert() {}

    /**
     * Asserts expected smart type value and actual value.
     * Strict type compare is false.
     * Strict order compare is false.
     * Elements order and other JSON specific is ignored.
     * @param expected The expected smart type.
     * @param actual The actual one.
     */
    public static void assertData(SmartDataObject expected, SmartDataObject actual) {
        assertData(expected, actual, false, false);
    }

    /**
     * Asserts expected smart type value and actual value.
     * Strict type compare is true.
     * Strict order compare is false.
     * Elements order and other JSON specific is ignored.
     * @param expected The expected smart type.
     * @param actual The actual one.
     */
    public static void assertDataStrictType(SmartDataObject expected, SmartDataObject actual) {
        assertData(expected, actual, true, false);
    }

    /**
     * Asserts expected smart type value and actual value.
     * Strict type compare is false.
     * Strict order compare is true.
     * Elements order and other JSON specific is ignored.
     * @param expected The expected smart type.
     * @param actual The actual one.
     */
    public static void assertDataStrictOrder(SmartDataObject expected, SmartDataObject actual) {
        assertData(expected, actual, true, true);
    }

    /**
     * Asserts expected smart type value and actual value.
     * Strict type compare is true.
     * Strict order compare is true.
     * Elements order and other JSON specific is ignored.
     * @param expected The expected smart type.
     * @param actual The actual one.
     */
    public static void assertDataStrictTypeAndOrder(SmartDataObject expected, SmartDataObject actual) {
        assertData(expected, actual, true, true);
    }

    private static void assertData(SmartDataObject expected, SmartDataObject actual,
                                         boolean strictType, boolean strictOrder) {
        DataValidationUtils.validateNotNull(expected, "expected");
        DataValidationUtils.validateNotNull(actual, "actual");
        DataValidationUtils.validateNotTheSame(expected, actual, "expected", "actual");

        Class<?> expectedClass = expected.getClass();
        Class<?> actualClass = actual.getClass();
        Field[] expectedFields = expectedClass.getDeclaredFields();
        Field[] actualFields = actualClass.getDeclaredFields();
        String expectedName = expected.getName();

        if (actualFields.length != expectedFields.length) {
            throw new SmartRuntimeException(String.format(
                    "Expected data object has %d fields but actual one has %d fields.",
                    expectedFields.length, actualFields.length));
        }
        try {
            for (Field expectdField : expectedFields) {
                expectdField.setAccessible(true);
                SmartClass expectedSmartClass = ((SmartClass) expectdField.get(expected));
                Object expectedValue = expectedSmartClass.getValue();
                Class<?> expectedValueClass = expectedValue.getClass();
                String fieldName = expectdField.getName();
                Field actualField = actualClass.getDeclaredField(fieldName);
                actualField.setAccessible(true);
                SmartClass actualSmartClass = ((SmartClass) actualField.get(actual));
                Object actualValue = actualSmartClass.getValue();
                Class<?> actualValueClass = actualValue.getClass();

                if (strictType) {
                    DataValidationUtils.validateTheSameType(expectedValue, actualValue,
                            "expectedValue", "actualValue");
                }
                else {
                    // Convert actual string value to object
                    if (actualValueClass != expectedValueClass) {
                        SmartType expectedValueType = new SmartType(ValueType.fromClass(expectedClass));
                        actualValue = ConverterUtils.objectToObject(expectedValueType, actualValue);
                    }
                }
                if (Config.getInstance().getDebugMode() && !expectedValue.equals(actualValue)) {
                    SmartAssert.updateExpectedValue(expectedSmartClass, actualSmartClass);
                }
                // JSONObject
                if (expectedValueClass == JSONObject.class) {
                    assertJsonObject((JSONObject) expectedValue, (JSONObject) actualValue, strictOrder);
                }
                // JSONArray
                else if (expectedValueClass == JSONArray.class) {
                    assertJsonArray((JSONArray) expectedValue, (JSONArray) actualValue, strictOrder);
                }
                // XML Node
                else if (expectedValue instanceof Node) {
                    assertXmlNode((Node) expectedValue, (Node) actualValue, strictOrder);
                }
                else {
                    Assert.assertEquals(actualValue, actualValue, expectedName);
                    log.debug("""
                            Smart assert of the expected and actual smart types passed OK.
                            Strict type: {}
                            Strict order: {}
                            Field name: {}
                            EXPECTED:
                            Type: {}
                            Value:
                            {}
                            ACTUAL:
                            Type: {}
                            Value:
                            {}
                            """.stripIndent(),
                            fieldName,
                            strictType, strictOrder,
                            expectedName, expectedValueClass.getName(), expectedValue,
                            actualClass.getName(), actualValue);
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot assert expected data object '%s' and actual one '%s'.",
                    expected.getName(), actual.getName()), e);
        }
    }

    /**
     * Asserts JSON object.
     * @param expectedJson The expected JSON.
     * @param actualJson The actual JSON.
     * @param strict The strict compare - true.
     */
    public static void assertJsonObject(JSONObject expectedJson, JSONObject actualJson, boolean strict) {
        DataValidationUtils.validateNotNull(expectedJson, "expectedJson");
        DataValidationUtils.validateNotNull(actualJson, "actualJson");
        DataValidationUtils.validateNotTheSame(expectedJson, actualJson, "expectedJson", "actualJson");

        JSONAssert.assertEquals(expectedJson, actualJson, strict);
    }

    /**
     * Asserts JSON array.
     * @param expectedJson The expected JSON.
     * @param actualJson The actual JSON.
     * @param strict The strict compare - true.
     */
    public static void assertJsonArray(JSONArray expectedJson, JSONArray actualJson, boolean strict) {
        DataValidationUtils.validateNotNull(expectedJson, "expectedJson");
        DataValidationUtils.validateNotNull(actualJson, "actualJson");
        DataValidationUtils.validateNotTheSame(expectedJson, actualJson, "expectedJson", "actualJson");

        JSONAssert.assertEquals(expectedJson, actualJson, strict);
    }

    /**
     * Assrerts XML node.
     * @param expectedXml The expected JSON.
     * @param actualXml The actual JSON.
     */
    public static void assertXmlNode(Node expectedXml, Node actualXml, boolean strict) {
        DataValidationUtils.validateNotNull(expectedXml, "expectedXml");
        DataValidationUtils.validateNotNull(actualXml, "actualXml");
        DataValidationUtils.validateNotTheSame(expectedXml, actualXml, "expectedXml", "actualXml");
        Diff diff;

        if (strict) {
            diff = DiffBuilder.compare(Input.fromNode(expectedXml))
                    .withTest(Input.fromNode(actualXml))
                    .ignoreWhitespace()
                    .checkForIdentical() // Use strict comparison
                    .build();
        }
        else {
            diff = DiffBuilder.compare(Input.fromNode(expectedXml))
                    .withTest(Input.fromNode(actualXml))
                    .ignoreWhitespace()
                    .checkForSimilar()
                    .build();
        }

        String mode = strict ? "Strict" : "";
        // Assert that there are no differences
        assertFalse(String.format(
                "%s XML Node assert failed:\n%s", mode,
                diff.toString()), diff.hasDifferences());
    }

    private static void updateExpectedValue(SmartClass expected, SmartClass actual) {
        String newValue = WebUtils.showPrompt(String.format("""
                ASSERT FAIL
                
                Actual data object '%s' field value does not equal expected one.
                Expected:
                %s
                Actual:
                %s
                
                Update expected value with actual value?
                
                Click OK to confirm update.
                OR click CANCEL to exit the test.
                """.stripTrailing(),
                expected.getName(),
                expected,
                actual),
                actual.toString());

        if (newValue.isEmpty()) {
            log.debug("User exited the test.");
            WebDriverFactory.hardSystemExit();
        }
        Object value = ConverterUtils.stringToObject(expected.getSmartType(), newValue);
        expected.setAndSaveValue(value);
        log.debug("Expected data object {} field value was updated to '{}'",
                expected.getName(), newValue);
    }
}
