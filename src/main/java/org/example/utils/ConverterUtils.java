package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.exceptions.SmartRuntimeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.util.HtmlUtils;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

import static java.lang.Float.POSITIVE_INFINITY;
import static org.apache.commons.lang3.ObjectUtils.isArray;
import static org.example.constants.Settings.*;

/**
 * Converter utils.
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class ConverterUtils {
    private static final String ESCAPED_QUOTE = "\"\"";
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
     * Escapes JavaScript.
     * @param jsonString The input to escape.
     * @return The escaped script.
     */
    public static String escapeJavaScript(String jsonString) {
        DataValidationUtils.validateNotNull(jsonString, "jsonString");
        StringBuilder escapedString = new StringBuilder();

        for (char c : jsonString.toCharArray()) {
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
        log.debug("""
                JavaScript string is escaped.
                Source:
                {}
                Target:
                {}
                """.stripIndent(),
                jsonString, escapedString);
        return escapedString.toString();
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
     * Escapes XML string.
     * @param xmlString The XML string.
     * @return The escaped XML string.
     */
    public static String escapeXmlString(String xmlString) {
        DataValidationUtils.validateNotNull(xmlString, "xmlString");

        try {
            String result = HtmlUtils.htmlEscape(xmlString);
            log.debug("""
                    XML string is escaped.
                    Source:
                    {}
                    Target:
                    {}
                    """.stripIndent(),
                    xmlString, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot escape XML string.
                    XML string:
                    %s
                    """.stripIndent(),
                    xmlString));
        }
    }

    /**
     * Unescapes XML string.
     * @param xmlString The XML string.
     * @return The escaped XML string.
     */
    public static String unescapeXmlString(String xmlString) {
        DataValidationUtils.validateNotNull(xmlString, "xmlString");

        try {
            String result = HtmlUtils.htmlUnescape(xmlString);
            log.debug("""
                    XML string is escaped.
                    Source:
                    {}
                    Target:
                    {}
                    """.stripIndent(),
                    xmlString, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot escape XML string.
                    XML string:
                    %s
                    """.stripIndent(),
                    xmlString));
        }
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
        DataValidationUtils.validateNotBlank(dateString, "dateString");

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
            JSONObject jsonObject;

            if (isXmlString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = new JSONObject(string);
            }
            log.debug("{} string converted to JSON object {}.", string, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
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
            JSONArray jsonArray;
            jsonArray = new JSONArray(string);

            log.debug("{} string converted to JSON array {}.", string, jsonArray);
            return jsonArray;
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
            if (isJsonObjectString(xmlString)) {
                JSONObject jsonObject = stringToJasonObject(xmlString);
                Node xmlNode = jsonObjectToXmlNode(jsonObject);
                xmlString = xmlNodeToString(xmlNode);
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            log.debug("{} string converted to XML document:\n{}.", xmlString, document);
            return document;
        }
        catch (Exception e) {
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
            if (isJsonObjectString(xmlString)) {
                JSONObject jsonObject = stringToJasonObject(xmlString);
                Node xmlNode = jsonObjectToXmlNode(jsonObject);
                xmlString = xmlNodeToString(xmlNode);
            }
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
            if (object == null || object == JSONObject.NULL) {
                string = NULL_VALUE_STRING;
            }
            else if (isNaN(object)) {
                string = NAN_VALUE_STRING;
            }
            else if (isFloatPositiveInfinite(object) || isDoublePositiveInfinite(object)) {
                string = POSITIVE_INFINITY_VALUE_STRING;
            }
            else if (isFloatNegativeInfinite(object) || isDoubleNegativeInfinite(object)) {
                string = NEGATIVE_INFINITY_VALUE_STRING;
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
            else if (object instanceof Collection collection) {
                JSONArray jsonArray = collectionToJsonArray(collection);
                string = jsonArrayToString(jsonArray);
            }
            else if (object instanceof Map map) {
                JSONObject jsonObject = mapToJSONObject(map);
                string = jsonObjectToString(jsonObject);
            }
            else if (object instanceof Record record) {
                string = recordToString(record);
            }
            else if (isArray(object)) {
                string = arrayToString((T[]) object);
            }
            else if (isPojoObject(object)) {
                string = pojoObjectToString(object);
            }
            else if (object.toString().startsWith(String.format("%s@", clasName))) {
                throw new SmartRuntimeException(String.format(
                        "You need to override default toString() method of the class: %s",
                        object.getClass().getName()));
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
            return normalizeLineSeparators(string);
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
     * Converts POJO class to JSON object.
     * @param pojObject The POJO object.
     * @return the JSON object.
     */
    public static JSONObject pojoObjectToJson(Object pojObject) {
        DataValidationUtils.validateNotNull(pojObject, "pojObject");

        JSONObject jsonObject = new JSONObject();
        try {
            for (Field field : pojObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(pojObject);
                String fieldName = field.getName();

                if (isJsonTypeObject(value)) {

                    if (value == null) {
                        jsonObject.put(fieldName, JSONObject.NULL);
                    }
                    else {
                        jsonObject.put(fieldName, value);
                    }
                }
                else {
                    if (isPojoObject(value)) {
                        // Recursive call for nested POJO
                        jsonObject.put(fieldName, pojoObjectToJson(value));
                    }
                    else {
                        String valueString = objectToString(value);
                        jsonObject.put(fieldName, valueString);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert POJO object to JSON.
                    POJO:
                    %s
                    """.stripIndent(),
                    pojObject), e);
        }
        log.debug("""
                POJO object converted to JSON object.
                POJO:
                {}
                JSON:
                {}
                """.stripIndent(),
                pojObject, jsonObject);
        return jsonObject;
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
            String stringValue = objectToString(sourceObject);
            targetObject = stringToObject(targetType, stringValue);
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
     * @param object The object.
     * @param <T> The object type.
     * @return The string.
     */
    public static <T> String pojoObjectToString(T object) {
        try {
            JSONObject jsonObject = pojoObjectToJson(object);
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
     * @param string The string.
     * @param type   The class type.
     * @return The Java POJO class object.
     */
    public static <T> T stringToPojoObject(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONObject jsonObject;

            if (isXmlString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = new JSONObject(string);
            }
            String className = type.getObjectClass().getName();
            // Load the class by name
            Class<?> customClass = Class.forName(className);
            // Create an instance of the class using the default constructor
            Object object = customClass.getDeclaredConstructor().newInstance();
            Map<String, SmartType> fieldTypesMap = type.getFieldTypesMap();
            Field[] fields = customClass.getDeclaredFields();

            // Iterate over the fields of the class
            for (Field field : fields) {
                // Make private fields accessible
                field.setAccessible(true);
                String fieldName = field.getName();

                // Set the field value if the JSON has the key
                if (jsonObject.has(fieldName)) {
                    Object jsonValue = jsonObject.get(fieldName);
                    SmartType fieldType = fieldTypesMap.get(fieldName);
                    Object targetObject = objectToObject(fieldType, jsonValue);
                    field.set(object, targetObject);
                }
                else {
                    String firstKey;
                    Object firstValue;
                    Iterator<String> keys = jsonObject.keys();

                    if (keys.hasNext()) {
                        firstKey = keys.next();
                        firstValue = jsonObject.get(firstKey);

                        if (firstValue instanceof JSONObject childJson &&
                                childJson.has(fieldName)) {
                            Object targetObject = childJson.get(fieldName);
                            field.set(object, targetObject);
                        }
                        else {
                            throw new SmartRuntimeException(String.format("""
                                Cannot convert JSON object to POJO object.
                                Field name is not present in JSON object.
                                Field name: %s
                                JSON:
                                %s
                                """.stripIndent(),
                                    fieldName, jsonObject));
                        }
                    }
                }
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
        }
        catch (Exception e) {
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
     * @param type   The target object type.
     * @param string The string.
     * @return The smart value.
     */
    public static SmartValue stringToSmartValue(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotNull(string, "string");

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
        }
        catch (Exception e) {
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
     * @param object The string.
     * @return The smart value.
     */
    public static String objectToCsvString(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

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
                    if (jsonRows.getJSONArray(0).length() != columnsSize) {
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
                        Object jsonValue = jsonColumns.get(j);

                        if (isCsvNoneStringObject(jsonValue)) {
                            stringBuffer.append(jsonValue);
                        }
                        else {
                            String callValue = objectToString(jsonValue);
                            stringBuffer.append(String.format("%s", escapeCSVField(callValue)));
                        }
                    }
                    stringBuffer.append("\n");
                }
                else {
                    if (i > 0) {
                        stringBuffer.append(",");
                    }
                    String cellValue = jsonRows.getString(i);
                    stringBuffer.append(String.format("\"%s\"", escapeCSVField(cellValue)));
                }
            }
            log.debug("""
                            Object converted to CSV string.
                            Object:
                            {}
                            CSV string:
                            {}
                            """.stripIndent(),
                    object, stringBuffer);
            return stringBuffer.toString();
        }
        catch (Exception e) {
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
     * @param type   The type name.
     * @param string The string.
     * @return The object.
     */
    public static <T> T stringToObject(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "value");

        T object = null;
        Class<?> objectClass = type.getObjectClass();

        try {
            if (objectClass.equals(SmartValue.class)) {
                object = (T) stringToSmartValue(type, string);
            }
            else if (objectClass.equals(String.class)) {
                object = (T) string;
            }
            else if (objectClass.equals(StringBuffer.class)) {
                object = (T) stringToStringBuffer(string);
            }
            else if (objectClass.equals(Character.class)) {
                object = (T)(Character) stringToCharacter(string);
            }
            else if (objectClass.equals(Short.class)) {
                Short.parseShort(string);
            }
            else if (objectClass.equals(Integer.class)) {
                object = (T)(Integer) Integer.parseInt(string);
            }
            else if (objectClass.equals(Long.class)) {
                object = (T)(Long) Long.parseLong(string);
            }
            else if (objectClass.equals(BigInteger.class)) {
                object = (T) new BigInteger(string);
            }
            else if (objectClass.equals(Float.class)) {
                object = (T)(Float) Float.parseFloat(string);
            }
            else if (objectClass.equals(Double.class)) {
                object = (T)(Double) Double.parseDouble(string);
            }
            else if (objectClass.equals(BigDecimal.class)) {
                object = (T) new BigDecimal(string);
            }
            else if (objectClass.equals(Boolean.class)) {
                object = (T)(Boolean) ConverterUtils.stringToBoolean(string);
            }
            else if (objectClass.equals(Date.class)) {
                object = (T) new Date(string);
            }
            else if (objectClass.equals(LocalDate.class)) {
                object = (T) stringToSmartLocalDate(string).getLocalDate();
            }
            else if (objectClass.equals(LocalDateTime.class)) {
                object = (T) stringToSmartLocalDateTime(string).getLocalDateTime();
            }
            else if (objectClass.equals(LocalTime.class)) {
                object = (T) stringToSmartLocalTime(string).getLocalTime();
            }
            else if (objectClass.equals(SmartDate.class)) {
                object = (T) stringToSmartDate(string);
            }
            else if (objectClass.equals(SmartLocalDate.class)) {
                object = (T) stringToSmartLocalDate(string);
            }
            else if (objectClass.equals(SmartLocalDateTime.class)) {
                object = (T) stringToSmartLocalDateTime(string);
            }
            else if (objectClass.equals(SmartLocalTime.class)) {
                object = (T) stringToSmartLocalTime(string);
            }
            else if (objectClass.equals(File.class)) {
                object = (T) stringToFile(string);
            }
            else if (objectClass.equals(java.net.URL.class)) {
                object = (T) stringToURL(string);
            }
            else if (objectClass.equals(java.net.URI.class)) {
                object = (T) stringToURI(string);
            }
            else if (objectClass.equals(Path.class)) {
                object = (T) stringToPath(string);
            }
            else if (objectClass.equals(JSONObject.class)) {
                object = (T) ConverterUtils.stringToJasonObject(string);
            }
            else if (objectClass.equals(JSONArray.class)) {
                object = (T) stringToJasonArray(string);
            }
            else {
                if (type.isArrayType()) {
                    object = (T) ConverterUtils.stringToArray(type, string);
                }
                else if (objectClass.isAssignableFrom(List.class)) {
                    object = (T) ConverterUtils.stringToList(type, string);
                }
                else if (objectClass.isAssignableFrom(Set.class)) {
                    object = (T) ConverterUtils.stringToSet(type, string);
                }
                else if (objectClass.isAssignableFrom(Queue.class)) {
                    object = (T) ConverterUtils.stringToQueue(type, string);
                }
                else if (objectClass.isAssignableFrom(Vector.class)) {
                    object = (T) ConverterUtils.stringToVector(type, string);
                }
                else if (objectClass.isAssignableFrom(Map.class)) {
                    object = (T) ConverterUtils.stringToMap(type, string);
                }
                else if (objectClass.isAssignableFrom(Document.class)) {
                    object = (T) ConverterUtils.stringToXmlDocument(string);
                }
                else if (objectClass.isAssignableFrom(Node.class)) {
                    object = (T) ConverterUtils.stringToXmlNode(string);
                }
                else if (objectClass.isEnum()) {
                    object = (T) stringToEnumValue(type, string);
                }
                else if (objectClass.isRecord()) {
                    object = stringToRecord(objectClass, string);
                }
                else if (type.getFieldTypesMap() != null) {
                    object = stringToPojoObject(type, string);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                            Cannot convert string to object.
                            String:
                            %s
                            Target type:
                            %s
                            """.stripIndent(),
                            string, type));
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
     * @param string The string.
     * @return The string buffer.
     */
    public static StringBuffer stringToStringBuffer(String string) {
        DataValidationUtils.validateNotEmpty(string, "string");
        return new StringBuffer(string);
    }

    /**
     * Converts CVS string to JSON array.
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

                    if (columnsSize < 2) {
                        throw new SmartRuntimeException(String.format("""
                                    CSV file line does not have a delimiter.
                                    CSV line:
                                    {}
                                    """.stripIndent(),
                                line));
                    }
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
     * @param collection The collection.
     * @return The JSON array.
     */
    public static <T> JSONArray collectionToJsonArray(Collection<T> collection) {
        DataValidationUtils.validateNotNull(collection, "collection");

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
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert collection to JSON array:\n'%s'", collection));
        }
    }

    /**
     * Converts map to JSON object.
     * @param map The map.
     * @return The JSON object.
     */
    public static <K, V> JSONObject mapToJSONObject(Map<K, V> map) {
        DataValidationUtils.validateNotNull(map, "map");

        try {
            return new JSONObject(map);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert map to JSON object:\n'%s'", map));
        }
    }

    /**
     * Convers JSON array to array.
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
            T[]array;

            if (!list.isEmpty()) {
                Class<?> valueClass = list.get(0).getClass();
                array = (T[]) Array.newInstance(valueClass, jsonArray.length());
                list.toArray(array);
            }
            else {
                array = (T[]) Array.newInstance(Object.class, 0);
            }
            log.debug("""
                    JSON array converted to array.
                    JSON array:
                    {}
                    Array:
                    {}
                    """.stripIndent(),
                    jsonArray, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert JSON array to array.
                            JSON array:
                            {}
                            """.stripIndent(),
                    jsonArray), e);
        }
    }

    /**
     * Converts array to JSON array.
     * @param array The array.
     * @param <T>   The array type.
     * @return The JSON array.
     */
    public static <T> JSONArray arrayToJsonArray(T[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            JSONArray jsonArray = new JSONArray();

            for (T element : array) {

                if (element == null) {
                    jsonArray.put(element);
                }
                else if (element instanceof String string) {
                    if (isXmlString(string)) {
                        JSONObject xmlJson = xmlStringToJsonObject(string);
                        jsonArray.put(xmlJson);
                    }
                    else if (isJsonObjectString(string)) {
                        JSONObject jsObject = stringToJasonObject(string);
                        jsonArray.put(jsObject);
                    }
                    else {
                        jsonArray.put(string);
                    }
                }
                else if (isJsonValue(element)) {
                    jsonArray.put(element);
                }
                else if (element instanceof Record record) {
                    JSONObject recordJson = recordToJsonObject(record);
                    jsonArray.put(recordJson);
                }
                else if (isPojoObject(element)) {
                    JSONObject pojoJson = pojoObjectToJson(element);
                    jsonArray.put(pojoJson);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                            Cannot convert array element to JSON array element.
                            Array element:
                            %s
                            """.stripIndent(),
                            element));
                }
            }
            log.debug("""
                    Array converted to JSON array.
                    Array:
                    {}
                    JSON array:
                    {}
                    """.stripIndent(),
                    array,
                    jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert array to JSON array.
                            Array:
                            %s
                            """.stripIndent(),
                            array), e);
        }
    }

    /**
     * Converts object to JSON array.
     * @param object The object.
     * @return The JSON array.
     * @param <T> The collection element type.
     */
    public static <T> JSONArray objectToJsonArray(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
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
        DataValidationUtils.validateNotNull(object, "object");
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
            else if (object instanceof Record record) {
                jsonObject = recordToJsonObject(record);
            }
            else if (isPojoObject(object)) {
                jsonObject = pojoObjectToJson(object);
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert object to JSON array:\n%s", object));
            }
            log.debug("Object is converted to JSON array:\n{}", jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to JSON array:\n%s", object), e);
        }
    }

    /**
     * Converts object to XML node object.
     * @param object The object.
     * @return The XML node object.
     */
    public static Node objectToXmlNode(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
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
            else if (object instanceof Record record) {
                JSONObject jsonObject = recordToJsonObject(record);
                xmlNode = jsonObjectToXmlNode(jsonObject);
            }
            else if (isPojoObject(object)) {
                JSONObject jsonObject = pojoObjectToJson(object);
                xmlNode = jsonObjectToXmlNode(jsonObject);
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert %s type to JSON array",
                        object.getClass().getName()));
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
     * Converts a string to a Java Record object.
     * @param recordClass The Record class type.
     * @param string      The string (in JSON or XML format).
     * @return The Java Record object.
     */
    public static <T> T stringToRecord(Class<?> recordClass, String string) {
        DataValidationUtils.validateNotNull(recordClass, "recordClass");
        DataValidationUtils.validateNotBlank(string, "string");

        if (!recordClass.isRecord()) {
            throw new SmartRuntimeException(String.format(
                    "Provided class is not a Record: %s", recordClass.getName()));
        }
        try {
            JSONObject jsonObject;

            if (isXmlString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = new JSONObject(string);
            }
            T record = createRecordInstanceFromJson(recordClass, jsonObject);
            log.debug("""
                    String converted to record.
                    String:
                    {}
                    Record class: {}
                    Record:
                    {}
                    """.stripIndent(),
                    string, recordClass.getName(), record);
            return record;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to Java Record object.
                    String:
                    %s
                    Record class: %s
                    """.stripIndent(),
                    string, recordClass.getName()), e);
        }
    }

    /**
     * Converts record object to JSON object.
     * @param record The record.
     * @return The JSON object.
     */
    public static JSONObject recordToJsonObject(Record record) {
        DataValidationUtils.validateNotNull(record, "record");
        JSONObject jsonObject = new JSONObject();

        try {
            RecordComponent[] components = record.getClass().getRecordComponents();

            for (RecordComponent component : components) {
                String name = component.getName();
                Object value = component.getAccessor().invoke(record);
                jsonObject.put(name, value);
            }
            log.debug("""
                    Record is converted to JSON object.
                    Record class: {}
                    Record:
                    {}
                    JSON:
                    {}
                    """.stripIndent(),
                    record.getClass().getName(),
                    record, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert record to JSON object.
                    Record class: %s
                    Record:
                    %s
                    """.stripIndent(),
                    record.getClass().getName(),
                    record), e);
        }
    }

    /**
     * Converts record object to string.
     * @param record The record object.
     * @return The string.
     */
    public static String recordToString(Record record) {
        DataValidationUtils.validateNotNull(record, "record");

        try {
            JSONObject jsonObject = recordToJsonObject(record);
            String string = jsonObjectToString(jsonObject);
            log.debug("""
                    Record is converted to string.
                    Record class: {}
                    Record:
                    {}
                    String:
                    {}
                    """.stripIndent(),
                    record.getClass().getName(),
                    record, string);
            return string;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert record to string.
                    Record class{ %s
                    Record:
                    %s
                    """.stripIndent(),
                    record.getClass().getName(), record), e);
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
                      enumValue = stringToEnumValue(type, string.toUpperCase());
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
     * Converts array to string.
     * @param array The array.
     * @param <T>   The array type.
     * @return The array string.
     */
    public static <T> String arrayToString(T[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            JSONArray jsonArray = arrayToJsonArray(array);
            String arrayString = jsonArrayToString(jsonArray);
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
     * @param targetType The array type.
     * @param string    The array string.
     * @param <T>       The array type.
     * @return The array.
     */
    public static <T> T[] stringToArray(SmartType targetType, String string) {
        DataValidationUtils.validateNotNull(targetType, "targetType");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            if (isXmlString(string)) {
                Node xmlNode = stringToXmlNode(string);
                string = xmlNodeToString(xmlNode);
            }
            else if (isCsvString(string)) {
                JSONArray jsonArray = stringToJasonArray(string);
                string = jsonArrayToString(jsonArray);
            }
            JSONArray jsonArray = new JSONArray(string);
            Class<?> valueClass = targetType.getValueSmartType().getObjectClass();
            T[] array = (T[]) Array.newInstance(valueClass, jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                // Get each JSONObject from the JSONArray
                Object jsonValue = jsonArray.get(i);

                // Convert JSON element to target type T
                T element = objectToObject(targetType.getValueSmartType(), jsonValue);
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

        try {
            JSONArray jsonArray = stringToJasonArray(string);
            Collection<T> collection = jsonArrayToCollection(type, jsonArray);
            List<T> list = new ArrayList<>(collection);
            log.debug("""
                    String converted to List.
                    String:
                    {}
                    List:
                    {}
                    """.stripIndent(),
                    string, list);
            return list;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to list.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON array format to set.
     * @param type The set type.
     * @param string The string.
     * @param <T> The set element type.
     * @return The set.
     */
    public static <T> Set<T> stringToSet(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONArray jsonArray = stringToJasonArray(string);
            Collection<T> collection = jsonArrayToCollection(type, jsonArray);
            Set<T> set = new HashSet<>(collection);
            log.debug("""
                    String converted to set.
                    String:
                    {}
                    Set:
                    {}
                    """.stripIndent(),
                    string, set);
            return set;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to set.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
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

        try {
            JSONArray jsonArray = stringToJasonArray(string);
            Collection<T> collection = jsonArrayToCollection(type, jsonArray);
            Queue<T> queue = new LinkedList<>(collection);
            log.debug("""
                    String converted to queue.
                    String:
                    {}
                    Queue:
                    {}
                    """.stripIndent(),
                    string, queue);
            return queue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to queue.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
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

        try {
            JSONArray jsonArray = stringToJasonArray(string);
            Collection<T> collection = jsonArrayToCollection(type, jsonArray);
            Vector<T> vector = new Vector<>(collection);
            log.debug("""
                    String converted to vector.
                    String:
                    {}
                    Queue:
                    {}
                    """.stripIndent(),
                    string, vector);
            return vector;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to vector.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON object format to map.
     * @param type      The target type.
     * @param string    The string.
     * @param <K>       The key type.
     * @param <V>       The value type.
     * @return The map.
     */
    public static <K,V> Map<K,V> stringToMap(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONObject jsonObject;

            if (isXmlString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = stringToJasonObject(string);
            }
            Map<K,V> map = jsonObjectToMap(type, jsonObject);
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
    public static <K,V> Map<K,V> jsonObjectToMap(SmartType type, JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            Class<K> keyClass = (Class<K>) type.getKeyClass();
            Class<V> valueClass = (Class<V>) type.getValueSmartType().getObjectClass();
            Map<K,V> map = createMapFromClasses(keyClass, valueClass);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String stringKey = keys.next();
                K key = objectToObject(SmartType.fromClass(type.getKeyClass()), stringKey);
                Object jsonValue = jsonObject.get(stringKey);
                SmartType elementType = SmartType.fromClass(valueClass);
                V value = objectToObject(elementType, jsonValue);

                map.put(key, value);
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

            if (xmlString.isEmpty()) {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert JSON object to XML node:\n%s",
                        jsonObject));
            }
            xmlString = String.format("<object>%s</object>", xmlString);

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
     * @param xmlString XML string.
     * @return The JSON object.
     */
    public static JSONObject xmlStringToJsonObject(String xmlString) {
        DataValidationUtils.validateNotBlank(xmlString, "xmlString");

        try {
            xmlString = escapeXmlString(xmlString);
            xmlString = unescapeXmlString(xmlString);
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            Iterator<String> keys = jsonObject.keys();

            // Check if the JSON objet has a root element and return its child JSON object.
            if (keys.hasNext()) {
                String rootKey = keys.next();
                jsonObject = jsonObject.getJSONObject(rootKey);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert XML string to JSON object.
                        XML string:
                        %s
                        """.stripIndent(),
                        xmlString));
            }
            log.debug("""
                    XML string converted to JSON object.
                    String:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    xmlString, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
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
     * @param xmlNode XML string.
     * @return The JSON object.
     */
    public static JSONObject xmlNodeToJsonObject(Node xmlNode) {
        DataValidationUtils.validateNotNull(xmlNode, "xmlNode");

        try {
            String xmlString = xmlNodeToString(xmlNode);
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            log.debug("""
                    XML node converted to JSON object.
                    XML:
                    {}
                    JSON:
                    {}
                    """.stripIndent(),
                    xmlNode, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
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
                number = Float.parseFloat(numberString);

                if (Float.isInfinite((float) number)) {
                    if ((float) number == Float.POSITIVE_INFINITY ||
                        (float) number == Float.NEGATIVE_INFINITY) {
                        number = Double.parseDouble(numberString);

                        if (Double.isInfinite((double) number)) {
                            if ((double) number == Float.POSITIVE_INFINITY ||
                                (double) number == Float.NEGATIVE_INFINITY) {
                                number = new BigDecimal(numberString);
                            }
                        }
                    }
                }
            } else {
                try {
                    number = Integer.parseInt(numberString);
                }
                catch (NumberFormatException e) {
                    try {
                        number = Long.parseLong(numberString);
                    }
                    catch (NumberFormatException nfe) {
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

    /**
     * Normalizes lineSeparators
     * @param string The input string.
     * @return The normalized string.
     */
    public static String normalizeLineSeparators(String string) {
        DataValidationUtils.validateNotNull(string, "string");

        return string.replace("\r\n", "\n");
    }

    public static String normalizeStringEncoding(String string) {
        DataValidationUtils.validateNotNull(string, "string");

        byte[] encoded = string.getBytes();
        String normalizedString = new String(encoded, StandardCharsets.UTF_8);
        log.debug("""
                String encoding is normalized.
                String:
                {}
                Normalized:
                {}
                """.stripIndent(),
                string, normalizedString);
        return normalizedString;
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
                object.equals(POSITIVE_INFINITY));
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

    private static <T> void stringToCollection(
            SmartType smartType,
            String string,
            Collection<T> collection) {
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

        char[] delimiters = {',', ';', '|', ':', '\t'};
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
                }
                else if (i < length - 1) {
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
            List<T> list = new ArrayList<>(Arrays.asList(array));
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

    private static boolean isPojoObject(Object object) {
        boolean result;

        if (object == null) {
            result = false;
        }
        else if (isNaN(object)) {
            result = false;
        }
        else if (isFloatPositiveInfinite(object) ||
                isDoublePositiveInfinite(object) ||
                isFloatNegativeInfinite(object) ||
                isDoubleNegativeInfinite(object)) {
            result = false;
        }
        else if (object instanceof Number ||
                object instanceof Boolean ||
                object instanceof Date ||
                object instanceof File ||
                object instanceof URL ||
                object instanceof URI ||
                object instanceof Path ||
                object instanceof Temporal ||
                object instanceof SmartValue ||
                object instanceof SmartObject ||
                object instanceof SmartTemporal ||
                object instanceof SmartType) {
            result = false;
        }
        else if (object instanceof StringBuffer) {
            result = false;
        }
        else if (object instanceof String) {
            result = false;
        }
        else if (object instanceof JSONObject) {
            result = false;
        }
        else if (object instanceof JSONArray) {
            result = false;
        }
        else if (object instanceof Node) {
            result = false;
        }
        else if (object instanceof List) {
            result = false;
        }
        else if (object instanceof Map) {
            result = false;
        }
        else if (isArray(object)) {
            result = false;
        }
        else {
            Class<?> objectClass = object.getClass();

            result = !objectClass.isPrimitive() &&
                    !objectClass.isArray() &&
                    !objectClass.isInterface() &&
                    !objectClass.isRecord() &&
                    !objectClass.isEnum() &&
                    !objectClass.isAnnotation() &&
                    !objectClass.isHidden() &&
                    !objectClass.isAnonymousClass();
        }
        log.debug("""
                Object is POJO?
                Result: {}
                Object:
                {}
                """.stripIndent(),
                result, object);
        return result;
    }

    private static boolean isJsonTypeObject(Object object) {
        boolean result;
        if (object == null) {
            result = true;
        }
        else {
            Class<?> objectClass = object.getClass();

            result = objectClass.isPrimitive() ||
                    objectClass == String.class ||
                    objectClass == Boolean.class ||
                    objectClass == Byte.class ||
                    objectClass == Character.class ||
                    objectClass.isArray() ||
                    objectClass.isEnum() ||
                    object instanceof Number ||
                    object instanceof JSONArray ||
                    object instanceof JSONObject ||
                    object instanceof Collection ||
                    object instanceof Map;
        }
        log.debug("""
                Object type is JSON type?
                Result: {}
                Object:
                {}
                """.stripIndent(),
                result, object);
        return result;
    }

    private static boolean isCsvNoneStringObject(Object object) {
        boolean result;

        if (object == null) {
            result = true;
        }
        else if (object instanceof String) {
            result = false;
        }
        else {
            Class<?> objectClass = object.getClass();

            result = objectClass.isPrimitive() ||
                    objectClass == Boolean.class ||
                    objectClass == Byte.class ||
                    objectClass == Character.class ||
                    object instanceof Number;
        }
        log.debug("""
                Object type is JSON type?
                Result: {}
                Object:
                {}
                """.stripIndent(),
                result, object);
        return result;
    }

    private static boolean isXmlString(String string) {
        try {
            string = escapeXmlString(string);
            string = unescapeXmlString(string);

            // Initialize a document builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.parse(new ByteArrayInputStream(string.getBytes()));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean isJsonObjectString(String string) {

        if (isXmlString(string)) {
            return false;
        }
        try {
            new JSONObject(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean isJsonArrayString(String string) {

        if (isXmlString(string)) {
            return false;
        }
        try {
            new JSONArray(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean isCsvString(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        string = string.trim();

        if (string.startsWith("[") || string.startsWith("{") ||
           (string.endsWith("]") || string.endsWith("}"))) {
            return false;
        }
        try {
            csvStringToJsonArray(string);
            return true;
        }
        catch (SmartRuntimeException e) {
            return false;
        }
    }

    private static <T> T createRecordInstanceFromJson(Class<?> recordClass, JSONObject jsonObject) throws Exception {
        Constructor<?> canonicalConstructor = recordClass.getDeclaredConstructors()[0];
        RecordComponent[] recordComponents = recordClass.getRecordComponents();
        Object[] constructorArgs = new Object[recordComponents.length];

        // Iterate over the record components (fields)
        for (int i = 0; i < recordComponents.length; i++) {
            String fieldName = recordComponents[i].getName();

            // Set the value from the JSON object if present
            if (jsonObject.has(fieldName)) {
                constructorArgs[i] = jsonObject.get(fieldName);
            }
            else {
                String firstKey;
                Object firstValue;
                Iterator<String> keys = jsonObject.keys();

                if (keys.hasNext()) {
                    firstKey = keys.next();
                    firstValue = jsonObject.get(firstKey);

                    if (firstValue instanceof JSONObject childJson &&
                            childJson.has(fieldName)) {
                        constructorArgs[i] = childJson.get(fieldName);
                    }
                    else {
                        throw new SmartRuntimeException(String.format("""
                                Cannot convert JSON object to record object.
                                Field name is not present in JSON object.
                                Field name: %s
                                JSON:
                                %s
                                """.stripIndent(),
                                fieldName, jsonObject));
                    }
                }
            }
        }
        // Instantiate the Record using its canonical constructor
        return (T) canonicalConstructor.newInstance(constructorArgs);
    }

    private static boolean isJsonValue(Object object) {

        if (object == null ||
            object.getClass().isPrimitive() ||
            object instanceof String ||
            object instanceof Number ||
            object instanceof Boolean ||
            object instanceof JSONArray ||
            object instanceof JSONObject ||
            object instanceof Collection ||
            object.getClass().isArray() ||
            object instanceof Map ||
            object instanceof Character) {
            return true;
        }
        else {
            return false;
        }
    }
}


