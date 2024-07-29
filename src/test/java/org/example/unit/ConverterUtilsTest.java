package org.example.unit;

import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

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
}