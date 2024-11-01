package org.example.unit;

import org.example.data.*;
import org.example.enums.Platform;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.unit.supplemental.classes.PojoClass;
import org.example.utils.ConvertUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.List;

import static org.example.constants.Settings.JSON_LAYOUT_SPACES;

public class SmartValueTest {

    @Test
    public void testToStringWithValidInteger() {
        SmartValue smartValue = new SmartValue(42);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "42");
    }

    @Test
    public void testToStringWithValidString() {
        SmartValue smartValue = new SmartValue("Hello World");
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "Hello World");
    }

    @Test
    public void testToStringWithValidDouble() {
        SmartValue smartValue = new SmartValue(42.42);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "42.42");
    }

    @Test
    public void testToStringWithValidBoolean() {
        SmartValue smartValue = new SmartValue(true);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "true");
    }

    @Test
    public void testToStringWithValidPojo() {
        ConvertUtilsTest.Person person =
                new ConvertUtilsTest.Person("John", "Doe", 30);
        SmartValue smartValue = new SmartValue(person);
        String expectedString = """
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "age": 30
                }""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithNullValue() {
        SmartValue smartValue = new SmartValue(null);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "null");
    }

    @Test
    public void testToStringWithInvalidType() {
        SmartValue smartValue = new SmartValue(new Object());
        String expectedString = "{}";
        String result = smartValue.toString();

        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "John");
        jsonObject.put("age", 30);
        SmartValue smartValue = new SmartValue(jsonObject);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, jsonObject.toString(JSON_LAYOUT_SPACES));
    }

    @Test
    public void testToStringWithJsonArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("Java");
        jsonArray.put("XML");
        SmartValue smartValue = new SmartValue(jsonArray);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, jsonArray.toString(JSON_LAYOUT_SPACES));
    }

    @Test
    public void testToStringWithXmlDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element rootElement = document.createElement("root");
        document.appendChild(rootElement);

        SmartValue smartValue = new SmartValue(document);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <root/>
                """.stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithXmlNode() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = document.createElement("root");
        document.appendChild(rootElement);
        Node xmlNode = document.getDocumentElement();
        SmartValue smartValue = new SmartValue(xmlNode);
        String expectedString = """
                <root/>
                """.stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithLocalDate() {
        LocalDate localDate = LocalDate.of(2024, 9, 10);
        SmartValue smartValue = new SmartValue(localDate);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, localDate.toString());  // Expected format: YYYY-MM-DD
    }

    @Test
    public void testToStringWithLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 9, 10, 15, 30, 45);
        SmartValue smartValue = new SmartValue(localDateTime);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, String.valueOf(localDateTime));  // Expected format: YYYY-MM-DDTHH:MM:SS
    }

    @Test
    public void testToStringWithLocalTime() {
        LocalTime localTime = LocalTime.of(15, 30, 45);
        SmartValue smartValue = new SmartValue(localTime);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, localTime.toString());  // Expected format: HH:MM:SS
    }

    @Test
    public void testToStringWithDate() {
        Date date = new Date();
        SmartValue smartValue = new SmartValue(date);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, date.toString());  // Expected format from Date's toString()
    }

    @Test
    public void testToStringWithUrl() throws Exception {
        URL url = new URL("https://www.example.com");
        SmartValue smartValue = new SmartValue(url);

        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, url.toString());  // Expected: "https://www.example.com"
    }

    @Test
    public void testToStringWithUri() throws Exception {
        URI uri = new URI("https://www.example.com");
        SmartValue smartValue = new SmartValue(uri);

        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, uri.toString());  // Expected: "https://www.example.com"
    }

    @Test
    public void testToStringWithPath() {
        Path path = Paths.get("/home/user/documents");
        SmartValue smartValue = new SmartValue(path);

        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, path.toString());  // Expected: "/home/user/documents"
    }

    @Test
    public void testToStringWithSmartDateUsingCustomFormat() {
        // Create a SmartDate with a date string and a custom format
        String dateString = "2024-09-10";
        SmartDate smartDate = SmartDate.fromString(dateString);
        SmartValue smartValue = new SmartValue(smartDate);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, dateString);  // Expected to use the custom format
    }

    @Test
    public void testToStringWithSmartLocalDateUsingCustomFormat() {
        // Create a SmartLocalDate with a date string and a custom format
        String dateString = "2024-09-10";
        SmartLocalDate smartLocalDate = SmartLocalDate.fromString(dateString);
        SmartValue smartValue = new SmartValue(smartLocalDate);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, dateString);  // Expected to use the custom format
    }

    @Test
    public void testToStringWithSmartLocalDateTimeUsingCustomFormat() {
        // Create a SmartLocalDateTime with a date-time string and a custom format
        String dateTimeString = "2024-09-10T15:30:45";
        SmartLocalDateTime smartLocalDateTime = SmartLocalDateTime.fromString(dateTimeString);
        SmartValue smartValue = new SmartValue(smartLocalDateTime);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, dateTimeString);  // Expected to use the custom format
    }

    @Test
    public void testToStringWithSmartLocalTimeUsingCustomFormat() {
        // Create a SmartLocalTime with a time string and a custom format
        String timeString = "15:30:45";
        SmartLocalTime smartLocalTime = SmartLocalTime.fromString(timeString);
        SmartValue smartValue = new SmartValue(smartLocalTime);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, timeString);  // Expected to use the custom format
    }

    @Test
    public void testToStringWithEnum() {
        // Create a SmartValue with an Enum
        Platform platform = Platform.WINDOWS;
        SmartValue smartValue = new SmartValue(platform);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, Platform.WINDOWS.toString());
    }

    @Test
    public void testToStringWithRecord() {
        // Create a SmartValue with a Record
        ConvertUtilsTest.PersonRecord person =
                new ConvertUtilsTest.PersonRecord("John", 30);
        SmartValue smartValue = new SmartValue(person);
        String expectedString = """
                {
                    "name": "John",
                    "age": 30
                }""";
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithPojo() {
        // Create a SmartValue with a POJO
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartValue smartValue = new SmartValue(pojo);
        String expectedString = """
                {
                    "integerList": [
                        1,
                        2
                    ],
                    "name": "Some name",
                    "stringArray": [
                        "one",
                        "two",
                        "three"
                    ],
                    "stringBooleanMap": {
                        "true": true,
                        "false": false
                    },
                    "nestedPojoObject": {
                        "date": "1970-05-23",
                        "platform": "WINDOWS"
                    },
                    "value": 2
                }""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithNull() {
        SmartValue result = new SmartValue(null);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.toString(), "null");
    }

    @Test
    public void testToStringWithNaN() {
        // Create a SmartValue with NaN (Not a Number)
        Double nanValue = Double.NaN;
        SmartValue smartValue = new SmartValue(nanValue);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "NaN");  // Expected: "NaN"
    }

    @Test
    public void testToStringWithPositiveInfinity() {
        // Create a SmartValue with positive infinity
        Double positiveInfinity = Double.POSITIVE_INFINITY;
        SmartValue smartValue = new SmartValue(positiveInfinity);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "Infinity");  // Expected: "Infinity"
    }

    @Test
    public void testToStringWithNegativeInfinity() {
        // Create a SmartValue with negative infinity
        Double negativeInfinity = Double.NEGATIVE_INFINITY;
        SmartValue smartValue = new SmartValue(negativeInfinity);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "-Infinity");  // Expected: "-Infinity"
    }

    @Test
    public void testToStringWithSmartValueWrappedInSmartValue() {
        // Create a SmartValue wrapping another SmartValue
        SmartValue innerSmartValue = new SmartValue("Inner Value");
        SmartValue outerSmartValue = new SmartValue(innerSmartValue);
        String result = outerSmartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, innerSmartValue.toString());  // Expected the same value
    }

    @Test
    public void testToStringWithFile() {
        // Create a File object
        File file = new File("/path/to/file.txt");
        SmartValue smartValue = new SmartValue(file);
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, file.toString());  // Expected: "/path/to/file.txt"
    }

    @Test
    public void testToStringWithIntegerArray() {
        // Create an array of integers
        Integer[] intArray = {1, 2, 3, 4, 5};
        SmartValue smartValue = new SmartValue(intArray);
        String expectedString = """
                [
                    1,
                    2,
                    3,
                    4,
                    5
                ]""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithList() {
        // Create a List of integers
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        SmartValue smartValue = new SmartValue(list);
        String expectedString = """
                [
                    1,
                    2,
                    3,
                    4,
                    5
                ]""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithSet() {
        // Create a Set of strings
        Set<String> set = new HashSet<>(Arrays.asList("A", "B", "C"));
        SmartValue smartValue = new SmartValue(set);
        String expectedString = """              
                [
                    "A",
                    "B",
                    "C"
                ]""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);  // Expected: "[A, B, C]" (order may vary)
    }

    @Test
    public void testToStringWithQueue() {
        // Create a Queue of strings
        Queue<String> queue = new LinkedList<>(Arrays.asList("First", "Second", "Third"));
        SmartValue smartValue = new SmartValue(queue);
        String expectedString = """              
                [
                    "First",
                    "Second",
                    "Third"
                ]""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithVector() {
        // Create a Vector of integers
        Vector<Integer> vector = new Vector<>(Arrays.asList(1, 2, 3, 4, 5));
        SmartValue smartValue = new SmartValue(vector);
        String expectedString = """
                [
                    1,
                    2,
                    3,
                    4,
                    5
                ]""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithMap() {
        // Create a Map of string keys and integer values
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        SmartValue smartValue = new SmartValue(map);
        String expectedString = """
                {
                    "two": 2,
                    "three": 3,
                    "one": 1
                }""".stripIndent();
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testToStringWithClass() {
        // With wrong type
        SmartValue smartValue = new SmartValue(getClass());
        String expectedString = "class org.example.unit.SmartValueTest";
        String result = smartValue.toString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedString);
    }

    @Test
    public void testEqualsWithSameObject() {
        SmartValue smartValue = new SmartValue("Test");
        boolean result = smartValue.equals(smartValue);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithEqualSmartValues() {
        SmartValue smartValue1 = new SmartValue("Test");
        SmartValue smartValue2 = new SmartValue("Test");
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentSmartValues() {
        SmartValue smartValue1 = new SmartValue("Test1");
        SmartValue smartValue2 = new SmartValue("Test2");
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithNullValue() {
        SmartValue smartValue = new SmartValue("Test");
        boolean result = smartValue.equals(null);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithDifferentObjectTypes() {
        SmartValue smartValue = new SmartValue("Test");
        boolean result = smartValue.equals("Not a SmartValue");

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithJsonObject() {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("key", "value");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("key", "value");

        SmartValue smartValue1 = new SmartValue(jsonObject1);
        SmartValue smartValue2 = new SmartValue(jsonObject2);

        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentJsonObject() {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("key", "value1");
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("key", "value2");
        SmartValue smartValue1 = new SmartValue(jsonObject1);
        SmartValue smartValue2 = new SmartValue(jsonObject2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithJsonArray() {
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put("value1");
        jsonArray1.put("value2");

        JSONArray jsonArray2 = new JSONArray();
        jsonArray2.put("value1");
        jsonArray2.put("value2");

        SmartValue smartValue1 = new SmartValue(jsonArray1);
        SmartValue smartValue2 = new SmartValue(jsonArray2);

        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentJsonArray() {
        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put("value1");
        JSONArray jsonArray2 = new JSONArray();
        jsonArray2.put("value2");
        SmartValue smartValue1 = new SmartValue(jsonArray1);
        SmartValue smartValue2 = new SmartValue(jsonArray2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithXmlDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document1 = builder.newDocument();
        Element root1 = document1.createElement("root");
        document1.appendChild(root1);

        Document document2 = builder.newDocument();
        Element root2 = document2.createElement("root");
        document2.appendChild(root2);

        SmartValue smartValue1 = new SmartValue(document1);
        SmartValue smartValue2 = new SmartValue(document2);

        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentXmlDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document1 = builder.newDocument();
        Element root1 = document1.createElement("root1");
        document1.appendChild(root1);
        Document document2 = builder.newDocument();
        Element root2 = document2.createElement("root2");
        document2.appendChild(root2);
        SmartValue smartValue1 = new SmartValue(document1);
        SmartValue smartValue2 = new SmartValue(document2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithXmlNode() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element node1 = document.createElement("node");
        node1.setTextContent("value");
        Element node2 = document.createElement("node");
        node2.setTextContent("value");
        SmartValue smartValue1 = new SmartValue(node1);
        SmartValue smartValue2 = new SmartValue(node2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentXmlNode() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element node1 = document.createElement("node1");
        Element node2 = document.createElement("node2");
        SmartValue smartValue1 = new SmartValue(node1);
        SmartValue smartValue2 = new SmartValue(node2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithList() {
        List<String> list1 = Arrays.asList("A", "B", "C");
        List<String> list2 = Arrays.asList("a", "b", "c");
        SmartValue smartValue1 = new SmartValue(list1);
        SmartValue smartValue2 = new SmartValue(list2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithListWrongOrder() {
        List<String> list1 = Arrays.asList("A", "B", "C");
        List<String> list2 = Arrays.asList("C", "B", "A");
        SmartValue smartValue1 = new SmartValue(list1);
        SmartValue smartValue2 = new SmartValue(list2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithDifferentList() {
        List<String> list1 = Arrays.asList("A", "B");
        List<String> list2 = Arrays.asList("A", "C");
        SmartValue smartValue1 = new SmartValue(list1);
        SmartValue smartValue2 = new SmartValue(list2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithSet() {
        Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
        Set<String> set2 = new HashSet<>(Arrays.asList("A", "B", "C"));
        SmartValue smartValue1 = new SmartValue(set1);
        SmartValue smartValue2 = new SmartValue(set2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithSetOtherOrder() {
        Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
        Set<String> set2 = new HashSet<>(Arrays.asList("B", "A", "C"));
        SmartValue smartValue1 = new SmartValue(set1);
        SmartValue smartValue2 = new SmartValue(set2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentSet() {
        Set<String> set1 = new HashSet<>(Arrays.asList("A", "B"));
        Set<String> set2 = new HashSet<>(Arrays.asList("A", "C"));
        SmartValue smartValue1 = new SmartValue(set1);
        SmartValue smartValue2 = new SmartValue(set2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithQueue() {
        Queue<String> queue1 = new LinkedList<>(Arrays.asList("A", "B", "C"));
        Queue<String> queue2 = new LinkedList<>(Arrays.asList("A", "B", "C"));
        SmartValue smartValue1 = new SmartValue(queue1);
        SmartValue smartValue2 = new SmartValue(queue2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithQueueWrongOrder() {
        Queue<String> queue1 = new LinkedList<>(Arrays.asList("A", "B", "C"));
        Queue<String> queue2 = new LinkedList<>(Arrays.asList("C", "B", "A"));
        SmartValue smartValue1 = new SmartValue(queue1);
        SmartValue smartValue2 = new SmartValue(queue2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithDifferentQueue() {
        Queue<String> queue1 = new LinkedList<>(Arrays.asList("A", "B"));
        Queue<String> queue2 = new LinkedList<>(Arrays.asList("A", "C"));
        SmartValue smartValue1 = new SmartValue(queue1);
        SmartValue smartValue2 = new SmartValue(queue2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithVector() {
        Vector<String> vector1 = new Vector<>(Arrays.asList("A", "B", "C"));
        Vector<String> vector2 = new Vector<>(Arrays.asList("A", "B", "C"));
        SmartValue smartValue1 = new SmartValue(vector1);
        SmartValue smartValue2 = new SmartValue(vector2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithVectorWrongOrder() {
        Vector<String> vector1 = new Vector<>(Arrays.asList("A", "B", "C"));
        Vector<String> vector2 = new Vector<>(Arrays.asList("C", "B", "A"));
        SmartValue smartValue1 = new SmartValue(vector1);
        SmartValue smartValue2 = new SmartValue(vector2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithDifferentVector() {
        Vector<String> vector1 = new Vector<>(Arrays.asList("A", "B"));
        Vector<String> vector2 = new Vector<>(Arrays.asList("A", "C"));
        SmartValue smartValue1 = new SmartValue(vector1);
        SmartValue smartValue2 = new SmartValue(vector2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithMap() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("A", 1);
        map1.put("B", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("A", 1);
        map2.put("B", 2);
        SmartValue smartValue1 = new SmartValue(map1);
        SmartValue smartValue2 = new SmartValue(map2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithMapOtherOrder() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("A", 1);
        map1.put("B", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("B", 2);
        map2.put("A", 1);
        SmartValue smartValue1 = new SmartValue(map1);
        SmartValue smartValue2 = new SmartValue(map2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentMaps() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("A", 1);
        map1.put("B", 2);
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("A", 1);
        SmartValue smartValue1 = new SmartValue(map1);
        SmartValue smartValue2 = new SmartValue(map2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithFile() {
        File file1 = new File("/path/to/file1.txt");
        File file2 = new File("/path/to/file1.txt");
        SmartValue smartValue1 = new SmartValue(file1);
        SmartValue smartValue2 = new SmartValue(file2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentFile() {
        File file1 = new File("/path/to/file1.txt");
        File file2 = new File("/path/to/file2.txt");
        SmartValue smartValue1 = new SmartValue(file1);
        SmartValue smartValue2 = new SmartValue(file2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithUrl() throws Exception {
        URL url1 = new URL("https://www.example.com");
        URL url2 = new URL("https://www.example.com");
        SmartValue smartValue1 = new SmartValue(url1);
        SmartValue smartValue2 = new SmartValue(url2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentUrl() throws Exception {
        URL url1 = new URL("https://www.example.com");
        URL url2 = new URL("https://www.different.com");
        SmartValue smartValue1 = new SmartValue(url1);
        SmartValue smartValue2 = new SmartValue(url2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithUri() throws Exception {
        URI uri1 = new URI("https://www.example.com");
        URI uri2 = new URI("https://www.example.com");
        SmartValue smartValue1 = new SmartValue(uri1);
        SmartValue smartValue2 = new SmartValue(uri2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentUri() throws Exception {
        URI uri1 = new URI("https://www.example.com");
        URI uri2 = new URI("https://www.different.com");
        SmartValue smartValue1 = new SmartValue(uri1);
        SmartValue smartValue2 = new SmartValue(uri2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithPath() {
        Path path1 = Paths.get("/path/to/file1.txt");
        Path path2 = Paths.get("/path/to/file1.txt");
        SmartValue smartValue1 = new SmartValue(path1);
        SmartValue smartValue2 = new SmartValue(path2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentPath() {
        Path path1 = Paths.get("/path/to/file1.txt");
        Path path2 = Paths.get("/path/to/file2.txt");
        SmartValue smartValue1 = new SmartValue(path1);
        SmartValue smartValue2 = new SmartValue(path2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithStringBuffer() {
        StringBuffer buffer1 = new StringBuffer("Hello");
        StringBuffer buffer2 = new StringBuffer("Hello");
        SmartValue smartValue1 = new SmartValue(buffer1);
        SmartValue smartValue2 = new SmartValue(buffer2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentStringBuffer() {
        StringBuffer buffer1 = new StringBuffer("Hello");
        StringBuffer buffer2 = new StringBuffer("World");
        SmartValue smartValue1 = new SmartValue(buffer1);
        SmartValue smartValue2 = new SmartValue(buffer2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithStringAndNull() {
        SmartValue smartValue = new SmartValue("Test");
        boolean result = smartValue.equals(null);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithNullSmartValues() {
        SmartValue smartValue1 = new SmartValue(null);
        SmartValue smartValue2 = new SmartValue(null);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDate() {
        Date date1 = new Date(1633024800000L);  // Example timestamp
        Date date2 = new Date(1633024800000L);
        SmartValue smartValue1 = new SmartValue(date1);
        SmartValue smartValue2 = new SmartValue(date2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentDate() {
        Date date1 = new Date(1633024800000L);  // Example timestamp
        Date date2 = new Date(1633025800000L);
        SmartValue smartValue1 = new SmartValue(date1);
        SmartValue smartValue2 = new SmartValue(date2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithLocalDate() {
        LocalDate localDate1 = LocalDate.of(2024, 9, 10);
        LocalDate localDate2 = LocalDate.of(2024, 9, 10);
        SmartValue smartValue1 = new SmartValue(localDate1);
        SmartValue smartValue2 = new SmartValue(localDate2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentLocalDate() {
        LocalDate localDate1 = LocalDate.of(2024, 9, 10);
        LocalDate localDate2 = LocalDate.of(2025, 9, 10);
        SmartValue smartValue1 = new SmartValue(localDate1);
        SmartValue smartValue2 = new SmartValue(localDate2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithLocalDateTime() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 9, 10, 15, 30, 45);
        LocalDateTime localDateTime2 = LocalDateTime.of(2024, 9, 10, 15, 30, 45);
        SmartValue smartValue1 = new SmartValue(localDateTime1);
        SmartValue smartValue2 = new SmartValue(localDateTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentLocalDateTime() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 9, 10, 15, 30, 45);
        LocalDateTime localDateTime2 = LocalDateTime.of(2025, 9, 10, 15, 30, 45);
        SmartValue smartValue1 = new SmartValue(localDateTime1);
        SmartValue smartValue2 = new SmartValue(localDateTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithLocalTime() {
        LocalTime localTime1 = LocalTime.of(15, 30, 45);
        LocalTime localTime2 = LocalTime.of(15, 30, 45);

        SmartValue smartValue1 = new SmartValue(localTime1);
        SmartValue smartValue2 = new SmartValue(localTime2);

        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentLocalTime() {
        LocalTime localTime1 = LocalTime.of(15, 30, 45);
        LocalTime localTime2 = LocalTime.of(16, 30, 45);
        SmartValue smartValue1 = new SmartValue(localTime1);
        SmartValue smartValue2 = new SmartValue(localTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithSmartDate() {
        SmartDate smartDate1 = SmartDate.fromString("2024-09-10");
        SmartDate smartDate2 = SmartDate.fromString("09/10/2024");
        SmartValue smartValue1 = new SmartValue(smartDate1);
        SmartValue smartValue2 = new SmartValue(smartDate2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentSmartDate() {
        SmartDate smartDate1 = SmartDate.fromString("2024-09-10");
        SmartDate smartDate2 = SmartDate.fromString("2025-09-10");
        SmartValue smartValue1 = new SmartValue(smartDate1);
        SmartValue smartValue2 = new SmartValue(smartDate2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithSmartLocalDate() {
        SmartLocalDate smartLocalDate1 = SmartLocalDate.fromString("2024-09-10");
        SmartLocalDate smartLocalDate2 = SmartLocalDate.fromString("Sep 10th, 2024");
        SmartValue smartValue1 = new SmartValue(smartLocalDate1);
        SmartValue smartValue2 = new SmartValue(smartLocalDate2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentSmartLocalDate() {
        SmartLocalDate smartLocalDate1 = SmartLocalDate.fromString("2024-09-10");
        SmartLocalDate smartLocalDate2 = SmartLocalDate.fromString("2025-09-10");
        SmartValue smartValue1 = new SmartValue(smartLocalDate1);
        SmartValue smartValue2 = new SmartValue(smartLocalDate2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithSmartLocalDateTime() {
        SmartLocalDateTime smartLocalDateTime1 = SmartLocalDateTime.fromString("2024-9-1 5:30:45 am");
        SmartLocalDateTime smartLocalDateTime2 = SmartLocalDateTime.fromString("1st Sep 2024 5:30:45");
        SmartValue smartValue1 = new SmartValue(smartLocalDateTime1);
        SmartValue smartValue2 = new SmartValue(smartLocalDateTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentSmartLocalDateTime() {
        SmartLocalDateTime smartLocalDateTime1 = SmartLocalDateTime.fromString("2024-09-10T15:30:45");
        SmartLocalDateTime smartLocalDateTime2 = SmartLocalDateTime.fromString("2025-09-10T15:30:45");
        SmartValue smartValue1 = new SmartValue(smartLocalDateTime1);
        SmartValue smartValue2 = new SmartValue(smartLocalDateTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithSmartLocalTime() {
        SmartLocalTime smartLocalTime1 = SmartLocalTime.fromString("5:30:05 PM");
        SmartLocalTime smartLocalTime2 = SmartLocalTime.fromString("17:30:05");
        SmartValue smartValue1 = new SmartValue(smartLocalTime1);
        SmartValue smartValue2 = new SmartValue(smartLocalTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentSmartLocalTime() {
        SmartLocalTime smartLocalTime1 = SmartLocalTime.fromString("15:30:45");
        SmartLocalTime smartLocalTime2 = SmartLocalTime.fromString("16:30:45");
        SmartValue smartValue1 = new SmartValue(smartLocalTime1);
        SmartValue smartValue2 = new SmartValue(smartLocalTime2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithEnum() {
        SmartValue smartValue1 = new SmartValue(Platform.LINUX);
        SmartValue smartValue2 = new SmartValue(Platform.LINUX);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentEnum() {
        SmartValue smartValue1 = new SmartValue(Platform.LINUX);
        SmartValue smartValue2 = new SmartValue(Platform.WINDOWS);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithPojo() {
        ConvertUtilsTest.Person person1 =
                new ConvertUtilsTest.Person("John", "Green", 30);
        ConvertUtilsTest.Person person2 =
                new ConvertUtilsTest.Person("John", "Green", 30);
        SmartValue smartValue1 = new SmartValue(person1);
        SmartValue smartValue2 = new SmartValue(person2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);
    }

    @Test
    public void testEqualsWithDifferentPojo() {
        ConvertUtilsTest.Person person1 =
                new ConvertUtilsTest.Person("John", "Green", 30);
        ConvertUtilsTest.Person person2 =
                new ConvertUtilsTest.Person("Jane", "Monro",25);
        SmartValue smartValue1 = new SmartValue(person1);
        SmartValue smartValue2 = new SmartValue(person2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithRecord() {
        ConvertUtilsTest.PersonRecord person1 =
                new  ConvertUtilsTest.PersonRecord("John", 30);
        ConvertUtilsTest.PersonRecord person2 =
                new  ConvertUtilsTest.PersonRecord("John", 30);
        SmartValue smartValue1 = new SmartValue(person1);
        SmartValue smartValue2 = new SmartValue(person2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);  // Both records have the same values (John, 30)
    }

    @Test
    public void testEqualsWithDifferentRecord() {
        ConvertUtilsTest.PersonRecord person1 =
                new  ConvertUtilsTest.PersonRecord("John", 30);
        ConvertUtilsTest.PersonRecord person2 =
                new  ConvertUtilsTest.PersonRecord("Jane", 25);
        SmartValue smartValue1 = new SmartValue(person1);
        SmartValue smartValue2 = new SmartValue(person2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);  // Different record values (John, 30 vs Jane, 25)
    }

    @Test
    public void testEqualsWithIntegerArray() {
        Integer[] array1 = {1, 2, 3, 4, 5};
        Integer[] array2 = {1, 2, 3, 4, 5};
        SmartValue smartValue1 = new SmartValue(array1);
        SmartValue smartValue2 = new SmartValue(array2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertTrue(result);  // Both arrays have the same elements
    }

    @Test
    public void testEqualsWithIntegerArrayWithWrongOrder() {
        Integer[] array1 = {1, 2, 3, 4, 5};
        Integer[] array2 = {5, 4, 3, 2, 1};
        SmartValue smartValue1 = new SmartValue(array1);
        SmartValue smartValue2 = new SmartValue(array2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);
    }

    @Test
    public void testEqualsWithDifferentIntegerArray() {
        Integer[] array1 = {1, 2, 3, 4, 5};
        Integer[] array2 = {1, 2, 3, 4, 6};
        SmartValue smartValue1 = new SmartValue(array1);
        SmartValue smartValue2 = new SmartValue(array2);
        boolean result = smartValue1.equals(smartValue2);

        Assert.assertFalse(result);  // Arrays have different elements
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testListEqualsArray() {
        Integer[] array = {1, 2, 3, 4, 5};
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        SmartValue smartValue1 = new SmartValue(array);
        SmartValue smartValue2 = new SmartValue(list);
        smartValue1.equals(smartValue2);
    }

    @Test
    public void testSetValueWithString() {
        SmartValue smartValue = new SmartValue();
        String value = "Hello, World!";
        smartValue.setValue(value);

        Assert.assertEquals(smartValue.getValue(), value);
    }

    @Test
    public void testSetValueWithInteger() {
        SmartValue smartValue = new SmartValue();
        Integer value = 123;
        smartValue.setValue(value);

        Assert.assertEquals(smartValue.getValue(), value);
    }

    @Test
    public void testSetValueWithNull() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        Assert.assertNull(smartValue.getValue());
    }

    @Test
    public void testSetValueWithList() {
        SmartValue smartValue = new SmartValue();
        List<String> list = Arrays.asList("A", "B", "C");
        smartValue.setValue(list);

        Assert.assertEquals(smartValue.getValue(), list);
    }

    @Test
    public void testGetFormatWithSmartDate() {
        SmartDate smartDate = SmartDate.fromString("2024-09-10");
        SmartValue smartValue = new SmartValue(smartDate);
        String format = smartValue.getFormat();

        Assert.assertNotNull(format);
        Assert.assertEquals(format, "yyyy-MM-dd");
    }

    @Test
    public void testGetFormatWithSmartLocalDate() {
        SmartLocalDate smartLocalDate = SmartLocalDate.fromString("10th SEP 2024");
        SmartValue smartValue = new SmartValue(smartLocalDate);
        String format = smartValue.getFormat();

        Assert.assertNotNull(format);
        Assert.assertEquals(format, "dd MMM yyyy");
    }

    @Test
    public void testGetFormatWithSmartLocalDateTime() {
        SmartLocalDateTime smartLocalDateTime = SmartLocalDateTime.fromString("2024-09-10T15:30:00");
        SmartValue smartValue = new SmartValue(smartLocalDateTime);
        String format = smartValue.getFormat();

        Assert.assertNotNull(format);
        Assert.assertEquals(format, "yyyy-MM-dd'T'HH:mm:ss");
    }

    @Test
    public void testGetFormatWithSmartLocalTime() {
        SmartLocalTime smartLocalTime = SmartLocalTime.fromString("15:30:45:123");
        SmartValue smartValue = new SmartValue(smartLocalTime);
        String format = smartValue.getFormat();

        Assert.assertNotNull(format);
        Assert.assertEquals(format, "HH:mm:ss:SSS");
    }

    @Test
    public void testGetFormatForSmartNumber() {
        SmartNumber smartNumber = SmartNumber.fromString("123");
        SmartValue smartValue = new SmartValue(smartNumber);
        String format = smartValue.getFormat();

        Assert.assertEquals(format, "###");
    }

    @Test
    public void testGetSmartTypeWithValidType() {
        SmartValue smartValue = new SmartValue("ABC");
        SmartType expectedType = SmartType.fromClass(String.class);
        SmartType actualType = smartValue.getSmartType();

        Assert.assertNotNull(actualType, "The smart type should not be null.");
        Assert.assertEquals(actualType.toString(), expectedType.toString(),
                "The smart type should match the expected type.");
    }

    @Test
    public void testGetSmartTypeWithNullType() {
        SmartValue smartValue = new SmartValue(null);
        SmartType expectedType = SmartType.fromObject(null);
        SmartType actualType = smartValue.getSmartType();

        Assert.assertNotNull(actualType, "The smart type should not be null.");
        Assert.assertEquals(actualType.toString(), expectedType.toString(),
                "The smart type should match the expected type.");
    }

    @Test
    public void testGetSmartTypeWithDifferentTypes() {
        SmartValue smartValue = new SmartValue(1);
        SmartType expectedType = SmartType.fromObject(1.0);
        SmartType actualType = smartValue.getSmartType();

        Assert.assertNotNull(actualType, "The smart type should not be null.");
        Assert.assertNotEquals(actualType.toString(), expectedType.toString(),
                "The smart type should not match the expected type.");
    }

    @Test
    public void testToCharacterWithValidChar() {
        SmartValue smartValue = new SmartValue();
        Character expectedChar = 'A';
        // Assume setUp() initializes value to 'A'
        smartValue.setValue(expectedChar);
        char actualChar = smartValue.toCharacter();

        Assert.assertEquals(actualChar, expectedChar.charValue(),
                "The character value should match the expected character.");
    }

    @Test
    public void testToCharacterWithValidStringContainingOneChar() {
        SmartValue smartValue = new SmartValue();
        String value = "B";  // Single character string
        smartValue.setValue(value);
        char actualChar = smartValue.toCharacter();

        Assert.assertEquals(actualChar, 'B',
                "The character value should match the expected character from string.");
    }

    @Test
    public void testToCharacterWithValidStringContainingOneNumber() {
        SmartValue smartValue = new SmartValue();
        int value = 1;
        smartValue.setValue(value);
        char actualChar = smartValue.toCharacter();

        Assert.assertEquals(actualChar, '1',
                "The character value should match the expected character from string.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToCharacterWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String value = "Invalid";  // String with more than one character
        // Assume setUp() initializes value to "Invalid"
        smartValue.setValue(value);

        // Expecting SmartRuntimeException because value cannot be converted to char
        smartValue.toCharacter();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToCharacterWithNullValue() {
        SmartValue smartValue = new SmartValue();
        // Assume setUp() initializes value to null
        smartValue.setValue(null);

        // Expecting SmartRuntimeException because value cannot be converted to char
        smartValue.toCharacter();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToCharacterWithNonCharObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 123;  // More than 1 digit
        smartValue.setValue(value);

        // Expecting SmartRuntimeException because value is more than 1 digit
        smartValue.toCharacter();
    }

    @Test
    public void testToShortWithValidShort() {
        SmartValue smartValue = new SmartValue();
        Short expectedShort = 123;
        smartValue.setValue(expectedShort);
        short actualShort = smartValue.toShort();

        Assert.assertEquals(actualShort, expectedShort.shortValue(),
                "The short value should match the expected short.");
    }

    @Test
    public void testToShortWithValidInteger() {
        SmartValue smartValue = new SmartValue();
        Integer value = 123;
        smartValue.setValue(value);
        short actualShort = smartValue.toShort();

        Assert.assertEquals(actualShort, value.shortValue(),
                "The short value should match the expected short from integer.");
    }

    @Test
    public void testToShortWithValidString() {
        SmartValue smartValue = new SmartValue();
        String value = "123.0";  // String containing a valid number
        smartValue.setValue(value);
        short actualShort = smartValue.toShort();

        Assert.assertEquals(actualShort, 123,
                "The short value should match the expected short from string.");
    }

    @Test
    public void testToIntegerWithValidInteger() {
        SmartValue smartValue = new SmartValue();
        Integer expectedInteger = 123;
        // Assume setUp() initializes value to 123
        smartValue.setValue(expectedInteger);
        int actualInteger = smartValue.toInteger();

        Assert.assertEquals(actualInteger, expectedInteger.intValue(),
                "The integer value should match the expected integer.");
    }

    @Test
    public void testToIntegerWithValidStringContainingInteger() {
        SmartValue smartValue = new SmartValue();
        String value = "456";  // String representing a valid integer
        smartValue.setValue(value);
        int actualInteger = smartValue.toInteger();

        Assert.assertEquals(actualInteger, 456,
                "The integer value should match the expected integer from string.");
    }

    @Test
    public void testToIntegerWithValidDouble() {
        SmartValue smartValue = new SmartValue();
        Double value = 123.0;  // Double value that can be converted to an integer
        smartValue.setValue(value);
        int actualInteger = smartValue.toInteger();

        Assert.assertEquals(actualInteger, value.intValue(),
                "The integer value should match the expected integer from double.");
    }

    @Test
    public void testToLongWithValidLong() {
        SmartValue smartValue = new SmartValue();
        Long expectedLong = 123456789L;
        smartValue.setValue(expectedLong);
        long actualLong = smartValue.toLong();

        Assert.assertEquals(actualLong, expectedLong.longValue(),
                "The long value should match the expected long.");
    }

    @Test
    public void testToLongWithValidInteger() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Integer value that can be converted to long
        smartValue.setValue(value);
        long actualLong = smartValue.toLong();

        Assert.assertEquals(actualLong, value.longValue(),
                "The long value should match the expected long from integer.");
    }

    @Test
    public void testToLongWithValidStringContainingLong() {
        SmartValue smartValue = new SmartValue();
        String value = "987654321";  // String representing a valid long value
        smartValue.setValue(value);
        long actualLong = smartValue.toLong();

        Assert.assertEquals(actualLong, 987654321L,
                "The long value should match the expected long from string.");
    }

    @Test
    public void testToLongWithValidDouble() {
        SmartValue smartValue = new SmartValue();
        Double value = 12345.0;  // Double that can be converted to long
        smartValue.setValue(value);
        long actualLong = smartValue.toLong();

        Assert.assertEquals(actualLong, value.longValue(),
                "The long value should match the expected long from double.");
    }

    @Test
    public void testToBigIntegerWithValidBigInteger() {
        SmartValue smartValue = new SmartValue();
        BigInteger expectedBigInteger = new BigInteger("123456789");
        smartValue.setValue(expectedBigInteger);
        BigInteger actualBigInteger = smartValue.toBigInteger();

        Assert.assertEquals(actualBigInteger, expectedBigInteger,
                "The BigInteger value should match the expected value.");
    }

    @Test
    public void testToBigIntegerWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long value = 123456789L;  // Long value that can be converted to BigInteger
        smartValue.setValue(value);
        BigInteger actualBigInteger = smartValue.toBigInteger();

        Assert.assertEquals(actualBigInteger, BigInteger.valueOf(value),
                "The BigInteger value should match the expected value from long.");
    }

    @Test
    public void testToBigIntegerWithValidStringContainingBigInteger() {
        SmartValue smartValue = new SmartValue();
        String value = "987654321";  // String representing a valid BigInteger value
        smartValue.setValue(value);

        BigInteger actualBigInteger = smartValue.toBigInteger();
        Assert.assertEquals(actualBigInteger, new BigInteger(value),
                "The BigInteger value should match the expected value from string.");
    }

    @Test
    public void testToBigIntegerWithValidDouble() {
        SmartValue smartValue = new SmartValue();
        Double value = 12345.0;  // Double value that can be converted to BigInteger
        // Assume setUp() initializes value to 12345.0
        smartValue.setValue(value);
        BigInteger actualBigInteger = smartValue.toBigInteger();

        Assert.assertEquals(actualBigInteger, BigInteger.valueOf(value.longValue()),
                "The BigInteger value should match the expected value from double.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBigIntegerWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String value = "Invalid";  // Invalid string for BigInteger conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException because value cannot be converted to BigInteger
        smartValue.toBigInteger();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBigIntegerWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException because value is null
        smartValue.toBigInteger();
    }

    @Test
    public void testToBigIntegerWithOutOfRangeDouble() {
        SmartValue smartValue = new SmartValue();
        BigDecimal value = new BigDecimal("1E+1000");
        smartValue.setValue(value);

        BigInteger result = smartValue.toBigInteger();
        Assert.assertEquals(result, value.toBigInteger());
    }

    @Test
    public void testToFloatWithValidFloat() {
        SmartValue smartValue = new SmartValue();
        Float expectedFloat = 123.45f;
        smartValue.setValue(expectedFloat);
        float actualFloat = smartValue.toFloat();

        Assert.assertEquals(actualFloat, expectedFloat,
                "The float value should match the expected float.");
    }

    @Test
    public void testToFloatWithValidInteger() {
        SmartValue smartValue = new SmartValue();
        Integer value = 123;  // Integer value that can be converted to float
        smartValue.setValue(value);
        float actualFloat = smartValue.toFloat();

        Assert.assertEquals(actualFloat, value.floatValue(),
                "The float value should match the expected float from integer.");
    }

    @Test
    public void testToFloatWithValidStringContainingFloat() {
        SmartValue smartValue = new SmartValue();
        String value = "123.45";  // String representing a valid float value
        smartValue.setValue(value);

        float actualFloat = smartValue.toFloat();
        Assert.assertEquals(actualFloat, 123.45f,
                "The float value should match the expected float from string.");
    }

    @Test
    public void testToFloatWithValidDouble() {
        SmartValue smartValue = new SmartValue();
        Double value = 123.45;  // Double that can be converted to float
        smartValue.setValue(value);
        float actualFloat = smartValue.toFloat();

        Assert.assertEquals(actualFloat, value.floatValue(),
                "The float value should match the expected float from double.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToFloatWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String value = "Invalid";  // Invalid string for float conversion

        // Assume setUp() initializes value to "Invalid"
        smartValue.setValue(value);

        // Expecting SmartRuntimeException because value cannot be converted to float
        smartValue.toFloat();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToFloatWithNullValue() {
        SmartValue smartValue = new SmartValue();

        // Assume setUp() initializes value to null
        smartValue.setValue(null);

        // Expecting SmartRuntimeException because value is null
        smartValue.toFloat();
    }

    @Test
    public void testToFloatWithOutOfRangeDouble() {
        SmartValue smartValue = new SmartValue();
        Double value = Double.MAX_VALUE;
        smartValue.setValue(value);

        float result = smartValue.toFloat();
        Assert.assertEquals(result, Float.POSITIVE_INFINITY,
                "The float value should match the expected float from string.");
    }

    @Test
    public void testToDoubleWithValidDouble() {
        SmartValue smartValue = new SmartValue();
        Double expectedDouble = 123.45;

        // Assume setUp() initializes value to 123.45
        smartValue.setValue(expectedDouble);

        double actualDouble = smartValue.toDouble();

        Assert.assertEquals(actualDouble, expectedDouble, "The double value should match the expected double.");
    }

    @Test
    public void testToDoubleWithValidInteger() {
        SmartValue smartValue = new SmartValue();
        Integer value = 123;  // Integer value that can be converted to double

        // Assume setUp() initializes value to 123
        smartValue.setValue(value);

        double actualDouble = smartValue.toDouble();

        Assert.assertEquals(actualDouble, value.doubleValue(), "The double value should match the expected double from integer.");
    }

    @Test
    public void testToDoubleWithValidStringContainingDouble() {
        SmartValue smartValue = new SmartValue();
        String value = "123.45";  // String representing a valid double value
        smartValue.setValue(value);
        double actualDouble = smartValue.toDouble();

        Assert.assertEquals(actualDouble, 123.45,
                "The double value should match the expected double from string.");
    }

    @Test
    public void testToDoubleWithValidFloat() {
        SmartValue smartValue = new SmartValue();
        Float value = 123.45f;  // Float that can be converted to double
        smartValue.setValue(value);

        double actualDouble = smartValue.toDouble();
        Assert.assertEquals(String.valueOf(actualDouble), String.valueOf(value),
                "The double value should match the expected double from float.");
    }

    @Test
    public void testToDoubleWithNanNDouble() {
        SmartValue smartValue = new SmartValue();
        Double nanValue = 0.0 / 0.0;  // NaN value
        smartValue.setValue(nanValue);

        double actualDouble = smartValue.toDouble();
        Assert.assertEquals(actualDouble, Double.NaN,
                "The double value should match the expected double from double NaN.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDoubleWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String value = "Invalid";  // Invalid string for double conversion

        // Assume setUp() initializes value to "Invalid"
        smartValue.setValue(value);

        // Expecting SmartRuntimeException because value cannot be converted to double
        smartValue.toDouble();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDoubleWithNullValue() {
        SmartValue smartValue = new SmartValue();

        // Assume setUp() initializes value to null
        smartValue.setValue(null);

        // Expecting SmartRuntimeException because value is null
        smartValue.toDouble();
    }

    @Test
    public void testToDoubleWithOutOfRangeValue() {
        SmartValue smartValue = new SmartValue();
        BigDecimal value = new BigDecimal("1E+400");  // Value too large for a double
        smartValue.setValue(value);

        double actual = smartValue.toDouble();
        Assert.assertEquals(actual, Double.POSITIVE_INFINITY);
    }

    @Test
    public void testToBigDecimalWithValidBigDecimal() {
        SmartValue smartValue = new SmartValue();
        BigDecimal expectedBigDecimal = new BigDecimal("12345.67");
        smartValue.setValue(expectedBigDecimal);
        BigDecimal actualBigDecimal = smartValue.toBigDecimal();

        Assert.assertEquals(actualBigDecimal, expectedBigDecimal,
                "The BigDecimal value should match the expected value.");
    }

    @Test
    public void testToBigDecimalWithValidInteger() {
        SmartValue smartValue = new SmartValue();
        int value = 12345;  // Integer value that can be converted to BigDecimal
        smartValue.setValue(value);

        BigDecimal actualBigDecimal = smartValue.toBigDecimal();
        Assert.assertEquals(actualBigDecimal, BigDecimal.valueOf(value),
                "The BigDecimal value should match the expected value from integer.");
    }

    @Test
    public void testToBigDecimalWithValidStringContainingBigDecimal() {
        SmartValue smartValue = new SmartValue();
        String value = "98765.43";  // String representing a valid BigDecimal value
        smartValue.setValue(value);

        BigDecimal actualBigDecimal = smartValue.toBigDecimal();
        Assert.assertEquals(actualBigDecimal, new BigDecimal(value),
                "The BigDecimal value should match the expected value from string.");
    }

    @Test
    public void testToBigDecimalWithValidDouble() {
        SmartValue smartValue = new SmartValue();
        double value = 1.23456789e300;  // Double value that can be converted to BigDecimal
        smartValue.setValue(value);
        BigDecimal actualBigDecimal = smartValue.toBigDecimal();

        Assert.assertEquals(actualBigDecimal, BigDecimal.valueOf(value),
                "The BigDecimal value should match the expected value from double.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBigDecimalWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String value = "Invalid";
        smartValue.setValue(value);

        smartValue.toBigDecimal();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBigDecimalWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        smartValue.toBigDecimal();
    }

    @Test
    public void testToBooleanWithValidBooleanTrue() {
        SmartValue smartValue = new SmartValue();
        Boolean expectedBoolean = true;
        smartValue.setValue(expectedBoolean);
        boolean actualBoolean = smartValue.toBoolean();

        Assert.assertEquals(actualBoolean, expectedBoolean,
                "The boolean value should match the expected true value.");
    }

    @Test
    public void testToBooleanWithValidBooleanFalse() {
        SmartValue smartValue = new SmartValue();
        Boolean expectedBoolean = false;
        smartValue.setValue(expectedBoolean);
        boolean actualBoolean = smartValue.toBoolean();

        Assert.assertEquals(actualBoolean, expectedBoolean,
                "The boolean value should match the expected false value.");
    }

    @Test
    public void testToBooleanWithValidStringTrue() {
        SmartValue smartValue = new SmartValue();
        String value = "true";
        smartValue.setValue(value);
        boolean actualBoolean = smartValue.toBoolean();

        Assert.assertTrue(actualBoolean,
                "The boolean value should be true for the string 'true'.");
    }

    @Test
    public void testToBooleanWithValidStringFalse() {
        SmartValue smartValue = new SmartValue();
        String value = "false";
        smartValue.setValue(value);
        boolean actualBoolean = smartValue.toBoolean();

        Assert.assertFalse(actualBoolean,
                "The boolean value should be false for the string 'false'.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBooleanWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String value = "Invalid";
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid string for boolean
        smartValue.toBoolean();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBooleanWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toBoolean();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToBooleanWithNonBooleanObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 123;  // Non-boolean object
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toBoolean();
    }

    @Test
    public void testToDateWithValidDate() {
        SmartValue smartValue = new SmartValue();
        Date expectedDate = new Date();  // Current date and time
        smartValue.setValue(expectedDate);
        Date actualDate = smartValue.toDate();

        Assert.assertEquals(actualDate.getTime(), expectedDate.getTime(),
                "The Date value should match the expected date.");
    }

    @Test
    public void testToDateWithValidString() {
        SmartValue smartValue = new SmartValue();
        String dateString = "2024-09-14T10:15:30";  // Valid date string in ISO 8601 format
        smartValue.setValue(dateString);
        Date actualDate = smartValue.toDate();
        Date expectedDate = javax.xml.bind.DatatypeConverter.parseDateTime(dateString).getTime();

        Assert.assertEquals(actualDate, expectedDate,
                "The Date value should match the expected date parsed from the string.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidDate = "invalid-date";
        smartValue.setValue(invalidDate);

        // Expecting SmartRuntimeException due to invalid date string
        smartValue.toDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateWithNonDateObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 1970;
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toDate();
    }

    @Test
    public void testToSmartDateWithValidSmartDate() {
        SmartValue smartValue = new SmartValue();
        SmartDate expectedSmartDate = SmartDate.fromString("2024-09-14");
        smartValue.setValue(expectedSmartDate);
        SmartDate actualSmartDate = smartValue.toSmartDate();

        Assert.assertEquals(actualSmartDate, expectedSmartDate,
                "The SmartDate value should match the expected SmartDate.");
    }

    @Test
    public void testToSmartDateWithValidString() {
        SmartValue smartValue = new SmartValue();
        String dateString = "2024-09-14";
        smartValue.setValue(dateString);
        SmartDate actualSmartDate = smartValue.toSmartDate();

        SmartDate expectedSmartDate = SmartDate.fromString(dateString);
        Assert.assertEquals(actualSmartDate, expectedSmartDate,
                "The SmartDate value should match the expected SmartDate from the string.");
    }

    @Test
    public void testToSmartDateWithWeekDayMonthDayAndYearString() {
        SmartValue smartValue = new SmartValue();
        String dateString = "Sat, Sep 14, 2024";
        smartValue.setValue(dateString);
        SmartDate actualSmartDate = smartValue.toSmartDate();

        SmartDate expectedSmartDate = SmartDate.fromString(dateString);
        Assert.assertEquals(actualSmartDate, expectedSmartDate,
                "The SmartDate value should match the expected SmartDate from the string.");
    }

    @Test
    public void testToSmartDateWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long timestamp = System.currentTimeMillis();
        smartValue.setValue(timestamp);
        SmartDate actualSmartDate = smartValue.toSmartDate();

        SmartDate expectedSmartDate = SmartDate.fromMilliseconds(timestamp);
        Assert.assertEquals(actualSmartDate, expectedSmartDate,
                "The SmartDate value should match the expected SmartDate from the timestamp.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartDateWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidDate = "invalid-date";
        smartValue.setValue(invalidDate);

        // Expecting SmartRuntimeException due to invalid date string
        smartValue.toSmartDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartDateWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSmartDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartDateWithNonDateObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for SmartDate conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartDate();
    }

    @Test
    public void testToLocalDateWithValidLocalDate() {
        SmartValue smartValue = new SmartValue();
        LocalDate expectedLocalDate = LocalDate.of(2024, 9, 14);
        smartValue.setValue(expectedLocalDate);
        LocalDate actualLocalDate = smartValue.toLocalDate();

        Assert.assertEquals(actualLocalDate, expectedLocalDate,
                "The LocalDate value should match the expected LocalDate.");
    }

    @Test
    public void testToLocalDateWithValidString() {
        SmartValue smartValue = new SmartValue();
        String dateString = "2024-09-14";  // Valid ISO date string
        smartValue.setValue(dateString);
        LocalDate actualLocalDate = smartValue.toLocalDate();

        LocalDate expectedLocalDate = LocalDate.parse(dateString);
        Assert.assertEquals(actualLocalDate, expectedLocalDate,
                "The LocalDate value should match the expected LocalDate parsed from the string.");
    }

    @Test
    public void testToLocalDateWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long epochMillis = 1726281600000L;  // Represents 2024-09-14 in milliseconds since epoch
        smartValue.setValue(epochMillis);
        LocalDate actualLocalDate = smartValue.toLocalDate();

        LocalDate expectedLocalDate = Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Assert.assertEquals(actualLocalDate, expectedLocalDate,
                "The LocalDate value should match the expected LocalDate from the epoch milliseconds.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalDateWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidDate = "invalid-date";
        smartValue.setValue(invalidDate);

        // Expecting SmartRuntimeException due to invalid date string
        smartValue.toLocalDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalDateWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toLocalDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalDateWithNonDateObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for LocalDate conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toLocalDate();
    }

    @Test
    public void testToSmartLocalDateWithValidSmartLocalDate() {
        SmartValue smartValue = new SmartValue();
        SmartLocalDate expectedSmartLocalDate = SmartLocalDate.fromString("2024-09-14");
        smartValue.setValue(expectedSmartLocalDate);
        SmartLocalDate actualSmartLocalDate = smartValue.toSmartLocalDate();

        Assert.assertEquals(actualSmartLocalDate, expectedSmartLocalDate,
                "The SmartLocalDate value should match the expected SmartLocalDate.");
    }

    @Test
    public void testToSmartLocalDateWithValidString() {
        SmartValue smartValue = new SmartValue();
        String dateString = "2024-09-14";  // Valid date string
        smartValue.setValue(dateString);
        SmartLocalDate actualSmartLocalDate = smartValue.toSmartLocalDate();

        SmartLocalDate expectedSmartLocalDate = SmartLocalDate.fromString(dateString);
        Assert.assertEquals(actualSmartLocalDate, expectedSmartLocalDate,
                "The SmartLocalDate value should match the expected SmartLocalDate parsed from the string.");
    }

    @Test
    public void testToSmartLocalDateWithValidDateTimeString() {
        SmartValue smartValue = new SmartValue();
        String dateString = "2024-09-14 5:30:45.123 PM -0700";  // Valid date string with time
        smartValue.setValue(dateString);
        SmartLocalDate actualSmartLocalDate = smartValue.toSmartLocalDate();

        SmartLocalDate expectedSmartLocalDate = SmartLocalDate.fromString(dateString);
        Assert.assertEquals(actualSmartLocalDate, expectedSmartLocalDate,
                "The SmartLocalDate value should match the expected SmartLocalDate parsed from the string.");
    }

    @Test
    public void testToSmartLocalDateWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long epochMillis = 1726281600000L;  // Represents 2024-09-14 in milliseconds since epoch
        smartValue.setValue(epochMillis);
        SmartLocalDate actualSmartLocalDate = smartValue.toSmartLocalDate();

        SmartLocalDate expectedSmartLocalDate = SmartLocalDate.fromMilliseconds(epochMillis);
        Assert.assertEquals(actualSmartLocalDate, expectedSmartLocalDate,
                "The SmartLocalDate value should match the expected SmartLocalDate from the epoch milliseconds.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalDateWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidDate = "invalid-date";
        smartValue.setValue(invalidDate);

        // Expecting SmartRuntimeException due to invalid date string
        smartValue.toSmartLocalDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalDateWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSmartLocalDate();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalDateWithNonDateObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for SmartLocalDate conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartLocalDate();
    }

    @Test
    public void testToLocalDateTimeWithValidLocalDateTime() {
        SmartValue smartCurrency = new SmartValue();
        LocalDateTime expectedLocalDateTime = LocalDateTime.of(2024, 9, 14, 15, 30);
        smartCurrency.setValue(expectedLocalDateTime);
        LocalDateTime actualLocalDateTime = smartCurrency.toLocalDateTime();

        Assert.assertNotNull(actualLocalDateTime, "The LocalDateTime object should not be null.");
        Assert.assertEquals(actualLocalDateTime, expectedLocalDateTime,
                "The LocalDateTime value should match the expected value.");
    }

    @Test
    public void testToLocalDateTimeWithValidString() {
        SmartValue smartCurrency = new SmartValue();
        String dateTimeString = "2024-09-14T15:30:00";  // Valid ISO date-time string
        smartCurrency.setValue(dateTimeString);
        LocalDateTime actualLocalDateTime = smartCurrency.toLocalDateTime();

        LocalDateTime expectedLocalDateTime = LocalDateTime.parse(dateTimeString);
        Assert.assertEquals(actualLocalDateTime, expectedLocalDateTime,
                "The LocalDateTime value should match the expected value parsed from the string.");
    }

    @Test
    public void testToLocalDateTimeWithValidLong() {
        SmartValue smartCurrency = new SmartValue();
        long epochMillis = 1726281600000L;  // Represents 2024-09-14T15:30:00 in milliseconds
        smartCurrency.setValue(epochMillis);
        LocalDateTime actualLocalDateTime = smartCurrency.toLocalDateTime();

        LocalDateTime expectedLocalDateTime = Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        Assert.assertEquals(actualLocalDateTime, expectedLocalDateTime,
                "The LocalDateTime value should match the expected LocalDateTime from the epoch milliseconds.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalDateTimeWithInvalidString() {
        SmartValue smartCurrency = new SmartValue();
        String invalidDateTime = "invalid-date-time";
        smartCurrency.setValue(invalidDateTime);

        // Expecting SmartRuntimeException due to invalid date-time string
        smartCurrency.toLocalDateTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalDateTimeWithNullValue() {
        SmartValue smartCurrency = new SmartValue();
        smartCurrency.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartCurrency.toLocalDateTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalDateTimeWithNonDateObject() {
        SmartValue smartCurrency = new SmartValue();
        Integer value = 12345;  // Invalid type for LocalDateTime conversion
        smartCurrency.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartCurrency.toLocalDateTime();
    }

    @Test
    public void testToSmartLocalDateTimeWithValidSmartLocalDateTime() {
        SmartValue smartValue = new SmartValue();
        SmartLocalDateTime expectedSmartLocalDateTime = SmartLocalDateTime.fromString("2024-09-14T15:30:00");
        smartValue.setValue(expectedSmartLocalDateTime);
        SmartLocalDateTime actualSmartLocalDateTime = smartValue.toSmartLocalDateTime();

        Assert.assertNotNull(actualSmartLocalDateTime, "The SmartLocalDateTime object should not be null.");
        Assert.assertEquals(actualSmartLocalDateTime, expectedSmartLocalDateTime,
                "The SmartLocalDateTime value should match the expected value.");
    }

    @Test
    public void testToSmartLocalDateTimeWithValidString() {
        SmartValue smartValue = new SmartValue();
        String dateTimeString = "2024-09-14T15:30:00";  // Valid ISO date-time string
        smartValue.setValue(dateTimeString);
        SmartLocalDateTime actualSmartLocalDateTime = smartValue.toSmartLocalDateTime();

        SmartLocalDateTime expectedSmartLocalDateTime = SmartLocalDateTime.fromString(dateTimeString);
        Assert.assertEquals(actualSmartLocalDateTime, expectedSmartLocalDateTime,
                "The SmartLocalDateTime value should match the expected value parsed from the string.");
    }

    @Test
    public void testToSmartLocalDateTimeWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long epochMillis = 1726281600000L;  // Represents 2024-09-14T15:30:00 in milliseconds
        smartValue.setValue(epochMillis);
        SmartLocalDateTime actualSmartLocalDateTime = smartValue.toSmartLocalDateTime();

        SmartLocalDateTime expectedSmartLocalDateTime = SmartLocalDateTime.fromString(
                Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime().toString());

        Assert.assertEquals(actualSmartLocalDateTime, expectedSmartLocalDateTime,
                "The SmartLocalDateTime value should match the expected SmartLocalDateTime from the epoch milliseconds.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalDateTimeWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidDateTime = "invalid-date-time";
        smartValue.setValue(invalidDateTime);

        // Expecting SmartRuntimeException due to invalid date-time string
        smartValue.toSmartLocalDateTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalDateTimeWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSmartLocalDateTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalDateTimeWithNonDateObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for SmartLocalDateTime conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartLocalDateTime();
    }

    @Test
    public void testToLocalTimeWithValidLocalTime() {
        SmartValue smartValue = new SmartValue();
        LocalTime expectedLocalTime = LocalTime.of(15, 30);
        smartValue.setValue(expectedLocalTime);
        LocalTime actualLocalTime = smartValue.toLocalTime();

        Assert.assertNotNull(actualLocalTime, "The LocalTime object should not be null.");
        Assert.assertEquals(actualLocalTime, expectedLocalTime,
                "The LocalTime value should match the expected value.");
    }

    @Test
    public void testToLocalTimeWithValidString() {
        SmartValue smartValue = new SmartValue();
        String timeString = "15:30:00";  // Valid ISO time string
        smartValue.setValue(timeString);
        LocalTime actualLocalTime = smartValue.toLocalTime();

        LocalTime expectedLocalTime = LocalTime.parse(timeString);
        Assert.assertEquals(actualLocalTime, expectedLocalTime,
                "The LocalTime value should match the expected value parsed from the string.");
    }

    @Test
    public void testToLocalTimeWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long epochMillis = 1726281600000L;  // Represents time in milliseconds
        smartValue.setValue(epochMillis);
        LocalTime actualLocalTime = smartValue.toLocalTime();

        LocalTime expectedLocalTime = Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
        Assert.assertEquals(actualLocalTime, expectedLocalTime,
                "The LocalTime value should match the expected LocalTime from the epoch milliseconds.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalTimeWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidTime = "invalid-time";
        smartValue.setValue(invalidTime);

        // Expecting SmartRuntimeException due to invalid time string
        smartValue.toLocalTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalTimeWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toLocalTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToLocalTimeWithNonTimeObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for LocalTime conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toLocalTime();
    }

    @Test
    public void testToSmartLocalTimeWithValidSmartLocalTime() {
        SmartValue smartValue = new SmartValue();
        SmartLocalTime expectedSmartLocalTime = SmartLocalTime.fromString("15:30:00");
        smartValue.setValue(expectedSmartLocalTime);
        SmartLocalTime actualSmartLocalTime = smartValue.toSmartLocalTime();

        Assert.assertNotNull(actualSmartLocalTime, "The SmartLocalTime object should not be null.");
        Assert.assertEquals(actualSmartLocalTime, expectedSmartLocalTime,
                "The SmartLocalTime value should match the expected value.");
    }

    @Test
    public void testToSmartLocalTimeWithValidString() {
        SmartValue smartValue = new SmartValue();
        String timeString = "15:30:00";  // Valid ISO time string
        smartValue.setValue(timeString);
        SmartLocalTime actualSmartLocalTime = smartValue.toSmartLocalTime();

        SmartLocalTime expectedSmartLocalTime = SmartLocalTime.fromString(timeString);
        Assert.assertEquals(actualSmartLocalTime, expectedSmartLocalTime,
                "The SmartLocalTime value should match the expected value parsed from the string.");
    }

    @Test
    public void testToSmartLocalTimeWithValidLong() {
        SmartValue smartValue = new SmartValue();
        long epochMillis = 1726281600000L;  // Represents time in milliseconds
        smartValue.setValue(epochMillis);
        SmartLocalTime actualSmartLocalTime = smartValue.toSmartLocalTime();

        SmartLocalTime expectedSmartLocalTime = SmartLocalTime.fromString(
                Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalTime().toString());

        Assert.assertEquals(actualSmartLocalTime, expectedSmartLocalTime,
                "The SmartLocalTime value should match the expected SmartLocalTime from the epoch milliseconds.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalTimeWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidTime = "invalid-time";
        smartValue.setValue(invalidTime);


        smartValue.toSmartLocalTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalTimeWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSmartLocalTime();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartLocalTimeWithNonTimeObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for SmartLocalTime conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartLocalTime();
    }

    @Test
    public void testToSmartNumberWithValidSmartNumber() {
        SmartValue smartValue = new SmartValue();
        SmartNumber expectedSmartNumber = SmartNumber.fromString("12345.67");
        smartValue.setValue(expectedSmartNumber);
        SmartNumber actualSmartNumber = smartValue.toSmartNumber();

        Assert.assertNotNull(actualSmartNumber, "The SmartNumber object should not be null.");
        Assert.assertEquals(actualSmartNumber, expectedSmartNumber,
                "The SmartNumber value should match the expected value.");
    }

    @Test
    public void testToSmartNumberWithValidString() {
        SmartValue smartValue = new SmartValue();
        String numberString = "12345.67";  // Valid numeric string
        smartValue.setValue(numberString);
        SmartNumber actualSmartNumber = smartValue.toSmartNumber();

        SmartNumber expectedSmartNumber = SmartNumber.fromString(numberString);
        Assert.assertEquals(actualSmartNumber, expectedSmartNumber,
                "The SmartNumber value should match the expected value parsed from the string.");
    }

    @Test
    public void testToSmartNumberWithValidLong() {
        SmartValue smartValue = new SmartValue();
        Long numberValue = 1234567L;  // Valid numeric value
        smartValue.setValue(numberValue);
        SmartNumber actualSmartNumber = smartValue.toSmartNumber();

        SmartNumber expectedSmartNumber = SmartNumber.fromString(numberValue.toString());
        Assert.assertEquals(actualSmartNumber, expectedSmartNumber,
                "The SmartNumber value should match the expected SmartNumber from the long value.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartNumberWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidNumber = "invalid-number";
        smartValue.setValue(invalidNumber);

        // Expecting SmartRuntimeException due to invalid number string
        smartValue.toSmartNumber();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartNumberWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSmartNumber();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartNumberWithNonNumberObject() {
        SmartValue smartValue = new SmartValue();
        String value = "non-number-object";  // Invalid type for SmartNumber conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartNumber();
    }

    @Test
    public void testToSmartCurrencyWithValidSmartCurrency() {
        SmartValue smartValue = new SmartValue();
        SmartCurrency expectedSmartCurrency = SmartCurrency.fromValue(1234.56, "US", "en");
        smartValue.setValue(expectedSmartCurrency);
        SmartCurrency actualSmartCurrency = smartValue.toSmartCurrency();

        Assert.assertNotNull(actualSmartCurrency, "The SmartCurrency object should not be null.");
        Assert.assertEquals(actualSmartCurrency, expectedSmartCurrency,
                "The SmartCurrency value should match the expected value.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartCurrencyWithValidString() {
        SmartValue smartValue = new SmartValue();
        String currencyString = "1234.56";
        smartValue.setValue(currencyString);

        // Should throws SmartRuntimeException - no currency code or symbol
        smartValue.toSmartCurrency();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartCurrencyWithValidLong() {
        SmartValue smartValue = new SmartValue();
        Long currencyValue = 1234567L;
        smartValue.setValue(currencyValue);

        // Throws SmartRuntimeException - no currency code or symbol
        smartValue.toSmartCurrency();
    }

    @Test
    public void testToSmartPhoneNumberWithValidSmartPhoneNumber() {
        SmartValue smartValue = new SmartValue();
        SmartPhoneNumber expectedSmartPhoneNumber = SmartPhoneNumber.fromString("+1234567890");
        smartValue.setValue(expectedSmartPhoneNumber);
        SmartPhoneNumber actualSmartPhoneNumber = smartValue.toSmartPhoneNumber();

        Assert.assertNotNull(actualSmartPhoneNumber, "The SmartPhoneNumber object should not be null.");
        Assert.assertEquals(actualSmartPhoneNumber, expectedSmartPhoneNumber,
                "The SmartPhoneNumber value should match the expected value.");
    }

    @Test
    public void testToSmartPhoneNumberWithValidString() {
        SmartValue smartValue = new SmartValue();
        String phoneNumberString = "+1234567890";  // Valid phone number string
        smartValue.setValue(phoneNumberString);
        SmartPhoneNumber actualSmartPhoneNumber = smartValue.toSmartPhoneNumber();

        SmartPhoneNumber expectedSmartPhoneNumber = SmartPhoneNumber.fromString(phoneNumberString);
        Assert.assertEquals(actualSmartPhoneNumber.toString(), expectedSmartPhoneNumber.toString(),
                "The SmartPhoneNumber value should match the expected value parsed from the string.");
    }

    @Test
    public void testToSmartPhoneNumberWithExtension() {
        SmartPhoneNumber phoneNumber = SmartPhoneNumber.fromString("+1(800)123-456 x1234");
        Number expectedPhoneNumber = new BigInteger("18001234561234");
        SmartValue smartValue = new SmartValue(phoneNumber);
        Number actualPhoneNumber = smartValue.toSmartPhoneNumber().getNumber();

        Assert.assertNotNull(actualPhoneNumber, "The phone number should not be null.");
        Assert.assertEquals(actualPhoneNumber, expectedPhoneNumber,
                "The converted phone number with extension should match the expected value.");
    }

    @Test
    public void testToSmartPhoneNumberWithLettersAndExtension() {
        SmartPhoneNumber phoneNumber = SmartPhoneNumber.fromString("+1-800-MY-APPLE x123");
        Number expectedPhoneNumber = new BigInteger("18006927753123");
        SmartValue smartValue = new SmartValue(phoneNumber);
        Number actualPhoneNumber = smartValue.toSmartPhoneNumber().getNumber();

        Assert.assertNotNull(actualPhoneNumber, "The phone number should not be null.");
        Assert.assertEquals(actualPhoneNumber, expectedPhoneNumber,
                "The converted phone number with extension should match the expected value.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartPhoneNumberWithInvalidString() {
        SmartValue smartValue = new SmartValue();
        String invalidPhoneNumber = "invalid-phone-number";
        smartValue.setValue(invalidPhoneNumber);

        // Expecting SmartRuntimeException due to invalid phone number string
        smartValue.toSmartPhoneNumber();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartPhoneNumberWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSmartPhoneNumber();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartPhoneNumberWithTooShortPhoneNumberObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for SmartPhoneNumber conversion - too short
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartPhoneNumber();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSmartPhoneNumberWithTooLongPhoneNumberObject() {
        SmartValue smartValue = new SmartValue();
        double value = 1e22;  // Invalid type for SmartPhoneNumber conversion - too long
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toSmartPhoneNumber();
    }

    @Test
    public void testToFileWithValidFile() {
        SmartValue smartValue = new SmartValue();
        File expectedFile = new File("/path/to/file.txt");
        smartValue.setValue(expectedFile);
        File actualFile = smartValue.toFile();

        Assert.assertNotNull(actualFile, "The File object should not be null.");
        Assert.assertEquals(actualFile, expectedFile,
                "The File value should match the expected value.");
    }

    @Test
    public void testToFileWithValidStringPath() {
        SmartValue smartValue = new SmartValue();
        String filePath = "/path/to/file.txt";
        File expectedFile = new File(filePath);
        smartValue.setValue(filePath);
        File actualFile = smartValue.toFile();

        Assert.assertNotNull(actualFile, "The File object should not be null.");
        Assert.assertEquals(actualFile, expectedFile,
                "The File value should match the expected value from the string path.");
    }

    @Test
    public void testToFileWithValidURI() {
        SmartValue smartValue = new SmartValue();
        File expectedFile = new File("C:\\Users\\michael\\Documents\\text.txt");
        smartValue.setValue(expectedFile);
        File actualFile = smartValue.toFile();

        Assert.assertNotNull(actualFile, "The File object should not be null.");
        Assert.assertEquals(actualFile, expectedFile,
                "The File value should match the expected value from the file.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToFileWithInvalidStringPath() {
        SmartValue smartValue = new SmartValue();
        String invalidFilePath = "invalid:/path/to/file.txt";
        smartValue.setValue(invalidFilePath);

        // Expecting SmartRuntimeException due to invalid file path
        smartValue.toFile();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToFileWithNull() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to invalid file path
        smartValue.toFile();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToFileWithNonFileObject() {
        SmartValue smartValue = new SmartValue();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "value"); // Invalid type for File conversion
        smartValue.setValue(jsonObject);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toFile();
    }

    @Test
    public void testToPathWithValidPath() {
        SmartValue smartValue = new SmartValue();
        Path expectedPath = Path.of("/path/to/file.txt");
        smartValue.setValue(expectedPath);
        Path actualPath = smartValue.toPath();

        Assert.assertNotNull(actualPath, "The Path object should not be null.");
        Assert.assertEquals(actualPath, expectedPath,
                "The Path value should match the expected value.");
    }

    @Test
    public void testToPathWithValidFile() {
        SmartValue smartValue = new SmartValue();
        File file = new File("/path/to/file.txt");
        Path expectedPath = file.toPath();
        smartValue.setValue(file);
        Path actualPath = smartValue.toPath();

        Assert.assertNotNull(actualPath, "The Path object should not be null.");
        Assert.assertEquals(actualPath, expectedPath,
                "The Path value should match the expected value.");
    }

    @Test
    public void testToPathWithValidStringPath() {
        SmartValue smartValue = new SmartValue();
        String filePath = "/path/to/file.txt";
        Path expectedPath = Path.of(filePath);
        smartValue.setValue(filePath);
        Path actualPath = smartValue.toPath();

        Assert.assertNotNull(actualPath, "The Path object should not be null.");
        Assert.assertEquals(actualPath, expectedPath,
                "The Path value should match the expected value from the string path.");
    }

    @Test
    public void testToPathWithValidWindowsStringPath() {
        SmartValue smartValue = new SmartValue();
        String filePath = "C:\\Users\\john\\OneDrive\\Documents\\text.prf";
        Path expectedPath = Path.of(filePath);
        smartValue.setValue(filePath);
        Path actualPath = smartValue.toPath();

        Assert.assertNotNull(actualPath, "The Path object should not be null.");
        Assert.assertEquals(actualPath, expectedPath,
                "The Path value should match the expected value from the string path.");
    }


    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToPathWithInvalidStringPath() {
        SmartValue smartValue = new SmartValue();
        String invalidFilePath = "invalid:/path/to/file.txt";
        smartValue.setValue(invalidFilePath);

        // Expecting SmartRuntimeException due to invalid file path
        smartValue.toPath();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToPathWithNonPathObject() {
        SmartValue smartValue = new SmartValue();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "value"); // Invalid type for File conversion
        smartValue.setValue(jsonObject);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toPath();
    }

    @Test
    public void testToURLWithValidURL() throws MalformedURLException {
        SmartValue smartValue = new SmartValue();
        URL expectedURL = new URL("https://www.example.com");
        smartValue.setValue(expectedURL);
        URL actualURL = smartValue.toURL();

        Assert.assertNotNull(actualURL, "The URL object should not be null.");
        Assert.assertEquals(actualURL, expectedURL,
                "The URL value should match the expected value.");
    }

    @Test
    public void testToURLWithValidStringURL() throws MalformedURLException {
        SmartValue smartValue = new SmartValue();
        String urlString = "https://www.example.com";
        URL expectedURL = new URL(urlString);
        smartValue.setValue(urlString);
        URL actualURL = smartValue.toURL();

        Assert.assertNotNull(actualURL, "The URL object should not be null.");
        Assert.assertEquals(actualURL, expectedURL,
                "The URL value should match the expected value from the string.");
    }

    @Test
    public void testToURLWithValidURI() throws MalformedURLException, URISyntaxException {
        SmartValue smartValue = new SmartValue();
        URI uri = new URI("https://www.example.com");
        URL expectedURL = uri.toURL();
        smartValue.setValue(uri);
        URL actualURL = smartValue.toURL();

        Assert.assertNotNull(actualURL, "The URL object should not be null.");
        Assert.assertEquals(actualURL, expectedURL,
                "The URL value should match the expected value from the URI.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToURLWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toURL();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToURLWithInvalidStringURL() {
        SmartValue smartValue = new SmartValue();
        String invalidURL = "htp://invalid-url";
        smartValue.setValue(invalidURL);

        // Expecting SmartRuntimeException due to invalid URL format
        smartValue.toURL();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToURLWithNonURLObject() {
        SmartValue smartValue = new SmartValue();
        Integer value = 12345;  // Invalid type for URL conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toURL();
    }

    @Test
    public void testToURIWithValidURI() throws URISyntaxException {
        SmartValue smartValue = new SmartValue();
        URI expectedURI = new URI("https://www.example.com");
        smartValue.setValue(expectedURI);
        // The URI object should be valid and match the expected value
        URI actualURI = smartValue.toURI();

        Assert.assertNotNull(actualURI, "The URI object should not be null.");
        Assert.assertEquals(actualURI, expectedURI,
                "The URI value should match the expected value.");
    }

    @Test
    public void testToURIWithValidStringURI() throws URISyntaxException {
        SmartValue smartValue = new SmartValue();
        String uriString = "https://www.example.com";
        URI expectedURI = new URI(uriString);
        smartValue.setValue(uriString);
        // The URI object should be valid and match the expected value from the string
        URI actualURI = smartValue.toURI();

        Assert.assertNotNull(actualURI, "The URI object should not be null.");
        Assert.assertEquals(actualURI, expectedURI,
                "The URI value should match the expected value from the string.");
    }

    @Test
    public void testToURIWithValidURL() throws MalformedURLException, URISyntaxException {
        SmartValue smartValue = new SmartValue();
        URL url = new URL("https://www.example.com");
        URI expectedURI = url.toURI();
        smartValue.setValue(url);
        // The URI object should be valid and match the expected value from the URL
        URI actualURI = smartValue.toURI();

        Assert.assertNotNull(actualURI, "The URI object should not be null.");
        Assert.assertEquals(actualURI, expectedURI,
                "The URI value should match the expected value from the URL.");
    }

    @Test
    public void testToURIWithValidFile() {
        SmartValue smartValue = new SmartValue();
        String filePth = "text.txt";
        File file = new File(filePth);
        smartValue.setValue(file);
        // The URI object should be valid and match the expected value from the file
        URI actualURI = smartValue.toURI();

        Assert.assertNotNull(actualURI, "The URI object should not be null.");
        Assert.assertEquals(actualURI, file.toURI(),
                "The URI string should match the expected file URI.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToURIWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toURI();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToURIWithInvalidStringURI() {
        SmartValue smartValue = new SmartValue();
        String invalidURI = "http://invalid-uri-%$&%^&%";
        smartValue.setValue(invalidURI);

        // Expecting SmartRuntimeException due to invalid URI format
        smartValue.toURI();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToURIWithNonURIObject() {
        SmartValue smartValue = new SmartValue();
        Class<?> value = getClass();  // Invalid type for URI conversion
        smartValue.setValue(value);

        // Expecting SmartRuntimeException due to invalid object type
        smartValue.toURI();
    }

    @Test
    public void testToListWithValidList() {
        SmartValue smartValue = new SmartValue();
        List<String> expectedList = Arrays.asList("one", "two", "three");
        smartValue.setValue(expectedList);
        List<String> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedList,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidArrayLisOfIntegers() {
        SmartValue smartValue = new SmartValue();
        List<Integer> expectedList = new ArrayList<>(Arrays.asList(1, 2, 3));
        smartValue.setValue(expectedList);
        List<Integer> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedList,
                "The List value should match the expected ArrayList value.");
    }

    @Test
    public void testToListWithValidArrayListOfChars() {
        SmartValue smartValue = new SmartValue();
        List<Character> expectedList = new ArrayList<>(Arrays.asList('A', 'B', 'C'));
        smartValue.setValue(expectedList);
        List<Integer> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedList,
                "The List value should match the expected ArrayList value.");
    }

    @Test
    public void testToListWithValidArrayListWithDifferentElementTypes() {
        SmartValue smartValue = new SmartValue();
        List<Object> expectedList = new ArrayList<>(Arrays.asList(1, "two", 'A', false));
        smartValue.setValue(expectedList);
        List<Object> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedList,
                "The List value should match the expected ArrayList value.");
    }

    @Test
    public void testToListWithVaQueue() {
        SmartValue smartValue = new SmartValue();
        Queue<String> expectedList = new LinkedList<>(Arrays.asList("A", "B", "C"));
        smartValue.setValue(expectedList);
        List<String> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedList,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidSet() {
        SmartValue smartValue = new SmartValue();
        Set<String> expectedSet = new HashSet<>();
        expectedSet.add("One");
        expectedSet.add("Two");
        expectedSet.add("Three");
        smartValue.setValue(expectedSet);
        List<String> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedSet,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidVector() {
        SmartValue smartValue = new SmartValue();
        Vector<Integer> expectedVector = new Vector<>();
        expectedVector.add(111);
        expectedVector.add(222);
        expectedVector.add(333);
        smartValue.setValue(expectedVector);
        List<String> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedVector,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidArray() {
        SmartValue smartValue = new SmartValue();
        Character[] expectedArray = {'A', 'B', 'C'};
        smartValue.setValue(expectedArray);
        List<Character> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList.toArray(), expectedArray,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidArrayOfDifferentTypes() {
        SmartValue smartValue = new SmartValue();
        Object[] expectedArray = {'A', "Two", 3, 12.3, true};
        smartValue.setValue(expectedArray);
        List<Character> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList.toArray(), expectedArray,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidJsonArrayOfStrings() {
        SmartValue smartValue = new SmartValue();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put("one");
        expectedArray.put("two");
        expectedArray.put("three");
        smartValue.setValue(expectedArray);
        List<Character> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(ConvertUtils.objectToString(actualList.toArray()),
                ConvertUtils.jsonArrayToString(expectedArray),
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidJsonArray() {
        SmartValue smartValue = new SmartValue();
        JSONArray expectedArray = new JSONArray();
        expectedArray.put(1);
        expectedArray.put("two");
        expectedArray.put('A');
        expectedArray.put(true);
        smartValue.setValue(expectedArray);
        List<Character> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList, expectedArray,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidJsonArrayString() {
        SmartValue smartValue = new SmartValue();
        String jsonArrayString = """
                [
                    1,
                    2,
                    3
                ]""".stripIndent();
        smartValue.setValue(jsonArrayString);
        List<Integer> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(ConvertUtils.objectToString(actualList), jsonArrayString,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidJsonArrayStringOfJsonArrays() {
        SmartValue smartValue = new SmartValue();
        String jsonArrayString = """
                [
                    [
                        11,
                        12
                    ],
                    [
                        21,
                        22
                    ]
                ]""".stripIndent();
        smartValue.setValue(jsonArrayString);
        List<List<Integer>> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(ConvertUtils.objectToString(actualList), jsonArrayString,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidJsonArrayStringWithDifferentTypes() {
        SmartValue smartValue = new SmartValue();
        String jsonArrayString = """
                [
                    "string",
                    123,
                    123.456,
                    true,
                    null
                ]""".stripIndent();
        smartValue.setValue(jsonArrayString);
        List<Object> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(ConvertUtils.objectToString(actualList), jsonArrayString,
                "The List value should match the expected value.");
    }

    @Test
    public void testToListWithValidCsvString() {
        SmartValue smartValue = new SmartValue();
        String jsonArrayString = """
                11, 12, 13
                21, 22, 23
                31, 32, 33
                """.stripIndent();
        smartValue.setValue(jsonArrayString);
        List<List<Integer>> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList.get(0).get(0), 11,
                "The List value with index 0, 0 should match the expected value 11.");
        Assert.assertEquals(actualList.get(2).get(2), 33,
                "The List value with index 2, 2 should match the expected value 33.");
    }

    @Test
    public void testToListWithValidCsvStringWithDifferentTypes() {
        SmartValue smartValue = new SmartValue();
        String jsonArrayString = """
                11, "aaa", true, 123.456
                21, "bbb", false, 0.0
                31, "ccc", false, null
                """.stripIndent();
        smartValue.setValue(jsonArrayString);
        List<List<Object>> actualList = smartValue.toList();

        Assert.assertNotNull(actualList, "The List object should not be null.");
        Assert.assertEquals(actualList.get(0).get(0), 11,
                "The List value with index 0, 0 should match the expected value 11.");
        Assert.assertEquals(actualList.get(0).get(3), 123.456,
                "The List value with index 0, 3 should match the expected value 123.456.");
        Assert.assertEquals(actualList.get(1).get(1), "bbb",
                "The List value with index 1, 1 should match the expected value 'bbb'.");
        Assert.assertEquals(actualList.get(2).get(2), false,
                "The List value with index 2, 2 should match the expected value false.");
        Assert.assertNull(actualList.get(2).get(3),
                "The List value with index 2, 3 should match the expected value null.");
    }

    @Test
    public void testToSetWithValidList() {
        SmartValue smartValue = new SmartValue();
        List<String> inputList = List.of("one", "two", "three", "two");
        smartValue.setValue(inputList);
        // Convert to Set
        Set<String> resultSet = smartValue.toSet();

        // Assertions
        Assert.assertNotNull(resultSet, "The resulting set should not be null.");
        Assert.assertEquals(resultSet.size(), 3, "The set size should be 3 as duplicates should be removed.");
        Assert.assertTrue(resultSet.contains("one"), "The set should contain 'one'.");
        Assert.assertTrue(resultSet.contains("two"), "The set should contain 'two'.");
        Assert.assertTrue(resultSet.contains("three"), "The set should contain 'three'.");
    }

    @Test
    public void testToSetWithEmptyList() {
        SmartValue smartValue = new SmartValue();
        List<String> emptyList = List.of();
        smartValue.setValue(emptyList);
        // Convert to Set
        Set<String> resultSet = smartValue.toSet();

        // Assertions
        Assert.assertNotNull(resultSet, "The resulting set should not be null.");
        Assert.assertTrue(resultSet.isEmpty(), "The set should be empty.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSetWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toSet();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToSetWithNonListValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue("InvalidType");

        // Expecting SmartRuntimeException due to invalid value type
        smartValue.toSet();
    }

    @Test
    public void testToQueueWithValidList() {
        SmartValue smartValue = new SmartValue();
        List<String> inputList = List.of("one", "two", "three");
        smartValue.setValue(inputList);
        // Convert to Queue
        Queue<String> resultQueue = smartValue.toQueue();

        // Assertions
        Assert.assertNotNull(resultQueue, "The resulting queue should not be null.");
        Assert.assertEquals(resultQueue.size(), 3, "The queue size should be 3.");
        Assert.assertEquals(resultQueue.peek(), "one", "The first element should be 'one'.");
        Assert.assertTrue(resultQueue.contains("two"), "The queue should contain 'two'.");
        Assert.assertTrue(resultQueue.contains("three"), "The queue should contain 'three'.");
    }

    @Test
    public void testToQueueWithEmptyList() {
        SmartValue smartValue = new SmartValue();
        List<String> emptyList = List.of();
        smartValue.setValue(emptyList);
        // Convert to Queue
        Queue<String> resultQueue = smartValue.toQueue();

        // Assertions
        Assert.assertNotNull(resultQueue, "The resulting queue should not be null.");
        Assert.assertTrue(resultQueue.isEmpty(), "The queue should be empty.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToQueueWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toQueue();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToQueueWithNonListValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(12345);

        // Expecting SmartRuntimeException due to invalid value type
        smartValue.toQueue();
    }

    @Test
    public void testToVectorWithValidList() {
        SmartValue smartValue = new SmartValue();
        List<String> inputList = List.of("one", "two", "three");
        smartValue.setValue(inputList);
        // Convert to Vector
        Vector<String> resultVector = smartValue.toVector();

        // Assertions
        Assert.assertNotNull(resultVector, "The resulting vector should not be null.");
        Assert.assertEquals(resultVector.size(), 3, "The vector size should be 3.");
        Assert.assertEquals(resultVector.get(0), "one", "The first element should be 'one'.");
        Assert.assertTrue(resultVector.contains("two"), "The vector should contain 'two'.");
        Assert.assertTrue(resultVector.contains("three"), "The vector should contain 'three'.");
    }

    @Test
    public void testToVectorWithEmptyList() {
        SmartValue smartValue = new SmartValue();
        List<String> emptyList = List.of();
        smartValue.setValue(emptyList);
        // Convert to Vector
        Vector<String> resultVector = smartValue.toVector();

        // Assertions
        Assert.assertNotNull(resultVector, "The resulting vector should not be null.");
        Assert.assertTrue(resultVector.isEmpty(), "The vector should be empty.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToVectorWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toVector();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToVectorWithNonListValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(12345);

        // Expecting SmartRuntimeException due to invalid value type
        smartValue.toVector();
    }

    @Test
    public void testToMapWithValidMap() {
        SmartValue smartValue = new SmartValue();
        Map<String, Integer> inputMap = Map.of("one", 1, "two", 2, "three", 3);
        smartValue.setValue(inputMap);
        // Convert to Map
        Map<String, Integer> resultMap = smartValue.toMap();

        Assert.assertNotNull(resultMap, "The resulting map should not be null.");
        Assert.assertEquals(resultMap.size(), 3, "The map size should be 3.");
        Assert.assertEquals(resultMap.get("one"), Integer.valueOf(1), "The value for key 'one' should be 1.");
        Assert.assertEquals(resultMap.get("two"), Integer.valueOf(2), "The value for key 'two' should be 2.");
        Assert.assertEquals(resultMap.get("three"), Integer.valueOf(3), "The value for key 'three' should be 3.");
    }

    @Test
    public void testToMapWithEmptyMap() {
        SmartValue smartValue = new SmartValue();
        Map<String, String> emptyMap = Map.of();
        smartValue.setValue(emptyMap);
        // Convert to Map
        Map<String, String> resultMap = smartValue.toMap();

        Assert.assertNotNull(resultMap, "The resulting map should not be null.");
        Assert.assertTrue(resultMap.isEmpty(), "The map should be empty.");
    }

    @Test
    public void testToMapWithValidJsonObject() {
        SmartValue smartValue = new SmartValue();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("one", 1);
        jsonObject.put("two", 2);
        jsonObject.put("three", 3);
        smartValue.setValue(jsonObject);
        // Convert to Map
        Map<String, Integer> resultMap = smartValue.toMap();

        Assert.assertNotNull(resultMap, "The resulting map should not be null.");
        Assert.assertEquals(resultMap.size(), 3, "The map size should be 3.");
        Assert.assertEquals(resultMap.get("one"), Integer.valueOf(1), "The value for key 'one' should be 1.");
        Assert.assertEquals(resultMap.get("two"), Integer.valueOf(2), "The value for key 'two' should be 2.");
        Assert.assertEquals(resultMap.get("three"), Integer.valueOf(3), "The value for key 'three' should be 3.");
    }

    @Test
    public void testToMapWithValidJsonObjectWithDifferentTypes() {
        SmartValue smartValue = new SmartValue();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("one", 1.1);
        jsonObject.put("two", "2");
        jsonObject.put("three", true);
        jsonObject.put("four", JSONObject.NULL);
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("one", 1.1);
        expectedMap.put("two", "2");
        expectedMap.put("three", true);
        expectedMap.put("four", null);
        smartValue.setValue(jsonObject);
        // Convert to Map
        Map<String, Object> resultMap = smartValue.toMap();

        Assert.assertNotNull(resultMap, "The resulting map should not be null.");
        Assert.assertEquals(resultMap.size(), 4, "The map size should be 4.");
        Assert.assertEquals(resultMap, expectedMap);
    }

    @Test
    public void testToMapWithValidXmlNodeWithDifferentTypes() {
        SmartValue smartValue = new SmartValue();
        Map<String, Object> expectedMap = new HashMap<>();
        // Convert all float points to BigDecimal to perform maps assertion
        expectedMap.put("one", new BigDecimal("123.456"));
        expectedMap.put("two", 2);
        expectedMap.put("three", true);
        expectedMap.put("four", "sting");
        // XML node cannot have null values, so we map null values will be omitted
        Node xmlNode = ConvertUtils.mapToXmlNode(expectedMap);
        smartValue.setValue(xmlNode);
        // Convert to Map
        Map<String, Object> resultMap = smartValue.toMap();

        Assert.assertNotNull(resultMap, "The resulting map should not be null.");
        Assert.assertEquals(resultMap.size(), 4, "The map size should be 4.");
        Assert.assertEquals(resultMap, expectedMap);
    }

    @Test
    public void testToMapWithValidXmlStringWithDifferentTypes() {
        SmartValue smartValue = new SmartValue();
        Map<String, Object> expectedMap = new HashMap<>();
        // Convert all float points to BigDecimal to perform maps assertion
        expectedMap.put("one", new BigDecimal("123.456"));
        expectedMap.put("two", 2);
        expectedMap.put("three", true);
        expectedMap.put("four", "sting");
        // XML node cannot have null values, so we map null values will be omitted
        String xmlString = """
                <expectedMap>
                    <two>2</two>
                    <three>true</three>
                    <four>sting</four>
                    <one>123.456</one>
                </expectedMap>
                """
                .stripIndent();
        smartValue.setValue(xmlString);
        // Convert to Map
        Map<String, Object> resultMap = smartValue.toMap();

        Assert.assertNotNull(resultMap, "The resulting map should not be null.");
        Assert.assertEquals(resultMap.size(), 4, "The map size should be 4.");
        Assert.assertEquals(resultMap, expectedMap);
    }

    @Test
    public void testToJsonObjectWithValidJsonString() {
        SmartValue smartValue = new SmartValue();
        String jsonString = "{\"key\":\"value\", \"number\":10}";
        // Set the value as a valid JSON string
        smartValue.setValue(jsonString);
        // Convert to JSON object
        JSONObject resultJsonObject = smartValue.toJsonObject();

        // Assertions
        Assert.assertNotNull(resultJsonObject, "The resulting JSON object should not be null.");
        Assert.assertEquals(resultJsonObject.getString("key"), "value", "The value for 'key' should be 'value'.");
        Assert.assertEquals(resultJsonObject.getInt("number"), 10, "The value for 'number' should be 10.");
    }

    @Test
    public void testToJsonObjectWithValidMap() {
        SmartValue smartValue = new SmartValue();
        Map<String, Object> inputMap = Map.of("name", "Alice", "age", 25);
        // Set the value as a valid Map
        smartValue.setValue(inputMap);
        // Convert to JSON object
        JSONObject resultJsonObject = smartValue.toJsonObject();

        // Assertions
        Assert.assertNotNull(resultJsonObject, "The resulting JSON object should not be null.");
        Assert.assertEquals(resultJsonObject.getString("name"), "Alice", "The value for 'name' should be 'Alice'.");
        Assert.assertEquals(resultJsonObject.getInt("age"), 25, "The value for 'age' should be 25.");
    }

    @Test
    public void testToJsonObjectWithValidJSONObject() {
        SmartValue smartValue = new SmartValue();
        JSONObject inputJsonObject = new JSONObject();
        inputJsonObject.put("product", "Laptop");
        inputJsonObject.put("price", 1500);
        // Set the value as a valid JSONObject
        smartValue.setValue(inputJsonObject);
        // Convert to JSON object
        JSONObject resultJsonObject = smartValue.toJsonObject();

        // Assertions
        Assert.assertNotNull(resultJsonObject, "The resulting JSON object should not be null.");
        Assert.assertEquals(resultJsonObject.getString("product"), "Laptop", "The value for 'product' should be 'Laptop'.");
        Assert.assertEquals(resultJsonObject.getInt("price"), 1500, "The value for 'price' should be 1500.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testToJsonObjectWithNullValue() {
        SmartValue smartValue = new SmartValue();
        // Set the value to null
        smartValue.setValue(null);

        // Expecting SmartRuntimeException due to null value
        smartValue.toJsonObject();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToJsonObjectWithInvalidJsonString() {
        SmartValue smartValue = new SmartValue();
        String invalidJsonString = "{invalidJson}";
        // Set the value as an invalid JSON string
        smartValue.setValue(invalidJsonString);

        // Expecting SmartRuntimeException due to invalid JSON string format
        smartValue.toJsonObject();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToJsonObjectWithNonJsonValue() {
        SmartValue smartValue = new SmartValue();
        Integer invalidValue = 42;
        // Set the value as a non-JSON compatible value
        smartValue.setValue(invalidValue);

        // Expecting SmartRuntimeException due to invalid value type
        smartValue.toJsonObject();
    }

    @Test
    public void testGetValueWithStringValue() {
        SmartValue smartValue = new SmartValue();
        String expectedValue = "Hello, World!";
        // Set the value as a string
        smartValue.setValue(expectedValue);
        // Retrieve the value
        Object resultValue = smartValue.getValue();

        // Assertions
        Assert.assertNotNull(resultValue, "The resulting value should not be null.");
        Assert.assertTrue(resultValue instanceof String, "The resulting value should be of type String.");
        Assert.assertEquals(resultValue, expectedValue, "The value should match the expected string.");
    }

    @Test
    public void testGetValueWithIntegerValue() {
        SmartValue smartValue = new SmartValue();
        Integer expectedValue = 123;
        // Set the value as an integer
        smartValue.setValue(expectedValue);
        // Retrieve the value
        Object resultValue = smartValue.getValue();

        // Assertions
        Assert.assertNotNull(resultValue, "The resulting value should not be null.");
        Assert.assertTrue(resultValue instanceof Integer, "The resulting value should be of type Integer.");
        Assert.assertEquals(resultValue, expectedValue, "The value should match the expected integer.");
    }

    @Test
    public void testGetValueWithListValue() {
        SmartValue smartValue = new SmartValue();
        List<String> expectedValue = List.of("one", "two", "three");
        // Set the value as a list
        smartValue.setValue(expectedValue);
        // Retrieve the value
        Object resultValue = smartValue.getValue();

        // Assertions
        Assert.assertNotNull(resultValue, "The resulting value should not be null.");
        Assert.assertTrue(resultValue instanceof List, "The resulting value should be of type List.");
        Assert.assertEquals(resultValue, expectedValue, "The value should match the expected list.");
    }

    @Test
    public void testGetValueWithMapValue() {
        SmartValue smartValue = new SmartValue();
        Map<String, Integer> expectedValue = Map.of("one", 1, "two", 2);
        // Set the value as a map
        smartValue.setValue(expectedValue);
        // Retrieve the value
        Object resultValue = smartValue.getValue();

        // Assertions
        Assert.assertNotNull(resultValue, "The resulting value should not be null.");
        Assert.assertTrue(resultValue instanceof Map, "The resulting value should be of type Map.");
        Assert.assertEquals(resultValue, expectedValue, "The value should match the expected map.");
    }

    @Test
    public void testGetValueWithoutSetUp() {
        SmartValue smartValue = new SmartValue();

        Assert.assertNull(smartValue.getValue());
    }

    @Test
    public void testGetSmartTypeWithStringValue() {
        SmartValue smartValue = new SmartValue();
        SmartType expectedSmartType = SmartType.fromClass(String.class);
        // Set the value as a string and set up the expected SmartType
        smartValue.setValue("Test String");
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType, "The smart type should match the expected type for String.");
    }

    @Test
    public void testGetSmartTypeWithIntegerValue() {
        SmartValue smartValue = new SmartValue();
        SmartType expectedSmartType = SmartType.fromClass(Integer.class);
        // Set the value as an integer and set up the expected SmartType
        smartValue.setValue(123);
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType, "The smart type should match the expected type for Integer.");
    }

    @Test
    public void testGetSmartTypeWithListValue() {
        SmartValue smartValue = new SmartValue();
        // Set the value as a list and set up the expected SmartType
        List<String> list = List.of("one", "two", "three");
        SmartType valueSmartType = SmartType.fromClass(String.class);
        SmartType expectedSmartType = SmartType.fromCollectionClass(list.getClass(), valueSmartType);
        smartValue.setValue(list);
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType, "The smart type should match the expected type for List.");
    }

    @Test
    public void testGetSmartTypeWithListValueWithDifferentValueTypes() {
        SmartValue smartValue = new SmartValue();
        // Set the value as a list and set up the expected SmartType
        List<Number> list = List.of(1, 2.0f, 3.0, new BigDecimal("4.0"));
        SmartType valueSmartType = SmartType.fromClass(Number.class);
        SmartType expectedSmartType = SmartType.fromCollectionClass(list.getClass(), valueSmartType);
        smartValue.setValue(list);
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType, "The smart type should match the expected type for List.");
    }

    @Test
    public void testGetSmartTypeWithMapValue() {
        SmartValue smartValue = new SmartValue();
        // Set the value as a map and set up the expected SmartType
        Map<String,Integer> map = Map.of("one", 1, "two", 2);
        smartValue.setValue(map);
        SmartType valueType = SmartType.fromClass(Integer.class);
        SmartType expectedSmartType = SmartType.fromMapClass(map.getClass(), String.class, valueType);
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType,
                "The smart type should match the expected type for Map.");
    }

    @Test
    public void testGetSmartTypeWithMapValueWithDifferentValueTypes() {
        SmartValue smartValue = new SmartValue();
        // Set the value as a map and set up the expected SmartType
        Map<String,Number> map = Map.of("one", 1, "two", 2.2);
        smartValue.setValue(map);
        SmartType valueType = SmartType.fromClass(Number.class);
        SmartType expectedSmartType = SmartType.fromMapClass(map.getClass(), String.class, valueType);
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType,
                "The smart type should match the expected type for Map.");
    }

    @Test
    public void testGetSmartTypeWithMapValueWithDifferentKeyTypes() {
        SmartValue smartValue = new SmartValue();
        // Set the value as a map and set up the expected SmartType
        Map<Number, String> map = Map.of(1, "one", 2.0f, "two", 3.0, "three");
        smartValue.setValue(map);
        SmartType valueType = SmartType.fromClass(String.class);
        SmartType expectedSmartType = SmartType.fromMapClass(map.getClass(), Number.class, valueType);
        // Retrieve the smart type
        SmartType resultSmartType = smartValue.getSmartType();

        // Assertions
        Assert.assertNotNull(resultSmartType, "The resulting smart type should not be null.");
        Assert.assertEquals(resultSmartType, expectedSmartType,
                "The smart type should match the expected type for Map.");
    }

    @Test
    public void testGetSmartTypeWithNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(null);
        SmartType expectedSmartType = SmartType.fromClass(Null.class);
        // Expecting SmartRuntimeException due to null value setup
        SmartType resultSmartType =  smartValue.getSmartType();

        Assert.assertNotNull(resultSmartType);
        Assert.assertEquals(resultSmartType, expectedSmartType);
    }

    @Test
    public void testGetSmartTypeWithJsonObjectNullValue() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(JSONObject.NULL);
        SmartType expectedSmartType = SmartType.fromClass(Null.class);
        // Expecting SmartRuntimeException due to null value setup
        SmartType resultSmartType =  smartValue.getSmartType();

        Assert.assertNotNull(resultSmartType);
        Assert.assertEquals(resultSmartType, expectedSmartType);
    }

    @Test
    public void testGetSmartTypeWithJsonObjectNullClass() {
        SmartValue smartValue = new SmartValue();
        smartValue.setValue(JSONObject.NULL);
        SmartType expectedSmartType = SmartType.fromClass(JSONObject.NULL.getClass());
        // Expecting SmartRuntimeException due to null value setup
        SmartType resultSmartType =  smartValue.getSmartType();

        Assert.assertNotNull(resultSmartType);
        Assert.assertEquals(resultSmartType, expectedSmartType);
    }
    @Test
    public void testGetSmartTypeWithoutSetUp() {
        SmartValue smartValue = new SmartValue();
        SmartType expected = SmartType.fromClass(Null.class);

        Assert.assertEquals(smartValue.getSmartType(), expected);
    }

    @Test
    public void testToJsonObjectWithValidPojo() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartValue smartValue = new SmartValue(pojo);
        JSONObject expected = ConvertUtils.pojoObjectToJsonObject(pojo);
        JSONObject result = smartValue.toJsonObject();

        Assert.assertNotNull(result);
        SmartAssert.assertJsonObject(result, expected, true);
    }

    @Test
    public void testToJsonObjectWithValidJsonObject() {
        JSONObject input = new JSONObject();
        input.put("key", "value");
        SmartValue smartValue = new SmartValue(input);
        JSONObject result = smartValue.toJsonObject();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("key"), "value");
    }

    @Test
    public void tesToJsonObjectWithValidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 100);
        SmartValue smartValue = new SmartValue(map);
        JSONObject result = smartValue.toJsonObject();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("key1"), "value1");
        Assert.assertEquals(result.getInt("key2"), 100);
    }

    @Test
    public void testToJsonObjectWithJsonString() {
        String jsonString = "{\"key\":\"value\"}";
        SmartValue smartValue = new SmartValue(jsonString);
        JSONObject result = smartValue.toJsonObject();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString("key"), "value");
    }

    @Test
    public void testToJsonObjectWithXmlString() {
        String xmlString = "<root><name>John</name><age>32</age></root>";
        SmartValue smartValue = new SmartValue(xmlString);
        JSONObject expected = ConvertUtils.stringToJsonObject(xmlString);
        JSONObject result = smartValue.toJsonObject();

        Assert.assertNotNull(result);
        SmartAssert.assertJsonObject(result, expected, true);
    }

    @Test
    public void testToJsonObjectWithUnsupportedTypeToEmptyJsonObject() {
        SmartValue smartValue = new SmartValue(new Object());
        JSONObject result = smartValue.toJsonObject();

        Assert.assertTrue(result.isEmpty());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToJsonObjectWithInvalidString() {
        SmartValue smartValue = new SmartValue("invalid json string");
        smartValue.toJsonObject();
    }

    @Test
    public void testToJsonArrayWithValidJsonArray() {
        JSONArray input = new JSONArray();
        input.put("value1");
        input.put(123);
        SmartValue smartValue = new SmartValue(input);
        JSONArray result = smartValue.toJsonArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getInt(1), 123);
    }

    @Test
    public void testToJsonArrayWithValidList() {
        List<Object> list = Arrays.asList("value1", 123, true);
        SmartValue smartValue = new SmartValue(list);
        JSONArray result = smartValue.toJsonArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getInt(1), 123);
        Assert.assertTrue(result.getBoolean(2));
    }

    @Test
    public void testToJsonArrayWithValidArray() {
        String[] array = {"value1", "value2"};
        SmartValue smartValue = new SmartValue(array);
        JSONArray result = smartValue.toJsonArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getString(1), "value2");
    }

    @Test
    public void testToJsonArrayWithValidJsonString() {
        String jsonString = "[\"value1\", 123]";
        SmartValue smartValue = new SmartValue(jsonString);
        JSONArray result = smartValue.toJsonArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getInt(1), 123);
    }

    @Test
    public void testToJsonArrayWithValidEmptyArray() {
        SmartValue smartValue = new SmartValue(new Object[0]);
        JSONArray result = smartValue.toJsonArray();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testToJsonArrayWithValidCsvString() {
        String csvString = """
                header1,header2,header3
                value1,123,true
                """.stripIndent();
        SmartValue smartValue = new SmartValue(csvString);
        JSONArray result = smartValue.toJsonArray();

        Assert.assertNotNull(result);
        JSONArray firstRow = result.getJSONArray(0);
        JSONArray secondRow = result.getJSONArray(1);

        Assert.assertEquals(firstRow.getString(0), "header1");
        Assert.assertEquals(firstRow.getString(1), "header2");
        Assert.assertEquals(firstRow.getString(2), "header3");
        Assert.assertEquals(secondRow.getString(0), "value1");
        Assert.assertEquals(secondRow.getInt(1), 123);
        Assert.assertTrue(secondRow.getBoolean(2));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToJsonArrayWithInvalidType() {
        SmartValue smartValue = new SmartValue(new Object());
        smartValue.toJsonArray();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToJsonArrayWithInvalidString() {
        SmartValue smartValue = new SmartValue("invalid json array string");
        smartValue.toJsonArray();
    }

    @Test
    public void testToXmlNodeWithValidXmlString() {
        String xmlString = "<root><element>value</element></root>";
        SmartValue smartValue = new SmartValue(xmlString);
        Node result = smartValue.toXmlNode();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getNodeName(), "root");
        Assert.assertEquals(result.getFirstChild().getNodeName(), "element");
        Assert.assertEquals(result.getFirstChild().getTextContent(), "value");
    }

    @Test
    public void testToXmlNodeWithValidJsonString() {
        String jsonString = "{\"element\":\"value\"}";
        SmartValue smartValue = new SmartValue(jsonString);
        Node result = smartValue.toXmlNode();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getNodeName(), "item");
        Assert.assertEquals(result.getFirstChild().getNodeName(), "element");
        Assert.assertEquals(result.getFirstChild().getTextContent(), "value");
    }

    @Test
    public void testToXmlNodeWithValidDocument() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        Document document = ConvertUtils.pojoObjectToXmlDocument(pojo);
        SmartValue smartValue = new SmartValue(document);
        Node result = smartValue.toXmlNode();

        Assert.assertNotNull(result);
        SmartAssert.assertXmlNode(result, document.getDocumentElement(), true);
    }

    @Test
    public void testToXmlNodeWithValidPojo() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartValue smartValue = new SmartValue(pojo);
        Node expected = ConvertUtils.pojoObjectToXmlNode(pojo);
        Node result = smartValue.toXmlNode();

        Assert.assertNotNull(result);
        SmartAssert.assertXmlNode(result.getFirstChild(), expected.getFirstChild(), true);
    }

    @Test
    public void testToXmlNodeWithValidElement() {
        Document document;
        try {
            document = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Element element = document.createElement("item");
        element.setTextContent("data");
        SmartValue smartValue = new SmartValue(element);
        Node result = smartValue.toXmlNode();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getNodeName(), "item");
        Assert.assertEquals(result.getTextContent(), "data");
    }

    @Test
    public void testToXmlNodeWithValidEmptyDocument() {
        Document document;
        try {
            document = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        SmartValue smartValue = new SmartValue(document);
        Node result = smartValue.toXmlNode();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getNodeType(), Node.DOCUMENT_NODE);
        Assert.assertNull(result.getFirstChild());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToXmlNodeWithInvalidType() {
        SmartValue smartValue = new SmartValue(123.456);
        smartValue.toXmlNode();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToXmlNodeWithInvalidString() {
        SmartValue smartValue = new SmartValue("invalid XML string");
        smartValue.toXmlNode();
    }

    @Test
    public void testToEnumValueWithValidEnumString() {
        SmartValue smartValue = new SmartValue("SUNDAY");
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        DayOfWeek result = smartValue.toEnumValue(enumType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, DayOfWeek.SUNDAY);
    }

    @Test
    public void testToEnumValueWithValidEnumOrdinal() {
        SmartValue smartValue = new SmartValue(0); // Ordinal value for MONDAY
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        DayOfWeek result = smartValue.toEnumValue(enumType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, DayOfWeek.MONDAY);
    }

    @Test
    public void testToEnumValueWithValidEnumObject() {
        SmartValue smartValue = new SmartValue(DayOfWeek.FRIDAY);
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        DayOfWeek result = smartValue.toEnumValue(enumType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, DayOfWeek.FRIDAY);
    }

    @Test
    public void testToEnumValueWithValidCaseInsensitiveString() {
        SmartValue smartValue = new SmartValue("tuesday");
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        DayOfWeek result = smartValue.toEnumValue(enumType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, DayOfWeek.TUESDAY);
    }

    @Test
    public void testToEnumValueWithValidEnumWithSpaces() {
        SmartValue smartValue = new SmartValue("   WEDNESDAY   ");
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        smartValue.toEnumValue(enumType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToEnumValueWithInvalidEnumString() {
        SmartValue smartValue = new SmartValue("INVALID_ENUM");
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        smartValue.toEnumValue(enumType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToEnumValueWithInvalidOrdinalValue() {
        SmartValue smartValue = new SmartValue(100); // Out of range ordinal value
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        smartValue.toEnumValue(enumType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToEnumValueWithUnsupportedType() {
        SmartValue smartValue = new SmartValue(new Object());
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        smartValue.toEnumValue(enumType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToEnumValueWithNullValue() {
        SmartValue smartValue = new SmartValue(null);
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        smartValue.toEnumValue(enumType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToEnumValueWithInvalidEnumType() {
        SmartValue smartValue = new SmartValue("MONDAY");
        SmartType invalidEnumType = SmartType.fromClass(Object.class);
        smartValue.toEnumValue(invalidEnumType);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testToEnumValueWithNullEnumType() {
        SmartValue smartValue = new SmartValue("MONDAY");
        smartValue.toEnumValue(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToEnumValueWithNegativeIndex() {
        SmartValue smartValue = new SmartValue(-1);
        SmartType enumType = SmartType.fromClass(DayOfWeek.class);
        smartValue.toEnumValue(enumType);
    }

    @Test
    public void testToPojoObjectWithValidPojo() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartValue smartValue = new SmartValue(pojo);
        SmartType pojoType = SmartType.fromObject(pojo);
        PojoClass result = smartValue.toPojoObject(pojoType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, pojo);
    }

    @Test
    public void testToPojoObjectWithValidJsonString() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        String jsonString = ConvertUtils.pojoObjectToString(pojo);
        SmartValue smartValue = new SmartValue(jsonString);
        SmartType pojoType = SmartType.fromObject(pojo);
        PojoClass result = smartValue.toPojoObject(pojoType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, pojo);
    }

    @Test
    public void testToPojoObjectWithValidMap() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        Map<String, Object> map = ConvertUtils.pojoObjectToMap(pojo);
        SmartValue smartValue = new SmartValue(map);
        SmartType pojoType = SmartType.fromObject(pojo);
        PojoClass result = smartValue.toPojoObject(pojoType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, pojo);
    }

    @Test
    public void testSmartValueToPojoObjectWithValidJsonString() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        JSONObject jsonObject = ConvertUtils.pojoObjectToJsonObject(pojo);
        String jsonString = ConvertUtils.jsonObjectToString(jsonObject);
        SmartValue smartValue = new SmartValue(jsonString);
        SmartType pojoType = SmartType.fromObject(pojo);
        PojoClass result = smartValue.toPojoObject(pojoType);
        Assert.assertNotNull(result);
        Assert.assertEquals(result, pojo);
    }

    @Test
    public void testToPojoObjectWithValidXmlString() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        Node xmlNode = ConvertUtils.pojoObjectToXmlNode(pojo);
        String xmlString = ConvertUtils.xmlNodeToString(xmlNode);
        SmartValue smartValue = new SmartValue(xmlString);
        SmartType pojoType = SmartType.fromObject(pojo);
        PojoClass result = smartValue.toPojoObject(pojoType);
        Assert.assertNotNull(result);
        Assert.assertEquals(result, pojo);
    }

    @Test
    public void testToPojoObjectWithValidJsonObject() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        JSONObject jsonObject = ConvertUtils.pojoObjectToJsonObject(pojo);
        SmartValue smartValue = new SmartValue(jsonObject);
        SmartType pojoType = SmartType.fromObject(pojo);
        PojoClass result = smartValue.toPojoObject(pojoType);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, pojo);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToPojoObjectWithInvalidString() {
        SmartValue smartValue = new SmartValue("invalid string");
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartType pojoType = SmartType.fromObject(pojo);
        smartValue.toPojoObject(pojoType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToPojoObjectWithNullValue() {
        SmartValue smartValue = new SmartValue(null);
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartType pojoType = SmartType.fromObject(pojo);
        smartValue.toPojoObject(pojoType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToPojoObjectWithMismatchedTargetType() {
        String jsonString = "{\"name\":\"test\",\"value\":456}";
        SmartValue smartValue = new SmartValue(jsonString);
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartType pojoType = SmartType.fromObject(pojo);
        smartValue.toPojoObject(pojoType);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToPojoObjectWithEmptyMap() {
        SmartValue smartValue = new SmartValue(new HashMap<>());
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        SmartType pojoType = SmartType.fromObject(pojo);
        smartValue.toPojoObject(pojoType);
    }

    @Test
    public void testToDateStringWithValidDate() {
        Date date = new Date();
        SmartValue smartValue = new SmartValue(date);
        String result = smartValue.toDateString("yyyy-MM-dd");

        Assert.assertNotNull(result);
        Assert.assertEquals(result, ConvertUtils.dateToString(date, "yyyy-MM-dd"));
    }

    @Test
    public void testToDateStringWithValidLocalDate() {
        LocalDate localDate = LocalDate.of(2024, 9, 20);
        SmartValue smartValue = new SmartValue(localDate);
        String result = smartValue.toDateString("yyyy-MM-dd");

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "2024-09-20");
    }

    @Test
    public void testToDateStringWithValidLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 9, 20, 10, 30, 0);
        SmartValue smartValue = new SmartValue(localDateTime);
        String result = smartValue.toDateString("yyyy-MM-dd HH:mm:ss");

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "2024-09-20 10:30:00");
    }

    @Test
    public void testToDateStringWithValidLocalTime() {
        LocalTime localTime = LocalTime.of(15, 45, 30);
        SmartValue smartValue = new SmartValue(localTime);
        String result = smartValue.toDateString("HH:mm:ss");

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "15:45:30");
    }

    @Test
    public void testToDateStringWithValidDateString() {
        String dateString = "2024-09-20";
        SmartValue smartValue = new SmartValue(dateString);
        String result = smartValue.toDateString("yyyy-MM-dd");

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "2024-09-20");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateStringWithInvalidObject() {
        SmartValue smartValue = new SmartValue(new Object());
        smartValue.toDateString("yyyy-MM-dd");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateStringWithNullValue() {
        SmartValue smartValue = new SmartValue(null);
        smartValue.toDateString("yyyy-MM-dd");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateStringWithInvalidDateFormat() {
        LocalDate localDate = LocalDate.of(2024, 9, 20);
        SmartValue smartValue = new SmartValue(localDate);
        smartValue.toDateString("invalid-format");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToDateStringWithInvalidString() {
        String invalidDateString = "invalid date";
        SmartValue smartValue = new SmartValue(invalidDateString);
        smartValue.toDateString("yyyy-MM-dd");
    }

    @Test
    public void testToArrayWithValidStringArray() {
        String[] input = {"one", "two", "three"};
        SmartValue smartValue = new SmartValue(input);
        String[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], "one");
        Assert.assertEquals(result[1], "two");
        Assert.assertEquals(result[2], "three");
    }

    @Test
    public void testToArrayWithValidIntegerArray() {
        Integer[] input = {1, 2, 3, 4};
        SmartValue smartValue = new SmartValue(input);
        Integer[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 4);
        Assert.assertEquals(result[0], Integer.valueOf(1));
        Assert.assertEquals(result[1], Integer.valueOf(2));
        Assert.assertEquals(result[2], Integer.valueOf(3));
        Assert.assertEquals(result[3], Integer.valueOf(4));
    }

    @Test
    public void testToArrayWithValidArrayOfDifferentTypes() {
        Object[] input = {1, "2", 3.0, true, null};
        SmartValue smartValue = new SmartValue(input);
        Object[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 5);
        Assert.assertEquals(result[0], 1);
        Assert.assertEquals(result[1], "2");
        Assert.assertEquals(result[2], 3.0);
        Assert.assertEquals(result[3], true);
        Assert.assertNull(result[4]);
    }

    @Test
    public void testToArrayWithValidList() {
        List<String> input = Arrays.asList("apple", "banana", "cherry");
        SmartValue smartValue = new SmartValue(input);
        String[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], "apple");
        Assert.assertEquals(result[1], "banana");
        Assert.assertEquals(result[2], "cherry");
    }

    @Test
    public void testToArrayWithEmptyArray() {
        Integer[] input = {};
        SmartValue smartValue = new SmartValue(input);
        Integer[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 0);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToArrayWithInvalidType() {
        // Invalid object type for array conversion
        SmartValue smartValue = new SmartValue(new Object());
        smartValue.toArray();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToArrayWithNullValue() {
        // Null value should throw exception
        SmartValue smartValue = new SmartValue(null);
        smartValue.toArray();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToArrayWithUnsupportedValue() {
        SmartValue smartValue = new SmartValue("This is a string, not an array");
        smartValue.toArray();
    }

    @Test
    public void testToArrayWithListOfIntegers() {
        List<Integer> input = Arrays.asList(1, 2, 3);
        SmartValue smartValue = new SmartValue(input);
        Integer[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], Integer.valueOf(1));
        Assert.assertEquals(result[1], Integer.valueOf(2));
        Assert.assertEquals(result[2], Integer.valueOf(3));
    }

    @Test
    public void testToArrayWithJsonArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(1);
        jsonArray.put(2);
        jsonArray.put(3);
        SmartValue smartValue = new SmartValue(jsonArray);
        Integer[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], Integer.valueOf(1));
        Assert.assertEquals(result[1], Integer.valueOf(2));
        Assert.assertEquals(result[2], Integer.valueOf(3));
    }

    @Test
    public void testToArrayWithJsonArrayString() {
        String jsonArrayString = "[1, 2, 3]";
        SmartValue smartValue = new SmartValue(jsonArrayString);
        Integer[] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], Integer.valueOf(1));
        Assert.assertEquals(result[1], Integer.valueOf(2));
        Assert.assertEquals(result[2], Integer.valueOf(3));
    }

    @Test
    public void testToArrayWithJsonCsvString() {
        String csvString = """
                11,12,13
                21,22,23
                31,32,33
                """.stripIndent();
        SmartValue smartValue = new SmartValue(csvString);
        Integer[][] result = smartValue.toArray();

        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0][0], Integer.valueOf(11));
        Assert.assertEquals(result[1][1], Integer.valueOf(22));
        Assert.assertEquals(result[2][2], Integer.valueOf(33));
    }

    @Test
    public void testToColorWithValidColorValue() {
        SmartValue smartValue = new SmartValue(new Color(255, 0, 0)); // Red color
        Color result = smartValue.toColor();
        Assert.assertEquals(result, new Color(255, 0, 0));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToColorWithInvalidValue() {
        SmartValue smartValue = new SmartValue("invalidColor");
        smartValue.toColor();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToColorWithNullValue() {
        SmartValue smartValue = new SmartValue(null);
        smartValue.toColor();
    }
}
