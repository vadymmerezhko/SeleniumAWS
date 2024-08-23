package org.example.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.CompareUtils;
import org.example.utils.ConverterUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Smart value class.
 */
@Slf4j
public class SmartValue {
    private static final String KEYWORD_PLACEHOLDER = "#KEYWORD#";
    private SmartType smartType;
    private String valueString;
    private String format;
    private String valueTemplate;
    private String keywordString;
    private Object value;
    private Object keyword;
    @Getter
    private String className;
    @Getter
    private Map<String, SmartValue> fieldValuesMap;
    @Setter @Getter
    private boolean strictOrder = false;
    @Setter @Getter
    private boolean strictType = true;

    /**
     * Constructs smart value.
     * Not initialized yet.
     */
    public SmartValue() {
    }

    /**
     * Constructs smart value by value object.
     * @param value The value object.
     */
    public SmartValue(Object value) {
        setValue(value);
    }

    /**
     *Constructs smart value by value object and keyword.
     * @param value The value object.
     * @param keyword The keyword.
     */
    public SmartValue(Object value, Object keyword) {
        setKeyword(keyword);
        setValue(value);
        log.debug("""
                Smart type object is created.
                Type: {}
                Value: {}
                Value template: {}
                """.stripIndent(),
                this.smartType, this.valueString,
                this.valueTemplate);
    }

    /**
     * Constructs smart value from class name abd field values map.
     * @param fieldValuesMap The class field smart values.
     */
    SmartValue(String className, Map<String, SmartValue> fieldValuesMap) {
        this.className = className;
        this.fieldValuesMap = fieldValuesMap;
        this.setUp();
        log.debug("""
                Smart type object is created.
                Type: {}
                Value: {}
                Value template: {}
                """.stripIndent(),
                this.smartType, this.valueString,
                this.valueTemplate);
    }

    /**
     * Smart value constructor by type name, value string and keyword string.
     * @param smartType The type name.
     * @param valueString The value string.
     */
    SmartValue(SmartType smartType, String valueString) {
        this(smartType, valueString, null);
    }

    /**
     * Smart value constructor by type name, value string and keyword string.
     * @param smartType The type name.
     * @param valueString The value string.
     */
    SmartValue(SmartType smartType, String valueString, String format) {
        this.smartType = smartType;
        this.valueString = valueString;
        this.format = format;
        valueTemplate = valueString;
        value = ConverterUtils.stringToObject(smartType, valueString);
        this.setUp();
        log.debug("""
                Smart type object is created.
                Type: {}
                Value: {}
                Value template: {}
                Keyword: {}
                """.stripIndent(),
                this.smartType, this.valueString,
                this.valueTemplate, this.keywordString);
    }

    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart value equals() called. The actual smart value object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart value equals() called. The actual and expected are the same object.");
            return true;
        }
        if (object instanceof SmartValue actualValue) {
            SmartValue expectedValue = this;
            try {
                boolean result = CompareUtils.compareObjects(
                        expectedValue, actualValue,
                        strictType, strictOrder);
                log.debug("""
                                Two smart class objects are compared.
                                Strict order: {}
                                Strict type: {}
                                Expected:
                                {}
                                Actual:
                                {}
                                Result:
                                {}
                                """.stripIndent(),
                        strictOrder, expectedValue, actualValue, result);
                return result;
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                                Cannot compare two smart class objects.
                                Expected:
                                %s
                                Actual:
                                %s
                                """.stripIndent(),
                        expectedValue,
                        actualValue), e);
            }
        }
        log.debug("Smart value equals() returned false.\n" +
                "The actual object is not a smart value.");
        return false;
    }

    /**
     * Converts smart value to string.
     * @return The value string.
     */
    public String toString() {
        setUp();
        log.debug("Smart value is returned: {}", valueString);
        return valueString;
    }

    public int hashCode() {
        setUp();
        return Objects.hash(smartType, value, valueString,
                valueTemplate, keyword, keywordString, format);
    }

    /**
     * Sets value object.
     * @param value The value object.
     */
    public void setValue(Object value) {
        this.value = value;
        valueString = ConverterUtils.objectToSting(value);
        valueTemplate = valueString;
        smartType = SmartType.fromObject(value);
        this.setUp();
        log.debug("Smart type value object is set to: {}", value);
    }

    /**
     * Gets value object.
     * @return The value object.
     */
    public Object getValue() {
        setUp();
        replaceKeywordPlaceholderIfDefined();
        log.debug("Smart type value object is returned: {}", value);
        return value;
    }

    /**
     * Gets value string.
     * @return The value string.
     */
    String getFormat() {
        setUp();
        log.debug("Smart value format returned: {}", format);
        return format;
    }

    /**
     * Gets value string.
     * @return The value string.
     */
    String getValueTemplate() {
        setUp();
        log.debug("Smart type value template is returned: {}", valueTemplate);
        return valueTemplate;
    }

    /**
     * Sets keyword object.
     * @param keyword The keyword object.
     */
    public void setKeyword(Object keyword) {
        this.keyword = keyword;

        if (keyword != null) {
            keywordString = ConverterUtils.objectToSting(keyword);
        }
        this.setUp();
        log.debug("Smart type keyword object is set: {}", keyword);
    }

    /**
     * Gets keyword object.
     * @return The keyword object.
     */
    public Object getKeyword() {
        log.debug("Smart type keyword object is returned: {}", keyword);
        return keyword;
    }

    /**
     * Gets value type.
     * @return The type.
     */
    public SmartType getSmartType() {
        setUp();
        log.debug("Smart type name is returned: {}", smartType);
        return smartType;
    }

    /**
     * Gets keyword string.
     * @return The keyword string.
     */
    String getKeywordString() {
        log.debug("Smart type keyword string is returned: {}", keyword);
        return keywordString;
    }

    /**
     * Converts value to char value.
     * @return The char value.
     */
    public char toCharacter() {
        setUp();
        char result = ConverterUtils.objectToCharacter(value);
        log.debug("""
                Smart class value converted to character.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to short value.
     * @return The short value.
     */
    public short toShort() {
        setUp();
        short result = ConverterUtils.objectToShort(value);
        log.debug("""
                Smart class value converted to short.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to integer value.
     * @return The integer value.
     */
    public int toInteger() {
        setUp();
        int result = ConverterUtils.objectToInteger(value);
        log.debug("""
                Smart class value converted to integer.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to long value.
     * @return The long value.
     */
    public long toLong() {
        setUp();
        long result = ConverterUtils.objectToLong(value);
        log.debug("""
                Smart class value converted to long.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to big integer value.
     * @return The long value.
     */
    public BigInteger toBigInteger() {
        setUp();
        BigInteger result = ConverterUtils.objectToBigInteger(value);
        log.debug("""
                Smart class value converted to big integer.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to float value.
     * @return The float value.
     */
    public float toFloat() {
        setUp();
        float result = ConverterUtils.objectToFloat(value);
        log.debug("""
                Smart class value converted to float.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to double value.
     * @return The double value.
     */
    public double toDouble() {
        setUp();
        double result = ConverterUtils.objectToDouble(value);
        log.debug("""
                Smart class value converted to double.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to big decimal value.
     * @return The big decimal value.
     */
    public BigDecimal toBigDecimal() {
        setUp();
        BigDecimal result = ConverterUtils.objetToBigDecimal(value);
        log.debug("""
                Smart class value converted to decimal.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to boolean value.
     * @return The boolean value.
     */
    public boolean toBoolean() {
        setUp();
        boolean result = ConverterUtils.objectToBoolean(value);
        log.debug("""
                Smart class value converted to boolean.
                Value: {}
                Boolean: {}
                """.stripIndent(),
                value,
                result);
        return result;
    }

    /**
     * Converts value to date value.
     * @return The date value.
     */
    public Date toDate() {
        setUp();
        Date result = ConverterUtils.objectToDate(value);
        log.debug("""
                Smart class value converted to date.
                Value: {}
                Format: {}
                Date: {}
                """.stripIndent(),
                value,
                format,
                result);
        return result;
    }

    /**
     * Converts value to local date value.
     * @return The local date value.
     */
    public LocalDate toLocalDate() {
        setUp();
        LocalDate result = ConverterUtils.objectToLocalDate(value);
        log.debug("""
                Smart class value converted to local date.
                Value: {}
                Format: {}
                Local date: {}
                """.stripIndent(),
                value,
                format,
                result);
        return result;
    }

    /**
     * Converts value to local date time value.
     * @return The local date time value.
     */
    public LocalDateTime toLocalDateTime() {
        setUp();
        LocalDateTime result = ConverterUtils.objectToLocalDateTime(value);
        log.debug("""
                Smart class value converted to local date time.
                Value: {}
                Format: {}
                Local date time: {}
                """.stripIndent(),
                value,
                format,
                result);
        return result;
    }

    /**
     * Converts value to local time value.
     * @return The local time  value.
     */
    public LocalTime toLocalTime() {
        setUp();
        LocalTime result = ConverterUtils.objectToLocalTime(value);
        log.debug("""
                Smart class value converted to local time.
                Value: {}
                Format: {}
                Local time: {}
                """.stripIndent(),
                value,
                format,
                result);
        return result;
    }

    /**
     * Converts value to file.
     * @return The file.
     */
    public File toFile() {
        setUp();
        File result = ConverterUtils.objectToFile(value);
        log.debug("""
                Smart class value converted to file.
                Value: {}
                Path: {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to path.
     * @return The path.
     */
    public Path toPath() {
        setUp();
        Path result = ConverterUtils.objectToPath(value);
        log.debug("""
                Smart class value converted to path.
                Value: {}
                Path: {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to URL.
     * @return The URL.
     */
    public URL toURL() {
        setUp();
        URL result = ConverterUtils.objectToURL(value);
        log.debug("""
                Smart class value converted to URL.
                Value: {}
                URL: {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to URI.
     * @return The URI.
     */
    public URI toURI() {
        setUp();
        URI result = ConverterUtils.objectToURI(value);
        log.debug("""
                Smart class value converted to URI.
                Value: {}
                URI: {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to list.
     * @return The list.
     * @param <T> The list type.
     */
    public <T> List<T> toList() {
        setUp();
        List<T> result = ConverterUtils.objectToList(value);
        log.debug("""
                Smart class value converted to list.
                Value:
                {}
                List:
                {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to set.
     * @return The set.
     * @param <T> The set type.
     */
    public <T> Set<T> toSet() {
        setUp();
        Set<T> result = ConverterUtils.objectToSet(value);
        log.debug("""
                Smart class value converted to set.
                Value:
                {}
                Set:
                {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to queue.
     * @return The queue.
     * @param <T> The queue type.
     */
    public <T> Queue<T> toQueue() {
        setUp();
        Queue<T> result = ConverterUtils.objectToQueue(value);
        log.debug("""
                Smart class value converted to queue.
                Value:
                {}
                Queue:
                {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to vector.
     * @return The vector.
     * @param <T> The vector type.
     */
    public <T> Vector<T> toVector() {
        setUp();
        Vector<T> result = ConverterUtils.objectToVector(value);
        log.debug("""
                Smart class value converted to vector.
                Value:
                {}
                Vector:
                {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to map.
     * @return The vector.
     * @param <K> The map key type.
     * @param <V> The map value type.
     */
    public <K, V> Map<K, V> toMap() {
        setUp();
        Map<K, V> result = ConverterUtils.objectToMap(value);
        log.debug("""
                Smart class value converted to vector.
                Value:
                {}
                Map:
                {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to JSON object.
     * @return The file.
     */
    public JSONObject toJsonObject() {
        setUp();
        JSONObject jsonObject = ConverterUtils.objectToJsonObject(value);
        log.debug("""
                JSON object returned.
                Value:
                {}
                """.stripIndent(),
                jsonObject);
        return jsonObject;
    }

    /**
     * Converts value to JSON object.
     * @return The file.
     */
    public JSONArray toJsonArray() {
        setUp();
        JSONArray jsonArray = ConverterUtils.objectToJsonArray(value);
        log.debug("""
                JSON array returned.
                Value:
                {}
                """.stripIndent(),
                jsonArray);
        return jsonArray;
    }

    /**
     * Converts value to JSON object.
     * @return The file.
     */
    public Node toXmlNode() {
        setUp();
        Node xmlNode = ConverterUtils.objectToXmlNode(value);
        log.debug("""
                XML node returned.
                Value:
                {}
                """.stripIndent(),
                xmlNode);
        return xmlNode;
    }

    /**
     * Converts value to JSON object.
     * @return The file.
     * @param <T> The enum type.
     */
    public <T extends Enum<T>> T toEnumValue(String className) {
        setUp();
        T enuValue = ConverterUtils.objectToEnumValue(className, value);
        log.debug("""
                Enum value returned.
                Value:
                {}
                """.stripIndent(),
                enuValue);
        return enuValue;
    }

    /**
     * Converts value to JSON object.
     * @return The file.
     * @param <T> The class type.
     */
    public <T> T toClassObject(String className) {
        setUp();
        T classValue = ConverterUtils.objectToClassObject(className, value);
        log.debug("""
                Class object returned.
                Value:
                {}
                """.stripIndent(),
                classValue);
        return classValue;
    }

    /**
     * Converts date object to date string with date format.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public String toDateString(String dateFormat) {
        setUp();
        String dateString;

        if (value instanceof Date date) {
            dateString = ConverterUtils.dateToString(date, dateFormat);
        }
        else if (value instanceof LocalDate localDate) {
            dateString = ConverterUtils.localDateToString(localDate, dateFormat);
        }
        else if (value instanceof LocalDateTime localDateTime) {
            dateString = ConverterUtils.localDateTimeToString(localDateTime, dateFormat);
        }
        else if (value instanceof LocalTime localTime) {
            dateString = ConverterUtils.localTimeToString(localTime, dateFormat);
        }
        else if (value instanceof String string) {
            Date date = ConverterUtils.stringToSmartDate(string);
            dateString = ConverterUtils.dateToString(date, dateFormat);
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert %s object to date string.", getClass().getName()));
        }
        log.debug("Smart class object {} converted to date string with format '{}': {}",
                value, dateFormat, dateString);
        return dateString;
    }

    private void replaceKeywordPlaceholderIfDefined() {
        if (keywordString != null) {
            if (valueTemplate.contains(KEYWORD_PLACEHOLDER)) {
                valueString = valueTemplate.replace(KEYWORD_PLACEHOLDER, keywordString);
                value = ConverterUtils.stringToObject(smartType, valueString);
            }
        }
    }

    /**
     * Validates the value.
     */
    void validateValue() {

        try {
            if (keywordIsEmpty()) {
                throw new RuntimeException("Keyword string is empty.");
            }
            else if (valueContainsMoreThanOneKeyword()) {
                throw new RuntimeException(String.format("""
                        Value string contain more then one keyword string.
                        Value:
                        %s
                        Keyword:
                        %s
                        """.stripIndent(),
                        valueString, keywordString));
            }
            else if (valueDoesNotContainKeyword()) {
                throw new RuntimeException(String.format("""
                        Value string does not contain keyword string.
                        Value:
                        %s
                        Keyword:
                        %s
                        """.stripIndent(),
                        valueString, keywordString));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot validate value string: %s", valueString), e);
        }
    }

    /**
     * Returns true if value is valid or false otherwise.
     * @return The true/false flag.
     */
    boolean validValue() {
        boolean result;
        try {
            validateValue();
            result = true;
        }
        catch (Exception e) {
            result = false;
        }
        log.debug("Smart value is valid: {}", result);
        return result;
    }
    int numberOfKeywordsInValue() {
        if (keywordString == null || valueString == null || valueString.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;

        while ((index = valueString.indexOf(keywordString, index)) != -1) {
            count++;
            index += keywordString.length();
        }
        return count;
    }

    boolean keywordIsEmpty() {
        if (keyword != null) {
            return keywordString.isEmpty();
        }
        return false;
    }

    boolean valueContainsMoreThanOneKeyword() {
        if (keyword != null) {
            return numberOfKeywordsInValue() > 1;
        }
        return false;
    }

    boolean valueDoesNotContainKeyword() {
        if (keyword != null) {
            return numberOfKeywordsInValue() == 0;
        }
        return false;
    }

    private void setValueTemplate() {
        if (keywordString != null && valueTemplate != null) {
            valueTemplate = valueString.replace(keywordString, KEYWORD_PLACEHOLDER);
        }
    }

    protected void setUp() {

        if (className != null) {
            setFieldValues();
            setClassValueFromFieldValues();
            valueString = ConverterUtils.objectToSting(value);
            valueTemplate = valueString;
            smartType = SmartType.fromObject(value);
        }
        validateValue();
        setValueTemplate();
    }

    private void setFieldValues() {

        try {
            setUp();
            Field[] fields = value.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(value);
                SmartValue fieldSmartValue = new SmartValue(fieldValue);
                fieldSmartValue.setKeyword(keyword);
                fieldValuesMap.put(fieldName, fieldSmartValue);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot setup class field values.
                    Class name: %s
                    Value:
                    %s
                    """.stripIndent(),
                    value.getClass().getName(), value), e);
        }
    }

    private void setClassValueFromFieldValues() {

        try {
            Class<?> vlaueClass = Class.forName(className);
            Object classValue = vlaueClass.getDeclaredConstructor().newInstance();
            Field[] fields = classValue.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                SmartValue fieldSmartValue = fieldValuesMap.get(fieldName);
                field.set(classValue, fieldSmartValue.getValue());
            }
            value = classValue;
            log.debug("""
                    Class value crested from class name and field smart values.
                    Class name: %s
                    Field values:
                    %s
                    Class value:
                    %s
                    """.stripIndent(),
                    className, fieldValuesMap, classValue);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot create class value from field values.
                    Class name: %s
                    Field values:
                    %s
                    """.stripIndent(),
                    className, fieldValuesMap), e);
        }
    }
}
