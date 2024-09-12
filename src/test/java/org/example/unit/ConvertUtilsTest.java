package org.example.unit;

import org.example.data.*;
import org.example.enums.Platform;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.unit.supplemental.NestedPojoClass;
import org.example.unit.supplemental.PojoClass;
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
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.constants.Settings.*;

public class ConvertUtilsTest {

    @Test
    public void testEscapeJavaScriptExcludeDoubleQuote() {
        String javaScript = "var x = \"John\\'s book\"; // Example code";
        String expected = "var x \\u003D \\\"John\\\\\\'s book\\\"; // Example code";
        String actual = ConvertUtils.escapeJavaScript(javaScript);
        Assert.assertEquals(actual, expected, "JavaScript was not properly escaped " +
                "when excluding double quotes.");
    }

    @Test
    public void testEscapeJavaScriptPositiveSimpleString() {
        String input = "This is a 'test' with \"double quotes\" and special chars < > &";
        String result = ConvertUtils.escapeJavaScript(input);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("\\'"));
        Assert.assertTrue(result.contains("\\\""));
        Assert.assertTrue(result.contains("\\u003C")); // for <
        Assert.assertTrue(result.contains("\\u003E")); // for >
        Assert.assertTrue(result.contains("\\u0026")); // for &
    }

    @Test
    public void testEscapeJavaScriptPositiveSpecialCharacters() {
        String input = "Line1\nLine2\tBackspace\bFormFeed\f";
        String result = ConvertUtils.escapeJavaScript(input);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains("\\n"));  // Newline
        Assert.assertTrue(result.contains("\\t"));  // Tab
        Assert.assertTrue(result.contains("\\b"));  // Backspace
        Assert.assertTrue(result.contains("\\f"));  // Form feed
    }

    @Test
    public void testEscapeJavaScriptPositiveEmptyString() {
        String input = "";
        String result = ConvertUtils.escapeJavaScript(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeJavaScriptNegativeNullString() {
        ConvertUtils.escapeJavaScript(null);
    }

    @Test
    public void testStringToBooleanWithValidInputTrue() {
        Assert.assertTrue(ConvertUtils.stringToBoolean("true"));
    }

    @Test
    public void testStringToBooleanWithValidInputFalse() {
        Assert.assertFalse(ConvertUtils.stringToBoolean("false"));
    }

    @Test(expectedExceptions = SmartRuntimeException.class,
            expectedExceptionsMessageRegExp = "Invalid boolean format: .*")
    public void testStringToBooleanWithInvalidInput() {
        ConvertUtils.stringToBoolean("maybe");
    }

    @Test
    public void testConvertSimpleDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        String dateString = "2020-12-31";
        Date expectedDate = sdf.parse(dateString);
        Date actualDate = ConvertUtils.stringToSmartDate(dateString);
        Assert.assertEquals(actualDate, expectedDate);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testBlankDate() {
        String dateString = "";
        ConvertUtils.stringToSmartDate(dateString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullDate() {
        ConvertUtils.stringToSmartDate(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testRandomStringAsDate() {
        ConvertUtils.stringToSmartDate("not a date");
    }

    @Test
    public void testValidJsonArray() {
        String validJsonArray = "[{\"name\":\"John\"}, {\"name\":\"Doe\"}]";
        JSONArray result = ConvertUtils.stringToJasonArray(validJsonArray);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.length(), 2, "There should be two elements.");
        Assert.assertEquals(result.getJSONObject(0).getString("name"), "John", "The first name should be John.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidJsonArray() {
        String invalidJsonArray = "[{name:\"John'}, {name:\"Doe\"}]";
        ConvertUtils.stringToJasonArray(invalidJsonArray);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testBlankJsonArray() {
        String invalidJsonArray = " ";
        ConvertUtils.stringToJasonArray(invalidJsonArray);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullJsonArray() {
        ConvertUtils.stringToJasonArray(null);
    }

    @Test
    public void testValidJsonObject() {
        String validJson = "{\"name\":\"John\", \"age\":30}";
        JSONObject result = ConvertUtils.stringToJasonObject(validJson);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getString("name"), "John", "The name should be John.");
        Assert.assertEquals(result.getInt("age"), 30, "The age should be 30.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidJsonObject() {
        String invalidJson = "{name:\"John\" age:30}";
        ConvertUtils.stringToJasonObject(invalidJson);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testBlankJsonObject() {
        String invalidJson = "  ";
        ConvertUtils.stringToJasonObject(invalidJson);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullJsonObject() {
        ConvertUtils.stringToJasonObject(null);
    }

    @Test
    public void testValidXmlObjectFromXmlString() {
        String validXml = "<person><name>John</name></person>";
        Document result = ConvertUtils.stringToXmlDocument(validXml);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getElementsByTagName("name").item(0).getTextContent(),
                "John", "The name should be John.");
    }

    @Test
    public void testValidXmlObjectFromJsonString() {
        String validXml = "{\"name\":\"John Doe\", \"age\":32}";
        Document result = ConvertUtils.stringToXmlDocument(validXml);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getElementsByTagName("name").item(0).getTextContent(),
                "John Doe", "The name should be John Doe.");
        Assert.assertEquals(result.getElementsByTagName("age").item(0).getTextContent(),
                "32", "The age should be 32.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidXmlObject() {
        String invalidXml = "<person><name>John</name>";
        ConvertUtils.stringToXmlDocument(invalidXml);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testBlankXmlObject() {
        ConvertUtils.stringToXmlDocument(" ");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullXmlObject() {
        ConvertUtils.stringToXmlDocument(null);
    }

    @Test
    public void testDateToStringValid() {
        Date now = new Date();
        String expectedFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(expectedFormat);
        String expectedDateString = sdf.format(now);
        String result = ConvertUtils.dateToString(now, expectedFormat);

        Assert.assertEquals(result, expectedDateString, "The formatted date string does not match expected output.");
    }

    @Test(expectedExceptions = SmartValidationException.class, expectedExceptionsMessageRegExp = ".*date.*")
    public void testDateToStringNullDate() {
        ConvertUtils.dateToString(null, "yyyy-MM-dd");
    }

    @Test(expectedExceptions = SmartValidationException.class, expectedExceptionsMessageRegExp = ".*dateFormat.*")
    public void testDateToStringBlankDateFormat() {
        ConvertUtils.dateToString(new Date(), " ");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDateToStringInvalidFormat() {
        ConvertUtils.dateToString(new Date(), "invalid-format");
    }

    @Test
    public void testLocalDateToStringPositive() {
        LocalDate localDate = LocalDate.of(2024, 8, 8);
        String dateFormat = "dd/MM/yyyy";
        String expectedDateString = "08/08/2024";

        String actualDateString = ConvertUtils.localDateToString(localDate, dateFormat);
        Assert.assertEquals(actualDateString, expectedDateString, "The date string should match the expected format.");
    }

    @Test
    public void testLocalDateToStringPositiveDifferentFormat() {
        LocalDate localDate = LocalDate.of(2024, 8, 8);
        String dateFormat = "yyyy-MM-dd";
        String expectedDateString = "2024-08-08";

        String actualDateString = ConvertUtils.localDateToString(localDate, dateFormat);
        Assert.assertEquals(actualDateString, expectedDateString, "The date string should match the expected format.");
    }

    @Test
    public void testEscapeCSVFieldWithSimpleString() {
        String input = "simple";
        String expected = "\"simple\"";
        String actual = ConvertUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped.");
    }

    @Test
    public void testEscapeCSVFieldWithComma() {
        String input = "value,with,comma";
        String expected = "\"value,with,comma\"";
        String actual = ConvertUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped when it contained commas.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeCSVFieldNegative() {
        ConvertUtils.escapeCSVField(null);
    }

    @Test
    public void testEscapeCSVFieldWithDoubleQuotes() {
        String input = "value\"with\"quotes";
        String expected = "\"value\"\"with\"\"quotes\"";
        String actual = ConvertUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped when it contained double quotes.");
    }

    @Test
    public void testEscapeCSVFieldWithEmptyString() {
        String input = "";
        String expected = "\"\"";
        String actual = ConvertUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped for an empty string.");
    }

    @Test
    public void testEscapeCSVFieldWithSpecialCharacters() {
        String input = "value,with\nnew\rline\tand\tab\"quotes\"";
        String expected = "\"value,with\nnew\rline\tand\tab\"\"quotes\"\"\"";
        String actual = ConvertUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped when it contained special characters.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeCSVFieldWithNullInput() {
        ConvertUtils.escapeCSVField(null);
    }

    @Test
    public void testCsvFieldValueToStringWithValidInputNoEscapes() {
        String input = "Sample text";
        String expected = "Sample text";
        String result = ConvertUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testCsvFieldValueToStringWithEscapedQuotes() {
        String input = "\"\"\"Sample\"\" text\"\"\"";
        String expected = "\"Sample\" text\"";
        String result = ConvertUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testCsvFieldValueToStringWithLeadingAndTrailingQuotes() {
        String input = "\"Sample text\"";
        String expected = "Sample text";
        String result = ConvertUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testCsvFieldValueToStringWithEmptyString() {
        String input = "";
        String expected = "";
        String result = ConvertUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCsvFieldValueToStringNegative() {
        ConvertUtils.csvFieldValueToString(null);
    }

    @Test
    public void testStringToSmartLocalDateValidDateString() {
        String dateString = "2023-08-31"; // Assuming this format matches the expected pattern in SmartDate
        String expectedFormat = "yyyy-MM-dd"; // The format should be adjusted based on your SmartDate logic
        SmartLocalDate smartLocalDate = ConvertUtils.stringToSmartLocalDate(dateString);

        Assert.assertNotNull(smartLocalDate, "SmartLocalDate should not be null");
        Assert.assertEquals(smartLocalDate.getLocalDate(), LocalDate.of(2023, 8, 31), "LocalDate value should match the input date");
        Assert.assertEquals(smartLocalDate.getFormat(), expectedFormat, "Format should match the expected format");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToSmartLocalDateInvalidDateString() {
        String invalidDateString = "invalid-date";
        ConvertUtils.stringToSmartLocalDate(invalidDateString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToSmartLocalDateNullDateString() {
        ConvertUtils.stringToSmartLocalDate(null);
    }

    @Test
    public void testStringToSmartLocalDateTimeValidString() {
        String dateTimeString = "2024-08-31T14:45:00"; // Example valid datetime string
        String expectedFormat = "yyyy-MM-dd'T'HH:mm:ss"; // Example format that SmartDate might return
        SmartLocalDateTime result = ConvertUtils.stringToSmartLocalDateTime(dateTimeString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getLocalDateTime(), LocalDateTime.parse(dateTimeString),
                "The LocalDateTime should match the parsed value.");
        Assert.assertEquals(result.getFormat(), expectedFormat,
                "The format should match the expected format.");
    }

    @Test
    public void testStringToSmartLocalTimePositive() {
        String validTimeString = "10:30:45"; // Example time string in a valid format
        SmartLocalTime result = ConvertUtils.stringToSmartLocalTime(validTimeString);

        Assert.assertNotNull(result, "The result should not be null");
        Assert.assertEquals(result.getLocalTime(), LocalTime.of(10, 30, 45), "The LocalTime should match the expected value");
        Assert.assertEquals(result.getFormat(), "HH:mm:ss", "The format should match the expected value");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToSmartLocalTimeNegative() {
        String invalidTimeString = "invalid-time"; // Example of an invalid time string
        ConvertUtils.stringToSmartLocalTime(invalidTimeString);
    }

    @Test
    public void testStringToFileValidPath() {
        String validFilePath = "src/test/resources/testfile.txt";
        File expectedFile = new File(validFilePath);
        File result = ConvertUtils.stringToFile(validFilePath);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPath(), expectedFile.getPath());
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToFileInvalidPath() {
        String invalidFilePath = "/invalid\0path"; // Null character is illegal in file paths

        ConvertUtils.stringToFile(invalidFilePath);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToFileEmptyPath() {
        String emptyFilePath = "";

        ConvertUtils.stringToFile(emptyFilePath);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToFileNullPath() {
        String nullFilePath = null;

        ConvertUtils.stringToFile(nullFilePath);
    }

    @Test
    public void testStringToURLValidURL() {
        String validUrlString = "https://www.example.com";
        URL result = ConvertUtils.stringToURL(validUrlString);

        Assert.assertNotNull(result, "URL object should not be null for a valid URL string.");
        Assert.assertEquals(result.toString(), validUrlString, "The URL object should match the input string.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToURLInvalidURL() {
        String invalidUrlString = "htp://www[dot]example.com";

        ConvertUtils.stringToURL(invalidUrlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURLBlankURL() {
        String blankUrlString = "";

        ConvertUtils.stringToURL(blankUrlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURLNullURL() {
        String nullUrlString = null;

        ConvertUtils.stringToURL(nullUrlString);
    }

    @Test
    public void testStringToURIValidURI() {
        String validURIString = "https://example.com/resource";
        URI result = ConvertUtils.stringToURI(validURIString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.toString(), validURIString, "The URI object should match the URI string.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURIEmptyURI() {
        ConvertUtils.stringToURI("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToURIInvalidURI() {
        String invalidURIString = "htp://[invalid_uri]";
        ConvertUtils.stringToURI(invalidURIString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURINullURI() {
        String nullURIString = null;
        ConvertUtils.stringToURI(nullURIString);
    }

    @Test
    public void testStringToPathValidFilePathShouldReturnPath() {
        String validFilePath = "C:/Users/Example/Documents/file.txt";
        Path result = ConvertUtils.stringToPath(validFilePath);
        Path expected = Paths.get(validFilePath);

        Assert.assertNotNull(result, "Path object should not be null");
        Assert.assertEquals(result, expected, "The path should match the input file path");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToPathInvalidFilePathFormatShouldThrowException() {
        String invalidFilePath = "Invalid/Path\\file?.txt";

        ConvertUtils.stringToPath(invalidFilePath);
    }

    @Test
    public void testStringToXmlNodeValidXml() {
        String validXmlString = "<root><child>value</child></root>";
        Node result = ConvertUtils.stringToXmlNode(validXmlString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getNodeName(), "root", "The root node name should be 'root'.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToXmlNodeInvalidXml() {
        String invalidXmlString = "<root><child>value</child>";  // Missing closing tag

        ConvertUtils.stringToXmlNode(invalidXmlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToXmlNodeEmptyString() {
        String emptyString = "";

        ConvertUtils.stringToXmlNode(emptyString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToXmlNodeNullString() {
        String nullString = null;

        ConvertUtils.stringToXmlNode(nullString);
    }

    @Test
    public void testLocalDateToDatepositive() {
        LocalDate localDate = LocalDate.of(2023, 8, 31);
        Date result = ConvertUtils.localDateToDate(localDate);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getTime(),
                Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime(),
                "The converted date should match the expected date.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateToDateNegativeNullLocalDate() {
        LocalDate localDate = null;

        ConvertUtils.localDateToDate(localDate);
    }

    @Test
    public void testLocalDateTimeToDatePositive() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 8, 31, 14, 30, 0);
        Date expectedDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date actualDate = ConvertUtils.localDateTimeToDate(localDateTime);

        Assert.assertEquals(actualDate, expectedDate, "The converted date should match the expected date.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateTimeToDateNegativeNullLocalDateTime() {
        ConvertUtils.localDateTimeToDate(null);
    }

    @Test
    public void testLocalTimeToDateValidTime() {
        LocalTime localTime = LocalTime.of(14, 30, 59);
        Date result = ConvertUtils.localTimeToDate(localTime);
        LocalTime expectedLocalTime = LocalTime.parse("14:30:59");
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), expectedLocalTime);
        Date expected = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(expected, result, "The result date should equal to expected one.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalTimeToDateNullLocalTime() {
        LocalTime localTime = null;

        ConvertUtils.localTimeToDate(localTime);
    }

    @Test
    public void testLocalDateToStringValidInput() {
        LocalDate localDate = LocalDate.of(2023, 8, 31);
        String dateFormat = "yyyy-MM-dd";
        String result = ConvertUtils.localDateToString(localDate, dateFormat);

        Assert.assertEquals(result, "2023-08-31");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateToStringNullDate() {
        LocalDate localDate = null;
        String dateFormat = "yyyy-MM-dd";

        ConvertUtils.localDateToString(localDate, dateFormat);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLocalDateToStringInvalidDateFormat() {
        LocalDate localDate = LocalDate.of(2023, 8, 31);
        String dateFormat = "invalid-format";

        ConvertUtils.localDateToString(localDate, dateFormat);
    }

    @Test
    public void testLocalDateTimeToStringPositive() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 8, 31, 14, 45, 30);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String expectedDateString = "2024-08-31 14:45:30";
        String actualDateString = ConvertUtils.localDateTimeToString(localDateTime, dateFormat);

        Assert.assertEquals(actualDateString, expectedDateString, "The date string should match the expected value.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateTimeToStringNullLocalDateTime() {
        LocalDateTime localDateTime = null;
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        ConvertUtils.localDateTimeToString(localDateTime, dateFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateTimeToStringBlankDateFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 8, 31, 14, 45, 30);
        String dateFormat = "   ";

        ConvertUtils.localDateTimeToString(localDateTime, dateFormat);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLocalDateTimeToStringInvalidDateFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 8, 31, 14, 45, 30);
        String dateFormat = "invalid-format";

        ConvertUtils.localDateTimeToString(localDateTime, dateFormat);
    }

    @Test
    public void testLocalTimeToStringValidInput() {
        LocalTime localTime = LocalTime.of(14, 30, 15); // 2:30:15 PM
        String timeFormat = "HH:mm:ss";

        String result = ConvertUtils.localTimeToString(localTime, timeFormat);
        Assert.assertEquals(result, "14:30:15");
    }

    @Test
    public void testLocalTimeToStringWithDifferentFormat() {
        LocalTime localTime = LocalTime.of(9, 5); // 9:05 AM
        String timeFormat = "hh:mm a";

        String result = ConvertUtils.localTimeToString(localTime, timeFormat);
        Assert.assertEquals(result, "09:05 AM");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLocalTimeToStringWithInvalidFormat() {
        LocalTime localTime = LocalTime.of(14, 30); // 2:30 PM
        String timeFormat = "invalidFormat";

        ConvertUtils.localTimeToString(localTime, timeFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalTimeToStringWithNullLocalTime() {
        LocalTime localTime = null;
        String timeFormat = "HH:mm:ss";

        ConvertUtils.localTimeToString(localTime, timeFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalTimeToStringWithBlankTimeFormat() {
        LocalTime localTime = LocalTime.of(14, 30); // 2:30 PM
        String timeFormat = "";

        ConvertUtils.localTimeToString(localTime, timeFormat);
    }

    @Test
    public void testXmlNodeToStringPositive() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Create root element
            Element root = doc.createElement("root");
            doc.appendChild(root);

            // Create child element
            Element child = doc.createElement("child");
            child.setTextContent("sample content");
            root.appendChild(child);

            // Convert XML Node to String
            String xmlString = ConvertUtils.xmlNodeToString(doc);

            Assert.assertTrue(xmlString.contains("<root>") && xmlString.contains("<child>sample content</child>"),
                    "XML string should contain the correct root and child elements.");
        }
        catch (Exception e) {
            Assert.fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testXmlNodeToStringNegativeNullNode() {
        ConvertUtils.xmlNodeToString(null);
    }

    @Test
    public void testJsonObjectToStringValidJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", 123);
        String jsonString = ConvertUtils.jsonObjectToString(jsonObject);

        Assert.assertNotNull(jsonString, "The returned string should not be null.");
        Assert.assertTrue(jsonString.contains("\"key1\": \"value1\""), "The JSON string should contain the key-value pair.");
        Assert.assertTrue(jsonString.contains("\"key2\": 123"), "The JSON string should contain the key-value pair.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonObjectToStringNullJsonObject() {
        ConvertUtils.jsonObjectToString(null);
    }

    @Test
    public void testJsonArrayToStringPositive() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value1");
        jsonArray.put(2);
        jsonArray.put(true);
        String expected = """
                [
                    "value1",
                    2,
                    true
                ]""".stripIndent();
        String result = ConvertUtils.jsonArrayToString(jsonArray);

        Assert.assertNotNull(result, "Result should not be null");
        Assert.assertEquals(result, expected, "The JSON string should match the expected format with 4-space indentation");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonArrayToStringWithNullArray() {
        ConvertUtils.jsonArrayToString(null);
    }

    @Test
    public void testObjectToStringWithNull() {
        String result = ConvertUtils.objectToString(null);
        Assert.assertEquals(result, NULL_VALUE_STRING);
    }

    @Test
    public void testObjectToStringWithNaN() {
        String result = ConvertUtils.objectToString(Double.NaN);
        Assert.assertEquals(result, "NaN");
    }

    @Test
    public void testObjectToStringWithPositiveInfinity() {
        String result = ConvertUtils.objectToString(Double.POSITIVE_INFINITY);
        Assert.assertEquals(result, POSITIVE_INFINITY_VALUE_STRING);
    }

    @Test
    public void testObjectToStringWithNegativeInfinity() {
        String result = ConvertUtils.objectToString(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(result, NEGATIVE_INFINITY_VALUE_STRING);
    }

    @Test
    public void testObjectToStringWithSmartValue() {
        SmartValue smartValue = new SmartValue("SmartValueContent");
        String result = ConvertUtils.objectToString(smartValue);
        Assert.assertEquals(result, smartValue.toString());
    }

    @Test
    public void testObjectToStringWithStringBuffer() {
        StringBuffer stringBuffer = new StringBuffer("StringBufferContent");
        String result = ConvertUtils.objectToString(stringBuffer);
        Assert.assertEquals(result, stringBuffer.toString());
    }

    @Test
    public void testObjectToStringWithEnum() {
        TestEnum testEnum = TestEnum.VALUE1;
        String result = ConvertUtils.objectToString(testEnum);
        Assert.assertEquals(result, testEnum.toString());
    }

    @Test
    public void testObjectToStringWithJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "value");
        String result = ConvertUtils.objectToString(jsonObject);
        Assert.assertEquals(result, jsonObject.toString(4));  // Assuming jsonObjectToString uses 4-space indentation
    }

    @Test
    public void testObjectToStringWithJSONArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value1");
        jsonArray.put("value2");
        String result = ConvertUtils.objectToString(jsonArray);
        Assert.assertEquals(result, jsonArray.toString(4));  // Assuming jsonArrayToString uses 4-space indentation
    }

    @Test
    public void testObjectToStringWithXmlNode() {
        String validXmlString = "<root><child>value</child></root>";
        Node xmlNode = ConvertUtils.stringToXmlNode(validXmlString);
        String expected = """
                <?xml version="1.0" encoding="UTF-8"?>
                <root>
                    <child>value</child>
                </root>
                """.stripIndent();
        String result = ConvertUtils.objectToString(xmlNode);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void testObjectToStringWithArray() {
        String[] array = {"element1", "element2"};
        String expected = """
                [
                    "element1",
                    "element2"
                ]""".stripIndent();
        String result = ConvertUtils.objectToString(array);
        Assert.assertEquals(result, expected);
    }

    @Test
    public void testObjectToStringWithCustomObjectWithToString() {
        CustomObject customObject = new CustomObject("CustomObjectContent");
        String result = ConvertUtils.objectToString(customObject);
        Assert.assertEquals(result, "{\"content\": \"CustomObjectContent\"}");
    }

    @Test
    public void testStringToEnumValuePositive() {
        // Positive Test Case: Valid enum value
        Class<?> enumClass = Platform.class;
        String stringValue = "WINDOWS";
        Platform result = ConvertUtils.stringToEnumValue(
                SmartType.fromClass(enumClass), stringValue);

        Assert.assertEquals(result, Platform.WINDOWS, "Enum value should match the expected value.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToEnumValueNegativeInvalidEnumValue() {
        // Negative Test Case: Invalid enum value
        Class<?> enumClassName = Platform.class;
        String stringValue = "INVALID_VALUE";

        ConvertUtils.stringToEnumValue(SmartType.fromClass(enumClassName), stringValue);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeNullEnumClassName() {
        // Negative Test Case: Null enum class name
        String stringValue = "VALUE_ONE";

        ConvertUtils.stringToEnumValue(SmartType.fromClass(null), stringValue);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeNullStringValue() {
        // Negative Test Case: Null string value
        Class<?> enumClass = Platform.class;
        String stringValue = null;

        ConvertUtils.stringToEnumValue(SmartType.fromClass(enumClass), stringValue);
    }

    @Test
    public void testObjectToObjectPositive() {
        // Arrange
        SmartType targetType = SmartType.fromClass(String.class);
        Integer sourceObject = 123;
        String result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "123");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToObjectNullTargetType() {
        SmartType targetType = null;
        Integer sourceObject = 123;

        ConvertUtils.objectToObject(targetType, sourceObject);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToObjectNullSourceObject() {
        SmartType targetType = SmartType.fromClass(String.class);
        Object sourceObject = null;

        ConvertUtils.objectToObject(targetType, sourceObject);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToObjectInvalidConversion() {
        SmartType targetType = SmartType.fromClass(Integer.class);
        String sourceObject = "InvalidNumber";

        ConvertUtils.objectToObject(targetType, sourceObject);
    }

    @Test
    public void testObjectToObjectList() {
        SmartType targetType = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        List<String> sourceObject = Arrays.asList("one", "two", "three");
        List<String> result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectMap() {
        SmartType targetType = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(Integer.class));
        Map<String, Integer> sourceObject = Map.of("one", 1, "two", 2);
        Map<String, Integer> result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectFile() {
        SmartType targetType = SmartType.fromClass(File.class);
        File sourceObject = new File("test.txt");
        File result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectURL() throws Exception {
        SmartType targetType = SmartType.fromClass(java.net.URL.class);
        URL sourceObject = new URL("http://example.com");
        URL result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectURI() throws Exception {
        SmartType targetType = SmartType.fromClass(java.net.URI.class);
        URI sourceObject = new URI("http://example.com");
        URI result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectLocalDate() {
        SmartType targetType = SmartType.fromClass(LocalDate.class);
        LocalDate sourceObject = LocalDate.of(2023, 8, 31);
        LocalDate result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectLocalDateTime() {
        SmartType targetType = SmartType.fromClass(LocalDateTime.class);
        LocalDateTime sourceObject = LocalDateTime.of(2023, 8, 31, 12, 30);
        LocalDateTime result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectLocalTime() {
        SmartType targetType = SmartType.fromClass(LocalTime.class);
        LocalTime sourceObject = LocalTime.of(12, 30, 15);
        LocalTime result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectDate() {
        SmartType targetType = SmartType.fromClass(Date.class);
        Date sourceObject = new Date("05/23/1970");
        Date result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartDate() {
        SmartType targetType = SmartType.fromClass(SmartDate.class);
        SmartDate sourceObject = SmartDate.parseDate("1970-05-23");
        SmartDate result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartLocalDate() {
        SmartType targetType = SmartType.fromClass(SmartLocalDate.class);
        SmartLocalDate sourceObject = SmartLocalDate.parseLocalDate("1970-05-23");
        SmartLocalDate result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartLocalDateTime() {
        SmartType targetType = SmartType.fromClass(SmartLocalDateTime.class);
        SmartLocalDateTime sourceObject = SmartLocalDateTime.parseLocalDateTime("1970-05-23 12:45");
        SmartLocalDateTime result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartLocalTime() {
        SmartType targetType = SmartType.fromClass(SmartLocalTime.class);
        SmartLocalTime sourceObject = SmartLocalTime.parseLocalTime("12:45:15");
        SmartLocalTime result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectEnum() {
        SmartType targetType = SmartType.fromClass(Platform.class);
        String sourceObject = "LINUX";
        Platform result = ConvertUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, Platform.LINUX);
    }

    @Test
    public void testObjectToPojoObject() {
        PojoClass sourceObject = createPojoObject();
        Map<String, SmartType> fieldsMap = new HashMap<>();
        fieldsMap.put("name", SmartType.fromClass(String.class));
        fieldsMap.put("value", SmartType.fromClass(Integer.class));
        fieldsMap.put("stringArray", SmartType.fromArrayValueSmartType(
                SmartType.fromClass(String.class)));
        fieldsMap.put("integerList", SmartType.fromCollectionClass(
                List.class, SmartType.fromClass(Integer.class)));
        fieldsMap.put("stringBooleanMap", SmartType.fromMapClass(
                Map.class, String.class, SmartType.fromClass(Boolean.class)));
        Map<String, SmartType> nestedPojoFieldsMap = new HashMap<>();
        nestedPojoFieldsMap.put("platform", SmartType.fromEnumClass(Platform.class));
        nestedPojoFieldsMap.put("date", SmartType.fromClass(LocalDate.class));
        fieldsMap.put("nestedPojoObject", SmartType.fromPojoClass(NestedPojoClass.class, nestedPojoFieldsMap));
        SmartType smartType = SmartType.fromPojoClass(PojoClass.class, fieldsMap);
        PojoClass result = ConvertUtils.objectToObject(smartType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testPojoObjectToString() {
        PojoClass sourceObject = createPojoObject();
        String result = ConvertUtils.objectToString(sourceObject);

        String expectedSting = """
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

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expectedSting);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToEnumValueNegativeInvalidEnumName() {
        // Negative test: Invalid enum name
        SmartType smartType = SmartType.fromEnumClass(Platform.class);
        String invalidEnumName = "INVALID"; // Not a valid enum name in Platform

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToEnumValue(smartType, invalidEnumName);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeNullEnumName() {
        // Negative test: Null enum name
        SmartType smartType = SmartType.fromEnumClass(Platform.class);
        String nullEnumName = null;

        ConvertUtils.stringToEnumValue(smartType, nullEnumName);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeEmptyEnumName() {
        // Negative test: Empty enum name
        SmartType smartType = SmartType.fromEnumClass(Platform.class);
        String emptyEnumName = "";

        ConvertUtils.stringToEnumValue(smartType, emptyEnumName);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeNullType() {
        // Negative test: Null SmartType
        SmartType smartType = null;
        String enumName = "MAC";

        ConvertUtils.stringToEnumValue(smartType, enumName);
    }

    @Test
    public void testPojoToJsonWithNullValue() {
        // Create a POJO with null value
        Person person = new Person(null, "Doe", 30);
        JSONObject jsonObject = ConvertUtils.pojoObjectToJson(person);

        Assert.assertTrue(jsonObject.has("firstName"));
        Assert.assertTrue(jsonObject.isNull("firstName"));
        Assert.assertEquals(jsonObject.getString("lastName"), "Doe");
        Assert.assertEquals(jsonObject.getInt("age"), 30);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testPojoToJsonWithInvalidTypeObject() {
        // Passing a invalid object to pojoObjectToJson
        boolean notPojo = true;
        ConvertUtils.pojoObjectToJson(notPojo);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testPojoToJsonWithNullObject() {
        // Passing a null object to pojoObjectToJson
        ConvertUtils.pojoObjectToJson(null);
    }

    @Test
    public void testCsvStringToObjectValidCsvString() {
        // Positive test case: Valid CSV string
        String validCsvString = "name,age\nJohn,30\nDoe,25";
        Object result = ConvertUtils.csvStringToObject(validCsvString);

        Assert.assertTrue(result instanceof SmartValue, "Result should be of type SmartValue");
        // Verify the contents of SmartValue (assuming it's convertible back to JSON format)
        SmartValue smartValue = new SmartValue(result);
        String expectedSmartValue = """
                [
                    [
                        "name",
                        "age"
                    ],
                    [
                        "John",
                        "30"
                    ],
                    [
                        "Doe",
                        "25"
                    ]
                ]""".stripIndent();
        Assert.assertEquals(smartValue.toString(), expectedSmartValue, "SmartValue content mismatch");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCsvStringToObjectNullCsvString() {
        // Negative test case: Null CSV string
        String nullCsvString = null;

        ConvertUtils.csvStringToObject(nullCsvString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToObjectInvalidCsvString() {
        // Negative test case: Invalid CSV string that cannot be parsed
        String invalidCsvString = """
                name, age
                John, 30
                Jane
                """.stripIndent();

        ConvertUtils.csvStringToObject(invalidCsvString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToObjectInvalidCsvLineBreak() {
        // Negative test case: Invalid CSV string that cannot be parsed
        String invalidCsvString = """
                name, quote
                John, "He said "Hello
                World!"
                Jane, "He said "Hello!""
                """.stripIndent();

        Object result = ConvertUtils.csvStringToObject(invalidCsvString);
        System.out.println(result);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToObjectInvalidCsvDelimiter() {
        // Negative test case: Invalid CSV string that cannot be parsed
        String invalidCsvString = """
                name, age
                John; 30
                """.stripIndent();

        Object result = ConvertUtils.csvStringToObject(invalidCsvString);
        System.out.println(result);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToObjectInvalidCsvQuotes() {
        // Negative test case: Invalid CSV string that cannot be parsed
        String invalidCsvString = """
                name, age
                "John", 30
                "Jane, 25
                """.stripIndent();

        Object result = ConvertUtils.csvStringToObject(invalidCsvString);
        System.out.println(result);
    }

    @Test
    public void testObjectToCsvStringValidObject() {
        // Creating a valid JSONArray object to convert to CSV
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(new JSONArray().put("John").put("Doe").put(30));
        jsonArray.put(new JSONArray().put("Jane").put("Smith").put(25));
        String expectedCsv = """
                "John","Doe",30
                "Jane","Smith",25
                """.stripIndent();

        String result = ConvertUtils.objectToCsvString(jsonArray);
        Assert.assertEquals(result, expectedCsv);
    }

    @Test
    public void testStringToObjectWithString() {
        SmartType type = SmartType.fromClass(String.class);
        String input = "Hello, World!";
        String result = ConvertUtils.stringToObject(type, input);

        Assert.assertEquals(result, input, "String should be returned as-is.");
    }

    @Test
    public void testStringToObjectWithInteger() {
        SmartType type = SmartType.fromClass(Integer.class);
        String input = "123";
        Integer result = ConvertUtils.stringToObject(type, input);
        Assert.assertEquals(result, Integer.valueOf(123), "String should be converted to Integer.");
    }

    @Test
    public void testStringToObjectWithBoolean() {
        SmartType type = SmartType.fromClass(Boolean.class);
        String input = "true";
        Boolean result = ConvertUtils.stringToObject(type, input);
        Assert.assertTrue(result, "String 'true' should be converted to Boolean true.");
    }

    @Test
    public void testStringToObjectWithBigDecimal() {
        SmartType type = SmartType.fromClass(BigDecimal.class);
        String input = "12345.67";
        BigDecimal result = ConvertUtils.stringToObject(type, input);
        Assert.assertEquals(result, new BigDecimal("12345.67"), "String should be converted to BigDecimal.");
    }

    @Test
    public void testStringToObjectWithJSONArray() {
        SmartType type = SmartType.fromClass(JSONArray.class);
        String input = "[1, 2, 3]";
        JSONArray result = ConvertUtils.stringToObject(type, input);
        Assert.assertEquals(result.length(), 3, "String should be converted to JSONArray with 3 elements.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToObjectWithNullType() {
        String input = "test";
        ConvertUtils.stringToObject(null, input);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToObjectWithInvalidInteger() {
        SmartType type = SmartType.fromClass(Integer.class);
        String input = "invalid";
        ConvertUtils.stringToObject(type, input);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToObjectWithInvalidBoolean() {
        SmartType type = SmartType.fromClass(Boolean.class);
        String input = "notABoolean";
        ConvertUtils.stringToObject(type, input);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToObjectWithInvalidDate() {
        SmartType type = SmartType.fromClass(Date.class);
        String input = "invalidDate";
        ConvertUtils.stringToObject(type, input);
    }

    @Test
    public void testStringToStringBufferPositive() {
        // Positive test case: Valid string input
        String input = "Hello,\nWorld!";
        StringBuffer result = ConvertUtils.stringToStringBuffer(input);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.toString(), input, "The StringBuffer should match the input string.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToStringBufferNegativeNullInput() {
        ConvertUtils.stringToStringBuffer(null);
    }

    @Test
    public void testCsvStringToJsonArrayValidInput() {
        // Valid CSV string
        String csvString = "name,age,city\nJohn,30,New York\nJane,25,Boston";
        // Expected JSONArray result
        JSONArray expectedJsonArray = new JSONArray();
        expectedJsonArray.put(new JSONArray().put("name").put("age").put("city"));
        expectedJsonArray.put(new JSONArray().put("John").put("30").put("New York"));
        expectedJsonArray.put(new JSONArray().put("Jane").put("25").put("Boston"));
        JSONArray result = ConvertUtils.csvStringToJsonArray(csvString);

        Assert.assertEquals(result.toString(), expectedJsonArray.toString());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToJsonArrayMismatchedColumns() {
        // Invalid CSV string with mismatched columns
        String csvString = "name,age,city\nJohn,30\nJane,25,Boston";

        // This should throw a SmartRuntimeException
        ConvertUtils.csvStringToJsonArray(csvString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToJsonArrayEmptyInput() {
        // Invalid empty CSV string
        String csvString = """
                John, Doe, 25
                Jack, 54
                """.stripIndent();

        // Expect a SmartRuntimeException due to empty input
        ConvertUtils.csvStringToJsonArray(csvString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testCsvStringToJsonArrayEmptyRow() {
        // Invalid CSV string with an empty row
        String csvString = "name,age,city\nJohn,30,New York\n\nJane,25,Boston";

        // This should throw a SmartRuntimeException
        ConvertUtils.csvStringToJsonArray(csvString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCsvStringToJsonArrayNullString() {
        // This should throw a SmartValidationException
        ConvertUtils.csvStringToJsonArray(null);
    }

    @Test
    public void testValidSingleCharacter() {
        // Positive test case: valid input string with a single character
        char result = ConvertUtils.stringToCharacter("A");
        Assert.assertEquals(result, 'A', "The returned character should be 'A'");
    }

    @Test
    public void testValidSingleCharacterLowercase() {
        // Positive test case: valid input with a different single character
        char result = ConvertUtils.stringToCharacter("b");
        Assert.assertEquals(result, 'b', "The returned character should be 'b'");
    }

    @Test
    public void testValidSingleCharacterNewLine() {
        // Positive test case: valid input with a new line character
        char result = ConvertUtils.stringToCharacter("\n");
        Assert.assertEquals(result, '\n', "The returned character should be new line break");
    }

    // Negative test case: empty string should throw an exception
    @Test(expectedExceptions = SmartValidationException.class)
    public void testEmptyString() {
        // Positive test case: valid input with a different single character
        ConvertUtils.stringToCharacter("");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testMultipleCharacters() {
        // Negative test case: string with more than one character should throw an exception
        ConvertUtils.stringToCharacter("AB");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullString() {
        // Negative test case: null string should throw an exception
        ConvertUtils.stringToCharacter(null);
    }

    @Test
    public void testCollectionToJsonArrayPositive() {
        // Positive test case: valid collection
        Collection<String> collection = Arrays.asList("item1", "item2", "item3");
        JSONArray jsonArray = ConvertUtils.collectionToJsonArray(collection);

        // Validate that the JSON array matches the collection size and content
        Assert.assertEquals(jsonArray.length(), collection.size(), "JSONArray length does not match collection size");
        for (int i = 0; i < collection.size(); i++) {
            Assert.assertEquals(jsonArray.getString(i), ((List<String>) collection).get(i),
                    "JSONArray item does not match collection item");
        }
    }

    @Test
    public void testCollectionToJsonArrayEmptyCollection() {
        // Positive test case: empty collection
        Collection<String> emptyCollection = new ArrayList<>();
        JSONArray jsonArray = ConvertUtils.collectionToJsonArray(emptyCollection);

        // Validate that the JSON array is empty
        Assert.assertEquals(jsonArray.length(), 0, "JSONArray should be empty for an empty collection");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCollectionToJsonArrayNullCollection() {
        // Negative test case: null collection should throw exception
        ConvertUtils.collectionToJsonArray(null);
    }

    @Test
    public void testCollectionToJsonArrayWithMixedTypes() {
        // Positive test case: collection with mixed types (e.g., String, Integer, Boolean)
        Collection<Object> collection = Arrays.asList("item1", 123, true);
        JSONArray jsonArray = ConvertUtils.collectionToJsonArray(collection);

        // Validate that the JSON array matches the collection size and content
        Assert.assertEquals(jsonArray.length(), collection.size(), "JSONArray length does not match collection size");
        Assert.assertEquals(jsonArray.getString(0), "item1");
        Assert.assertEquals(jsonArray.getInt(1), 123);
        Assert.assertEquals(jsonArray.getBoolean(2), true);
    }

    @Test
    public void testMapToJSONObjectValidMap() {
        // Positive test case - valid map
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John Doe");
        map.put("age", 30);
        map.put("isActive", true);
        JSONObject jsonObject = ConvertUtils.mapToJasonObject(map);

        Assert.assertEquals(jsonObject.getString("name"), "John Doe");
        Assert.assertEquals(jsonObject.getInt("age"), 30);
        Assert.assertEquals(jsonObject.getBoolean("isActive"), true);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testMapToJSONObjectInvalidMapType() {
        // Negative test case - null map.
        ConvertUtils.mapToJasonObject(null);
    }

    @Test
    public void testJsonArrayToIntegerArray() {
        // Positive test case - JSONArray of integers
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(1);
        jsonArray.put(2);
        jsonArray.put(3);
        Integer[] result = ConvertUtils.jsonArrayToArray(jsonArray);

        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], Integer.valueOf(1));
        Assert.assertEquals(result[1], Integer.valueOf(2));
        Assert.assertEquals(result[2], Integer.valueOf(3));
    }

    @Test
    public void testEmptyJsonArrayToIntegerArray() {
        // Positive test case - empty JSONArray.
        JSONArray jsonArray = new JSONArray();
        Object[] result = ConvertUtils.jsonArrayToArray(jsonArray);

        Assert.assertEquals(result.length, 0);
    }

    @Test
    public void testJsonArrayToStringArray() {
        // Positive test case - JSONArray of strings
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("apple");
        jsonArray.put("banana");
        jsonArray.put("cherry");

        // Convert to String array
        String[] result = ConvertUtils.jsonArrayToArray(jsonArray);

        // Verify the results
        Assert.assertEquals(result.length, 3);
        Assert.assertEquals(result[0], "apple");
        Assert.assertEquals(result[1], "banana");
        Assert.assertEquals(result[2], "cherry");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullJsonArrayThrowsException() {
        // Negative test case - null JSONArray
        ConvertUtils.jsonArrayToArray(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidCastThrowsException() {
        // Negative test case - trying to cast JSONArray elements to the wrong type
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("string");
        jsonArray.put(42); // Integer

        // Attempt to convert to a String array (should fail)
        ConvertUtils.jsonArrayToArray(jsonArray);
    }

    @Test
    public void testArrayToJsonArrayWithValidStringArray() {
        // Positive test case: Valid String array
        String[] stringArray = {"apple", "banana", "cherry"};
        JSONArray jsonArray = ConvertUtils.arrayToJsonArray(stringArray);

        // Validate the JSON array
        Assert.assertEquals(jsonArray.length(), 3);
        Assert.assertEquals(jsonArray.getString(0), "apple");
        Assert.assertEquals(jsonArray.getString(1), "banana");
        Assert.assertEquals(jsonArray.getString(2), "cherry");
    }

    @Test
    public void testArrayToJsonArrayWithEmptyArray() {
        // Positive test case: Empty array
        Integer[] emptyArray = new Integer[0];
        JSONArray jsonArray = ConvertUtils.arrayToJsonArray(emptyArray);

        // Validate the empty JSON array
        Assert.assertEquals(jsonArray.length(), 0);
    }

    @Test
    public void testArrayToJsonArrayWithIntegerArray() {
        // Positive test case: Valid Integer array
        Integer[] intArray = {1, 2, 3};
        JSONArray jsonArray = ConvertUtils.arrayToJsonArray(intArray);

        // Validate the JSON array
        Assert.assertEquals(jsonArray.length(), 3);
        Assert.assertEquals(jsonArray.getInt(0), 1);
        Assert.assertEquals(jsonArray.getInt(1), 2);
        Assert.assertEquals(jsonArray.getInt(2), 3);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testArrayToJsonArrayWithNullArray() {
        // Negative test case: Null array should throw an exception
        ConvertUtils.arrayToJsonArray(null);
    }

    @Test
    public void testArrayToJsonArrayWithMixedTypeArray() {
        // Positive test case: Mixed type array
        Object[] mixedArray = {"string", 123, true};
        JSONArray jsonArray = ConvertUtils.arrayToJsonArray(mixedArray);

        // Validate the JSON array
        Assert.assertEquals(jsonArray.length(), 3);
        Assert.assertEquals(jsonArray.getString(0), "string");
        Assert.assertEquals(jsonArray.getInt(1), 123);
        Assert.assertEquals(jsonArray.getBoolean(2), true);
    }

    @Test
    public void testJsonToArrayObjectIsJSONArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value1");
        jsonArray.put("value2");
        JSONArray result = ConvertUtils.objectToJsonArray(jsonArray);

        Assert.assertEquals(result.length(), 2);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getString(1), "value2");
    }

    @Test
    public void testObjectToJsonArrayObjectIsCollection() {
        // Positive test: Passing a Collection
        List<String> collection = Arrays.asList("value1", "value2", "value3");
        JSONArray result = ConvertUtils.objectToJsonArray(collection);

        Assert.assertEquals(result.length(), 3);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getString(1), "value2");
        Assert.assertEquals(result.getString(2), "value3");
    }

    @Test
    public void testObjectToJsonArrayObjectIsArray() {
        // Positive test: Passing an array
        String[] array = {"value1", "value2", "value3"};
        JSONArray result = ConvertUtils.objectToJsonArray(array);

        Assert.assertEquals(result.length(), 3);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getString(1), "value2");
        Assert.assertEquals(result.getString(2), "value3");
    }

    @Test
    public void testObjectToJsonArrayObjectIsJSONString() {
        // Positive test: Passing a JSON array string
        String jsonArrayString = "[\"value1\", \"value2\", \"value3\"]";
        JSONArray result = ConvertUtils.objectToJsonArray(jsonArrayString);

        Assert.assertEquals(result.length(), 3);
        Assert.assertEquals(result.getString(0), "value1");
        Assert.assertEquals(result.getString(1), "value2");
        Assert.assertEquals(result.getString(2), "value3");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToJsonArrayObjectIsInvalidJSONString() {
        // Negative test: Passing an invalid string (not a JSON array)
        String invalidJsonString = "invalid string";

        // This should throw a SmartRuntimeException
        ConvertUtils.objectToJsonArray(invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToJsonArrayObjectIsUnsupportedType() {
        // Negative test: Passing an unsupported object type
        // Passing an unsupported object type (Integer)
        ConvertUtils.objectToJsonArray(42);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToJsonArrayObjectIsNull() {
        // Negative test: Passing null
        // This should throw a SmartRuntimeException
        ConvertUtils.objectToJsonArray(null);
    }

    @Test
    public void testObjectToJsonObjectWithPojoObject() {
        PojoClass pojoObject = createPojoObject();
        JSONObject result = ConvertUtils.objectToJsonObject(pojoObject);
        String expectedString = ConvertUtils.objectToString(pojoObject);
        String resultString = ConvertUtils.objectToString(result);

        Assert.assertEquals(expectedString, resultString);
    }

    @Test
    public void testObjectToJsonObjectWithMapObject() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("true", true);
        map.put("false", false);
        map.put("null", null);
        JSONObject result = ConvertUtils.objectToJsonObject(map);
        String expectedString = ConvertUtils.objectToString(map);
        String resultString = ConvertUtils.objectToString(result);

        Assert.assertEquals(expectedString, resultString);
    }

    @Test
    public void testObjectToJsonObjectWithXmlNodeObject() {
        Node xmlNode = createMockNode();
        JSONObject result = ConvertUtils.objectToJsonObject(xmlNode);
        String expectedString = """
                {"person": {
                    "name": "John Doe",
                    "age": 30
                }}""".stripIndent();
        String resultString = ConvertUtils.objectToString(result);

        Assert.assertEquals(resultString, expectedString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToJsonObjectWithInvalidJsonString() {
        // Negative test - invalid JSON string
        String invalidJson = "Invalid JSON string";
        ConvertUtils.objectToJsonObject(invalidJson);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToJsonObjectWithUnsupportedObject() {
        // Negative test - not supported object
        Object unsupportedObject = Object.class;
        ConvertUtils.objectToJsonObject(unsupportedObject);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToJsonObjectWithNullObject() {
        // Negative test - null object.
        ConvertUtils.objectToJsonObject(null);
    }

    @Test
    public void testObjectToXmlNodeWithNode() {
        // Test with a Node object
        Node mockNode = createMockNode();
        Node result = ConvertUtils.objectToXmlNode(mockNode);

        Assert.assertEquals(result, mockNode, "Expected the same Node object.");
    }

    @Test
    public void testObjectToXmlNodeWithJSONObject() {
        // Test with a JSONObject object
        String jsonString = "{\"name\":\"John Doe\",\"age\":30}";
        JSONObject person = new JSONObject(jsonString);
        Node result = ConvertUtils.objectToXmlNode(person);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <person>
                    <name>John Doe</name>
                    <age>30</age>
                </person>
                """.stripIndent();
        String resultString = ConvertUtils.objectToString(result);

        Assert.assertNotNull(result, "Expected a valid XML Node from JSONObject.");
        Assert.assertEquals(resultString, expectedString, "Result XML string should equal expected XML string");
    }

    @Test
    public void testObjectToXmlNodeWithMap() {
        // Test with a Map object
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        Node result = ConvertUtils.objectToXmlNode(map);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <map>
                    <key>value</key>
                </map>
                """.stripIndent();
        String resultString = ConvertUtils.objectToString(result);

        Assert.assertNotNull(result, "Expected a valid XML Node from Map.");
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testObjectToXmlNodeWithString() {
        // Test with a String object
        String xmlString = "<root><child>value</child></root>";
        Node result = ConvertUtils.objectToXmlNode(xmlString);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <root>
                    <child>value</child>
                </root>
                """.stripIndent();
        String resultString = ConvertUtils.normalizeLineSeparators(
                ConvertUtils.objectToString(result));

        Assert.assertNotNull(result, "Expected a valid XML Node from String.");
        Assert.assertEquals(expectedString, resultString);
    }

    @Test
    public void testObjectToXmlNodeWithPojo() {
        // Test with a POJO object that gets converted to JSON and then to XML
        PojoClass pojo = createPojoObject();
        Node result = ConvertUtils.objectToXmlNode(pojo);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <pojo>
                    <integerList>
                        <integerList>1</integerList>
                        <integerList>2</integerList>
                    </integerList>
                    <name>Some name</name>
                    <stringArray>one</stringArray>
                    <stringArray>two</stringArray>
                    <stringArray>three</stringArray>
                    <stringBooleanMap>
                        <true>true</true>
                        <false>false</false>
                    </stringBooleanMap>
                    <nestedPojoObject>
                        <date>1970-05-23</date>
                        <platform>windows</platform>
                    </nestedPojoObject>
                    <value>2</value>
                </pojo>
                """.stripIndent();
        String resultString = ConvertUtils.normalizeLineSeparators(
                ConvertUtils.objectToString(result));

        Assert.assertNotNull(result, "Expected a valid XML Node from POJO.");
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testObjectToXmlNodeWithRecord() {
        // Test with a record object that gets converted to JSON and then to XML
        PersonRecord record = new PersonRecord("John Doe", 30);
        Node result = ConvertUtils.objectToXmlNode(record);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <record>
                    <name>John Doe</name>
                    <age>30</age>
                </record>
                """.stripIndent();
        String resultString = ConvertUtils.normalizeLineSeparators(
                ConvertUtils.objectToString(result));

        Assert.assertNotNull(result, "Expected a valid XML Node from map.");
        Assert.assertEquals(expectedString, resultString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToXmlNodeWithNullObject() {
        // Test with a null object
        ConvertUtils.objectToXmlNode(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToXmlNodeWithWrongXmlString() {
        // Test with a string that cannot be converted to XML
        String invalidXmlString = "invalid XML string";

        ConvertUtils.objectToXmlNode(invalidXmlString);
    }

    @Test
    public void testStringToRecordWithJsonString() {
        // Positive Test: Valid JSON string
        String jsonString = "{\"name\":\"John Doe\",\"age\":30}";
        PersonRecord person = ConvertUtils.stringToRecord(PersonRecord.class, jsonString);

        Assert.assertNotNull(person, "Record should not be null");
        Assert.assertEquals(person.name(), "John Doe", "Name should be 'John Doe'");
        Assert.assertEquals(person.age(), 30, "Age should be 30");
    }

    @Test
    public void testStringToRecordWithXmlString() {
        // Positive Test: Valid XML string (assuming conversion is handled by xmlStringToJsonObject)
        String xmlString = "<PersonRecord><name>John Doe</name><age>30</age></PersonRecord>";
        PersonRecord person = ConvertUtils.stringToRecord(PersonRecord.class, xmlString);

        Assert.assertNotNull(person, "Record should not be null");
        Assert.assertEquals(person.name(), "John Doe", "Name should be 'John Doe'");
        Assert.assertEquals(person.age(), 30, "Age should be 30");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToRecordWithInvalidJson() {
        // Negative Test: Invalid JSON string
        String invalidJsonString = "{\"name\":\"John Doe\",\"age\":\"invalid_age\"}";

        // This should throw an exception due to invalid age
        ConvertUtils.stringToRecord(Person.class, invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToRecordWithInvalidXml() {
        // Negative Test: Invalid XML string
        String invalidXmlString = "<Person><name>John Doe</name><age>invalid_age</age></Person>";

        // This should throw an exception due to invalid age format
        ConvertUtils.stringToRecord(Person.class, invalidXmlString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToRecordWithNonRecordClass() {
        // Negative Test: Non-record class
        String jsonString = "{\"name\":\"John Doe\",\"age\":30}";

        // Attempt to use a non-record class (String.class in this case)
        ConvertUtils.stringToRecord(String.class, jsonString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToRecordWithNullClass() {
        // Negative Test: Null record class
        String jsonString = "{\"name\":\"John Doe\",\"age\":30}";

        // This should throw an exception due to null record class
        ConvertUtils.stringToRecord(null, jsonString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToRecordWithEmptyString() {
        // Negative Test: Empty string
        String emptyString = "";

        // This should throw an exception due to empty string
        ConvertUtils.stringToRecord(Person.class, emptyString);
    }

    @Test
    public void testRecordToJsonObjectWithValidRecord() {
        // Positive Test: Valid record to JSON conversion
        PersonRecord person = new PersonRecord("John Doe", 30);
        JSONObject jsonObject = ConvertUtils.recordToJsonObject(person);

        Assert.assertEquals(jsonObject.getString("name"), "John Doe", "Expected name to match");
        Assert.assertEquals(jsonObject.getInt("age"), 30, "Expected age to match");
    }


    @Test(expectedExceptions = SmartValidationException.class)
    public void testRecordToJsonObjectWithNullRecord() {
        // Negative Test: Null record should throw an exception
        ConvertUtils.recordToJsonObject(null);
    }

    @Test
    public void testRecordToJsonObjectWithNestedRecord() {
        // Positive Test: Record with nested record should be converted successfully
        Address address = new Address("New York", "USA");
        PersonAdressRecord personWithAddress = new PersonAdressRecord("John Doe", 30, address);
        JSONObject jsonObject = ConvertUtils.recordToJsonObject(personWithAddress);

        Assert.assertEquals(jsonObject.getString("name"), "John Doe", "Expected name to match");
        Assert.assertEquals(jsonObject.getInt("age"), 30, "Expected age to match");
    }

    @Test
    public void testRecordToJsonObjectWithEmptyRecord() {
        // Positive Test: Empty record
        record EmptyRecord() {}
        EmptyRecord emptyRecord = new EmptyRecord();
        JSONObject jsonObject = ConvertUtils.recordToJsonObject(emptyRecord);

        Assert.assertTrue(jsonObject.isEmpty(), "Expected empty JSON object");
    }

    @Test
    public void testRecordToStringWithValidRecord() {
        // Positive Test: Valid record conversion to string
        PersonRecord person = new PersonRecord("John Doe", 30);
        String result = ConvertUtils.recordToString(person);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertTrue(result.contains("John Doe"), "The result should contain the name 'John Doe'.");
        Assert.assertTrue(result.contains("30"), "The result should contain the age '30'.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testRecordToStringWithNullRecord() {
        // Negative Test: Passing a null record should throw SmartValidationException
        ConvertUtils.recordToString(null);
    }

    @Test
    public void testObjectToEnumValueWithEnumObject() {
        // Positive test: Object is already an enum value
        SmartType enumType = SmartType.fromClass(Platform.class);
        Object inputObject = Platform.LINUX;
        Platform result = ConvertUtils.objectToEnumValue(enumType, inputObject);

        // Validate the result is the expected enum value
        Assert.assertEquals(result, Platform.LINUX, "Expected the enum value LINUX.");
    }

    @Test
    public void testObjectToEnumWithStringObjectCaseInsensitive() {
        // Positive test: Object is a string in different case that can be converted to enum value
        SmartType enumType = SmartType.fromClass(Platform.class);
        Object inputObject = "linux";
        ConvertUtils.objectToEnumValue(enumType, inputObject);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToEnumWithInvalidString() {
        // Negative test: Object is an invalid string that doesn't match any enum value
        SmartType enumType = SmartType.fromClass(Platform.class);
        Object inputObject = "INVALID"; // Not a valid Color enum

        // Expecting a SmartRuntimeException to be thrown
        ConvertUtils.objectToEnumValue(enumType, inputObject);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToEnumWithNullObject() {
        // Negative test: Object is null
        SmartType enumType = SmartType.fromClass(Platform.class);
        Object inputObject = null;

        // Expecting a SmartRuntimeException to be thrown
        ConvertUtils.objectToEnumValue(enumType, inputObject);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToEnumWithNullEnumType() {
        // Negative test: Enum type is null
        SmartType enumType = null;
        Object inputObject = "LINUX";

        // Expecting a SmartRuntimeException to be thrown
        ConvertUtils.objectToEnumValue(enumType, inputObject);
    }

    @Test
    public void testArrayToStringWithValidArray() {
        // Positive test: Convert a valid array of integers
        Integer[] intArray = {1, 2, 3, 4, 5};
        String expected = """
                [
                    1,
                    2,
                    3,
                    4,
                    5
                ]""".stripIndent();
        String result = ConvertUtils.arrayToString(intArray);

        Assert.assertEquals(result, expected, "The array string should match the expected format.");
    }

    @Test
    public void testArrayToStringWithEmptyArray() {
        // Positive test: Convert an empty array
        String[] emptyArray = {};
        String expected = "[]";
        String result = ConvertUtils.arrayToString(emptyArray);

        Assert.assertEquals(result, expected, "An empty array should convert to '[]'.");
    }

    @Test
    public void testArrayToStringWithStringArray() {
        // Positive test: Convert an array of strings
        String[] stringArray = {"apple", "banana", "cherry"};
        String expected = """
                [
                    "apple",
                    "banana",
                    "cherry"
                ]""".stripIndent();
        String result = ConvertUtils.arrayToString(stringArray);

        Assert.assertEquals(result, expected, "The string array should be converted to the correct string format.");
    }

    @Test
    public void testArrayToStringWithObjectArray() {
        // Positive test: Convert an array of custom objects
        Person[] peopleArray = {
                new Person("John", "Doe", 30),
                new Person("Jane", "Smith",25)};
        String expected = """
                [
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "age": 30
                    },
                    {
                        "firstName": "Jane",
                        "lastName": "Smith",
                        "age": 25
                    }
                ]""".stripIndent();
        String result = ConvertUtils.arrayToString(peopleArray);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, expected, "The array string result should equal to expected string.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testArrayToStringWithNullArray() {
        // Negative test: Pass a null array (expecting SmartRuntimeException)
        ConvertUtils.arrayToString(null);
    }

    @Test
    public void testArrayToStringWithNullValuesInArray() {
        // Negative test: Array contains null values
        String[] stringArray = {"apple", null, "cherry"};
        String expected = """
                [
                    "apple",
                    null,
                    "cherry"
                ]""".stripIndent();
        String result = ConvertUtils.arrayToString(stringArray);

        Assert.assertEquals(result, expected, "Array with null elements should handle nulls properly.");
    }

    @Test
    public void testStringToArrayWithJsonArray() {
        SmartType smartType = SmartType.fromArrayValueSmartType(SmartType.fromClass(Integer.class));
        String jsonArrayString = "[1, 2, 3, 4, 5]";
        Integer[] result = ConvertUtils.stringToArray(smartType, jsonArrayString);
        Integer[] expectedArray = {1, 2, 3, 4, 5};

        // Assert the results
        Assert.assertEquals(result, expectedArray, "The array should match the expected result.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToArrayWithInvalidJson() {
        SmartType smartType = SmartType.fromClass(Integer.class);
        String invalidJsonString = "[1, 2, invalid, 4]";

        // This should throw a SmartRuntimeException due to the invalid JSON
        ConvertUtils.stringToArray(smartType, invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToArrayWithNonArrayString() {
        SmartType smartType = SmartType.fromClass(Integer.class);
        String nonArrayString = "This is not a JSON array";

        ConvertUtils.stringToArray(smartType, nonArrayString);
    }

    @Test
    public void testStringToListWithValidJsonArray() {
        // Set up SmartType for the list type (assuming SmartType is a mock or concrete class)
        SmartType type = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        String jsonArrayString = "[\"one\", \"two\", \"three\"]";
        List<String> result = ConvertUtils.stringToList(type, jsonArrayString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.size(), 3, "The list should contain 3 elements.");
        Assert.assertEquals(result.get(0), "one");
        Assert.assertEquals(result.get(1), "two");
        Assert.assertEquals(result.get(2), "three");
    }

    @Test
    public void testStringToListWithEmptyJsonArray() {
        // Set up SmartType for the list type
        SmartType type = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        // Empty JSON array string
        String jsonArrayString = "[]";
        List<String> result = ConvertUtils.stringToList(type, jsonArrayString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertTrue(result.isEmpty(), "The list should be empty.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToListWithInvalidJsonArray() {
        // Set up SmartType for the list type
        SmartType type = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        // Invalid JSON string (not a valid array)
        String invalidJsonString = "{\"key\":\"value\"}";

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToList(type, invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToListWithNonArrayString() {
        // Set up SmartType for the list type
        SmartType type = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        // A string that is not a JSON array
        String nonArrayString = "\"Just a string\"";

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToList(type, nonArrayString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToListWithNullType() {
        // Null SmartType
        SmartType type = null;
        String jsonArrayString = "[\"one\", \"two\", \"three\"]";

        // This should throw SmartValidationException due to null type
        ConvertUtils.stringToList(type, jsonArrayString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToListWithBlankString() {
        // Set up SmartType for the list type
        SmartType type = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        // Blank string (not a valid JSON array)
        String blankString = "";

        // This should throw SmartValidationException due to blank string
        ConvertUtils.stringToList(type, blankString);
    }

    @Test
    public void testStringToSetWithValidJsonArray() {
        // Set up SmartType for the set type (assuming SmartType is a mock or concrete class)
        SmartType type = SmartType.fromCollectionClass(Set.class, SmartType.fromClass(String.class));
        String jsonArrayString = "[\"one\", \"two\", \"three\"]";
        Set<String> expected = new HashSet<>();
        expected.add("one");
        expected.add("two");
        expected.add("three");
        Set<String> result = ConvertUtils.stringToSet(type, jsonArrayString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result, expected, "The result set should be equal the expected set.");
    }

    @Test
    public void testStringToSetWithEmptyJsonArray() {
        // Set up SmartType for the set type
        SmartType type = SmartType.fromCollectionClass(Set.class, SmartType.fromClass(String.class));
        // Empty JSON array string
        String jsonArrayString = "[]";
        // Expected - empty set
        Set<String> expected = new HashSet<>();
        Set<String> result = ConvertUtils.stringToSet(type, jsonArrayString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result, expected, "The result set should be equal the expected set.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToSetWithInvalidJsonArray() {
        // Set up SmartType for the set type
        SmartType type = SmartType.fromCollectionClass(Set.class, SmartType.fromClass(String.class));
        // Invalid JSON string (not a valid array)
        String invalidJsonString = "{\"key\":\"value\"}";

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToSet(type, invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToSetWithNonArrayString() {
        // Set up SmartType for the set type
        SmartType type = SmartType.fromCollectionClass(Set.class, SmartType.fromClass(String.class));
        // A string that is not a JSON array
        String nonArrayString = "\"Just a string\"";

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToSet(type, nonArrayString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToSetWithNullType() {
        // Null SmartType
        SmartType type = null;
        String jsonArrayString = "[\"one\", \"two\", \"three\"]";

        // This should throw SmartValidationException due to null type
        ConvertUtils.stringToSet(type, jsonArrayString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToSetWithBlankString() {
        // Set up SmartType for the list type
        SmartType type = SmartType.fromCollectionClass(List.class, SmartType.fromClass(String.class));
        // Blank string (not a valid JSON array)
        String blankString = "";

        // This should throw SmartValidationException due to blank string
        ConvertUtils.stringToSet(type, blankString);
    }
    @Test
    public void testStringToQueueWithValidJsonArray() {
        // Set up SmartType for the queue type (assuming SmartType is a mock or concrete class)
        SmartType type = SmartType.fromCollectionClass(Queue.class, SmartType.fromClass(String.class));
        String jsonArrayString = "[\"one\", \"two\", \"three\"]";
        Queue<String> expected = new LinkedList<>();
        expected.add("one");
        expected.add("two");
        expected.add("three");
        Queue<String> result = ConvertUtils.stringToQueue(type, jsonArrayString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result, expected, "The result set should be equal the expected set.");
    }

    @Test
    public void testStringToQueueWithEmptyJsonArray() {
        // Set up SmartType for the queue type
        SmartType type = SmartType.fromCollectionClass(Queue.class, SmartType.fromClass(String.class));
        // Empty JSON array string
        String jsonArrayString = "[]";
        // Expected - empty set
        Queue<String> expected = new LinkedList<>();
        Queue<String> result = ConvertUtils.stringToQueue(type, jsonArrayString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result, expected, "The result set should be equal the expected set.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToQueueWithInvalidJsonArray() {
        // Set up SmartType for the queue type
        SmartType type = SmartType.fromCollectionClass(Queue.class, SmartType.fromClass(String.class));
        // Invalid JSON string (not a valid array)
        String invalidJsonString = "{\"key\":\"value\"}";

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToQueue(type, invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToQueueWithNonArrayString() {
        // Set up SmartType for the queue type
        SmartType type = SmartType.fromCollectionClass(Queue.class, SmartType.fromClass(String.class));
        // A string that is not a JSON array
        String nonArrayString = "\"Just a string\"";

        // This should throw a SmartRuntimeException
        ConvertUtils.stringToQueue(type, nonArrayString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToQueueWithNullType() {
        // Null SmartType
        SmartType type = null;
        String jsonArrayString = "[\"one\", \"two\", \"three\"]";

        // This should throw SmartValidationException due to null type
        ConvertUtils.stringToQueue(type, jsonArrayString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToQueueWithBlankString() {
        // Set up SmartType for the list type
        SmartType type = SmartType.fromCollectionClass(Queue.class, SmartType.fromClass(String.class));
        // Blank string (not a valid JSON array)
        String blankString = "";

        // This should throw SmartValidationException due to blank string
        ConvertUtils.stringToQueue(type, blankString);
    }
    
    @Test
    public void testStringToVectorValidJsonString() {
        String validJsonString = "[\"element1\", \"element2\", \"element3\"]";
        SmartType type = SmartType.fromCollectionClass(Vector.class, SmartType.fromClass(String.class));
        Vector<String> result = ConvertUtils.stringToVector(type, validJsonString);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.get(0), "element1");
        Assert.assertEquals(result.get(1), "element2");
        Assert.assertEquals(result.get(2), "element3");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToVectorNullType() {
        // Negative test case - null type
        String validJsonString = "[\"element1\", \"element2\", \"element3\"]";

        ConvertUtils.stringToVector(null, validJsonString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToVectorBlankString() {
        // Negative test case - blank string
        SmartType type = SmartType.fromCollectionClass(Vector.class, SmartType.fromClass(String.class));

        ConvertUtils.stringToVector(type, "");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToVectorInvalidJsonString() {
        // Negative test case - invalid JSON format
        String invalidJsonString = "not a json array";
        SmartType type = SmartType.fromCollectionClass(Vector.class, SmartType.fromClass(String.class));

        ConvertUtils.stringToVector(type, invalidJsonString);
    }

    @Test
    public void testStringToMapWithValidJsonString() {
        String jsonString = "{\"key1\": \"value1\", \"key2\": \"value2\"}";
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(String.class));
        Map<String, String> result = ConvertUtils.stringToMap(type, jsonString);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("key1"), "value1");
        Assert.assertEquals(result.get("key2"), "value2");
    }

    @Test
    public void testStringToMapWithValidXmlString() {
        String xmlString = "<root><key1>value1</key1><key2>value2</key2></root>";
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(String.class));
        Map<String, String> result = ConvertUtils.stringToMap(type, xmlString);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.get("key1"), "value1");
        Assert.assertEquals(result.get("key2"), "value2");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToMapWithInvalidJsonString() {
        String invalidJsonString = "Invalid JSON";
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(String.class));

        ConvertUtils.stringToMap(type, invalidJsonString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToMapWithInvalidXmlString() {
        String invalidXmlString = "<root><key1>value1<key2>value2</key2>";
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(String.class));

        ConvertUtils.stringToMap(type, invalidXmlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToMapWithEmptyString() {
        String emptyString = "";
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(String.class));

        ConvertUtils.stringToMap(type, emptyString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToMapWithNullType() {
        String xmlString = "<root><key1>value1</key1><key2>value2</key2></root>";
        ConvertUtils.stringToMap(null, xmlString);
    }

    @Test
    public void testJsonObjectToMapPositive() {
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(Integer.class));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", 1);
        jsonObject.put("key2", 2);
        Map<String, Integer> result = ConvertUtils.jsonObjectToMap(type, jsonObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get("key1"), Integer.valueOf(1));
        Assert.assertEquals(result.get("key2"), Integer.valueOf(2));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonObjectToMapNllTypeNegative() {
        // Prepare input data
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", 1);

        ConvertUtils.jsonObjectToMap(null, jsonObject);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonObjectToMapNullJsonObjectNegative() {
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(Integer.class));

        // Expect SmartRuntimeException due to null JSON object
        ConvertUtils.jsonObjectToMap(type, null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testJsonObjectToMapInvalidValueTypeNegative() {
        SmartType type = SmartType.fromMapClass(Map.class, String.class, SmartType.fromClass(Integer.class));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");

        // Expect SmartRuntimeException due to invalid value type
        ConvertUtils.jsonObjectToMap(type, jsonObject);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testJsonObjectToMapInvalidKeyTypeNegative() {
        SmartType type = SmartType.fromMapClass(Map.class, Integer.class, SmartType.fromClass(Integer.class));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");

        // Expect SmartRuntimeException due to invalid value type
        ConvertUtils.jsonObjectToMap(type, jsonObject);
    }

    @Test
    public void testXmlStringToJsonObjectPositive() {
        String xmlString = "<person><name>John Doe</name><age>30</age></person>";
        JSONObject jsonObject = ConvertUtils.xmlStringToJsonObject(xmlString);

        Assert.assertNotNull(jsonObject);
        Assert.assertTrue(jsonObject.has("name"));
        Assert.assertTrue(jsonObject.has("age"));
        Assert.assertEquals(jsonObject.getString("name"), "John Doe");
        Assert.assertEquals(jsonObject.getInt("age"), 30);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testXmlStringToJsonObjectNullInput() {
        ConvertUtils.xmlStringToJsonObject(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testXmlStringToJsonObjectInvalidXml() {
        String invalidXmlString = "<person><name>John Doe</name><age>30"; // Missing closing tags

        ConvertUtils.xmlStringToJsonObject(invalidXmlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testXmlStringToJsonObjectBlankString() {
        String blankXmlString = "   ";

        ConvertUtils.xmlStringToJsonObject(blankXmlString);
    }

    @Test
    public void testXmlNodeToJsonObjectPositive() {
        Node xmlNode = createSampleXmlNode();
        JSONObject jsonObject = ConvertUtils.xmlNodeToJsonObject(xmlNode);

        Assert.assertNotNull(jsonObject);
        JSONObject personJsonObject = jsonObject.getJSONObject("person");
        Assert.assertTrue(personJsonObject.has("name"));
        Assert.assertTrue(personJsonObject.has("age"));
        Assert.assertEquals(personJsonObject.getString("name"), "John Doe");
        Assert.assertEquals(personJsonObject.getInt("age"), 30);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testXmlNodeToJsonObjectNullInput() {
        ConvertUtils.xmlNodeToJsonObject(null);
    }

    @Test
    public void testStringToNumberPositiveInteger() {
        String numberString = "123";
        Number result = ConvertUtils.stringToNumber(numberString);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Integer);
        Assert.assertEquals(result.intValue(), 123);
    }

    @Test
    public void testStringToNumberPositiveFloat() {
        String numberString = "123.45";
        Number result = ConvertUtils.stringToNumber(numberString);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Float);
        Assert.assertEquals(result.floatValue(), 123.45f);
    }

    @Test
    public void testStringToNumberPositiveDouble() {
        Number result = ConvertUtils.stringToNumber("3.4028236E38");

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof Double);
        Assert.assertEquals(result.doubleValue(), 3.4028236E38, 0.000001);
    }

    @Test
    public void testStringToNumberPositiveBigInteger() {
        String numberString = "12345678901234567890";
        Number result = ConvertUtils.stringToNumber(numberString);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof BigInteger);
        Assert.assertEquals(result, new BigInteger(numberString));
    }

    @Test
    public void testStringToNumberPositiveBigDecimal() {
        String numberString = "1.7976931348623157E309";
        Number result = ConvertUtils.stringToNumber(numberString);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof BigDecimal);
        Assert.assertEquals(result, new BigDecimal(numberString));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToNumberNullInput() {
        ConvertUtils.stringToNumber(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToNumberEmptyInput() {
        ConvertUtils.stringToNumber("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToNumberInvalidNumberString() {
        String invalidNumberString = "abc123";

        ConvertUtils.stringToNumber(invalidNumberString);
    }

    @Test
    public void testNormalizeLineSeparatorsPositiveWindowsLineSeparators() {
        String input = "Line1\r\nLine2\r\nLine3";
        String result = ConvertUtils.normalizeLineSeparators(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "Line1\nLine2\nLine3");
    }

    @Test
    public void testNormalizeLineSeparatorsPositiveUnixLineSeparators() {
        String input = "Line1\nLine2\nLine3";
        String result = ConvertUtils.normalizeLineSeparators(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "Line1\nLine2\nLine3");
    }

    @Test
    public void testNormalizeLineSeparatorsPositiveMixedLineSeparators() {
        String input = "Line1\r\nLine2\nLine3\r\n";
        String result = ConvertUtils.normalizeLineSeparators(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "Line1\nLine2\nLine3\n");
    }

    @Test
    public void testNormalizeLineSeparatorsPositiveEmptyString() {
        String input = "";
        String result = ConvertUtils.normalizeLineSeparators(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNormalizeLineSeparatorsNullInput() {
        ConvertUtils.normalizeLineSeparators(null);
    }

    @Test
    public void testNormalizeStringEncodingPositiveUtf8String() {
        String input = "This is a UTF-8 string.";
        String result = ConvertUtils.normalizeStringEncoding(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, input);
    }

    @Test
    public void testNormalizeStringEncodingPositiveSpecialCharacters() {
        String input = "Spécîål Çhåräçtérs";
        String result = ConvertUtils.normalizeStringEncoding(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, input);
    }

    @Test
    public void testNormalizeStringEncodingPositiveEmptyString() {
        String input = "";
        String result = ConvertUtils.normalizeStringEncoding(input);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, input);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNormalizeStringEncodingNullInput() {
        ConvertUtils.normalizeStringEncoding(null);
    }

    @Test
    public void testJsonObjectToXmlDocumentWithValidJson() {
        JSONObject person = new JSONObject();
        person.put("firstName", "John");
        person.put("firstName", "Doe");
        person.put("age", 33);
        JSONArray books = new JSONArray();
        books.put("Hary Potter");
        books.put("One Flew Over the Cuckoo's Nest");
        person.put("books", books);

        Document result = ConvertUtils.jsonObjectToXmlDocument(person);
        Assert.assertNotNull(result, "The XML document should not be null.");

        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <person>
                    <firstName>Doe</firstName>
                    <books>
                        <book>Hary Potter</book>
                        <book>One Flew Over the Cuckoo's Nest</book>
                    </books>
                    <age>33</age>
                </person>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testJsonObjectToXmlNodeWithValidJson() {
        JSONObject person = new JSONObject();
        person.put("firstName", "John");
        person.put("firstName", "Doe");
        person.put("age", 33);
        JSONArray bookmarks = new JSONArray();
        bookmarks.put(12);
        bookmarks.put(238);
        JSONArray books = new JSONArray();
        books.put("Hary Potter");
        books.put("One Flew Over the Cuckoo's Nest");
        person.put("books", books);
        books.put(bookmarks);

        Node result = ConvertUtils.jsonObjectToXmlDocument(person);
        Assert.assertNotNull(result, "The XML node should not be null.");

        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <person>
                    <firstName>Doe</firstName>
                    <books>
                        <book>Hary Potter</book>
                        <book>One Flew Over the Cuckoo's Nest</book>
                        <bookmarks>
                            <bookmark>12</bookmark>
                            <bookmark>238</bookmark>
                        </bookmarks>
                    </books>
                    <age>33</age>
                </person>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonObjectToXmlDocumentWithNullJson() {
        JSONObject jsonObject = null;
        ConvertUtils.jsonObjectToXmlDocument(jsonObject);
    }

    @Test
    public void testJsonObjectToXmlDocumentWithEmptyJson() {
        JSONObject empty = new JSONObject();
        Document result = ConvertUtils.jsonObjectToXmlDocument(empty);

        Assert.assertNotNull(result, "XML Document should be not null.");
        String expectedXmlString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <empty/>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedXmlString);
    }

    @Test
    public void testJsonArrayToXmlDocumentWithValidArrayWithRootName() {
        JSONArray examples = new JSONArray();
        JSONObject firstExample = new JSONObject();
        firstExample.put("name", "Item 1");
        firstExample.put("value", 100);
        examples.put(firstExample);

        JSONObject secondExample = new JSONObject();
        secondExample.put("name", "Item 2");
        secondExample.put("value", 200);
        examples.put(secondExample);

        Document xmlDocument = ConvertUtils.jsonArrayToXmlDocument(examples);
        Assert.assertNotNull(xmlDocument, "The XML document should not be null.");
        Assert.assertEquals(xmlDocument.getElementsByTagName("name").item(0).getTextContent(), "Item 1",
                "First element's 'name' should match.");
        Assert.assertEquals(xmlDocument.getElementsByTagName("name").item(1).getTextContent(), "Item 2",
                "Second element's 'name' should match.");

        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <examples>
                    <example>
                        <name>Item 1</name>
                        <value>100</value>
                    </example>
                    <example>
                        <name>Item 2</name>
                        <value>200</value>
                    </example>
                </examples>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(xmlDocument);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testJsonArrayToXmlDocumentWithValidArrayWithDefaultRootName() {
        JSONArray examples = new JSONArray();
        JSONObject firstExample = new JSONObject();
        firstExample.put("name", "Item 1");
        firstExample.put("value", 100);
        examples.put(firstExample);

        JSONObject secondExample = new JSONObject();
        secondExample.put("name", "Item 2");
        secondExample.put("value", 200);
        examples.put(secondExample);

        Document xmlDocument = ConvertUtils.jsonArrayToXmlDocument(examples);
        Assert.assertNotNull(xmlDocument, "The XML document should not be null.");
        Assert.assertEquals(xmlDocument.getElementsByTagName("name").item(0).getTextContent(), "Item 1",
                "First element's 'name' should match.");
        Assert.assertEquals(xmlDocument.getElementsByTagName("name").item(1).getTextContent(), "Item 2",
                "Second element's 'name' should match.");

        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <examples>
                    <example>
                        <name>Item 1</name>
                        <value>100</value>
                    </example>
                    <example>
                        <name>Item 2</name>
                        <value>200</value>
                    </example>
                </examples>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(xmlDocument);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testJsonArrayToXmlDocument() {
        JSONArray examples = new JSONArray();
        JSONObject firstExample = new JSONObject();
        firstExample.put("name", "Custom Item 1");
        firstExample.put("value", 500);
        examples.put(firstExample);

        Document xmlDocument = ConvertUtils.jsonArrayToXmlDocument(examples);
        Assert.assertNotNull(xmlDocument, "The XML document should not be null.");
        Assert.assertEquals(xmlDocument.getDocumentElement().getNodeName(), "examples", "Root element should match 'customRoot'.");
        Assert.assertEquals(xmlDocument.getElementsByTagName("name").item(0).getTextContent(), "Custom Item 1", "First element's 'name' should match.");
    }

    @Test
    public void testMapToXmlDocumentWithValidSimpleMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "John");
        map.put("age", "30");
        Document result = ConvertUtils.mapToXmlDocument(map);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getDocumentElement().getNodeName(), "map");
        Assert.assertEquals(result.getElementsByTagName("name").item(0).getTextContent(), "John");
        Assert.assertEquals(result.getElementsByTagName("age").item(0).getTextContent(), "30");
    }

    @Test
    public void testMapToXmlDocumentWithNestedMap() {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("city", "New York");
        addressMap.put("zip", "10001");
        map.put("name", "John");
        map.put("address", addressMap);
        Document result = ConvertUtils.mapToXmlDocument(map);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getDocumentElement().getNodeName(), "map");
        Assert.assertEquals(result.getElementsByTagName("city").item(0).getTextContent(), "New York");
        Assert.assertEquals(result.getElementsByTagName("zip").item(0).getTextContent(), "10001");
    }

    @Test
    public void testMapToXmlDocumentWithJsonObject() {
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "value");
        map.put("jsonObject", jsonObject);
        Document result = ConvertUtils.mapToXmlDocument(map);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getElementsByTagName("key").item(0).getTextContent(), "value");
    }

    @Test
    public void testMapToXmlDocument() {
        Map<String, Object> bigMap = new HashMap<>();
        bigMap.put("name", "John Doe");
        bigMap.put("age", 30);
        LocalDate dob = LocalDate.of(1987, 6, 14);
        bigMap.put("dob", dob);
        String[] array = {"one", "two", "three"};
        bigMap.put("array", array);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        bigMap.put("list", list);
        JSONObject jsonObject = new JSONObject("{\"make\":\"Honda\", \"model\":\"Civic\"}");
        bigMap.put("jsonObject", jsonObject);
        JSONArray jsonArray = new JSONArray("[11, 22, 33]");
        bigMap.put("jsonArray", jsonArray);
        Address address = new Address("New York", "USA");
        PojoClass pojoObject = createPojoObject();
        bigMap.put("pojo", pojoObject);
        PersonAdressRecord personWithAddress = new PersonAdressRecord("John Doe", 30, address);
        bigMap.put("record", personWithAddress);
        Document result = ConvertUtils.mapToXmlDocument(bigMap);

        Assert.assertNotNull(result);
        Document expected = ConvertUtils.stringToXmlDocument("""
               <?xml version="1.0" encoding="UTF-8"?>
               <bigMap>
                   <pojo>
                       <integerList>
                           <integerList>1</integerList>
                           <integerList>2</integerList>
                       </integerList>
                       <name>Some name</name>
                       <stringArray>
                           <stringArray>one</stringArray>
                           <stringArray>two</stringArray>
                           <stringArray>three</stringArray>
                       </stringArray>
                       <nestedPojoObject>
                           <date>1970-05-23</date>
                           <platform>windows</platform>
                       </nestedPojoObject>
                       <stringBooleanMap>
                           <true>true</true>
                           <false>false</false>
                       </stringBooleanMap>
                       <value>2</value>
                   </pojo>
                   <array>
                       <array>one</array>
                       <array>two</array>
                       <array>three</array>
                   </array>
                   <dob>1987-06-14</dob>
                   <record/>
                   <name>John Doe</name>
                   <jsonObject>
                       <model>Civic</model>
                       <make>Honda</make>
                   </jsonObject>
                   <list>
                       <list>1</list>
                       <list>2</list>
                   </list>
                   <age>30</age>
                   <jsonArray>
                       <jsonArray>11</jsonArray>
                       <jsonArray>22</jsonArray>
                       <jsonArray>33</jsonArray>
                   </jsonArray>
               </bigMap>
               """.stripIndent());
        SmartAssert.assertXmlNode(expected, result, false);
    }

    @Test
    public void testMapToXmlDocumentWithEmptyMap() {
        Map<String, Boolean> emptyMap = new HashMap<>();
        Document result = ConvertUtils.mapToXmlDocument(emptyMap);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <emptyMap/>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testMapToXmlDocumentWithNullMap() {
        ConvertUtils.mapToXmlDocument(null);
    }

    @Test
    public void testMapToXmlNodeWithJsonObject() {
        Map<String, Object> exampleMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "value");
        exampleMap.put("jsonObject", jsonObject);
        Document result = ConvertUtils.mapToXmlDocument(exampleMap);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <exampleMap>
                    <jsonObject>
                        <key>value</key>
                    </jsonObject>
                </exampleMap>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testMapToXmlNodeWithNullMap() {
        ConvertUtils.mapToXmlNode(null);
    }

    @Test
    public void testRecordToXmlDocumentWithValidRecord() {
        PersonRecord person = new PersonRecord("John Doe", 30);
        Document result = ConvertUtils.recordToXmlDocument(person);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <person>
                    <name>John Doe</name>
                    <age>30</age>
                </person>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testRecordToXmlDocumentWithNullRecord() {
        ConvertUtils.recordToXmlDocument(null);
    }

    @Test
    public void testRecordToXmlNodeWithValidRecord() {
        PersonRecord person = new PersonRecord("John Doe", 30);
        Node result = ConvertUtils.recordToXmlNode(person);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <person>
                    <name>John Doe</name>
                    <age>30</age>
                </person>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testPojoToXmlDocument() {
        PojoClass pojo = createPojoObject();
        Document result = ConvertUtils.pojoObjectToXmlDocument(pojo);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <pojo>
                    <integerList>
                        <integerList>1</integerList>
                        <integerList>2</integerList>
                    </integerList>
                    <name>Some name</name>
                    <stringArray>one</stringArray>
                    <stringArray>two</stringArray>
                    <stringArray>three</stringArray>
                    <stringBooleanMap>
                        <true>true</true>
                        <false>false</false>
                    </stringBooleanMap>
                    <nestedPojoObject>
                        <date>1970-05-23</date>
                        <platform>windows</platform>
                    </nestedPojoObject>
                    <value>2</value>
                </pojo>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testJsonObjectToXmlNodeWithValidSimpleJsonObject() {
        JSONObject person = new JSONObject();
        person.put("name", "John");
        person.put("age", 30);
        Node result = ConvertUtils.jsonObjectToXmlNode(person);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <person>
                    <name>John</name>
                    <age>30</age>
                </person>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testJsonObjectToXmlNodeWithEmptyJsonObject() {
        JSONObject emptyJson = new JSONObject();
        Node result = ConvertUtils.jsonObjectToXmlNode(emptyJson);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <emptyJson/>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test
    public void testJsonObjectToXmlNodeWithArrayInJsonObject() {
        JSONArray skiLls = new JSONArray();
        skiLls.put("Java");
        skiLls.put("XML");
        JSONObject experience = new JSONObject();
        experience.put("skills", skiLls);
        Node result = ConvertUtils.jsonObjectToXmlNode(experience);

        Assert.assertNotNull(result);
        String expectedString = """
                <?xml version="1.0" encoding="UTF-8"?>
                <experience>
                    <skills>
                        <skill>Java</skill>
                        <skill>XML</skill>
                    </skills>
                </experience>
                """.stripIndent();
        String resultString = ConvertUtils.xmlNodeToString(result);
        Assert.assertEquals(resultString, expectedString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonObjectToXmlNodeWithNullJasonObject() {
        ConvertUtils.jsonObjectToXmlNode(null);
    }
    private Node createSampleXmlNode() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Create the root element (for example, <person>)
            Element root = document.createElement("person");

            // Create child elements (e.g., <name>John Doe</name>)
            Element name = document.createElement("name");
            name.appendChild(document.createTextNode("John Doe"));

            Element age = document.createElement("age");
            age.appendChild(document.createTextNode("30"));

            // Append child elements to the root element
            root.appendChild(name);
            root.appendChild(age);

            // Append the root element to the document
            document.appendChild(root);

            // Return the root element (or any other part of the XML tree if needed)
            return root;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(e);
        }
    }

    public record PersonRecord(String name, int age) {
    }

    public record PersonAdressRecord(String name, int age, Address address) {}

    record Address(String city, String country) {}

    // A sample POJO for testing
    public static class Person {
        private final String firstName;

        private final String lastName;
        private final int age;

        public Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }

    private enum TestEnum {
        VALUE1, VALUE2
    }

    private static class CustomObject {
        private final String content;

        public CustomObject(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    private static PojoClass createPojoObject() {
        int[] intArray = {1, 2};
        List<Integer> arrayList = Arrays.stream(intArray)
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        Map<String, Boolean> stringBooleanMap = new HashMap<>();
        stringBooleanMap.put("true", true);
        stringBooleanMap.put("false", false);
        // Set nested Pojo
        NestedPojoClass nestedPojoObject = new NestedPojoClass();
        nestedPojoObject.setPlatform(Platform.WINDOWS);
        nestedPojoObject.setDate(LocalDate.of(1970, 5, 23));
        PojoClass pojoObject = new PojoClass();
        pojoObject.setName("Some name");
        pojoObject.setValue(2);
        pojoObject.setStringArray(new String[] {"one", "two", "three"});
        pojoObject.setIntegerList(arrayList);
        pojoObject.setStringBooleanMap(stringBooleanMap);
        pojoObject.setNestedPojoObject(nestedPojoObject);
        return pojoObject;
    }

    // Utility method for mocking a Node (replace with actual mock if needed)
    private Node createMockNode() {
        try {
            // Create a DocumentBuilderFactory and DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Create a new Document
            Document document = builder.newDocument();

            // Create the root element (node)
            Element rootElement = document.createElement("person");
            document.appendChild(rootElement);

            // Create child elements (nodes) for the root element
            Element nameElement = document.createElement("name");
            nameElement.appendChild(document.createTextNode("John Doe"));

            Element ageElement = document.createElement("age");
            ageElement.appendChild(document.createTextNode("30"));

            // Append the child elements to the root element
            rootElement.appendChild(nameElement);
            rootElement.appendChild(ageElement);
            return rootElement;
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        return null;
    }
}