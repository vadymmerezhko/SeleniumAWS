package com.smarte2e.unit;

import com.smarte2e.data.SmartLocalDate;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.exceptions.SmartValidationException;
import com.smarte2e.utils.CompareUtils;
import com.smarte2e.utils.ConvertUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Node;

import java.time.LocalDate;

public class CompareUtilsTest  {

    @Test
    public void testCompareXmlNodesWithSameNodes() {
        Node expectedXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent());
        Node actualXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent());
        boolean strict = true;

        boolean result = CompareUtils.compareXmlNodes(expectedXml, actualXml, strict);
        Assert.assertTrue(result, "The comparison result should be true for identical XML nodes.");
    }

    @Test
    public void testCompareXmlNodesWithSameNodesNegative() {
        Node expectedXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent());
        Node actualXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                </items>
                """.stripIndent());
        boolean strict = true;

        boolean result = CompareUtils.compareXmlNodes(expectedXml, actualXml, strict);
        Assert.assertFalse(result, "The comparison result should be false for identical XML nodes.");
    }

    @Test
    public void testCompareXmlNodesWithDifferentOrderNonStrict() {
        // Same XML structure but with different order of child nodes
        Node expectedXml = ConvertUtils.stringToXmlNode("<root><child1>value1</child1><child2>value2</child2></root>");
        Node actualXml = ConvertUtils.stringToXmlNode("<root><child2>value2</child2><child1>value1</child1></root>");
        boolean strict = false;

        boolean result = CompareUtils.compareXmlNodes(expectedXml, actualXml, strict);
        Assert.assertTrue(result, "The comparison should pass in non-strict mode when the order of nodes is different.");
    }

    @Test
    public void testCompareXmlNodesWithDifferentValuesNonStrict() {
        // Same structure but different values for child nodes
        Node expectedXml = ConvertUtils.stringToXmlNode("<root><child>value1</child></root>");
        Node actualXml = ConvertUtils.stringToXmlNode("<root><child>value2</child></root>");
        boolean strict = false;

        boolean result = CompareUtils.compareXmlNodes(expectedXml, actualXml, strict);
        Assert.assertFalse(result, "The comparison should fail when values differ in non-strict mode.");
    }

    @Test
    public void testCompareXmlNodesWithMissingChildNonStrict() {
        // One XML node has a missing child element
        Node expectedXml = ConvertUtils.stringToXmlNode("<root><child1>value1</child1><child2>value2</child2></root>");
        Node actualXml = ConvertUtils.stringToXmlNode("<root><child1>value1</child1></root>");
        boolean strict = false;

        boolean result = CompareUtils.compareXmlNodes(expectedXml, actualXml, strict);
        Assert.assertFalse(result, "The comparison should fail when a child element is missing in non-strict mode.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCompareXmlNodesWithActualNullValuesNonStrict() {
        // One of the XML nodes is null
        Node expectedXml = ConvertUtils.stringToXmlNode("<root><child>value1</child></root>");
        boolean strict = false;

        CompareUtils.compareXmlNodes(expectedXml, null, strict);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCompareXmlNodesWithExpectedNullValuesNonStrict() {
        // One of the XML nodes is null
        Node actualXml = ConvertUtils.stringToXmlNode("<root><child>value1</child></root>");
        boolean strict = false;

        CompareUtils.compareXmlNodes(null, actualXml, strict);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCompareXmlNodesWithInvalidXmlNonStrict() {
        // One of the XML nodes is invalid
        Node expectedXml = ConvertUtils.stringToXmlNode("<root><child>value1</child></root>");
        Node actualXml = ConvertUtils.stringToXmlNode("<root><child><invalid></child></root>");
        boolean strict = false;

        CompareUtils.compareXmlNodes(expectedXml, actualXml, strict);
    }

    @Test
    public void testCompareJsonObjectsWithJSONArrayStrict() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = new JSONObject();
        JSONArray actualArray = new JSONArray();
        actualArray.put("value1");
        actualArray.put("value2");
        actualJson.put("items", actualArray);

        boolean strict = true;
        boolean result = CompareUtils.compareJsonObjects(expectedJson, actualJson, strict);
        Assert.assertTrue(result, "The comparison should pass in strict mode when the arrays are identical and in the same order.");
    }

    @Test
    public void testCompareJsonObjectsWithItselfStrict() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        boolean strict = true;
        boolean result = CompareUtils.compareJsonObjects(expectedJson, expectedJson, strict);
        Assert.assertTrue(result, "The comparison should pass in strict mode when the arrays are identical and in the same order.");
    }

    @Test
    public void testCompareJsonObjectsWithJSONArrayNonStrict() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = new JSONObject();
        JSONArray actualArray = new JSONArray();
        actualArray.put("value2");
        actualArray.put("value1");
        actualJson.put("items", actualArray);

        boolean strict = false;
        boolean result = CompareUtils.compareJsonObjects(expectedJson, actualJson, strict);
        Assert.assertTrue(result, "The comparison should pass in non-strict mode when the arrays have the same elements but in different order.");
    }

    @Test
    public void testCompareJsonObjectsWithJSONArrayNonStrictAdditionalFields() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = new JSONObject();
        JSONArray actualArray = new JSONArray();
        actualArray.put("value2");
        actualArray.put("value1");
        actualJson.put("items", actualArray);
        actualJson.put("extraField", "extraValue");

        boolean strict = false;
        boolean result = CompareUtils.compareJsonObjects(expectedJson, actualJson, strict);
        Assert.assertTrue(result, "The comparison should pass in non-strict mode even with additional fields in actual JSON.");
    }

    @Test
    public void testCompareJsonObjectsWithJSONArrayDifferentOrderStrict() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = new JSONObject();
        JSONArray actualArray = new JSONArray();
        actualArray.put("value2");
        actualArray.put("value1");
        actualJson.put("items", actualArray);

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonObjects(expectedJson, actualJson, strict));
    }

    @Test
    public void testCompareJsonObjectsWithJSONArrayDifferentValuesStrict() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = new JSONObject();
        JSONArray actualArray = new JSONArray();
        actualArray.put("value3");
        actualArray.put("value4");
        actualJson.put("items", actualArray);

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonObjects(expectedJson, actualJson, strict));
    }

    @Test
    public void testCompareJsonObjectsWithJSONArrayMissingFieldsStrict() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = new JSONObject();
        actualJson.put("otherField", "otherValue");

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonObjects(expectedJson, actualJson, strict));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCompareJsonObjectsWithActualNullJSONObject() {
        JSONObject expectedJson = new JSONObject();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");
        expectedJson.put("items", expectedArray);

        JSONObject actualJson = null;

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonObjects(expectedJson, actualJson, strict));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCompareJsonObjectsWithExpectedNullJSONObject() {
        JSONObject expectedJson = null;

        JSONObject actualJson = new JSONObject();
        JSONArray actualArray = new JSONArray();
        actualArray.put("value1");
        actualArray.put("value2");
        actualJson.put("items", actualArray);

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonObjects(expectedJson, actualJson, strict));
    }

    @Test
    public void testCompareJsonArraysIdenticalStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray actualArray = new JSONArray();
        actualArray.put("value1");
        actualArray.put("value2");

        boolean strict = true;
        boolean result = CompareUtils.compareJsonArrays(expectedArray, actualArray, strict);
        Assert.assertTrue(result, "The comparison result should be true for identical JSON arrays in strict mode.");
    }

    @Test
    public void testCompareJsonArraysIdenticalNonStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray actualArray = new JSONArray();
        actualArray.put("value2");
        actualArray.put("value1");

        boolean strict = false;
        boolean result = CompareUtils.compareJsonArrays(expectedArray, actualArray, strict);
        Assert.assertTrue(result, "The comparison result should be true for identical JSON arrays with different order in non-strict mode.");
    }

    @Test
    public void testCompareJsonArraysWithNestedObjectsNonStrict() {
        JSONObject obj1 = new JSONObject();
        obj1.put("name", "John");
        JSONObject obj2 = new JSONObject();
        obj2.put("name", "Doe");

        JSONArray expectedArray = new JSONArray();
        expectedArray.put(obj1);
        expectedArray.put(obj2);

        JSONArray actualArray = new JSONArray();
        actualArray.put(obj2);
        actualArray.put(obj1);

        boolean strict = false;
        boolean result = CompareUtils.compareJsonArrays(expectedArray, actualArray, strict);
        Assert.assertTrue(result, "The comparison should pass in non-strict mode with nested objects in different order.");
    }

    @Test
    public void testCompareJsonArraysWithItselfStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");


        boolean strict = true;
        boolean result = CompareUtils.compareJsonArrays(expectedArray, expectedArray, strict);
        Assert.assertTrue(result, "The comparison result should be true for identical JSON arrays in strict mode.");
    }

    @Test
    public void testCompareJsonArraysDifferentOrderStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray actualArray = new JSONArray();
        actualArray.put("value2");
        actualArray.put("value1");

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonArrays(expectedArray, actualArray, strict));
    }

    @Test
    public void testCompareJsonArraysWithDifferentValuesStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray actualArray = new JSONArray();
        actualArray.put("value3");
        actualArray.put("value4");

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonArrays(expectedArray, actualArray, strict));
    }

    @Test
    public void testCompareJsonArraysWithDifferentLengthsStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray actualArray = new JSONArray();
        actualArray.put("value1");

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonArrays(expectedArray, actualArray, strict));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCompareJsonArraysWithActualNullStrict() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonArrays(expectedArray, null, strict));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCompareJsonArraysWithExpectedNullStrict() {
        JSONArray actualArray = new JSONArray();
        actualArray.put("value3");
        actualArray.put("value4");

        boolean strict = true;
        Assert.assertFalse(CompareUtils.compareJsonArrays(null, actualArray, strict));
    }

    @Test
    public void testCompareObjectsIdenticalStrictType() {
        Object expected = "testString";
        Object actual = "testString";

        boolean strictType = true;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expected, actual, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass for identical objects in strict type mode.");
    }

    @Test
    public void testCompareObjectsDifferentTypeNonStrict() {
        Object expected = 100.0;
        Object actual = 100;

        boolean strictType = false;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expected, actual, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass when strict type is false, even if types differ.");
    }

    @Test
    public void testCompareDateWithStringNotStrictType() {
        LocalDate expected = LocalDate.of(1970, 5, 23);
        Object actual = "05/23/1970";

        boolean strictType = false;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expected, actual, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass when strict type is false, even if types differ.");
    }

    @Test
    public void testCompareStringWithLocalDateNotStrictType() {
        String expected = "1970-05-23";
        Object actual = LocalDate.of(1970, 5, 23);

        boolean strictType = false;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expected, actual, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass when strict type is false, even if types differ.");
    }

    @Test
    public void testCompareStringWithSmartLocalDateNotStrictType() {
        String expected = "05/23/1970";
        Object actual = SmartLocalDate.fromString("05/23/1970");

        boolean strictType = false;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expected, actual, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass when strict type is false, even if types differ.");
    }

    @Test
    public void testCompareObjectsDifferentTypeNonStrictPositive() {
        Object expected = 100;
        Object actual = 100.0;

        boolean strictType = false;
        boolean strictOrder = true;

        Assert.assertTrue(CompareUtils.compareObjects(expected, actual, strictType, strictOrder));
    }

    @Test
    public void testCompareObjectsWithJsonArraysNonStrictOrder() {
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("value1");
        expectedArray.put("value2");

        JSONArray actualArray = new JSONArray();
        actualArray.put("value2");
        actualArray.put("value1");

        boolean strictType = true;
        boolean strictOrder = false;

        boolean result = CompareUtils.compareObjects(expectedArray, actualArray, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass when order doesn't matter (non-strict mode).");
    }

    @Test
    public void testCompareObjectsWithJsonObjectsStrictOrder() {
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("name", "John");
        expectedJson.put("age", 30);

        JSONObject actualJson = new JSONObject();
        actualJson.put("name", "John");
        actualJson.put("age", 30);

        boolean strictType = true;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expectedJson, actualJson, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison should pass for identical JSON objects in strict order mode.");
    }

    @Test
    public void testCompareXmlStringWithXmlNodeNoStrictOrder() {
        String expectedXmlString = """
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent();
        Node actualXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent());
        boolean strictType = false;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expectedXmlString, actualXml, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison result should be true.");
    }

    @Test
    public void testCompareXmlNodeWithXmlStringNoStrictOrder() {
        Node expectedXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent());
        String actualXmlString = """
                <items>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                </items>
                """.stripIndent();

        boolean strictType = false;
        boolean strictOrder = false;

        boolean result = CompareUtils.compareObjects(expectedXml, actualXmlString, strictType, strictOrder);
        Assert.assertTrue(result, "The comparison result should be true.");
    }

    @Test
    public void testCompareXmlStringWithXmlNode() {
        String expectedXmlString = """
                <items>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                </items>
                """.stripIndent();
        Node actualXml = ConvertUtils.stringToXmlNode("""
                <items>
                    <item>
                        <name>Item 2</name>
                        <value>200</value>
                    </item>
                    <item>
                        <name>Item 1</name>
                        <value>100</value>
                    </item>
                </items>
                """.stripIndent());
        boolean strictType = false;
        boolean strictOrder = true;

        boolean result = CompareUtils.compareObjects(expectedXmlString, actualXml, strictType, strictOrder);
        Assert.assertFalse(result, "The comparison result should be true.");
    }

}
