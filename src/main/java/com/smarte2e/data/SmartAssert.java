package com.smarte2e.data;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.utils.ConvertUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import com.smarte2e.configs.Config;
import com.smarte2e.ui.factories.WebDriverFactory;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.utils.DataValidator;
import com.smarte2e.utils.WebUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.w3c.dom.Node;

import java.lang.reflect.Field;


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
    public static void assertData(SmartData expected, SmartData actual) {
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
    // TODO: Add unit test
    public static void assertDataStrictType(SmartData expected, SmartData actual) {
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
    // TODO: Add unit test
    public static void assertDataStrictOrder(SmartData expected, SmartData actual) {
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
    // TODO: Add unit test
    public static void assertDataStrictTypeAndOrder(SmartData expected, SmartData actual) {
        assertData(expected, actual, true, true);
    }

    private static void assertData(SmartData expected, SmartData actual,
                                   boolean strictType, boolean strictOrder) {
        DataValidator.notNull(expected, "expected");
        DataValidator.notNull(actual, "actual");
        DataValidator.notTheSame(expected, actual, "expected", "actual");

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
                String fieldName = expectdField.getName();
                Field actualField = actualClass.getDeclaredField(fieldName);
                actualField.setAccessible(true);
                SmartValue actualValue = (SmartValue) actualField.get(actual);
                SmartValue expectedValue = (SmartValue) expectdField.get(expected);
                Class<?> expectedValueClass = expectedValue.getClass();
                SmartType expectedValueType = expectedValue.getSmartType();
                SmartType actualValueType = actualValue.getSmartType();

                if (strictType) {
                    DataValidator.theSameType(expectedValue, actualValue,
                            "expectedValue", "actualValue");
                }
                else {
                    // Convert actual value to expected type value
                    if (!actualValueType.equals(expectedValueType)) {
                        actualValue = new SmartValue(ConvertUtils.objectToObject(
                                expectedValueType, actualValue.getValue()));
                    }
                }
                Object expectedValueObject = expectedValue.getValue();
                Object actualValueObject = actualValue.getValue();

                // Make possibility to update expected value in debug mode
                // if it doesn't equals actual one in debug mode
                if (Config.getInstance().getDebugMode() && !expectedValue.equals(actualValue)) {
                    SmartAssert.updateExpectedValue(expectedValue, actualValue);
                }
                // JSONObject
                if (expectedValueObject instanceof JSONObject expectedJsonObject) {
                    assertJsonObject(expectedJsonObject, (JSONObject) actualValueObject, strictOrder);
                }
                // JSONArray
                else if (expectedValueObject instanceof JSONArray expectedJsonArray) {
                    assertJsonArray(expectedJsonArray, (JSONArray) actualValueObject, strictOrder);
                }
                // XML Node
                else if (expectedValueObject instanceof Node expectedNode) {
                    assertXmlNode(expectedNode, (Node) actualValueObject, strictOrder);
                }
                else {
                    Assert.assertEquals(expectedValue, actualValue, expectedName);
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
        DataValidator.notNull(expectedJson, "expectedJson");
        DataValidator.notNull(actualJson, "actualJson");
        DataValidator.notTheSame(expectedJson, actualJson, "expectedJson", "actualJson");

        JSONAssert.assertEquals(expectedJson, actualJson, strict);
    }

    /**
     * Asserts JSON array.
     * @param expectedJson The expected JSON.
     * @param actualJson The actual JSON.
     * @param strict The strict compare - true.
     */
    public static void assertJsonArray(JSONArray expectedJson, JSONArray actualJson, boolean strict) {
        DataValidator.notNull(expectedJson, "expectedJson");
        DataValidator.notNull(actualJson, "actualJson");
        DataValidator.notTheSame(expectedJson, actualJson, "expectedJson", "actualJson");

        JSONAssert.assertEquals(expectedJson, actualJson, strict);
    }

    /**
     * Asserts XML node.
     * @param expectedXml The expected JSON.
     * @param actualXml The actual JSON.
     * @param strict The strict assert flag.
     */
    public static void assertXmlNode(Node expectedXml, Node actualXml, boolean strict) {
        DataValidator.notNull(expectedXml, "expectedXml");
        DataValidator.notNull(actualXml, "actualXml");
        DataValidator.notTheSame(expectedXml, actualXml, "expectedXml", "actualXml");

        JSONObject expectedJson = ConvertUtils.xmlNodeToJsonObject(expectedXml);
        JSONObject actualJson = ConvertUtils.xmlNodeToJsonObject(actualXml);
        assertJsonObject(expectedJson, actualJson, strict);
    }

    private static void updateExpectedValue(SmartValue expected, SmartValue actual) {
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
        Object value = ConvertUtils.stringToObject(expected.getSmartType(), newValue);
        expected.setAndSaveValue(value);
        log.debug("Expected data object {} field value was updated to '{}'",
                expected.getName(), newValue);
    }
}
