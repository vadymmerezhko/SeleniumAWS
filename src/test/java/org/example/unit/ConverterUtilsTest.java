package org.example.unit;

import lombok.Getter;
import lombok.Setter;
import org.example.data.*;
import org.example.enums.ValueType;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
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

import static org.example.enums.ValueType.*;

public class ConverterUtilsTest {

    @Test
    public void testEscapeJavaScriptExcludeDoubleQuote() {
        String javaScript = "var x = \"John\\'s book\"; // Example code";
        String expected = "var x \\u003D \"John\\\\\\'s book\"; // Example code";
        String actual = ConverterUtils.escapeJavaScriptExceptDoubleQuote(javaScript);
        Assert.assertEquals(actual, expected, "JavaScript was not properly escaped " +
                "when excluding double quotes.");
    }

    @Test
    public void testEscapeJavaScriptExceptSingleQuotes() {
        String javaScript = "var y = \"John's book\"; // Example code";
        String expected = "var y \\u003D \\\"John's book\\\"; // Example code";
        String actual = ConverterUtils.escapeJavaScriptExceptSingleQuotes(javaScript);
        Assert.assertEquals(actual, expected,
                "JavaScript was not properly escaped when excluding single quotes.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeJavaScriptExcludeDoubleQuoteNullInput() {
        ConverterUtils.escapeJavaScriptExceptDoubleQuote(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeJavaScriptExceptSingleQuotesNullInput() {
        ConverterUtils.escapeJavaScriptExceptSingleQuotes(null);
    }

    @Test
    public void testStringToBooleanWithValidInputTrue() {
        Assert.assertTrue(ConverterUtils.stringToBoolean("true"));
    }

    @Test
    public void testStringToBooleanWithValidInputFalse() {
        Assert.assertFalse(ConverterUtils.stringToBoolean("false"));
    }

    @Test(expectedExceptions = SmartRuntimeException.class,
            expectedExceptionsMessageRegExp = "Invalid boolean format: .*")
    public void testStringToBooleanWithInvalidInput() {
        ConverterUtils.stringToBoolean("maybe");
    }

    @Test
    public void testConvertSimpleDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        String dateString = "2020-12-31";
        Date expectedDate = sdf.parse(dateString);
        Date actualDate = ConverterUtils.stringToSmartDate(dateString);
        Assert.assertEquals(actualDate, expectedDate);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testBlankDate() {
        String dateString = "";
        ConverterUtils.stringToSmartDate(dateString);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testNullDate() {
        ConverterUtils.stringToSmartDate(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testRandomStringAsDate() {
        ConverterUtils.stringToSmartDate("not a date");
    }

    @Test
    public void testValidJsonArray() {
        String validJsonArray = "[{\"name\":\"John\"}, {\"name\":\"Doe\"}]";
        JSONArray result = ConverterUtils.stringToJasonArray(validJsonArray);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.length(), 2, "There should be two elements.");
        Assert.assertEquals(result.getJSONObject(0).getString("name"), "John", "The first name should be John.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidJsonArray() {
        String invalidJsonArray = "[{name:\"John'}, {name:\"Doe\"}]";
        ConverterUtils.stringToJasonArray(invalidJsonArray);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testBlankJsonArray() {
        String invalidJsonArray = " ";
        ConverterUtils.stringToJasonArray(invalidJsonArray);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testNullJsonArray() {
        ConverterUtils.stringToJasonArray(null);
    }

    @Test
    public void testValidJsonObject() {
        String validJson = "{\"name\":\"John\", \"age\":30}";
        JSONObject result = ConverterUtils.stringToJasonObject(validJson);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getString("name"), "John", "The name should be John.");
        Assert.assertEquals(result.getInt("age"), 30, "The age should be 30.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidJsonObject() {
        String invalidJson = "{name:\"John\" age:30}";
        ConverterUtils.stringToJasonObject(invalidJson);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testBlankJsonObject() {
        String invalidJson = "  ";
        ConverterUtils.stringToJasonObject(invalidJson);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testNullJsonObject() {
        ConverterUtils.stringToJasonObject(null);
    }

    @Test
    public void testValidXmlObject() {
        String validXml = "<person><name>John</name></person>";
        Document result = ConverterUtils.stringToXmlDocument(validXml);
        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getElementsByTagName("name").item(0).getTextContent(), "John", "The name should be John.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testInvalidXmlObject() {
        String invalidXml = "<person><name>John</name>";
        ConverterUtils.stringToXmlDocument(invalidXml);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testBlankXmlObject() {
        ConverterUtils.stringToXmlDocument(" ");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testNullXmlObject() {
        ConverterUtils.stringToXmlDocument(null);
    }

    @Test
    public void testDateToStringValid() {
        Date now = new Date();
        String expectedFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(expectedFormat);
        String expectedDateString = sdf.format(now);
        String result = ConverterUtils.dateToString(now, expectedFormat);

        Assert.assertEquals(result, expectedDateString, "The formatted date string does not match expected output.");
    }

    @Test(expectedExceptions = SmartValidationException.class, expectedExceptionsMessageRegExp = ".*date.*")
    public void testDateToStringNullDate() {
        ConverterUtils.dateToString(null, "yyyy-MM-dd");
    }

    @Test(expectedExceptions = SmartValidationException.class, expectedExceptionsMessageRegExp = ".*dateFormat.*")
    public void testDateToStringBlankDateFormat() {
        ConverterUtils.dateToString(new Date(), " ");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDateToStringInvalidFormat() {
        ConverterUtils.dateToString(new Date(), "invalid-format");
    }

    @Test
    public void testLocalDateToString_Positive() {
        LocalDate localDate = LocalDate.of(2024, 8, 8);
        String dateFormat = "dd/MM/yyyy";
        String expectedDateString = "08/08/2024";

        String actualDateString = ConverterUtils.localDateToString(localDate, dateFormat);
        Assert.assertEquals(actualDateString, expectedDateString, "The date string should match the expected format.");
    }

    @Test
    public void testLocalDateToString_Positive_DifferentFormat() {
        LocalDate localDate = LocalDate.of(2024, 8, 8);
        String dateFormat = "yyyy-MM-dd";
        String expectedDateString = "2024-08-08";

        String actualDateString = ConverterUtils.localDateToString(localDate, dateFormat);
        Assert.assertEquals(actualDateString, expectedDateString, "The date string should match the expected format.");
    }

    @Test
    public void testEscapeCSVField_withSimpleString() {
        String input = "simple";
        String expected = "\"simple\"";
        String actual = ConverterUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped.");
    }

    @Test
    public void testEscapeCSVField_withComma() {
        String input = "value,with,comma";
        String expected = "\"value,with,comma\"";
        String actual = ConverterUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped when it contained commas.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeCSVField_Negative() {
        ConverterUtils.escapeCSVField(null);
    }

    @Test
    public void testEscapeCSVField_withDoubleQuotes() {
        String input = "value\"with\"quotes";
        String expected = "\"value\"\"with\"\"quotes\"";
        String actual = ConverterUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped when it contained double quotes.");
    }

    @Test
    public void testEscapeCSVField_withEmptyString() {
        String input = "";
        String expected = "\"\"";
        String actual = ConverterUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped for an empty string.");
    }

    @Test
    public void testEscapeCSVField_withSpecialCharacters() {
        String input = "value,with\nnew\rline\tand\tab\"quotes\"";
        String expected = "\"value,with\nnew\rline\tand\tab\"\"quotes\"\"\"";
        String actual = ConverterUtils.escapeCSVField(input);
        Assert.assertEquals(actual, expected, "The CSV field value was not correctly escaped when it contained special characters.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testEscapeCSVField_withNullInput() {
        ConverterUtils.escapeCSVField(null);
    }

    @Test
    public void testCsvFieldValueToString_withValidInput_noEscapes() {
        String input = "Sample text";
        String expected = "Sample text";
        String result = ConverterUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testCsvFieldValueToString_withEscapedQuotes() {
        String input = "\"\"\"Sample\"\" text\"\"\"";
        String expected = "\"Sample\" text\"";
        String result = ConverterUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testCsvFieldValueToString_withLeadingAndTrailingQuotes() {
        String input = "\"Sample text\"";
        String expected = "Sample text";
        String result = ConverterUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testCsvFieldValueToString_withEmptyString() {
        String input = "";
        String expected = "";
        String result = ConverterUtils.csvFieldValueToString(input);

        Assert.assertEquals(result, expected);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testCsvFieldValueToString_Negative() {
        ConverterUtils.csvFieldValueToString(null);
    }

    @Test
    public void testStringToSmartLocalDate_ValidDateString() {
        String dateString = "2023-08-31"; // Assuming this format matches the expected pattern in SmartDate
        String expectedFormat = "yyyy-MM-dd"; // The format should be adjusted based on your SmartDate logic
        SmartLocalDate smartLocalDate = ConverterUtils.stringToSmartLocalDate(dateString);

        Assert.assertNotNull(smartLocalDate, "SmartLocalDate should not be null");
        Assert.assertEquals(smartLocalDate.getLocalDate(), LocalDate.of(2023, 8, 31), "LocalDate value should match the input date");
        Assert.assertEquals(smartLocalDate.getFormat(), expectedFormat, "Format should match the expected format");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToSmartLocalDate_InvalidDateString() {
        String invalidDateString = "invalid-date";
        ConverterUtils.stringToSmartLocalDate(invalidDateString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToSmartLocalDate_NullDateString() {
        String nullDateString = null;
        ConverterUtils.stringToSmartLocalDate(nullDateString);
    }

    @Test
    public void testStringToSmartLocalDateTime_ValidString() {
        String dateTimeString = "2024-08-31T14:45:00"; // Example valid datetime string
        String expectedFormat = "yyyy-MM-dd'T'HH:mm:ss"; // Example format that SmartDate might return
        SmartLocalDateTime result = ConverterUtils.stringToSmartLocalDateTime(dateTimeString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getLocalDateTime(), LocalDateTime.parse(dateTimeString),
                "The LocalDateTime should match the parsed value.");
        Assert.assertEquals(result.getFormat(), expectedFormat,
                "The format should match the expected format.");
    }

    @Test
    public void testStringToSmartLocalTime_Positive() {
        String validTimeString = "10:30:45"; // Example time string in a valid format
        SmartLocalTime result = ConverterUtils.stringToSmartLocalTime(validTimeString);

        Assert.assertNotNull(result, "The result should not be null");
        Assert.assertEquals(result.getLocalTime(), LocalTime.of(10, 30, 45), "The LocalTime should match the expected value");
        Assert.assertEquals(result.getFormat(), "HH:mm:ss", "The format should match the expected value");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToSmartLocalTime_Negative() {
        String invalidTimeString = "invalid-time"; // Example of an invalid time string
        ConverterUtils.stringToSmartLocalTime(invalidTimeString);
    }

    @Test
    public void testStringToFile_ValidPath() {
        String validFilePath = "src/test/resources/testfile.txt";
        File expectedFile = new File(validFilePath);
        File result = ConverterUtils.stringToFile(validFilePath);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.getPath(), expectedFile.getPath());
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToFile_InvalidPath() {
        String invalidFilePath = "/invalid\0path"; // Null character is illegal in file paths

        ConverterUtils.stringToFile(invalidFilePath);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToFile_EmptyPath() {
        String emptyFilePath = "";

        ConverterUtils.stringToFile(emptyFilePath);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToFile_NullPath() {
        String nullFilePath = null;

        ConverterUtils.stringToFile(nullFilePath);
    }

    @Test
    public void testStringToURL_ValidURL() {
        String validUrlString = "https://www.example.com";
        URL result = ConverterUtils.stringToURL(validUrlString);

        Assert.assertNotNull(result, "URL object should not be null for a valid URL string.");
        Assert.assertEquals(result.toString(), validUrlString, "The URL object should match the input string.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToURL_InvalidURL() {
        String invalidUrlString = "htp://www[dot]example.com";

        ConverterUtils.stringToURL(invalidUrlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURL_BlankURL() {
        String blankUrlString = "";

        ConverterUtils.stringToURL(blankUrlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURL_NullURL() {
        String nullUrlString = null;

        ConverterUtils.stringToURL(nullUrlString);
    }

    @Test
    public void testStringToURI_ValidURI() {
        String validURIString = "https://example.com/resource";
        URI result = ConverterUtils.stringToURI(validURIString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.toString(), validURIString, "The URI object should match the URI string.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURI_EmptyURI() {
        ConverterUtils.stringToURI("");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToURI_InvalidURI() {
        String invalidURIString = "htp://[invalid_uri]";
        ConverterUtils.stringToURI(invalidURIString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToURI_NullURI() {
        String nullURIString = null;
        ConverterUtils.stringToURI(nullURIString);
    }

    @Test
    public void testStringToPath_ValidFilePath_ShouldReturnPath() {
        String validFilePath = "C:/Users/Example/Documents/file.txt";
        Path result = ConverterUtils.stringToPath(validFilePath);
        Path expected = Paths.get(validFilePath);

        Assert.assertNotNull(result, "Path object should not be null");
        Assert.assertEquals(result, expected, "The path should match the input file path");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToPath_InvalidFilePathFormat_ShouldThrowException() {
        String invalidFilePath = "Invalid/Path\\file?.txt";

        ConverterUtils.stringToPath(invalidFilePath);
    }

    @Test
    public void testStringToXmlNode_ValidXml() {
        String validXmlString = "<root><child>value</child></root>";
        Node result = ConverterUtils.stringToXmlNode(validXmlString);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getNodeName(), "root", "The root node name should be 'root'.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToXmlNode_InvalidXml() {
        String invalidXmlString = "<root><child>value</child>";  // Missing closing tag

        ConverterUtils.stringToXmlNode(invalidXmlString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToXmlNode_EmptyString() {
        String emptyString = "";

        ConverterUtils.stringToXmlNode(emptyString);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToXmlNode_NullString() {
        String nullString = null;

        ConverterUtils.stringToXmlNode(nullString);
    }

    @Test
    public void testLocalDateToDate_positive() {
        LocalDate localDate = LocalDate.of(2023, 8, 31);
        Date result = ConverterUtils.localDateToDate(localDate);

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(result.getTime(),
                Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime(),
                "The converted date should match the expected date.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateToDate_negative_nullLocalDate() {
        LocalDate localDate = null;

        ConverterUtils.localDateToDate(localDate);
    }

    @Test
    public void testLocalDateTimeToDate_Positive() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 8, 31, 14, 30, 0);
        Date expectedDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date actualDate = ConverterUtils.localDateTimeToDate(localDateTime);

        Assert.assertEquals(actualDate, expectedDate, "The converted date should match the expected date.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateTimeToDate_Negative_NullLocalDateTime() {
        ConverterUtils.localDateTimeToDate(null);
    }

    @Test
    public void testLocalTimeToDate_ValidTime() {
        LocalTime localTime = LocalTime.of(14, 30, 59);
        Date result = ConverterUtils.localTimeToDate(localTime);
        LocalTime expectedLocalTime = LocalTime.parse("14:30:59");
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), expectedLocalTime);
        Date expected = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Assert.assertNotNull(result, "The result should not be null.");
        Assert.assertEquals(expected, result, "The result date should equal to expected one.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalTimeToDate_NullLocalTime() {
        LocalTime localTime = null;

        ConverterUtils.localTimeToDate(localTime);
    }

    @Test
    public void testLocalDateToString_validInput() {
        LocalDate localDate = LocalDate.of(2023, 8, 31);
        String dateFormat = "yyyy-MM-dd";
        String result = ConverterUtils.localDateToString(localDate, dateFormat);

        Assert.assertEquals(result, "2023-08-31");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateToString_nullDate() {
        LocalDate localDate = null;
        String dateFormat = "yyyy-MM-dd";

        ConverterUtils.localDateToString(localDate, dateFormat);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLocalDateToString_invalidDateFormat() {
        LocalDate localDate = LocalDate.of(2023, 8, 31);
        String dateFormat = "invalid-format";

        ConverterUtils.localDateToString(localDate, dateFormat);
    }

    @Test
    public void testLocalDateTimeToStringPositive() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 8, 31, 14, 45, 30);
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        String expectedDateString = "2024-08-31 14:45:30";
        String actualDateString = ConverterUtils.localDateTimeToString(localDateTime, dateFormat);

        Assert.assertEquals(actualDateString, expectedDateString, "The date string should match the expected value.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateTimeToStringNullLocalDateTime() {
        LocalDateTime localDateTime = null;
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        ConverterUtils.localDateTimeToString(localDateTime, dateFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalDateTimeToStringBlankDateFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 8, 31, 14, 45, 30);
        String dateFormat = "   ";

        ConverterUtils.localDateTimeToString(localDateTime, dateFormat);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLocalDateTimeToStringInvalidDateFormat() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 8, 31, 14, 45, 30);
        String dateFormat = "invalid-format";

        ConverterUtils.localDateTimeToString(localDateTime, dateFormat);
    }

    @Test
    public void testLocalTimeToStringValidInput() {
        LocalTime localTime = LocalTime.of(14, 30, 15); // 2:30:15 PM
        String timeFormat = "HH:mm:ss";

        String result = ConverterUtils.localTimeToString(localTime, timeFormat);
        Assert.assertEquals(result, "14:30:15");
    }

    @Test
    public void testLocalTimeToStringWithDifferentFormat() {
        LocalTime localTime = LocalTime.of(9, 5); // 9:05 AM
        String timeFormat = "hh:mm a";

        String result = ConverterUtils.localTimeToString(localTime, timeFormat);
        Assert.assertEquals(result, "09:05 AM");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLocalTimeToStringWithInvalidFormat() {
        LocalTime localTime = LocalTime.of(14, 30); // 2:30 PM
        String timeFormat = "invalidFormat";

        ConverterUtils.localTimeToString(localTime, timeFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalTimeToStringWithNullLocalTime() {
        LocalTime localTime = null;
        String timeFormat = "HH:mm:ss";

        ConverterUtils.localTimeToString(localTime, timeFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testLocalTimeToStringWithBlankTimeFormat() {
        LocalTime localTime = LocalTime.of(14, 30); // 2:30 PM
        String timeFormat = "";

        ConverterUtils.localTimeToString(localTime, timeFormat);
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
            String xmlString = ConverterUtils.xmlNodeToString(doc);

            Assert.assertTrue(xmlString.contains("<root>") && xmlString.contains("<child>sample content</child>"),
                    "XML string should contain the correct root and child elements.");
        }
        catch (Exception e) {
            Assert.fail("Test failed due to unexpected exception: " + e.getMessage());
        }
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testXmlNodeToStringNegativeNullNode() {
        ConverterUtils.xmlNodeToString(null);
    }

    @Test
    public void testJsonObjectToStringValidJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", 123);
        String jsonString = ConverterUtils.jsonObjectToString(jsonObject);

        Assert.assertNotNull(jsonString, "The returned string should not be null.");
        Assert.assertTrue(jsonString.contains("\"key1\":\"value1\""), "The JSON string should contain the key-value pair.");
        Assert.assertTrue(jsonString.contains("\"key2\":123"), "The JSON string should contain the key-value pair.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonObjectToStringNullJsonObject() {
        ConverterUtils.jsonObjectToString(null);
    }

    @Test
    public void testJsonArrayToStringPositive() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value1");
        jsonArray.put(2);
        jsonArray.put(true);
        String result = ConverterUtils.jsonArrayToString(jsonArray);
        String expected = "[\n" +
                "    \"value1\",\n" +
                "    2,\n" +
                "    true\n" +
                "]";
        Assert.assertNotNull(result, "Result should not be null");
        Assert.assertEquals(result, expected, "The JSON string should match the expected format with 4-space indentation");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testJsonArrayToStringWithNullArray() {
        ConverterUtils.jsonArrayToString(null);
    }

    @Test
    public void testObjectToStringWithNull() {
        String result = ConverterUtils.objectToString(null);
        Assert.assertEquals(result, "NULL_VALUE");  // Assuming "NULL_VALUE" is the constant used for null
    }

    @Test
    public void testObjectToStringWithNaN() {
        String result = ConverterUtils.objectToString(Double.NaN);
        Assert.assertEquals(result, "NaN");  // Assuming "NAN" is the constant used for NaN
    }

    @Test
    public void testObjectToStringWithPositiveInfinity() {
        String result = ConverterUtils.objectToString(Double.POSITIVE_INFINITY);
        Assert.assertEquals(result, POSITIVE_INFINITY.toString());
    }

    @Test
    public void testObjectToStringWithNegativeInfinity() {
        String result = ConverterUtils.objectToString(Double.NEGATIVE_INFINITY);
        Assert.assertEquals(result, NEGATIVE_INFINITY.toString());
    }

    @Test
    public void testObjectToStringWithSmartValue() {
        SmartValue smartValue = new SmartValue("SmartValueContent");
        String result = ConverterUtils.objectToString(smartValue);
        Assert.assertEquals(result, smartValue.toString());
    }

    @Test
    public void testObjectToStringWithStringBuffer() {
        StringBuffer stringBuffer = new StringBuffer("StringBufferContent");
        String result = ConverterUtils.objectToString(stringBuffer);
        Assert.assertEquals(result, stringBuffer.toString());
    }

    @Test
    public void testObjectToStringWithEnum() {
        TestEnum testEnum = TestEnum.VALUE1;
        String result = ConverterUtils.objectToString(testEnum);
        Assert.assertEquals(result, testEnum.toString());
    }

    @Test
    public void testObjectToStringWithJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "value");
        String result = ConverterUtils.objectToString(jsonObject);
        Assert.assertEquals(result, jsonObject.toString(4));  // Assuming jsonObjectToString uses 4-space indentation
    }

    @Test
    public void testObjectToStringWithJSONArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("value1");
        jsonArray.put("value2");
        String result = ConverterUtils.objectToString(jsonArray);
        Assert.assertEquals(result, jsonArray.toString(4));  // Assuming jsonArrayToString uses 4-space indentation
    }

    @Test
    public void testObjectToStringWithXmlNode() {
        String validXmlString = "<root><child>value</child></root>";
        Node xmlNode = ConverterUtils.stringToXmlNode(validXmlString);
        String result = ConverterUtils.objectToString(xmlNode);
        String expected = ConverterUtils.xmlNodeToString(xmlNode);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testObjectToStringWithArray() {
        String[] array = {"element1", "element2"};
        String result = ConverterUtils.objectToString(array);
        Assert.assertEquals(result, "[element1, element2]");
    }

    @Test
    public void testObjectToStringWithCustomObjectWithToString() {
        CustomObject customObject = new CustomObject("CustomObjectContent");
        String result = ConverterUtils.objectToString(customObject);
        Assert.assertEquals(result, customObject.toString());
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToStringWithCustomObjectWithoutToString() {
        CustomObjectWithoutToString customObject = new CustomObjectWithoutToString();
        ConverterUtils.objectToString(customObject);
    }

    @Test
    public void testStringToEnumValuePositive() {
        // Positive Test Case: Valid enum value
        String enumClassName = "org.example.enums.ValueType";
        String stringValue = "STRING";
        ValueType result = ConverterUtils.stringToEnumValue(enumClassName, stringValue);

        Assert.assertEquals(result, ValueType.STRING, "Enum value should match the expected value.");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToEnumValueNegativeInvalidEnumValue() {
        // Negative Test Case: Invalid enum value
        String enumClassName = "org.example.enums.ValueType";
        String stringValue = "INVALID_VALUE";

        ConverterUtils.stringToEnumValue(enumClassName, stringValue);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testStringToEnumValueNegativeInvalidEnumClassName() {
        // Negative Test Case: Invalid enum class name
        String enumClassName = "InvalidEnum";
        String stringValue = "VALUE_ONE";

        ConverterUtils.stringToEnumValue(enumClassName, stringValue);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeNullEnumClassName() {
        // Negative Test Case: Null enum class name
        String enumClassName = null;
        String stringValue = "VALUE_ONE";

        ConverterUtils.stringToEnumValue(enumClassName, stringValue);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringToEnumValueNegativeNullStringValue() {
        // Negative Test Case: Null string value
        String enumClassName = "org.example.enums.ValueType";
        String stringValue = null;

        ConverterUtils.stringToEnumValue(enumClassName, stringValue);
    }

    @Test
    public void testObjectToObjectPositive() {
        // Arrange
        SmartType targetType = new SmartType(ValueType.STRING);
        Integer sourceObject = 123;
        String result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "123");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToObjectNullTargetType() {
        SmartType targetType = null;
        Integer sourceObject = 123;

        ConverterUtils.objectToObject(targetType, sourceObject);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testObjectToObjectNullSourceObject() {
        SmartType targetType = new SmartType(ValueType.STRING);
        Object sourceObject = null;

        ConverterUtils.objectToObject(targetType, sourceObject);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testObjectToObjectInvalidConversion() {
        // Arrange
        SmartType targetType = new SmartType(ValueType.INTEGER);
        String sourceObject = "InvalidNumber";

        // Act & Assert
        ConverterUtils.objectToObject(targetType, sourceObject);
    }

    @Test
    public void testObjectToObjectList() {
        SmartType targetType = new SmartType(ValueType.LIST, new SmartType(ValueType.STRING));
        List<String> sourceObject = Arrays.asList("one", "two", "three");
        List<String> result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectMap() {
        SmartType targetType = new SmartType(ValueType.MAP, ValueType.STRING, new SmartType(ValueType.INTEGER));
        Map<String, Integer> sourceObject = Map.of("one", 1, "two", 2);
        Map<String, Integer> result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectFile() {
        SmartType targetType = new SmartType(ValueType.FILE);
        File sourceObject = new File("test.txt");
        File result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectURL() throws Exception {
        SmartType targetType = new SmartType(ValueType.URL);
        URL sourceObject = new URL("http://example.com");
        URL result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectURI() throws Exception {
        SmartType targetType = new SmartType(ValueType.URI);
        URI sourceObject = new URI("http://example.com");
        URI result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectLocalDate() {
        SmartType targetType = new SmartType(ValueType.LOCAL_DATE);
        LocalDate sourceObject = LocalDate.of(2023, 8, 31);
        LocalDate result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectLocalDateTime() {
        SmartType targetType = new SmartType(ValueType.LOCAL_DATE_TIME);
        LocalDateTime sourceObject = LocalDateTime.of(2023, 8, 31, 12, 30);
        LocalDateTime result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectLocalTime() {
        SmartType targetType = new SmartType(ValueType.LOCAL_TIME);
        LocalTime sourceObject = LocalTime.of(12, 30, 15);
        LocalTime result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectDate() {
        SmartType targetType = new SmartType(ValueType.DATE);
        Date sourceObject = new Date("05/23/1970");
        Date result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartDate() {
        SmartType targetType = new SmartType(ValueType.SMART_DATE);
        SmartDate sourceObject = SmartDate.parseDate("1970-05-23");
        SmartDate result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartLocalDate() {
        SmartType targetType = new SmartType(ValueType.SMART_LOCAL_DATE);
        SmartLocalDate sourceObject = SmartLocalDate.parseLocalDate("1970-05-23");
        SmartLocalDate result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartLocalDateTime() {
        SmartType targetType = new SmartType(ValueType.SMART_LOCAL_DATE_TIME);
        SmartLocalDateTime sourceObject = SmartLocalDateTime.parseLocalDateTime("1970-05-23 12:45");
        SmartLocalDateTime result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectSmartLocalTime() {
        SmartType targetType = new SmartType(ValueType.SMART_LOCAL_TIME);
        SmartLocalTime sourceObject = SmartLocalTime.parseLocalTime("12:45:15");
        SmartLocalTime result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    @Test
    public void testObjectToObjectEnum() {
        SmartType targetType = new SmartType(ValueType.STRING.getClass().getName());
        String sourceObject = "STRING";
        ValueType result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, ValueType.STRING);
    }

    @Test
    public void testObjectToObjectClass() {
        Map<String, SmartType> fieldsMap = new HashMap<>();
        fieldsMap.put("name", new SmartType(STRING));
        fieldsMap.put("value", new SmartType(INTEGER));
        fieldsMap.put("stringArray", new SmartType(ARRAY, new SmartType(STRING)));
        SmartType targetType = new SmartType(PojoClass.class.getName(), fieldsMap);
        PojoClass sourceObject = new PojoClass();
        sourceObject.setName("Some name");
        sourceObject.setValue(2);
        sourceObject.setStringArray(new String[] {"one", "two", "three"});
        PojoClass result = ConverterUtils.objectToObject(targetType, sourceObject);

        Assert.assertNotNull(result);
        Assert.assertEquals(result, sourceObject);
    }

    private enum TestEnum {
        VALUE1, VALUE2
    }

    private class CustomObject {
        private final String content;

        public CustomObject(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    private class CustomObjectWithoutToString {
        // No toString() method implemented
    }
}