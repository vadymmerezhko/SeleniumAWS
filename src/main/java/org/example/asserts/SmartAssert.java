package org.example.asserts;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.example.configs.Config;
import org.example.data.SmartDataObject;
import org.example.data.SmartType;
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

                    for (Field actualField : actualFields) {
                        actualField.setAccessible(true);
                        Object actuaalObject = actualField.get(actual);

                        if (actuaalObject instanceof SmartType actualType) {
                            String actualName = actualType.getName();

                            if (expectedName.equals(actualName)) {

                                if (Config.getInstance().getDebugMode() &&
                                        !expectedObject.equals(actuaalObject)) {
                                    SmartAssert.updateExpectedValue(expectedType, actualType);
                                }

                                if (actuaalObject instanceof JSONObject) {
                                    assertJsonObject((JSONObject) expectedObject, (JSONObject) actuaalObject, false);
                                }
                                else if (actuaalObject instanceof JSONArray) {
                                    assertJsonArray((JSONArray) expectedObject, (JSONArray) actuaalObject, false);
                                }
                                else if (actuaalObject instanceof Node) {
                                    assertXmlNode((Node) expectedObject, (Node) actuaalObject, false);
                                }
                                else {
                                    Assert.assertEquals(actuaalObject, actuaalObject, expectedName);
                                }
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

    private static void updateExpectedValue(SmartType expectedType, SmartType actualType) {
        String newValue = WebUtils.showPrompt(String.format("""
                ASSERT FAIL
                
                Actual data object '%s' field value does not equal expected one.
                Expected: '%s'
                Actual: '%s'
                
                Update expected field with actual value.
                
                Click OK to confirm update.
                OR click CANCEL to exit the test.
                """.stripTrailing(),
                expectedType.getName(),
                expectedType.toString(),
                actualType.toString()),
                actualType.toString());

        if (newValue.isEmpty()) {
            log.debug("User exited the test.");
            WebDriverFactory.hardSystemExit();
        }
        expectedType.setAndSaveString(newValue);
        log.debug("Expected data object {} field value was updated to '{}'",
                expectedType.getName(), newValue);
    }
}
