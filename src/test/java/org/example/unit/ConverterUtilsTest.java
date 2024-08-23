package org.example.unit;

import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;

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
    public void testStringToIntegerWithValidInput() {
        Assert.assertEquals(ConverterUtils.stringToInteger("123"), 123);
    }

    @Test
    public void testStringToLongWithValidInput() {
        Assert.assertEquals(ConverterUtils.stringToLong("12345678910"), 12345678910L);
    }

    @Test
    public void testStringToFloatWithValidInput() {
        Assert.assertEquals(ConverterUtils.stringToFloat("123.45"), 123.45f);
    }

    @Test
    public void testStringToDoubleWithValidInput() {
        Assert.assertEquals(ConverterUtils.stringToDouble("123.456"), 123.456);
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
            expectedExceptionsMessageRegExp = "Invalid integer format: .*")
    public void testStringToIntegerWithInvalidInput() {
        ConverterUtils.stringToInteger("abc");
    }

    @Test(expectedExceptions = SmartRuntimeException.class,
            expectedExceptionsMessageRegExp = "Invalid long format: .*")
    public void testStringToLongWithInvalidInput() {
        ConverterUtils.stringToLong("abc");
    }

    @Test(expectedExceptions = SmartRuntimeException.class,
            expectedExceptionsMessageRegExp = "Invalid float format: .*")
    public void testStringToFloatWithInvalidInput() {
        ConverterUtils.stringToFloat("abc");
    }

    @Test(expectedExceptions = SmartRuntimeException.class,
            expectedExceptionsMessageRegExp = "Invalid double format: .*")
    public void testStringToDoubleWithInvalidInput() {
        ConverterUtils.stringToDouble("abc");
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
}