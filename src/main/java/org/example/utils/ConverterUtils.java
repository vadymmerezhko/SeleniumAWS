package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.exceptions.SmartRuntimeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.lang3.ObjectUtils.isArray;
import static org.example.constants.Settings.JSON_LAYOUT_SPACES;
import static org.example.constants.Settings.NULL_VALUE_STRING;
import static org.example.enums.ValueType.*;

/**
 * Converter utils.
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class ConverterUtils {
    private static final String ESCAPED_QUOTE = "\"\"";
    private static final String CLASS = "class";
    private static final String INSTANCE = "instance";
    private static final String[] DATE_FORMATS = {
            // Date and time with time zone:
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", // ISO 8601 with milliseconds and time zoe like +0200
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", // ISO 8601 with timezone offset
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",

            // Date and Time formats with seconds:
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd hh:mm:ss a", // With AM/PM.
            "MM/dd/yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "dd MMM yyyy HH:mm:ss",
            "dd MMMM yyyy HH:mm:ss",
            "MMM dd, yyyy HH:mm:ss",
            "MMMM dd, yyyy HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssX",

            // Date and Time formats without year and with seconds:
            "EEE, dd MMM yyyy HH:mm:ss z", // RFC 822 date
            "EEE, dd MMM yyyy HH:mm:ssz", // With time zone like PST
            "EEE, dd MMMM yyyy HH:mm:ss z", // With time zone like PST
            "EEE, dd MMMM yyyy HH:mm:ssz", // With time zone like PST
            "EEE, dd MMMM yyyy HH:mm:ss Z", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mm:ssZ", // With time zoe like +0200
            "MM/dd/yyyy HH:mm:ss Z", // With time zone like +0200
            "MM/dd/yyyy HH:mm:ssZ", // With time zone like +0200
            "dd-MM-yyyy HH:mm:ss Z", // With time zone like +0200
            "dd-MM-yyyy HH:mm:ssZ", // With time zone like +0200
            "yyyy-MM-dd HH:mm:ss Z",  // ISO 8601 timezone like +0200
            "yyyy-MM-dd HH:mm:ssZ", // With time zone like +0200
            "yyyy-MM-dd hh:mm:ss a Z",  // With AM/PM and timezone like +0200
            "yyyy-MM-dd hh:mm:ssa Z", // With time zone like +0200
            "yyyy-MM-dd hh:mm:ssaZ", // With time zone like +0200
            "yyyy-MM-dd'T'HH:mm:ss z", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mm:ssz", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mm:ssZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mm:ssX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mm:ssXXX", // With time zoe like +02:00
            "MM-dd'T'HH:mm:ssX",
            "MM-dd'T'HH:mm:ss z", // With time zoe like PST
            "MM-dd'T'HH:mm:ssz", // With time zoe like PST
            "MM-dd'T'HH:mm:ssZ", // With time zoe like +0200
            "MM-dd'T'HH:mm:ssX", // With time zoe like +02
            "MM-dd'T'HH:mm:ssXXX", // With time zoe like +02:00
            "MM-dd HH:mm:ss",
            "MM-dd hh:mm:ss a", // With AM/PM.
            "MM/dd HH:mm:ss",
            "dd-MM HH:mm:ss",
            "dd MMM HH:mm:ss",
            "MMM dd HH:mm:ss",
            "MMMM dd HH:mm:ss",

            // Date and Time with timezone and without seconds:
            "EEE, dd MMM yyyy HH:mm z", // With time zoe like PST
            "EEE, dd MMMM yyyy HH:mm z", // With time zoe like PST
            "EEE, dd MMM yyyy HH:mm Z", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mm Z", // With time zoe like +0200
            "EEE, dd MMM yyyy HH:mmz", // With time zoe like PST
            "EEE, dd MMMM yyyy HH:mmz", // With time zoe like PST
            "EEE, dd MMM yyyy HH:mmZ", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mmZ", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mmX", // With time zoe like +02
            "EEE, dd MMMM yyyy HH:mmXXX", // With time zoe like +02:00
            "MM/dd/yyyy HH:mm Z", // With time zoe like +0200
            "dd-MM-yyyy HH:mm Z", // With time zoe like +0200
            "yyyy-MM-dd HH:mm Z", // With time zoe like +0200
            "yyyy-MM-dd hh:mm a Z", // With time zoe like +0200
            "yyyy-MM-dd hh:mma Z", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mmXXX", // With time zoe like +02:00
            "MM/dd/yyyy HH:mmZ", // With time zoe like +0200
            "dd-MM-yyyy HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd hh:mm aZ", // With time zoe like +0200
            "yyyy-MM-dd hh:mmaZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mmXXX", // With time zoe like +02:00

            // Date and time with time zone:
            "yyyy-MM-dd'T'HH:mm z", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mmz", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mmXXX", // With time zoe like +02:00

            // Date and Time formats without year and without seconds:
            "MM-dd'T'HH:mmX",
            "MM-dd'T'HH:mm z", // With time zoe like PST
            "MM-dd'T'HH:mmz", // With time zoe like PST
            "MM-dd'T'HH:mmZ", // With time zoe like +0200
            "MM-dd'T'HH:mmX", // With time zoe like +02
            "MM-dd'T'HH:mmXXX", // With time zoe like +02:00
            "MM-dd HH:mm",
            "MM-dd hh:mma", // With AM/PM.
            "MM-dd hh:mm a", // With AM/PM.
            "MM/dd HH:mm",
            "dd-MM HH:mm",
            "dd MMM HH:mm",
            "MMM dd HH:mm",
            "MMMM dd HH:mm",

            // Date and Time formats without seconds:
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd hh:mm a", // With AM/PM.
            "MM/dd/yyyy HH:mm",
            "dd-MM-yyyy HH:mm",
            "yyyy/MM/dd HH:mm",
            "dd MMM yyyy HH:mm",
            "dd MMMM yyyy HH:mm",
            "MMM dd, yyyy HH:mm",
            "MMMM dd, yyyy HH:mm",

            // Simple Date formats:
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd-MM-yyyy",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "dd MMM yyyy",
            "dd MMMM yyyy",
            "MMM dd, yyyy",
            "MMMM dd, yyyy",
            "MM-dd",
            "MM/dd",
            "dd-MM",
            "dd MMM",
            "dd MMMM",
            "MMM dd",
            "MMMM dd",
            "MMM",
            "MMMM",
            "E",
            "EEEE",

            // Time-only formats:
            "HH:mm:ss.SSSZ",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSS Z",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSS z",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSSz",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSSX",  // Time with milliseconds and timezone like +02.
            "HH:mm:ss.SSS X",  // Time with milliseconds and timezone like +02.
            "HH:mm:ss.SSSXXX",  // Time with milliseconds and timezone like +02:00.
            "HH:mm:ss.SSS XXX",  // Time with milliseconds and timezone like +02:00.
            "HH:mm:ss.SSS Z",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSSZ",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSS z",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSSz",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSS", // With milliseconds
            "hh:mm:ss.SSS a", // With AM/PM and milliseconds.
            "hh:mm:ss.SSSa", // With AM/PM and milliseconds.
            "HH:mm:ssZ",  // Time with timezone like +0200.
            "HH:mm:ss Z",  // Time with timezone like +0200.
            "hh:mm:ss a Z",  // Time with timezone and AM/PM.
            "hh:mm:ssa Z",  // Time with timezone and AM/PM.
            "hh:mm:ssaZ",  // Time with timezone and AM/PM.
            "hh:mm:ssa z",  // Time with timezone like PST and AM/PM.
            "HH:mm:ssX",  // Time with timezone like +02.
            "HH:mm:ss X",  // Time with timezone like +02
            "hh:mm:ss a X",  // Time with timezone like +02 and AM/PM
            "hh:mm:ssa X",  // Time with timezone like +02 and AM/PM
            "hh:mm:ssaX",  // Time with timezone like +02 and AM/PM
            "HH:mm:ssXXX",  // Time with timezone like +02.00
            "HH:mm:ss XXX",  // Time with timezone like +02.00
            "hh:mm:ss a XXX",  // Time with timezone like +02.00 and AM/PM
            "hh:mm:ssa XXX",  // Time with timezone like +02.00 and AM/PM
            "hh:mm:ssaXXX",  // Time with timezone like +02.00 and AM/PM
            "HH:mm:ss Z",  // Time with timezone like +0200.
            "HH:mm:ssZ",  // Time with timezone like +0200.
            "HH:mm:ss z",  // Time with timezone like PST.
            "HH:mm:ssz",  // Time with timezone like PST.
            "hh:mm:ss a Z",  // Time with timezone like +0200 and AM/PM.
            "hh:mm:ssa Z",  // Time with timezone like +0200 and AM/PM.
            "hh:mm:ss a z",  // Time with timezone like PST and AM/PM.
            "hh:mm:ssa z",  // Time with timezone like PST and AM/PM.
            "HH:mm:ss",
            "hh:mm:ss a", // With AM/PM.
            "hh:mm:ssa", // With AM/PM.

            // Simple time:
            "hh:mm a", // With AM/PM.
            "hh:mma", // With AM/PM.
            "HH:mm",

            // Hour only time:
            "HH a", // Hour with AM/PM like 02PM
            "HHa", // Hour with AM/PM like 02PM
            "hh a", // Hour with AM/PM like 2 PM
            "hha", // Hour with AM/PM like 2PM
    };

    private ConverterUtils() {
    }

    /**
     * Escapes JavaScript string excluding double quotes.
     *
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
     *
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
     *
     * @param script The input to escape.
     * @return The escaped script.
     */
    public static String escapeJavaScript(String script) {
        DataValidationUtils.validateNotNull(script, "script");

        return escapeJavaScriptExcept(script, 'a');
    }

    /**
     * Convert string to CSV field value.
     * Escapes CSV field value.
     *
     * @param string The string value.
     * @return The escaped CSV field value.
     */
    public static String escapeCSVField(String string) {
        DataValidationUtils.validateNotNull(string, "string");
        // Escape " with ""
        String csvFieldValue = string.replace("\"", ESCAPED_QUOTE);
        // Enclose the field in double quotes
        csvFieldValue = String.format("\"%s\"", csvFieldValue);
        log.debug("""
                        String converted to CSV field value.
                        CSV:
                        {}
                        String:
                        {}
                        """.stripIndent(),
                csvFieldValue, string);
        return csvFieldValue;
    }

    /**
     * Converts CSV field to string.
     * Replaces CSV string double quotes escape.
     *
     * @param csvString The CSV string.
     * @return The string without CSV escape.
     */
    public static String csvFieldValueToString(String csvString) {
        DataValidationUtils.validateNotNull(csvString, "csvScript");
        String string = csvString.replace(ESCAPED_QUOTE, "\"");
        string = trimQuotes(string);
        log.debug("""
                        CSV field value converted to normal string.
                        CSV;
                        String:
                        {}
                        """.stripIndent(),
                csvString, string);
        return string;
    }

    /**
     * Converts string value to boolean value.
     *
     * @param string The string value.
     * @return The boolean value.
     */
    public static boolean stringToBoolean(String string) {
        DataValidationUtils.validateNotBlank(string, "string");
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
     *
     * @param dateString The string value.
     * @return The date value or null if cannot convert.
     */
    public static SmartDate stringToSmartDate(String dateString) {
        DataValidationUtils.validateNotBlank(dateString, "dateString");

        try {
            Date date = null;
            String dateFormat = null;
            // Replace day suffixes
            dateString = removeOrdinalSuffix(dateString);
            // Replace double spaces with one space
            dateString = dateString.replaceAll("\\s{2,}", " ");

            for (String format : DATE_FORMATS) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    date = formatter.parse(dateString);
                    dateFormat = format;
                    break;
                }
                catch (ParseException e) {
                    // Ignore exception
                }
            }
            if (date == null) {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart date.
                    String: {}
                    """.stripIndent(),
                        dateString));
            }
            SmartDate smartDate = new SmartDate(date, dateFormat);
            log.debug("""
                String converted to smart date.
                String: {}
                Smart local date: {}
                """.stripIndent(),
                smartDate);
            return smartDate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart date.
                    String: {}
                    """.stripIndent(),
                    dateString), e);
        }
    }

    /**
     * Converts string value to smart local date value.
     * @param dateString The date string value.
     * @return The smart local date value.
     */
    public static SmartLocalDate stringToSmartLocalDate(String dateString) {
        try {
            SmartDate smartDate = stringToSmartDate(dateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            SmartLocalDate smartLocalDate =
                    new SmartLocalDate(localDate, smartDate.getFormat());
            log.debug("""
                String converted to smart local date.
                String: {}
                Smart local date: {}
                """.stripIndent(),
            dateString, smartLocalDate);
            return smartLocalDate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart local date.
                    String: %s
                    """.stripIndent(),
                    dateString), e);
        }
    }

    /**
     * Converts string value to smart local date time value.
     * @param dateTimeString The date time string value.
     * @return The smart local date time value.
     */
    public static SmartLocalDateTime stringToSmartLocalDateTime(String dateTimeString) {
        try {
            SmartDate smartDate = stringToSmartDate(dateTimeString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
            SmartLocalDateTime smartLocalDateTime =
                    new SmartLocalDateTime(localDateTime, smartDate.getFormat());
            log.debug("""
                String converted to smart local date time.
                String: {}
                Smart local date time: {}
                """.stripIndent(),
                    dateTimeString, smartLocalDateTime);
            return smartLocalDateTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart local date time.
                    String: %s
                    """.stripIndent(),
                    dateTimeString), e);
        }
    }

    /**
     * Converts string value to smart local time value.
     * @param timeString The time string value.
     * @return The smart local time value.
     */
    public static SmartLocalTime stringToSmartLocalTime(String timeString) {
        try {
            SmartDate smartDate = stringToSmartDate(timeString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
            LocalTime localTime = LocalTime.parse(timeString, formatter);
            SmartLocalTime smartLocalTime =
                    new SmartLocalTime(localTime, smartDate.getFormat());
            log.debug("""
               Cannot convert string to smart local time.
                String: {}
                Smart local time: {}
                """.stripIndent(),
                    timeString, smartLocalTime);
            return smartLocalTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    String converted to smart local time.
                    String: {}
                    """.stripIndent(),
                    timeString), e);
        }
    }

    /**
     * Converts string value to JSON object value.
     *
     * @param string The string value.
     * @return The JSON object value.
     */
    public static JSONObject stringToJasonObject(String string) {
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONObject json = new JSONObject(string);
            log.debug("{} string converted to JSON object {}.", string, json);
            return json;
        } catch (JSONException | NullPointerException e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid JSON object format: %s.", string));
        }
    }

    /**
     * Converts string value to JSON array value.
     *
     * @param string The string value.
     * @return The JSON array value.
     */
    public static JSONArray stringToJasonArray(String string) {
        DataValidationUtils.validateNotBlank(string, "string");

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
     * Converts string value to XML document value.
     *
     * @param xmlString The string value.
     * @return The XML document value.
     */
    public static Document stringToXmlDocument(String xmlString) {
        DataValidationUtils.validateNotBlank(xmlString, "xmlString");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            log.debug("{} string converted to XML document:\n{}.", xmlString, document);
            return document;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to XML document:\n%s.", xmlString));
        }
    }

    /**
     * Converts file path string to file object.
     *
     * @param filePath The file path string;
     * @return The file;
     */
    public static File stringToFile(String filePath) {
        DataValidationUtils.validateFilePathFormat(filePath, "filePath");

        try {
            File file = new File(filePath);
            log.debug("""
                    File path converted to File object.
                    File path:
                    {}
                    File object:
                    {}
                    """.stripIndent(),
                    filePath, file);
            return file;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to file object: %s.", filePath));
        }
    }

    /**
     * Converts URL string to URL object.
     * @param urlString The file path string;
     * @return The URL object;
     */
    public static URL stringToURL(String urlString) {
        DataValidationUtils.validateNotBlank(urlString, "urlString");

        try {
            URL url = new URL(urlString);
            log.debug("""
                    URL string converted to URL object.
                    URL string:
                    {}
                    URL object:
                    {}
                    """.stripIndent(),
                    urlString, url);
            return url;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert URL string to URL object:\n%s.", urlString));
        }
    }

    /**
     * Converts URI string to URI object.
     * @param uriString The URI string;
     * @return The URI object;
     */
    public static URI stringToURI(String uriString) {
        DataValidationUtils.validateNotBlank(uriString, "urlString");

        try {
            URI uri = new URI(uriString);
            log.debug("""
                    URI string converted to URI object.
                    URI string:
                    {}
                    URI object:
                    {}
                    """.stripIndent(),
                    uriString, uri);
            return uri;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert URI string to URI object:\n%s.", uriString));
        }
    }

    /**
     * Converts file path string to path object.
     * @param filePath The file path string;
     * @return The file;
     */
    public static Path stringToPath(String filePath) {
        DataValidationUtils.validateFilePathFormat(filePath, "filePath");

        try {
            Path path = Paths.get(filePath);
            log.debug("File path string '{}' converted to path object: {}",
                    filePath, path);
            return path;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to path object: %s.", filePath));
        }
    }

    /**
     * Converts string value to XML node value.
     * @param xmlString The string value.
     * @return The XML node value.
     */
    public static Node stringToXmlNode(String xmlString) {
        DataValidationUtils.validateNotBlank(xmlString, "xmlString");

        try {
            Node node = stringToXmlDocument(xmlString).getDocumentElement();
            log.debug("{} string converted to XML node:\n{}.", xmlString, node);
            return node;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to XML Node:\n%s.", xmlString));
        }
    }

    /**
     * Converts local date object to date.
     * @param localDate The date.
     * @return The date.
     */
    public static Date localDateToDate(LocalDate localDate) {
        DataValidationUtils.validateNotNull(localDate, "localDate");

        try {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            log.debug("Local date {} converted to date: '{}'.",
                    localDate, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date %s to date.'",
                    localDate), e);
        }
    }

    /**
     * Converts local date time object to date.
     * @param localDateTime The date.
     * @return The date.
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        DataValidationUtils.validateNotNull(localDateTime, "LocalDateTime");

        try {
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            log.debug("Local date time {} converted to date: '{}'.",
                    localDateTime, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date %s to date.'",
                    localDateTime), e);
        }
    }

    /**
     * Converts local time object to date.
     * @param localTime The local time.
     * @return The date.
     */
    public static Date localTimeToDate(LocalTime localTime) {
        DataValidationUtils.validateNotNull(localTime, "localTime");

        try {
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            log.debug("Local time {} converted to date: '{}'.",
                    localTime, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local time %s to date.'",
                    localTime));
        }
    }

    /**
     * Converts local date object to string by date format.
     * @param localDate  The local date.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public static String localDateToString(LocalDate localDate, String dateFormat) {
        DataValidationUtils.validateNotNull(localDate, "localDate");
        DataValidationUtils.validateNotBlank(dateFormat, dateFormat);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            String dataString = localDate.format(formatter);
            log.debug("Local date object {} with format '{}' converted to date string: '{}'.",
                    localDate, dateFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date object %s to string with date format '%s'",
                    localDate, dateFormat));
        }
    }

    /**
     * Converts local date time object to string by date time format.
     * @param localDateTime The local date time.
     * @param dateTimeFormat The date time format.
     * @return The date time string.
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String dateTimeFormat) {
        DataValidationUtils.validateNotNull(localDateTime, "localDate");
        DataValidationUtils.validateNotBlank(dateTimeFormat, dateTimeFormat);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
            String dataString = localDateTime.format(formatter);
            log.debug("Local date time object {} with format '{}' converted to date string: '{}'.",
                    localDateTime, dateTimeFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date time object %s to string with date format '%s'",
                    localDateTime, dateTimeFormat));
        }
    }

    /**
     * Converts local time object to string by time format.
     * @param localTime  The date.
     * @param timeFormat The time format.
     * @return The time string.
     */
    public static String localTimeToString(LocalTime localTime, String timeFormat) {
        DataValidationUtils.validateNotNull(localTime, "localTime");
        DataValidationUtils.validateNotBlank(timeFormat, timeFormat);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
            String dataString = localTime.format(formatter);
            log.debug("Local time object {} with format '{}' converted to date string: '{}'.",
                    localTime, timeFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local time object %s to string with date format '%s'",
                    localTime, timeFormat));
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

    /**
     * Converts XML node object to string.
     * @param xml The XML document.
     * @return The XML string.
     */
    public static String xmlNodeToString(Node xml) {
        DataValidationUtils.validateNotNull(xml, "xml");

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Set the output properties to format the XML with a 4-space indentation
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource domSource = new DOMSource(xml);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);

            String xmlString = writer.toString();
            log.debug("XML node converted to XML string: {}.", xmlString);
            return xmlString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert XML document object to string:\n%s",
                    xml), e);
        }
    }

    /**
     * Converts JSON object to string.
     * @param jsonObject The XML document.
     * @return The JSON array string.
     */
    public static String jsonObjectToString(JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            String jsonString = jsonObject.toString(JSON_LAYOUT_SPACES);
            log.debug("JSON object converted to string: {}.", jsonString);
            return jsonString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert JSON object to string:\n%s",
                    jsonObject), e);
        }
    }

    /**
     * Converts JSON array to string.
     * @param jsonArray The JSON array.
     * @return The JSON array string.
     */
    public static String jsonArrayToString(JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            return jsonArray.toString(JSON_LAYOUT_SPACES);
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot convert JSON array to string", e);
        }
    }

    /**
     * Converts Object to string.
     * Throws exception if Object has default toString() method.
     * @param object The object.
     * @return The object string.
     */
    public static <T> String objectToString(Object object) {
        String string = null;

        try {
            if (object == null) {
                string = NULL_VALUE_STRING;
            }
            else if (isNaN(object)) {
                string = NAN.toString();
            }
            else if (isFloatPositiveInfinite(object) || isDoublePositiveInfinite(object)) {
                string = POSITIVE_INFINITY.toString();
            }
            else if (isFloatNegativeInfinite(object) || isDoubleNegativeInfinite(object)) {
                string = NEGATIVE_INFINITY.toString();
            }
            else if (object instanceof SmartValue smartValue) {
                string = smartValue.toString();
            }
            else if (object instanceof StringBuffer stringBuffer) {
                string = stringBuffer.toString();
            }
            else if (object instanceof String stringValue) {
                string = stringValue;
            }
            else if (object instanceof Enum enumValue) {
                string = String.valueOf(enumValue);
            }
            if (string != null) {
                log.debug("Object {} converted to string: {}", object, string);
                return string;
            }
            String clasName = object.getClass().getName();

            if (object instanceof JSONObject jsonObject) {
                string = ConverterUtils.jsonObjectToString(jsonObject);
            }
            else if (object instanceof JSONArray jsonArray) {
                string = ConverterUtils.jsonArrayToString(jsonArray);
            }
            else if (object instanceof Node node) {
                string = ConverterUtils.xmlNodeToString(node);
            }
            else if (object instanceof List list) {
                JSONArray jsonArray = listToJsonArray(list);
                string = jsonArrayToString(jsonArray);
            }
            else if (object instanceof Map map) {
                JSONObject jsonObject = mapToJSONObject(map);
                string = jsonObjectToString(jsonObject);
            }
            else if (isArray(object)) {
                string = arrayToString((T[]) object);
            }
            else if (object.toString().startsWith(String.format("%s@", clasName))) {
                throw new SmartRuntimeException("You need to override default toString() method of %s class.");
            }
            else {
                string = String.valueOf(object);
            }
            if (string == null) {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert object to string.
                        Object:
                        {}
                        """.stripIndent(), object));
            }
            log.debug("""
                            "Object converted to string.
                            Object:
                            {}
                            String:
                            {}
                            """.stripIndent(),
                    object, string);
            return string;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to string.
                    Object:
                    %s
                    """.stripIndent(),
                    object), e);
        }
    }

    /**
     * Converts string to enum value.
     * @param type enum type.
     * @param enumName The string.
     * @return The enum value.
     * @param <T> The enum type.
     */
    public static <T extends Enum<T>> T stringToEnumValue(SmartType type, String enumName) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(enumName, "enumName");

        try {
            Class<T> enumClass = (Class<T>) type.getObjectClass();
            T enumValue = Enum.valueOf(enumClass, enumName);
            log.debug("""
                    String converted to enum value by enum class name.
                    Enum class name:
                    {}
                    Enum name:
                    {}
                    """.stripIndent(),
                    enumClass.getName(), enumName);
            return enumValue;
        }
        catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert string to enum value.
                    String: %s
                    """.stripIndent(),
                    enumName), e);
        }
    }

    /**
     * Converts object to object or throws exception if cannot covert.
     * @param targetType   The target object type.
     * @param sourceObject The object to convert.
     * @return The result object.
     */
    public static <T> T objectToObject(SmartType targetType, Object sourceObject) {
        DataValidationUtils.validateNotNull(targetType, "targetType");
        DataValidationUtils.validateNotNull(sourceObject, "sourceObject");
        T targetObject;

        try {
            String stringValue;

            if (targetType.getFieldTypesMap() != null) {
                stringValue = classObjectToString(sourceObject);
            }
            else if (List.class.isAssignableFrom(targetType.getObjectClass())) {
                JSONArray jsonArray = collectionToJsonArray((Collection<?>) sourceObject);
                stringValue = jsonArrayToString(jsonArray);
            }
            else if (Map.class.isAssignableFrom(targetType.getObjectClass())) {
                JSONObject jsonObject = mapToJSONObject((Map<?,?>) sourceObject);
                stringValue = jsonObjectToString(jsonObject);
            }
            else if (targetType.getObjectClass().isArray()) {
                JSONArray jsonArray = objectToObject(new SmartType(JSONArray.class), sourceObject);
                stringValue = jsonArrayToString(jsonArray);
            }
            else {
                stringValue = objectToString(sourceObject);
            }
            targetObject = (T) stringToObject(targetType, stringValue);
            log.debug("""
                            Source object converted to target object.
                            Source:
                            class: {}
                            value:
                            {}
                            Target:
                            class: {}
                            value:
                            {}
                            """.stripIndent(),
                    sourceObject.getClass().getName(), sourceObject,
                    targetObject.getClass().getName(), targetObject);
            return targetObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert source object to target object.
                            Source:
                            Class: %s
                            Source object:
                            %s
                            Target type:
                            %s
                            """.stripIndent(),
                    sourceObject.getClass().getName(),
                    sourceObject, targetType), e);
        }
    }

    /**
     * Converts Java POJO class object to string.
     *
     * @param <T> The object type.
     * @return The string.
     */
    public static <T> String classObjectToString(T object) {
        try {
            JSONObject jsonObject = objectToJsonObject(object);
            String string = jsonObjectToString(jsonObject);
            log.debug("""
                            Java POJO class object  converted to string.
                            Object:
                            {}
                            String:
                            {}
                            """.stripIndent(),
                    string, object);
            return string;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert class object to string.
                            Object:
                            %s
                            """.stripIndent(),
                    object), e);
        }
    }

    /**
     * Coverts string to Java POJO class object.
     *
     * @param string The string.
     * @param type   The class type.
     * @return The Java POJO class object.
     */
    public static <T> T stringToPojoClassObject(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONObject jsonClass = new JSONObject(string);
            String className = type.getObjectClass().getName();
            // Load the class by name
            Class<?> customClass = Class.forName(className);
            // Create an instance of the class using the default constructor
            Object object = customClass.getDeclaredConstructor().newInstance();
            Map<String, SmartType> fieldTypesMap = type.getFieldTypesMap();

            // Iterate over the fields of the class
            for (Field field : customClass.getDeclaredFields()) {
                // Make private fields accessible
                field.setAccessible(true);
                String fieldName = field.getName();

                // Set the field value if the JSON has the key
                if (jsonClass.has(fieldName)) {
                    Object jsonValue = jsonClass.get(fieldName);
                    SmartType fieldType = fieldTypesMap.get(fieldName);
                    Object targetObject = objectToObject(fieldType, jsonValue);
                    field.set(object, targetObject);
                    continue;
                }
                throw new SmartRuntimeException(String.format("""
                                Class field is not present.
                                Class name: %s
                                Field name: %s
                                """.stripIndent(),
                        className, fieldName));
            }
            log.debug("""
                            String converted to Java POJO class object.
                            String:
                            {}
                            Class:
                            {}
                            """.stripIndent(),
                    string, object);
            return (T) object;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to Java POJO class object.
                            String:
                            %s
                            """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts CSV string to object.
     *
     * @param csvString The CSV string.
     * @return The object.
     */
    public static Object csvStringToObject(String csvString) {
        DataValidationUtils.validateNotNull(csvString, "csvString");

        try {
            JSONArray jsonArray = csvStringToJsonArray(csvString);
            SmartValue smartValue = new SmartValue(jsonArrayToString(jsonArray));
            log.debug("""
                            CVS string converted to object.
                            CVS:
                            {}
                            Smart value:
                            {}
                            """.stripIndent(),
                    csvString, smartValue);
            return smartValue;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to CSV object.
                            String:
                            %s
                            """.stripIndent(),
                    csvString), e);
        }
    }

    /**
     * Converts string to smart value.
     *
     * @param type   The target object type.
     * @param string The string.
     * @return The smart value.
     */
    public static SmartValue stringToSmartValue(SmartType type, String string) {
        try {
            Object object = stringToObject(type, string);
            SmartValue smartValue = new SmartValue(object);
            log.debug("""
                            String converted to smart value.
                            String:
                            {}
                            Smart value:
                            {}
                            """.stripIndent(),
                    string, smartValue);
            return smartValue;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to smart value.
                            String:
                            %s
                            """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts object to CSV string.
     *
     * @param object The string.
     * @return The smart value.
     */
    public static String objectToCsvString(Object object) {
        try {
            JSONArray jsonRows = objectToJsonArray(object);
            StringBuffer stringBuffer = new StringBuffer();
            int rowsSize = jsonRows.length();
            int columnsSize = 0;

            for (int i = 0; i < rowsSize; i++) {

                if (jsonRows.get(i) instanceof JSONArray jsonColumns) {

                    if (columnsSize == 0) {
                        columnsSize = jsonColumns.length();
                    }
                    if (jsonRows.length() != columnsSize) {
                        throw new SmartRuntimeException(String.format("""
                                        Object rows have different size.
                                        Objet:
                                        %s
                                        """.stripIndent(),
                                object,
                                jsonRows));
                    }
                    for (int j = 0; j < columnsSize; j++) {
                        if (j > 0) {
                            stringBuffer.append(",");
                        }
                        String callValue = jsonColumns.getString(j);
                        stringBuffer.append(String.format("\"%s\"", escapeCSVField(callValue)));
                    }
                    stringBuffer.append("\n");
                } else {
                    if (i > 0) {
                        stringBuffer.append(",");
                    }
                    String cellValue = jsonRows.getString(i);
                    stringBuffer.append(String.format("\"%s\"", escapeCSVField(cellValue)));
                }
            }
            stringBuffer.append("\n");
            log.debug("""
                            Object converted to CSV string.
                            Object:
                            {}
                            CSV string:
                            {}
                            """.stripIndent(),
                    object, stringBuffer);
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert object to CSV string.
                            Object:
                            %s
                            """.stripIndent(),
                    object), e);
        }
    }

    /**
     * Converts string to object by object smart type.
     *
     * @param type   The type name.
     * @param string The string.
     * @return The object.
     */
    public static Object stringToObject(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "value");

        Object object = null;
        Class<?> objectClass = type.getObjectClass();

        try {
            if (objectClass.equals(SmartValue.class)) {
                object = stringToSmartValue(type, string);
            }
            else if (objectClass.equals(String.class)) {
                object = string;
            }
            else if (objectClass.equals(StringBuffer.class)) {
                object = stringToStringBuffer(string);
            }
            else if (objectClass.equals(Character.class)) {
                object = stringToCharacter(string);
            }
            else if (objectClass.equals(Short.class)) {
                Short.parseShort(string);
            }
            else if (objectClass.equals(Integer.class)) {
                object = Integer.parseInt(string);
            }
            else if (objectClass.equals(Long.class)) {
                object = Long.parseLong(string);
            }
            else if (objectClass.equals(BigInteger.class)) {
                object = new BigInteger(string);
            }
            else if (objectClass.equals(Float.class)) {
                object = Float.parseFloat(string);
            }
            else if (objectClass.equals(Double.class)) {
                object = Double.parseDouble(string);
            }
            else if (objectClass.equals(BigDecimal.class)) {
                object = new BigDecimal(string);
            }
            else if (objectClass.equals(Boolean.class)) {
                object = ConverterUtils.stringToBoolean(string);
            }
            else if (objectClass.equals(Date.class)) {
                object = new Date(string);
            }
            else if (objectClass.equals(LocalDate.class)) {
                object = stringToSmartLocalDate(string).getLocalDate();
            }
            else if (objectClass.equals(LocalDateTime.class)) {
                object = stringToSmartLocalDateTime(string).getLocalDateTime();
            }
            else if (objectClass.equals(LocalTime.class)) {
                object = stringToSmartLocalTime(string).getLocalTime();
            }
            else if (objectClass.equals(SmartDate.class)) {
                object = stringToSmartDate(string);
            }
            else if (objectClass.equals(SmartLocalDate.class)) {
                object = stringToSmartLocalDate(string);
            }
            else if (objectClass.equals(SmartLocalDateTime.class)) {
                object = stringToSmartLocalDateTime(string);
            }
            else if (objectClass.equals(SmartLocalTime.class)) {
                object = stringToSmartLocalTime(string);
            }
            else if (objectClass.equals(File.class)) {
                object = ConverterUtils.stringToFile(string);
            }
            else if (objectClass.equals(java.net.URL.class)) {
                object = ConverterUtils.stringToURL(string);
            }
            else if (objectClass.equals(java.net.URI.class)) {
                object = ConverterUtils.stringToURI(string);
            }
            else if (objectClass.equals(Path.class)) {
                object = ConverterUtils.stringToPath(string);
            }
            else if (objectClass.equals(JSONObject.class)) {
                object = ConverterUtils.stringToJasonObject(string);
            }
            else if (objectClass.equals(JSONArray.class)) {
                object = ConverterUtils.stringToJasonArray(string);
            }
            else {
                if (objectClass.isArray()) {
                    object = ConverterUtils.stringToArray(type, string);
                }
                else if (objectClass.isAssignableFrom(List.class)) {
                    object = ConverterUtils.stringToList(type, string);
                }
                else if (objectClass.isAssignableFrom(Set.class)) {
                    object = ConverterUtils.stringToSet(type, string);
                }
                else if (objectClass.isAssignableFrom(Queue.class)) {
                    object = ConverterUtils.stringToQueue(type, string);
                }
                else if (objectClass.isAssignableFrom(Map.class)) {
                    object = ConverterUtils.stringToMap(type, string);
                }
                else if (objectClass.isAssignableFrom(Node.class)) {
                    object = ConverterUtils.stringToXmlDocument(string);
                }
                else if (objectClass.isAssignableFrom(Enum.class)) {
                    object = stringToEnumValue(type, string);
                }
                else {
                    object = ConverterUtils.stringToPojoClassObject(type, string);
                }
            }
            DataValidationUtils.validateNotNull(object, "object");
            log.debug("""
                            String converted to object.
                            String:
                            {}
                            Target type:
                            {}
                            Object:
                            {}
                            """.stripIndent(),
                    string, type, object);
            return object;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to object.
                            String:
                            %s
                            Target type:
                            %s
                            """.stripIndent(),
                    string, type), e);
        }
    }

    /**
     * Converts string to string buffer.
     *
     * @param string The string.
     * @return The string buffer.
     */
    public static StringBuffer stringToStringBuffer(String string) {
        DataValidationUtils.validateNotEmpty(string, "string");
        return new StringBuffer(string);
    }

    /**
     * Converts CVS string to JSON array.
     *
     * @param csvString The string.
     * @return The string buffer.
     */
    public static JSONArray csvStringToJsonArray(String csvString) {
        DataValidationUtils.validateNotEmpty(csvString, "csvString");

        try {
            JSONArray jsonArray = new JSONArray();
            String[] lines = csvString.split("\n");
            int columnsSize = 0;

            // Iterate over the rest of the lines
            for (String line : lines) {
                List<String> fieldValues = parseCsvRowString(line);

                if (columnsSize == 0) {
                    columnsSize = fieldValues.size();
                }
                if (fieldValues.size() != columnsSize) {
                    throw new SmartRuntimeException(String.format("""
                                    CSV file rows have different number of fields.
                                    CSV:
                                    {}
                                    """.stripIndent(),
                            csvString));
                }
                JSONArray rowJsonArray = new JSONArray();

                for (String fieldValue : fieldValues) {
                    // Replace escaped come placeholder back escaped coma.
                    String string = csvFieldValueToString(fieldValue);
                    rowJsonArray.put(string);
                }
                jsonArray.put(rowJsonArray);
            }
            log.debug("""
                            CSV string converted to JSON array.
                            CSV string:
                            {}
                            JSON array:
                            {}
                            """.stripIndent(),
                    csvString, jsonArray);
            return jsonArray;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert CSV string to JSON array.
                            CSV string:
                            %s
                            """.stripIndent(),
                    csvString), e);
        }
    }

    /**
     * Converts string to char.
     *
     * @param charString The char string.
     * @return The char.
     */
    public static char stringToCharacter(String charString) {
        DataValidationUtils.validateNotEmpty(charString, "charString");
        DataValidationUtils.validateMax(charString.length(), 1, "charString");
        return charString.charAt(0);
    }

    /**
     * Converts collection to JSON array.
     *
     * @param collection The collection.
     * @return The JSON array.
     */
    public static <T> JSONArray collectionToJsonArray(Collection<T> collection) {
        try {
            JSONArray jsonArray = new JSONArray(collection);
            log.debug("""
                            Collection converted to JSON array.
                            Collection:
                            {}
                            JSON array:
                            {}
                            """.stripIndent(),
                    collection, jsonArray);
            return jsonArray;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert collection to JSON array:\n'%s'", collection));
        }
    }

    /**
     * Convers map to JSON object.
     *
     * @param map The map.
     * @return The JSON object.
     */
    public static <K, V> JSONObject mapToJSONObject(Map<K, V> map) {
        try {
            return new JSONObject(map);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert map to JSON object:\n'%s'", map));
        }
    }

    /**
     * Convers JSON array to array.
     *
     * @param jsonArray The JSON array.
     * @param <T>       The array type.
     * @return The array.
     */
    public static <T> T[] jsonArrayToArray(JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            List<T> list = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.get(i);
                list.add((T) object);
            }
            T[] array = (T[]) list.toArray();
            log.debug("""
                            JSON array converted to array.
                            JSON array:
                            {}
                            Array:
                            {}
                            """.stripIndent(),
                    jsonArray, array);
            return array;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert JSON array to array.
                            JSON array:
                            {}
                            """.stripIndent(),
                    jsonArray), e);
        }
    }

    /**
     * Convers JSON array to list.
     * @param type The target type.
     * @param jsonArray The JSON array.
     * @param <T>       The list type.
     * @return The list.
     */
    public static <T> List<T> jsonArrayToList(SmartType type, JSONArray jsonArray) {
        return (List<T>) jsonArrayToCollection(type, jsonArray);
    }

    /**
     * Convers JSON array to set.
     * @param type The target type.
     * @param jsonArray The JSON array.
     * @param <T>       The set type.
     * @return The set.
     */
    public static <T> Set<T> jsonArrayToSet(SmartType type, JSONArray jsonArray) {
        return (Set<T>) jsonArrayToCollection(type, jsonArray);
    }

    /**
     * Convers JSON array to vector.
     * @param type The target type.
     * @param jsonArray The JSON array.
     * @param <T>       The vector type.
     * @return The vector.
     */
    public static <T> Vector<T> jsonArrayToVector(SmartType type, JSONArray jsonArray) {
        return (Vector<T>) jsonArrayToCollection(type, jsonArray);
    }

    /**
     * Convers JSON array to set.
     * @param type The target type.
     * @param jsonArray The JSON array.
     * @param <T>       The set type.
     * @return The set.
     */
    public static <T> Queue<T> jsonArrayToQueue(SmartType type, JSONArray jsonArray) {
        return (Queue<T>) jsonArrayToCollection(type, jsonArray);
    }

    /**
     * Converts array to JSON array.
     *
     * @param array The array.
     * @param <T>   The array type.
     * @return The JSON array.
     */
    public static <T> JSONArray arrayToJsonArray(T[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            JSONArray jsonArray = new JSONArray();
            for (T element : array) {
                jsonArray.put(element);
            }
            log.debug("""
                    Array converted to JSON array.
                    Array:
                    {}
                    JSON array:
                    {}
                    """.stripIndent(),
                    arrayToString(array), jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert array to JSON array.
                            Array:
                            %s
                            """.stripIndent(),
                            arrayToString(array)), e);
        }
    }

    /**
     * Converts list to JSON array.
     *
     * @param list The list.
     * @param <T>  The list type.
     * @return The JSON array.
     */
    public static <T> JSONArray listToJsonArray(List<T> list) {
        return collectionToJsonArray(list);
    }

    /**
     * Converts set to JSON array.
     *
     * @param set The set.
     * @param <T> The set type.
     * @return The JSON array.
     */
    public static <T> JSONArray setToJsonArray(Set<T> set) {
        return collectionToJsonArray(set);
    }

    /**
     * Converts queue to JSON array.
     *
     * @param queue The queue.
     * @param <T>   The queue type.
     * @return The JSON array.
     */
    public static <T> JSONArray queueToJsonArray(Queue<T> queue) {
        return collectionToJsonArray(queue);
    }

    /**
     * Converts vector to JSON array.
     *
     * @param vector The queue.
     * @param <T>    The queue type.
     * @return The JSON array.
     */
    public static <T> JSONArray vectorToJsonArray(Vector<T> vector) {
        return collectionToJsonArray(vector);
    }

    /**
     * Converts object to JSON array.
     * @param object The object.
     * @return The JSON array.
     * @param <T> The collection element type.
     */
    public static <T> JSONArray objectToJsonArray(Object object) {
        JSONArray jsonArray;

        try {
            if (object.getClass() == JSONArray.class) {
                jsonArray = (JSONArray) object;
            }
            else if (object instanceof Collection collection) {
                jsonArray = collectionToJsonArray(collection);
            }
            else if (isArray(object)) {
                jsonArray = arrayToJsonArray((T[]) object);
            }
            else if (object instanceof String string) {
                jsonArray = stringToJasonArray(string);
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert %s type to JSON array", object.getClass().getName()));
            }
            log.debug("Object is converted to JSON array:\n{}", jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to JSON array:\n%s", object));
        }
    }

    /**
     * Converts object to JSON object.
     * @param object The object.
     * @return The JSON array.
     */
    public static JSONObject objectToJsonObject(Object object) {
        JSONObject jsonObject;

        try {
            if (object.getClass() == JSONObject.class) {
                jsonObject = (JSONObject) object;
            }
            else if (object instanceof Map map) {
               jsonObject = mapToJSONObject(map);
            }
            else if (object instanceof Node xmlNode) {
                jsonObject = xmlNodeToJsonObject(xmlNode);
            }
            else if (object instanceof String string) {
                jsonObject = stringToJasonObject(string);
            }
            else {
                try {
                   // Convert POJO class to JSON object.
                   jsonObject = new JSONObject();
                   Class<?> objectClass = object.getClass();
                   Field[] fields = objectClass.getDeclaredFields();

                   for (Field field : fields) {
                       field.setAccessible(true);
                       String fieldName = field.getName();
                       Object fieldValue = field.get(object);
                       jsonObject.put(fieldName,fieldValue);
                   }
                }
                catch (SmartRuntimeException e) {
                    throw new SmartRuntimeException(String.format(
                            "Cannot convert %s type to JSON array",
                            object.getClass().getName()));
                }
            }
            log.debug("Object is converted to JSON array:\n{}", jsonObject.toString());
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to JSON array:\n%s", object.toString()));
        }
    }

    /**
     * Converts object to XML node object.
     * @param object The object.
     * @return The XML node object.
     */
    public static Node objectToXmlNode(Object object) {
        Node xmlNode;

        try {
            if (object instanceof Node node) {
                xmlNode = node;
            }
            else if (object instanceof JSONObject jsonObject) {
                xmlNode = jsonObjectToXmlNode(jsonObject);
            }
            else if (object instanceof Map map) {
                JSONObject jsonObject = mapToJSONObject(map);
                xmlNode = jsonObjectToXmlNode(jsonObject);
            }
            else if (object instanceof String string) {
                xmlNode = stringToXmlNode(string);
            }
            else {
                try {
                    String classValueString = objectToCsvString(object);
                    JSONObject jsonObject = stringToJasonObject(classValueString);
                    xmlNode = jsonObjectToXmlNode(jsonObject);
                }
                catch (SmartRuntimeException e) {
                    throw new SmartRuntimeException(String.format(
                            "Cannot convert %s type to JSON array",
                            object.getClass().getName()));
                }
            }
            log.debug("Object is converted to JSON array:\n{}", xmlNode.toString());
            return xmlNode;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to JSON array:\n%s", object.toString()));
        }
    }

    /**
     * Converts object to enum value
     * @param type The enum type.
     * @param object The object.
     * @param <T> The enum type.
     * @return The enum value..
     */
    public static <T extends Enum<T>> T objectToEnumValue(SmartType type, Object object) {
        DataValidationUtils.validateNotNull(type, "enumClassNme");
        DataValidationUtils.validateNotNull(object, "object");
        T enumValue = null;

        try {
            try {
                enumValue = (T) object;
            }
            catch (ClassCastException e) {

                if (object instanceof String string) {
                      enumValue = stringToEnumValue(type, string);
                }
            }
            log.debug("Object is converted to enum value: {}", enumValue);
            return enumValue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to enum value: %s", object));
        }
    }

    /**
     * Converts object to POJO class object.
     * @param object The object.
     * @param <T> The class type.
     * @return The POJO class object.
     */
    public static <T> T objectToClassObject(String classNme, Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        T classObject = null;

        try {
            try {
                classObject = (T) object;
            }
            catch (ClassCastException e) {

                if (object instanceof JSONObject jsonObject) {
                    classObject = jsonObjectToClassObject(jsonObject, classNme);
                }
                else if (object instanceof Node xmlNode) {
                    JSONObject jsonObject = xmlNodeToJsonObject(xmlNode);
                    classObject = jsonObjectToClassObject(jsonObject, classNme);
                }
                else if (object instanceof Map map) {
                    JSONObject jsonObject = mapToJSONObject(map);
                    classObject = jsonObjectToClassObject(jsonObject, classNme);
                }
                else if (object instanceof String string) {
                    JSONObject jsonObject = stringToJasonObject(string);
                    classObject = jsonObjectToClassObject(jsonObject, classNme);
                }
            }
            log.debug("Object is converted to class object:\n{}", classObject);
            return classObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to class object:\n%s", object));
        }
    }

    /**
     * Converts JSON object to POJO class by its name.
     * @param jsonObject The JSON object.
     * @param className The class name.
     * @return The class object.
     * @param <T> The class type.
     */
    public static <T> T jsonObjectToClassObject(JSONObject jsonObject, String className) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");
        DataValidationUtils.validateNotBlank(className, className);
        T classObject = null;

        try {
            Class<?> pojoClass = Class.forName(className);
            Object obj = pojoClass.getDeclaredConstructor().newInstance();

            for (String key : jsonObject.keySet()) {
                Field field = pojoClass.getDeclaredField(key);
                field.setAccessible(true);
                Object value = jsonObject.get(key);
                field.set(obj, value);
                classObject = (T) obj;
                log.debug("""
                        JSON object converted to POJO class object.
                        JSON:
                        {}
                        Class object:
                        {}
                        """.stripIndent(),
                        jsonObject,
                        classObject);
            }
            return classObject;
        }
        catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert JSON object to class object:\n%s",
                        jsonObject), e);
       }
    }

    /**
     * Converts array to string.
     *
     * @param array The array.
     * @param <T>   The array type.
     * @return The array string.
     */
    public static <T> String arrayToString(T[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            String arrayString = Arrays.toString(array);
            log.debug("Array {} converted to string '{}'", array, arrayString);
            return arrayString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert array to string: %s", Arrays.toString(array)));
        }
    }

    /**
     * Converts string in JSON array format to array.
     *
     * @param smartType The array type.
     * @param string    The array string.
     * @param <T>       The array type.
     * @return The array.
     */
    public static <T> T[] stringToArray(SmartType smartType, String string) {

        try {
            JSONArray jsonArray = new JSONArray(string);
            Class<?> valueClass = smartType.getValueSmartType().getObjectClass();
            T[] array = (T[]) Array.newInstance(valueClass, jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                // Get each JSONObject from the JSONArray
                String jsonString = jsonArray.getString(i);
                T element = (T) stringToObject(smartType.getValueSmartType(), jsonString);
                array[i] = element;
            }
            log.debug("""
                    String converted to array.
                    String:
                    {}
                    Array:
                    {}
                    """.stripIndent(),
                    string, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to array.
                            String:
                            %s
                            """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON array format to list.
     * @param type The list type.
     * @param string The string.
     * @param <T> The list element type.
     * @return The list.
     */
    public static <T> List<T> stringToList(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        List<T> list = (List<T>) createCollectionsFromClass(
                type.getValueSmartType().getObjectClass(),
                type.getKeyClass(),
                type.getValueSmartType());
        return (List<T>) stringToCollection(type, string, list);
    }

    /**
     * Converts string in JSON array format to set.
     * @param type The set type.
     * @param string The string.
     * @param <T> The set element type.
     * @return The list.
     */
    public static <T> Set<T> stringToSet(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        Set<T> set = (Set<T>) createCollectionsFromClass(
                type.getValueSmartType().getObjectClass(),
                type.getKeyClass(),
                type.getValueSmartType());
        return (Set<T>) stringToCollection(type, string, set);
    }

    /**
     * Converts string in JSON array format to queue.
     * @param type The queue type.
     * @param string The string.
     * @param <T> The queue element type.
     * @return The queue.
     */
    public static <T> Queue<T> stringToQueue(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        Queue<T> queue = (Queue<T>) createCollectionsFromClass(
                type.getValueSmartType().getObjectClass(),
                type.getKeyClass(),
                type.getValueSmartType());
        return (Queue<T>) stringToCollection(type, string, queue);
    }

    /**
     * Converts string in JSON array format to vector.
     * @param type The vector type.
     * @param string The string.
     * @param <T> The vector element type.
     * @return The vector.
     */
    public static <T> Vector<T> stringToVector(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        Vector<T> vector = (Vector<T>) createCollectionsFromClass(
                type.getValueSmartType().getObjectClass(),
                type.getKeyClass(),
                type.getValueSmartType());
        return (Vector<T>) stringToCollection(type, string, vector);
    }

    /**
     * Converts string in JSON object format to map.
     * @param type      The target type.
     * @param string    The string.
     * @param <K>       The key type.
     * @param <V>       The value type.
     * @return The map.
     */
    private static <K,V> Map<K,V> stringToMap(SmartType type, String string) {
        try {
            JSONObject jsonObject = stringToJasonObject(string);
            Map<K,V> map = (Map<K,V>) jsonObjectToMap(type, jsonObject);
            log.debug("""
                    String converted to map.
                    String:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    string, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to map.
                            String:
                            %s
                            """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Convers JSON object to map.
     * @param type The target type.
     * @param jsonObject The JSON object.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return The map.
     */
    public static <K,V,SK,SV> Map<K,V> jsonObjectToMap(SmartType type, JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            Class<K> keyClass = (Class<K>) type.getKeyClass();
            Class<V> valueClass = (Class<V>) type.getObjectClass();
            SmartType valueType = type.getValueSmartType();
            Class<SK> subElementKeyClass = valueType != null ? (Class<SK>) valueType.getKeyClass() : null;
            Class<SV> subElementValueClass = valueType != null ? (Class<SV>) valueType.getObjectClass() : null;
            Map<K,V> map = null;
            Map<K,Collection<SV>> mapOfCollections = null;
            Map<K,Map<SK,SV>> mapOfMaps = null;

            if (subElementValueClass == null) {
                map = createMapFromClasses(keyClass, valueClass);
            }
            else if (subElementKeyClass == null) {
                mapOfCollections = createMapOfCollectionsFromClasses(keyClass, subElementValueClass);
            }
            else {
                mapOfMaps = createMapOfMapsFromClasses(keyClass, subElementKeyClass, subElementValueClass);
            }
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String stringKey = keys.next();
                K key = objectToObject(new SmartType(type.getKeyClass()), stringKey);

                if (map != null) {
                    Object jsonValue = jsonObject.get(stringKey);
                    SmartType elementType = new SmartType(valueClass);
                    V value = objectToObject(elementType, jsonValue);
                    map.put(key, value);
                }
                else if (mapOfCollections != null) {
                    JSONArray jsonCollection = jsonObject.getJSONArray(stringKey);
                    SmartType collectionType = new SmartType(Collection.class,
                            new SmartType(subElementValueClass));
                    Collection<SV> subCollection = jsonArrayToCollection(collectionType, jsonCollection);
                    mapOfCollections.put(key, subCollection);
                }
                else {
                    JSONObject jsonMap = jsonObject.getJSONObject(stringKey);
                    SmartType mapType = new SmartType(Map.class, subElementKeyClass,
                            new SmartType(subElementValueClass));
                    Map<SK,SV> subMap = jsonObjectToMap(mapType, jsonMap);
                    mapOfMaps.put(key, subMap);
                }
            }
            if (mapOfCollections != null) {
                map = (Map<K, V>) mapOfCollections;
            }
            else if (mapOfMaps != null) {
                map = (Map<K, V>) mapOfMaps;
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert JSON object to map:\n%s", jsonObject));
            }
            log.debug("""
                    JSON converted to map.
                    JSON:
                    {}
                    Target type:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    jsonObject, type, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert JSON object to map:\n%s", jsonObject), e);
        }
    }

    /**
     * Convers JSON object to XML node.
     * @param jsonObject The JSON object.
     * @return The XML node.
     */
    public static Node jsonObjectToXmlNode(JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            String xmlString = XML.toString(jsonObject);
            Node xmlNode = stringToXmlNode(xmlString);
            log.debug("""
                    JSON object converted to XML node.
                    JSON object:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    jsonObject, xmlNode);
            return xmlNode;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert JSON object to XML node:\n%s",
                    jsonObject), e);
        }
    }

    /**
     * Converts XML string to JSON object.
     *
     * @param xmlString XML string.
     * @return The JSON object.
     */
    public static JSONObject xmlStringToJson(String xmlString) {
        try {
            return XML.toJSONObject(xmlString);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert XML string to JSON object.
                            XML string:
                            %s
                            """.stripIndent(),
                    xmlString), e);
        }
    }

    /**
     * Converts XML node to JSON object.
     *
     * @param xmlNode XML string.
     * @return The JSON object.
     */
    public static JSONObject xmlNodeToJsonObject(Node xmlNode) {
        try {
            String xmlString = xmlNodeToString(xmlNode);
            return XML.toJSONObject(xmlString);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert XML node to JSON object.
                            XML node:
                            %s
                            """.stripIndent(),
                    xmlNode), e);
        }
    }

    /**
     * Converts number string to number.
     * @param numberString The number string.
     * @return The number.
     */
    public static Number stringToNumber(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");
        Number number;

        try {
            if (numberString.contains(".")) {
                try {
                    number = Float.parseFloat(numberString);
                } catch (NumberFormatException e) {
                    try {
                        number = Double.parseDouble(numberString);
                    } catch (NumberFormatException nfe) {
                        number = new BigDecimal(numberString);
                    }
                }
            } else {
                try {
                    number = Integer.parseInt(numberString);
                } catch (NumberFormatException e) {
                    try {
                        number = Long.parseLong(numberString);
                    } catch (NumberFormatException nfe) {
                        number = new BigInteger(numberString);
                    }
                }
            }
            log.debug("""
                    Number string converted to number.
                    Number string: %s
                    Number: %s
                    """.stripIndent(),
                    numberString, number);
            return number;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert number string to number.
                    Number string:
                    %s
                    """.stripIndent(),
                    numberString), e);
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

    private static boolean isXmlArray(Object object) {

        if (object instanceof Node node) {

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NodeList children = node.getChildNodes();
                String firstChildName = null;
                int similarChildCount = 0;

                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        if (firstChildName == null) {
                            firstChildName = child.getNodeName();
                        }
                        if (firstChildName.equals(child.getNodeName())) {
                            similarChildCount++;
                        }
                    }
                }
                return similarChildCount > 1;
            }
        }
        return false;
    }

    private static <T> Collection<T> jsonArrayToCollection(SmartType type, JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            Collection<T> collection;
            Class<?> objectClass = type.getObjectClass();

            if (objectClass.isAssignableFrom(List.class)) {
                collection = new ArrayList<T>();
            }
            else if (objectClass.isAssignableFrom(Set.class)) {
                collection = new HashSet<T>();
            }
            else if (objectClass.isAssignableFrom(Queue.class)) {
                collection = new LinkedList<T>();
            }
            else if (objectClass.isAssignableFrom(Vector.class)) {
                collection = new Vector<T>();
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Unsupported collection type: %s", type));
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.get(i);
                T elementObject = objectToObject(type.getValueSmartType(), object);
                collection.add(elementObject);
            }
            log.debug("""
                    JSON array converted to collection.
                    Collection class: {}
                    JSON array:
                    {}
                    Collection:
                    {}
                    """.stripIndent(),
                    objectClass.getName(), jsonArray, collection);
            return collection;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert JSON array to collection.
                            JSON array:
                            {}
                            """.stripIndent(),
                    jsonArray), e);
        }
    }

    private static String removeOrdinalSuffix(String dateString) {
        // Regular expression to match ordinal suffixes (1st, 2nd, 3rd, 4th, etc.)
        return dateString.replaceAll("(?<=\\d)(st|nd|rd|th)", "");
    }

    private static boolean isNaN(Object object) {
        return ((object.getClass() == Float.class && Float.isNaN((Float) object)) ||
                (object.getClass() == Double.class && Double.isNaN((Double) object)));
    }

    private static boolean isFloatPositiveInfinite(Object object) {
        return (object.getClass() == Float.class &&
                object.equals(Float.POSITIVE_INFINITY));
    }

    private static boolean isFloatNegativeInfinite(Object object) {
        return (object.getClass() == Float.class &&
                object.equals(Float.NEGATIVE_INFINITY));
    }

    private static boolean isDoublePositiveInfinite(Object object) {
        return (object.getClass() == Double.class &&
                object.equals(Double.POSITIVE_INFINITY));
    }

    private static boolean isDoubleNegativeInfinite(Object object) {
        return (object.getClass() == Double.class &&
                object.equals(Double.NEGATIVE_INFINITY));
    }

    private static <T> Collection<T> stringToCollection(
            SmartType smartType, String string, Collection<T> collection) {
        String className = collection.getClass().getSimpleName();

        try {
            T[] array = stringToArray(smartType, string);

            Collections.addAll(collection, array);
            log.debug("""
                            String converted to {}.
                            String:
                            {}
                            List:
                            {}
                            """.stripIndent(),
                    className, string, collection);
            return collection;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to %s.
                            String:
                            %s
                            """.stripIndent(),
                    className, string), e);
        }
    }

    private static String trimQuotes(String string) {
        String trimmedString = string.trim();

        if (trimmedString.startsWith("\"") && trimmedString.endsWith("\"")) {
            return trimmedString.substring(1, trimmedString.length() - 1);
        } else {
            return trimmedString;
        }
    }

    private static List<String> parseCsvRowString(String csvRow) {
        DataValidationUtils.validateNotNull(csvRow, "csvRow");

        char[] delimiters = {',', ';', '|', ':', '\t', ' '};
        List<String> values = new ArrayList<>();

        if (csvRow.isEmpty()) {
            return values;
        }
        boolean inQuotes = false;
        int length = csvRow.length();
        int valueStartIndex = 0;

        for (char delimiter : delimiters) {

            for (int i = 0; i < length; i++) {
                char previousCharValue = ' ';
                char nextCharValue = ' ';
                char charValue = csvRow.charAt(i);

                if (i > 0) {
                    previousCharValue = csvRow.charAt(i - 1);
                } else if (i < length - 1) {
                    nextCharValue = csvRow.charAt(i + 1);
                }

                if (charValue == '"' && nextCharValue != '"' && previousCharValue != '"') {
                    inQuotes = !inQuotes;
                }
                if (charValue == delimiter && !inQuotes) {
                    String value = csvRow.substring(valueStartIndex, i);
                    values.add(value);
                    valueStartIndex = i + 1;
                }
            }
            String value = csvRow.substring(valueStartIndex);
            values.add(value);
            return values;
        }
        throw new SmartRuntimeException(String.format("""
                        CSV row does not contain valid delimiters.
                        CSV:
                        %s
                        Valid delimiters:
                        %s
                        """.stripIndent(),
                csvRow, String.valueOf(delimiters)));
    }

    private static <T,K,ST,SK> Collection<?> createCollectionsFromClass(
            Class<T> collectionClass,
            Class<K> elemnetKeyClass,
            SmartType elementValueType) {
        Class<T> elementClass = (Class<T>) elementValueType.getObjectClass();
        T[] array = createArrayFromClass(elementClass);

        if (collectionClass == List.class) {
            List<T> list = Arrays.asList(array);
            collectionLog(collectionClass, elemnetKeyClass,elementValueType, list);
            return list;
        }
        else if (collectionClass == Set.class) {
            Set<T> set = new HashSet<>(Arrays.asList(array));
            collectionLog(collectionClass, elemnetKeyClass,elementValueType, set);
            return set;
        }
        else if (collectionClass == Queue.class) {
            new LinkedList<>(Arrays.asList(array));
            Queue<T> queue = new LinkedList<>();
            collectionLog(collectionClass, elemnetKeyClass,elementValueType, queue);
            return queue;
        }
        else if (collectionClass == Vector.class) {
            Vector<T> vector = new Vector<>(List.of(array));
            collectionLog(collectionClass, elemnetKeyClass,elementValueType, vector);
            return vector;
        }
        throw new SmartRuntimeException(String.format("""
                Cannot create collection from:
                Object class: %s
                Key class: %s
                Value type: %s
                """.stripIndent(),
                collectionClass.getName(),
                elemnetKeyClass.getName(),
                elementValueType));
    }

    private static void collectionLog(
            Class<?> collectionClass,
            Class<?> elemnetKeyClass,
            SmartType elementValueType,
            Collection<?> collection) {
        log.debug("""
                {} created from:
                Object class: {}
                Key class: {}
                Value type: {}
                {}:
                {}
                """.stripIndent(),
                collectionClass.getSimpleName(),
                collectionClass.getName(),
                elemnetKeyClass != null ? elemnetKeyClass.getName() : null,
                elementValueType,
                collectionClass.getSimpleName(),
                collection);
    }

    public static <K,V> Map<K,V> createMapFromClasses(Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> map = new HashMap<>();
        log.debug("""
                Map created from:
                Key class: {}
                Value class: {}
                Map:
                {}
                """.stripIndent(),
                keyClass.getName(),
                valueClass.getName(),
                map);
        return map;
    }

    public static <K,SV> Map<K,Collection<SV>> createMapOfCollectionsFromClasses(
            Class<K> keyClass,
            Class<SV> subCollectionValueClass) {
        Map<K, Collection<SV>> map = new HashMap<>();
        log.debug("""
                Map of maps created from:
                Key class: {}
                Sub collection value class: {}
                Map:
                {}
                """.stripIndent(),
                keyClass.getName(),
                subCollectionValueClass.getName(),
                map);
        return map;
    }

    public static <K,V,SK,SV> Map<K, Map<SK,SV>>createMapOfMapsFromClasses(
            Class<K> keyClass,
            Class<SK> subMapKeyClass,
            Class<SV> subMapValueClass) {
        Map<K, Map<SK,SV>> map = new HashMap<>();
        log.debug("""
                Map of maps created from:
                Key class: {}
                Sub map key class: {}
                Sub map value class: {}
                Map:
                {}
                """.stripIndent(),
                keyClass.getName(),
                subMapKeyClass.getName(),
                subMapValueClass.getName(),
                map);
        return map;
    }

    private static <T>  T[] createArrayFromClass(Class<?> valueClass) {
        T[] array = (T[]) Array.newInstance(valueClass, 0);
        log.debug("""
                Array created from value class:
                Class: {}
                Array:
                {}
                """.stripIndent(),
                valueClass, array);
        return array;
    }
}


