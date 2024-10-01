package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.data.SmartType;
import org.example.data.SmartValue;
import org.example.exceptions.SmartRuntimeException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.Arrays;


/**
 * Compare utils class.
 */
@Slf4j
public final class CompareUtils {

    private CompareUtils() {}

    /**
     * Compares to XML Nodes.
     * @param expectedXml The expected node.
     * @param actualXml The actual node.
     * @return The result.
     */
    public static boolean compareXmlNodes(Node expectedXml, Node actualXml, boolean strict) {
        DataValidationUtils.validateNotNull(expectedXml, "expectedXml");
        DataValidationUtils.validateNotNull(actualXml, "actualXml");
        boolean result = false;

        if (expectedXml == null && actualXml == null) {
            log.debug("Actual XML nod and expected XML node are the same object.");
            return true;
        }
        else if (expectedXml == actualXml) {
            log.debug("Actual XML nod and expected XML node are the same object.");
            return true;
        }
        DataValidationUtils.validateNotNull(expectedXml, "expectedXml");
        DataValidationUtils.validateNotNull(actualXml, "actualXml");

        try {
            JSONObject expectedJson = ConvertUtils.xmlNodeToJsonObject(expectedXml);
            JSONObject actualJson = ConvertUtils.xmlNodeToJsonObject(actualXml);
            result = CompareUtils.compareJsonObjects(expectedJson, actualJson, strict);
            log.debug("""
                    Actual XML node compared to expected XML node.
                    Result: {}
                    Expected:
                    {}
                    Actual:
                    {}
                    """.stripIndent(),
                    result, strict,
                    ConvertUtils.xmlNodeToString(expectedXml),
                    ConvertUtils.xmlNodeToString(actualXml));
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot compare actual XML node to expected XML node.
                    Result: %b
                    Strict: %b
                    Expected:
                    %s
                    Actual:
                    %s
                    """.stripIndent(),
                    result, strict,
                    ConvertUtils.xmlNodeToString(expectedXml),
                    ConvertUtils.xmlNodeToString(actualXml)), e);
        }
    }

    /**
     * Compares two JSON objects.
     * @param expected The expected object.
     * @param actual The actual object.
     * @param strictOrder The strict mode - false: ignore fields and values order.
     * @return Teh true/false result.
     */
    public static boolean compareJsonObjects(JSONObject expected,
                                             JSONObject actual,
                                             boolean strictOrder) {
        try {
            boolean result = false;

            if (expected == null && actual == null) {
                log.debug("Actual JSON object and expected JSON object are null.");
                return true;
            } else if (expected == actual) {
                log.debug("Actual JSON object and expected JSON object are the same object.");
                return true;
            }
            else {
                DataValidationUtils.validateNotNull(expected, "expectedJson");
                DataValidationUtils.validateNotNull(actual, "actualJson");

                try {
                    JSONAssert.assertEquals(expected, actual, strictOrder);
                    result = true;
                }
                catch (AssertionError e) {
                    // Ignore exception
                }
            }
            log.debug("""
                    Actual JSON object compared to expected JSON object.
                    Result: {}
                    Strict: {}
                    Expected:
                    {}
                    Actual:
                    {}
                    """.stripIndent(),
                    result, strictOrder,
                    ConvertUtils.jsonObjectToString(expected),
                    ConvertUtils.jsonObjectToString(actual));
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot compare actual XML node to expected XML node.
                    Strict order: %b
                    Expected:
                    %s
                    Actual:
                    %s
                    """.stripIndent(),
                    strictOrder,
                    ConvertUtils.jsonObjectToString(expected),
                    ConvertUtils.jsonObjectToString(actual)), e);
        }
    }

    /**
     * Compares two JSON arrays.
     * @param expected The expected object.
     * @param actual The actual object.
     * @param strictOrder The strict mode - false: ignore fields and values order.
     * @return Teh true/false result.
     */
    public static boolean compareJsonArrays(JSONArray expected,
                                            JSONArray actual,
                                            boolean strictOrder) {
        try {
            boolean result = false;

            if (expected == null && actual == null) {
                log.debug("Actual JSON array and expected JSON array are null.");
                result = true;
            } else if (expected == actual) {
                log.debug("Actual JSON array and expected JSON array are the same object.");
                result = true;

            } else {
                DataValidationUtils.validateNotNull(expected, "expected");
                DataValidationUtils.validateNotNull(actual, "actual");

                try {
                    JSONAssert.assertEquals(expected, actual, strictOrder);
                    result = true;
                } catch (AssertionError ignored) {
                }
            }
            log.debug("""
                    "JSON arrays comparison passed.
                    Result: {}
                    Strict: {}
                    Expected:
                    {}
                    Actual:
                    {}
                    """.stripIndent(),
                    result, strictOrder, expected, actual);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot compare actual XML node to expected XML node.
                    Strict order: %b
                    Expected:
                    %s
                    Actual:
                    %s
                    """.stripIndent(),
                    strictOrder,
                    ConvertUtils.jsonArrayToString(expected),
                    ConvertUtils.jsonArrayToString(actual)), e);
        }
    }

    /**
     * Compares two objects.
     * @param expected The expected object.
     * @param actual The actual object.
     * @return true/false result.
     */
    public static <T> boolean compareObjects(Object expected, Object actual,
                                               boolean strictType, boolean strictOrder) {
        try {
            boolean result;

            if (expected == null && actual == null) {
                log.debug("Expected and actual objects are null.");
                return true;
            } else if (expected == actual) {
                log.debug("Expected and actual objects are the same object.");
                return true;
            }
            DataValidationUtils.validateNotNull(expected, "expected");
            Class<?> expectedClass = expected.getClass();
            Class<?> actualClass = actual.getClass();
            SmartType expectedType = SmartType.fromClass(expectedClass);

            if (strictType) {
                DataValidationUtils.validateTheSameType(expected, actual,
                        "expected", "actual");
            } else {
                // Convert actual string value to object
                if (actualClass != expectedClass &&
                        !(actual instanceof Number && expected instanceof Number)) {
                    actual = ConvertUtils.objectToObject(expectedType, actual);
                }
            }
            // JSON object
            if (expected instanceof JSONObject expecteJsonObject) {
                result = compareJsonObjects(expecteJsonObject, (JSONObject) actual, strictOrder);
            }
            // JSON array
            else if (expected instanceof JSONArray expectedJsonArray) {
                result = compareJsonArrays(expectedJsonArray, (JSONArray) actual, strictOrder);
            }
            // XML node
            else if (expected instanceof Node expectedNode) {
                result = compareXmlNodes(expectedNode, (Node) actual, strictOrder);
            }
            else if (expectedClass.isArray()) {
                result = Arrays.equals((T[]) expected, (T[])actual);
            }
            else {
                // Numbers
                if (!strictType && actual instanceof Number && expected instanceof Number) {
                    BigDecimal expectedBigDecimal = new BigDecimal(expected.toString());
                    BigDecimal actualBigDecimal = new BigDecimal(actual.toString());
                    // Compare any number type to any number type
                    result = expectedBigDecimal.compareTo(actualBigDecimal) == 0;
                }
                // String buffer
                else if (actual instanceof StringBuffer actualStringBuffer &&
                        expected instanceof StringBuffer expectedStringBuffer) {
                    return expectedStringBuffer.toString().equals(actualStringBuffer.toString());
                }
                // Smart value
                else if (actual instanceof SmartValue actualSmartValue &&
                        expected instanceof SmartValue expectedSmartValue) {
                    result = compareObjects(expectedSmartValue.getValue(),
                            actualSmartValue.getValue(), strictType, strictOrder);
                }
                // POJO
                else if (ConvertUtils.isPojoObject(expected)) {
                    JSONObject expectedPojoJson = ConvertUtils.pojoObjectToJson(expected);
                    JSONObject actualPojoJson = ConvertUtils.pojoObjectToJson(actual);
                    result = compareJsonObjects(expectedPojoJson, actualPojoJson, strictOrder);
                }
                else {
                    result = expected.equals(actual);
                }
            }
            log.debug("""
                            Expected and actual objects comparison passed.
                            Result: {}
                            Strict compare: {}
                            EXPECTED:
                            Type: {}
                            Value:
                            {}
                            ACTUAL:
                            Type: {}
                            Value:
                            {}
                            """.stripIndent(),
                    result, strictOrder, expectedClass.getName(), expected,
                    actualClass.getName(), actual);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot compare actual object to expected object.
                    Strict order: %b
                    Expected:
                    %s
                    Actual:
                    %s
                    """.stripIndent(),
                    strictOrder,
                    ConvertUtils.objectToString(expected),
                    ConvertUtils.objectToString(actual)), e);
        }
    }

    /**
     * Compares expected number with actual number.
     * @param expected The expected number.
     * @param actual The actual number.
     * @return The tru/false result.
     */
    public static boolean compareNumbers(Number expected, Number actual) {
        boolean result;

        if (expected == null && actual == null) {
            result = true;
        }
        else if (expected == null || actual == null) {
            result = false;
        }
        else {
            BigDecimal expectedBigDecimal = new BigDecimal(String.valueOf(expected));
            BigDecimal actualBigDecimal = new BigDecimal(String.valueOf(actual));

            result = expectedBigDecimal.compareTo(actualBigDecimal) == 0;
        }
        log.debug("""
                Two numbers are compared.
                Expected: {}
                Actual: {}
                Result: {}
                """.trim(),
                expected, actual, result);
        return result;
    }
}
