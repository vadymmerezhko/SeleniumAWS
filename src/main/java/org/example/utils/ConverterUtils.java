package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.example.exceptions.SmartRuntimeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Converter utils.
 */
@Slf4j
public class ConverterUtils {
    private static final String STRING = "String";
    private static final String INTEGER = "Integer";
    private static final String LONG = "Long";
    private static final String BIG_INTEGER = "BigInteger";
    private static final String FLOAT = "Float";
    private static final String DOUBLE = "Double";
    private static final String BIG_DECIMAL = "BigDecimal";
    private static final String BOOLEAN = "Boolean";
    private static final String DATE = "Date";
    private static final String LOCAL_DATE = "LocalDate";
    private static final String JSON_OBJECT = "JSONObject";
    private static final String JSON_ARRAY = "JSONArray";
    private static final String XML_NODE = "Node";
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
    public static int stringToInteger(String string) {
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
    public static long stringToLong(String string) {
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
    public static float stringToFloat(String string) {
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
    public static double stringToDouble(String string) {
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
    public static boolean stringToBoolean(String string) {
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
    public static Date stringToDate(String string) {
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
    public static JSONObject stringToJasonObject(String string) {
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
    public static JSONArray stringToJasonArray(String string) {
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

    /**
     * Converts XML node object to string.
     * @param xml The XML document.
     * @return The XML string.
     */
    public static String xmlToString(Node xml) {
        DataValidationUtils.validateNotNull(xml, "xml");

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(xml);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            String xmlString = writer.toString();
            log.debug("XML document converted to XML string: {}.", xmlString);
            return xmlString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot convert XML document object to string", e);
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
            return jsonObject.toString();
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot convert JSON object to string", e);
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
            return jsonArray.toString();
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
    public static String objectToSting(Object object) {
        DataValidationUtils.validateNotNull(object, "value");
        String clasName = object.getClass().getName();

        if (object.toString().startsWith(String.format("%s@", clasName))) {
            throw new SmartRuntimeException("You need to override default toString() method of %s class.");
        }
        if (object instanceof JSONObject) {
            return ConverterUtils.jsonObjectToString((JSONObject) object);
        }
        else if (object instanceof JSONArray) {
            return ConverterUtils.jsonArrayToString((JSONArray) object);
        }
        else if (object instanceof Node) {
            return ConverterUtils.xmlToString((Node) object);
        }
        else {
            return String.valueOf(object);
        }
    }

    /**
     * Converts object to integer or throws exception if cannot covert.
     * @param object The object to convert.
     * @return The integer value.
     */
    public static int objectToInteger(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        Integer result = null;

        if ((object instanceof String)) {
            result = Integer.parseInt((String) object);
            return result;
        }
        if (!(object instanceof Number)) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert %s to integer.", className));
        }
        try {
            Number number = (Number) object;
            // Use appropriate conversion based on the class of the Number
            if (number instanceof Integer) {
                result =  (Integer) number; // Already an Integer
                return result;
            } else if (number instanceof Byte || number instanceof Short) {
                return number.intValue(); // Convert Byte or Short to Integer
            } else if (number instanceof Long) {
                long value = number.longValue();
                if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                    result = (int) value; // Safely convert Long to Integer
                    return result;
                } else {
                    throw new ArithmeticException("Long value out of Integer range");
                }
            } else if (number instanceof Float || number instanceof Double || number instanceof BigDecimal) {
                double value = number.doubleValue();
                if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                    result = (int) Math.round(value); // Convert floating-point to Integer
                    return result;
                } else {
                    throw new SmartRuntimeException("Floating-point value out of Integer range");
                }
            } else {
                throw new SmartRuntimeException("Unsupported numeric type: " + className);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to integer.", className), e);
        }
        finally {
            log.debug("Converting {} to integer resul: {}.", object, result);
        }
    }

    /**
     * Converts object to long or throws exception if cannot covert.
     * @param object The object to convert.
     * @return The long value.
     */
    public static long objectToLong(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        Long result = null;

        try {
            if (object instanceof Long) {
                result = (Long) object; // Already a Long
                return result;
            } else if (object instanceof Number) {
                // For other Number types like Integer, Byte, Short, Float, Double, BigDecimal
                result = ((Number) object).longValue();
                return result;
            } else if (object instanceof String) {
                result = Long.parseLong((String) object); // Convert String to Long
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to Long: " + className);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to long.", className), e);
        }
        finally {
            log.debug("Converting {} to long resul: {}.", object, result);
        }
    }

    /**
     * Converts object to big integer or throws exception if cannot covert.
     * @param object The object to convert.
     * @return The big integer value.
     */
    public static BigInteger objectToBigInteger(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        BigInteger result = null;

        try {
            if (object instanceof BigInteger) {
                result = (BigInteger) object; // Already a BigInteger
                return result;
            } else if (object instanceof Number) {
                // For other Number types like Integer, Byte, Short, Long, Float, Double, BigDecimal
                if (object instanceof BigDecimal) {
                    result = ((BigDecimal) object).toBigInteger();
                    return result;
                } else if (object instanceof Float || object instanceof Double) {
                    result = BigDecimal.valueOf(((Number) object).doubleValue()).toBigInteger();
                    return result;
                } else {
                    result = BigInteger.valueOf(((Number) object).longValue());
                    return result;
                }
            } else if (object instanceof String) {
                result = new BigInteger((String) object); // Convert String to BigInteger
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to BigInteger: " + object.getClass().getName());
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to big integer.", className), e);
        }
        finally {
            log.debug("Converting {} to big integer resul: {}.", object, result);
        }
    }

    /**
     * Converts object to float or throws exception if cannot covert.
     * @param object The object to convert.
     * @return Teh float value.
     */
    public static float objectToFloat(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        Float result = null;

        try {
            if (object instanceof Float) {
                result = (Float) object; // Already a Float
                return result;
            } else if (object instanceof Number) {
                // For other Number types like Integer, Byte, Short, Long, Double, BigDecimal
                result = ((Number) object).floatValue();
                return result;
            } else if (object instanceof String) {
                result = Float.parseFloat((String) object); // Convert String to Float
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to Float: " + className);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to float.", className), e);
        }
        finally {
            log.debug("Converting {} to float resul: {}.", object, result);
        }
    }

    /**
     * Converts object to double or throws exception if cannot covert.
     * @param object The object to convert.
     * @return Teh double value.
     */
    public static double objectToDouble(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        Double result = null;

        try {
            if (object instanceof Double) {
                result = (Double) object; // Already a Double
                return result;
            } else if (object instanceof Number) {
                // For other Number types like Integer, Byte, Short, Long, Float, BigDecimal
                result = ((Number) object).doubleValue();
                return result;
            } else if (object instanceof String) {
                result = Double.parseDouble((String) object); // Convert String to Double
                return result;
            } else {
                throw new IllegalArgumentException("Unsupported type for conversion to Double: " + object.getClass().getName());
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to double.", className), e);
        }
        finally {
            log.debug("Converting {} to double resul: {}.", object, result);
        }
    }

    /**
     * Converts object to big decimal or throws exception if cannot covert.
     * @param object The object to convert.
     * @return Teh big decimal value.
     */
    public static BigDecimal objetToBigDecimal(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        BigDecimal result = null;

        try {
            if (object instanceof BigDecimal) {
                result = (BigDecimal) object; // Already a BigDecimal
                return result;
            } else if (object instanceof Number) {
                // For other Number types like Integer, Byte, Short, Long, Float, Double
                result = BigDecimal.valueOf(((Number) object).doubleValue());
                return result;
            } else if (object instanceof String) {
                result =new BigDecimal((String) object); // Convert String to BigDecimal
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to BigDecimal: " + object.getClass().getName());
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to big decimal.", className), e);
        }
        finally {
            log.debug("Converting {} to big decimal resul: {}.", object, result);
        }
    }

    /**
     * Converts object to boolean or throws exception if cannot covert.
     * @param object The object to convert.
     * @return The boolean value.
     */
    public static boolean objectToBoolean(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        Boolean result = null;

        try {
            if ((object instanceof Boolean)) {
                result = objectToBoolean(object);
                return result;
            }
            else if (object instanceof String) {
                result = stringToBoolean((String) object);
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to boolean: " + className);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to boolean.", className), e);
        }
        finally {
            log.debug("Converting {} to boolean resul: {}.", object, result);
        }
    }

    /**
     * Converts object to date or throws exception if cannot covert.
     * @param object The object to convert.
     * @return The date value.
     */
    public static Date objectToDate(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        Date result = null;

        try {
            if (object instanceof Date) {
                result = objectToDate(object);
                return result;
            } else if (object instanceof String) {
                result = stringToDate((String) object);
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to date: " + className);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to date.", className), e);
        }
        finally {
            log.debug("Converting {} to date result: {}.", object, result);
        }
    }

    /**
     * Converts object to local date or throws exception if cannot covert.
     * @param object The object to convert.
     * @return The local date value.
     */
    public static LocalDate objectToLocalDate(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        String className = object.getClass().getName();
        LocalDate result = null;

        try {
            if (object instanceof Date) {
                result = ConverterUtils.objectToLocalDate((Date) object);
                return result;
            } else if (object instanceof LocalDate) {
                result = (LocalDate) object;
                return result;
            } else if (object instanceof String) {
                result = objectToLocalDate(stringToDate((String) object));
                return result;
            } else {
                throw new SmartRuntimeException("Unsupported type for conversion to local date: " + className);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("Cannot convert %s to date.", className), e);
        }
        finally {
            log.debug("Converting {} to date result: {}.", object, result);
        }
    }

    /**
     * Converts string to object by object type (simple class name).
     * @param type The type name.
     * @param string The string.
     * @return The object.
     */
    public static Object stringToObject(String type, String string) {
        DataValidationUtils.validateNotBlank(type, "type");
        DataValidationUtils.validateNotBlank(string, "value");
        Object object;

        try {
            switch (type) {
                case STRING -> object = string;
                case INTEGER -> object = Integer.parseInt(string);
                case LONG -> object = Long.parseLong(string);
                case BIG_INTEGER -> object = new BigDecimal(string);
                case FLOAT -> object = Float.parseFloat(string);
                case DOUBLE -> object = Double.parseDouble(string);
                case BIG_DECIMAL -> object = new BigDecimal(string);
                case BOOLEAN -> object = ConverterUtils.stringToBoolean(string);
                case JSON_OBJECT -> object = ConverterUtils.stringToJasonObject(string);
                case JSON_ARRAY -> object = ConverterUtils.stringToJasonArray(string);
                case XML_NODE -> object = ConverterUtils.stringToXmlObject(string);
                default -> {
                    throw new SmartRuntimeException(String.format(
                            "Smart object data type %s is not supported", type));
                }
            }
            log.debug("String '{}' converted to object {}", string, object);
            return object;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to Object.\nString: '%s'", string));
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
