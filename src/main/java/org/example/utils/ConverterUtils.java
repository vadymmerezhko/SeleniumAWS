package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.example.exceptions.SmartRuntimeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Converter utils.
 */
@Slf4j
public class ConverterUtils {
    private static final String[] DATE_FORMATS = {
            // Simple Date formats:
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "dd MMM yyyy",
            "MMM dd, yyyy",
            "dd MMM yyyy",
            "MM-dd",
            "MM/dd",
            "dd/MM",
            "dd-MM",
            "dd MMM",
            "MMM dd",

            // Date and Time formats:
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd hh:mm:ss a", // With AM/PM.
            "MM/dd/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "dd MMM yyyy HH:mm:ss",
            "MMM dd, yyyy HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ssXXX",

            // Date and Time formats without year:
            "MM-dd HH:mm:ss",
            "MM-dd hh:mm:ss a", // With AM/PM.
            "MM/dd HH:mm:ss",
            "dd/MM HH:mm:ss",
            "dd-MM HH:mm:ss",
            "dd MMM HH:mm:ss",
            "MMM dd HH:mm:ss",
            "MM-dd'T'HH:mm:ssX",
            "MM-dd'T'HH:mm:ssZ",
            "MM-dd'T'HH:mm:ssXXX",

            // Date and Time with timezone:
            "yyyy-MM-dd HH:mm:ss Z",  // ISO 8601 timezone.
            "yyyy-MM-dd hh:mm:ss a Z",  // With AM/PM and timezone.
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",  // ISO 8601 with milliseconds.
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",  // ISO 8601 with timezone offset.
            "EEE, dd MMM yyyy HH:mm:ss z",  // RFC 822 date.

            // Time-only formats:
            "HH:mm",
            "hh:mm a", // With AM/PM.
            "HH:mm:ss",
            "hh:mm:ss a", // With AM/PM.
            "HH:mm:ss.SSS",
            "hh:mm:ss.SSS a", // With AM/PM.
            "HH:mm:ss Z",  // Time with timezone.
            "hh:mm:ss a Z",  // Time with timezone and AM/PM.
            "HH:mm:ss.SSS Z",  // Time with milliseconds and timezone.
            "HH:mm:ssXXX"  // Time with ISO 8601 timezone.
    };

    private ConverterUtils() {}

    /**
     * Escapes JavaScript string excluding double quotes.
     * @param javaScript The JavaScript string.
     * @return The escaped JavaScript string.
     */
    public static String escapeJavaScriptExceptDoubleQuote(String javaScript) {
        DataValidationUtils.validateNotNull(javaScript, "javaScript");

        String escapedJson = escapeJavaScriptExcept(javaScript, '"');
        log.debug("JavaScript {} after escape: {}.", javaScript, escapedJson);
        return escapedJson;
    }

    /**
     * Escapes JavaScript string excluding single quotes.
     * @param javaScript The JavaScript string.
     * @return The escaped JavaScript string.
     */
    public static String escapeJavaScriptExceptSingleQuotes(String javaScript) {
        DataValidationUtils.validateNotNull(javaScript, "javaScript");

        String escapedJson = escapeJavaScriptExcept(javaScript, '\'');
        log.debug("JavaScript {} after escape: {}.", javaScript, escapedJson);
        return escapedJson;
    }

    /**
     * Escapes JavaScript.
     * @param script The input to escape.
     * @return The escaped script.
     */
    public static String escapeJavaScript(String script) {
        DataValidationUtils.validateNotNull(script, "script");

        return escapeJavaScriptExcept(script, 'a');
    }

    /**
     * Converts string value to integer value.
     * @param string The string value.
     * @return The integer value.
     */
    public static  int stringToInteger(String string) {
        try {
            int result = Integer.parseInt(string);
            log.debug("{} string converted to integer {}.", string, result);
            return result;
        }
        catch (NumberFormatException  e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid integer format: %s.", string), e);
        }
    }

    /**
     * Converts string value to long value.
     * @param string The string value.
     * @return The long value.
     */
    public static  long stringToLong(String string) {
        try {
            long result = Long.parseLong(string);
            log.debug("{} string converted to long {}.", string, result);
            return result;
        }
        catch (NumberFormatException  e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid long format: %s.", string), e);
        }
    }

    /**
     * Converts string value to float value.
     * @param string The string value.
     * @return The float value.
     */
    public static  float stringToFloat(String string) {
        try {
            float result = Float.parseFloat(string);
            log.debug("{} string converted to double {}.", string, result);
            return result;
        }
        catch (NumberFormatException  e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid float format: %s.", string), e);
        }
    }

    /**
     * Converts string value to double value.
     * @param string The string value.
     * @return The double value.
     */
    public static  double stringToDouble(String string) {
        try {
            double result = Double.parseDouble(string);
            log.debug("{} string converted to double {}.", string, result);
            return result;
        }
        catch (NumberFormatException  e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid double format: %s.", string), e);
        }
    }

    /**
     * Converts string value to boolean value.
     * @param string The string value.
     * @return The boolean value.
     */
    public static  boolean stringToBoolean(String string) {
        boolean result;

        switch (string.trim()) {
            case "true" -> result = true;
            case "false" -> result = false;
            default -> throw new SmartRuntimeException(String.format(
                    "Invalid boolean format: %s.", string));
        }
        log.debug("{} string converted to boolean {}.", string, result);
        return result;
    }

    /**
     * Converts string value to date value.
     * @param string The string value.
     * @return The date value.
     */
    public static  Date stringToDate(String string) {
        try {
            Date result = DateUtils.parseDateStrictly(string, DATE_FORMATS);
            log.debug("{} string converted to double {}.", string, result);
            return result;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid date format: %s.", string), e);
        }
    }

    /**
     * Converts string value to JSON object value.
     * @param string The string value.
     * @return The JSON object value.
     */
    public static  JSONObject stringToJasonObject(String string) {
        try {
            JSONObject json = new JSONObject(string);
            log.debug("{} string converted to JSON object {}.", string, json);
            return json;
        }
        catch (JSONException | NullPointerException e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid JSON object format: %s.", string));
        }
    }

    /**
     * Converts string value to JSON array value.
     * @param string The string value.
     * @return The JSON array value.
     */
    public static  JSONArray stringToJasonArray(String string) {
        try {
            JSONArray json = new JSONArray(string);
            log.debug("{} string converted to JSON array {}.", string, json);
            return json;
        }
        catch (JSONException | NullPointerException e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid JSON array format: %s.", string));
        }
    }

    /**
     * Converts string value to XML object value.
     * @param xmlString The string value.
     * @return The XML object value.
     */
    public static Document stringToXmlObject(String xmlString) {
        DataValidationUtils.validateNotBlank(xmlString, "xmlString");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xml = builder.parse(new InputSource(new StringReader(xmlString)));
            log.debug("{} string converted to XML object {}.", xmlString, xml);
            return xml;
        }
        catch (ParserConfigurationException | SAXException | IOException | NullPointerException e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid XML object format: %s.", xmlString));
        }
    }

    /**
     * Converts local date object to date..
     * @param localDate The date.
     * @return The date.
     */
    public static Date localDateToDate(LocalDate localDate) {
        try {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            log.debug("LocalDate object {} to Date object: '{}'.",
                    localDate, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert LocalDate object %s to Date object.'",
                    localDate));
        }
    }

    /**
     * Converts local date object to string by date format.
     * @param localDate The date.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public static String localDateToString(LocalDate localDate, String dateFormat) {
        try {
            Date date = localDateToDate(localDate);
            String dataString = dateToString(date, dateFormat);
            log.debug("LocalDate object {} with format '{}' converted to date string: '{}'.",
                    localDate, dateFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert LocalDate object %s to string with date format '%s'",
                    localDate, dateFormat));
        }
    }

    /**
     * Converts date object to string by date format.
     * @param date The date.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public static String dateToString(Date date, String dateFormat) {
        DataValidationUtils.validateNotNull(date, "date");
        DataValidationUtils.validateNotBlank(dateFormat, "dateFormat");

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String dateString = formatter.format(date);
            log.debug("Date object {} with format '{}' converted to date string: '{}'.",
                    date, dateFormat, dateString);
            return dateString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert Date object %s to string with date format '%s'",
                    date, dateFormat));
        }
    }

    private static String escapeJavaScriptExcept(String input, char exceptChar) {
        if (input == null) {
            return null;
        }
        StringBuilder escapedString = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c == exceptChar) {
                escapedString.append(c);
                continue;
            }
            switch (c) {
                case '\'' -> escapedString.append("\\'");
                case '\"' -> escapedString.append("\\\"");
                case '\\' -> escapedString.append("\\\\");
                case '\n' -> escapedString.append("\\n");
                case '\r' -> escapedString.append("\\r");
                case '\t' -> escapedString.append("\\t");
                case '\b' -> escapedString.append("\\b");
                case '\f' -> escapedString.append("\\f");
                case '<' -> escapedString.append("\\u003C");
                case '>' -> escapedString.append("\\u003E");
                case '&' -> escapedString.append("\\u0026");
                case '=' -> escapedString.append("\\u003D");
                case '-' -> escapedString.append("\\u002D");
                default -> {
                    if (c < 32 || c > 126) {
                        escapedString.append(String.format("\\u%04x", (int) c));
                    } else {
                        escapedString.append(c);
                    }
                }
            }
        }
        return escapedString.toString();
    }
}
