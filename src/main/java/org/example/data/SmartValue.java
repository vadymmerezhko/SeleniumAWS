package org.example.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.ui.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Node;

import java.awt.*;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;

/**
 * Smart value class.
 */
@Slf4j
public class SmartValue extends SmartObject implements FormattedValue {
    static final ConcurrentMap<String, SmartValue> valuesMap = readAllDataObjectsFromFiles();
    private static final String SOME_VALUE = "Some value";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String FORMAT = "format";
    private static final String OBJECT_CLASS = "objectClass";
    private static final String KEY_CLASS = "keyClass";
    private static final String VALUE_TYPE = "valueType";
    private static final String FIELD_TYPES = "fieldTypes";
    private static final String FIELD_VALUES = "fieldValues";
    private SmartType smartType;
    private String valueString;
    private String format;
    private String valueTemplate;
    private String keywordString;
    private Object value;
    private Object keyword;
    @Getter
    private String className;
    private SmartObject parent;
    private String name;
    // Root name is needed when converting JSON to XML - its taken from value parameter name.
    private String parentName;
    private String fieldName;
    @Getter
    private Map<String, SmartValue> fieldValuesMap;
    @Setter
    @Getter
    private boolean strictOrder = false;
    @Setter
    @Getter
    private boolean strictType = true;
    private boolean createdFromTemplate = false;
    private boolean valueSet = false;
    boolean classField = false;

    /**
     * Reads asynchronously all data objects from JSON file
     */
    private static ConcurrentMap<String, SmartValue> readAllDataObjectsFromFiles() {
        Set<String> fileNames = FileSystemUtils.getFileNamesInFolder(DATA_OBJECTS_FOLDER_PATH);
        ConcurrentMap<String, SmartValue> valuesMap = new ConcurrentHashMap<>();

        Thread thread = new Thread(() -> {
            try {
                log.debug("Asynchronous data object files reading began.");

                for (String fileName : fileNames) {
                    readValueFromFile(fileName, valuesMap);
                }
                log.debug("Asynchronous reading data objects from files finished.");
            } catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read all data object files from: %s",
                        DATA_OBJECTS_FOLDER_PATH), e);
            }
        });
        thread.start();
        return valuesMap;
    }

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
     * Constructs smart value by value object and keyword.
     * @param value   The value object.
     * @param keyword The keyword.
     */
    public SmartValue(Object value, Object keyword) {
        setValue(value);
        setKeyword(keyword);
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
        classField = true;
        setUpValue();
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
     * Smart value constructor by type name, value template string.
     * @param smartType     The type name.
     * @param valueTemplate The value template string..
     */
    SmartValue(SmartType smartType, String valueTemplate) {
        this(smartType, valueTemplate, null);
    }

    /**
     * Smart value constructor by type name, value template and format string.
     * @param smartType     The type name.
     * @param valueTemplate The value string.
     */
    SmartValue(SmartType smartType, String valueTemplate, String format) {
        this.smartType = smartType;
        this.format = format;
        this.valueTemplate = valueTemplate;
        createdFromTemplate = true;
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

    /**
     * Converts smart value to string.
     * @return The value string.
     */
    @Override
    public String toString() {
        setUp();
        log.debug("Smart value is converted to string:\n{}", valueString);
        return valueString;
    }

    /**
     * Compares this smart value to other object.
     * @param object The object.
     * @return The result: equals - true, otherwise - false.
     */
    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart value equals() called. The actual object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart value equals() called. The actual and expected are the same object.");
            return true;
        }
        if (object instanceof SmartValue actualValue) {
            try {
                boolean result = CompareUtils.compareObjects(
                        this, actualValue,
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
                        strictOrder, this, actualValue, result);
                return result;
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                        Cannot compare two smart value objects.
                        Expected:
                        %s
                        Actual:
                        %s
                        """.stripIndent(),
                        this, actualValue), e);
            }
        }
        log.debug("Smart value equals() returned false.\n" +
                "The actual object is not a smart value.");
        return false;
    }

    @Override
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
        // Check if value is also smart value object
        if (value instanceof SmartValue smartValue) {
            // Set just wrapped value of the smart value
            setValue(smartValue.getValue());
        }
        else {
            // Set value as is
            this.value = value;
        }
        smartType = SmartType.fromObject(value);
        valueSet = true;
        log.debug("Smart type value object is set to: {}", value);
    }

    /**
     * Gets value object.
     * Throws an exception if value is not set yet.
     * @return The value object.
     */
    public Object getValue() {
        setUp();
        log.debug("Smart type value object is returned: {}", value);
        return value;
    }

    /**
     * Gets value string.
     * @return The value string.
     */
    public String getFormat() {
        setUp();
        log.debug("Smart value format returned: {}", format);
        return format;
    }

    /**
     * Gets value string.
     * @return The value string.
     */
    public String getValueTemplate() {
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
    // TODO: add unit tests
    String getKeywordString() {
        setUp();
        log.debug("Smart type keyword string is returned: {}", keywordString);
        return keywordString;
    }

    /**
     * Converts value to char value.
     * @return The char value.
     */
    public char toCharacter() {
        setUp();
        char result = ConvertUtils.objectToObject(SmartType.fromClass(Character.class), value);
        log.debug("""
                Smart value converted to character.
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
        short result = ConvertUtils.objectToObject(SmartType.fromClass(Short.class), value);
        log.debug("""
                Smart value converted to short.
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
        int result = ConvertUtils.objectToObject(SmartType.fromClass(Integer.class), value);
        log.debug("""
                Smart value converted to integer.
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
        long result = ConvertUtils.objectToObject(SmartType.fromClass(Long.class), value);
        log.debug("""
                Smart value converted to long.
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
        BigInteger result = ConvertUtils.objectToObject(SmartType.fromClass(BigInteger.class), value);
        log.debug("""
                Smart value converted to big integer.
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
        float result = ConvertUtils.objectToObject(SmartType.fromClass(Float.class), value);
        log.debug("""
                Smart value converted to float.
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
        double result = ConvertUtils.objectToObject(SmartType.fromClass(Double.class), value);
        log.debug("""
                Smart value converted to double.
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
        BigDecimal result = ConvertUtils.objectToObject(SmartType.fromClass(BigDecimal.class), value);
        log.debug("""
                Smart value converted to decimal.
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
        boolean result = ConvertUtils.objectToObject(SmartType.fromClass(Boolean.class), value);
        log.debug("""
                Smart value converted to boolean.
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
        Date result = ConvertUtils.objectToObject(SmartType.fromClass(Date.class), value);
        log.debug("""
                Smart value converted to date.
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
     * Converts value to smart date value.
     * @return The smart date value.
     */
    public SmartDate toSmartDate() {
        setUp();
        SmartDate result = ConvertUtils.objectToObject(SmartType.fromClass(SmartDate.class), value);
        log.debug("""
                Smart value converted to smart date.
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
        LocalDate result = ConvertUtils.objectToObject(SmartType.fromClass(LocalDate.class), value);
        log.debug("""
                Smart value converted to local date.
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
     * Converts value to smart local date value.
     * @return The smart local date value.
     */
    public SmartLocalDate toSmartLocalDate() {
        setUp();
        SmartLocalDate result = ConvertUtils.objectToObject(SmartType.fromClass(SmartLocalDate.class), value);
        log.debug("""
                Smart value converted to smart local date.
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
     * Converts value to local date time value.
     * @return The local date time value.
     */
    public LocalDateTime toLocalDateTime() {
        setUp();
        LocalDateTime result = ConvertUtils.objectToObject(SmartType.fromClass(LocalDateTime.class), value);
        log.debug("""
                Smart value converted to local date time.
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
     * Converts value to smart local date time value.
     * @return The smart local date time value.
     */
    public SmartLocalDateTime toSmartLocalDateTime() {
        setUp();
        SmartLocalDateTime result = ConvertUtils.objectToObject(SmartType.fromClass(SmartLocalDateTime.class), value);
        log.debug("""
                Smart value converted to smart local date time.
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
     * Converts value to local time value.
     * @return The local time  value.
     */
    public LocalTime toLocalTime() {
        setUp();
        LocalTime result = ConvertUtils.objectToObject(SmartType.fromClass(LocalTime.class), value);
        log.debug("""
                Smart value converted to local time.
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
     * Converts value to smart local time value.
     * @return The smart local time value.
     */
    public SmartLocalTime toSmartLocalTime() {
        setUp();
        SmartLocalTime result = ConvertUtils.objectToObject(SmartType.fromClass(SmartLocalTime.class), value);
        log.debug("""
                Smart value converted to smart local time.
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
     * Converts value to smart number value.
     * @return The smart number value.
     */
    public SmartNumber toSmartNumber() {
        setUp();
        SmartNumber result = ConvertUtils.objectToObject(SmartType.fromClass(SmartNumber.class), value);
        log.debug("""
                Smart value converted to smart number.
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
     * Converts value to smart currency value.
     * @return The smart smart currency value.
     */
    public SmartCurrency toSmartCurrency() {
        setUp();
        SmartCurrency result = ConvertUtils.objectToObject(SmartType.fromClass(SmartCurrency.class), value);
        log.debug("""
                Smart value converted to smart currency.
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
     * Converts value to smart phone number value.
     * @return The smart phone value.
     */
    public SmartPhoneNumber toSmartPhoneNumber() {
        setUp();
        SmartPhoneNumber result = ConvertUtils.objectToObject(SmartType.fromClass(SmartPhoneNumber.class), value);
        log.debug("""
                Smart value converted to smart phone number.
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
     * Converts value to file.
     * @return The file.
     */
    public File toFile() {
        setUp();
        File result = ConvertUtils.objectToFile(value);
        log.debug("""
                Smart value converted to file.
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
        Path result = ConvertUtils.objectToPath(value);
        log.debug("""
                Smart value converted to path.
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
        URL result = ConvertUtils.objectToURL(value);
        log.debug("""
                Smart value converted to URL.
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
        URI result = ConvertUtils.objectToURI(value);
        log.debug("""
                Smart value converted to URI.
                Value: {}
                URI: {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to color value.
     * @return The color value.
     */
    public Color toColor() {
        setUp();
        Color result = ConvertUtils.objectToObject(SmartType.fromClass(Color.class), value);
        log.debug("""
                Smart value converted to color.
                Value: {}
                Format: {}
                Color: {}
                """.stripIndent(),
                value,
                format,
                result);
        return result;
    }

    /**
     * Converts value to array.
     * @param <T> The array value type.
     * @return The array.
     */
    public <T> T[] toArray() {
        setUp();
        T[] result = ConvertUtils.objectToArray(value);
        log.debug("""
                Smart value converted to array.
                Value:
                {}
                Array:
                {}
                """.stripIndent(),
                value, result);
        return result;
    }

    /**
     * Converts value to list.
     * @param <T> The list type.
     * @return The list.
     */
    public <T> List<T> toList() {
        setUp();
        List<T> result = ConvertUtils.objectToList(value);
        log.debug("""
                Smart value converted to list.
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
     * @param <T> The set type.
     * @return The set.
     */
    public <T> Set<T> toSet() {
        setUp();
        Set<T> result = new HashSet<>(toList());
        log.debug("""
                Smart value converted to set.
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
     * @param <T> The queue type.
     * @return The queue.
     */
    public <T> Queue<T> toQueue() {
        setUp();
        Queue<T> result = new LinkedList<>(toList());
        log.debug("""
                Smart value converted to queue.
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
     * @param <T> The vector type.
     * @return The vector.
     */
    public <T> Vector<T> toVector() {
        setUp();
        Vector<T> result = new Vector<>(toList());
        log.debug("""
                Smart value converted to vector.
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
     * It can convert other map, JSON object, XML node,
     * JSON object string and XML string.
     * @param <K> The map key type.
     * @param <V> The map value type.
     * @return The map.
     */
    public <K, V> Map<K, V> toMap() {
        setUp();
        Map<K, V> result = ConvertUtils.objectToMap(value);
        log.debug("""
                Smart value converted to map.
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
        JSONObject jsonObject = ConvertUtils.objectToJsonObject(value);
        log.debug("""
                JSON object returned.
                Value:
                {}
                """.stripIndent(),
                jsonObject);
        return jsonObject;
    }

    /**
     * Converts value to JSON array.
     * @return The JSON array.
     */
    public JSONArray toJsonArray() {
        setUp();
        JSONArray jsonArray = ConvertUtils.objectToJsonArray(value);
        log.debug("""
                JSON array returned.
                Value:
                {}
                """.stripIndent(),
                jsonArray);
        return jsonArray;
    }

    /**
     * Converts value to XML node.
     * @return The XML node.
     */
    public Node toXmlNode() {
        setUp();
        Node xmlNode = ConvertUtils.objectToXmlNode(value);
        log.debug("""
                XML node returned.
                Value:
                {}
                """.stripIndent(),
                xmlNode);
        return xmlNode;
    }

    /**
     * Converts value to Enum value.
     * @param enumType The enum type.
     * @param <T> The enum type.
     * @return The enum value.
     */
    public <T extends Enum<T>> T toEnumValue(SmartType enumType) {
        setUp();
        T enuValue = ConvertUtils.objectToEnumValue(enumType, value);
        log.debug("""
                        Enum value returned.
                        Value:
                        {}
                        """.stripIndent(),
                enuValue);
        return enuValue;
    }

    /**
     * Converts value to POJO object.
     * @param <T> The POJO class type.
     * @return The POJO object.
     */
    public <T> T toPojoObject(SmartType targetType) {
        DataValidationUtils.validateNotNull(targetType, "targetType");
        setUp();
        T classValue = ConvertUtils.objectToObject(targetType, value);
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
            dateString = ConvertUtils.dateToString(date, dateFormat);
        }
        else if (value instanceof LocalDate localDate) {
            dateString = ConvertUtils.localDateToString(localDate, dateFormat);
        }
        else if (value instanceof LocalDateTime localDateTime) {
            dateString = ConvertUtils.localDateTimeToString(localDateTime, dateFormat);
        }
        else if (value instanceof LocalTime localTime) {
            dateString = ConvertUtils.localTimeToString(localTime, dateFormat);
        }
        else if (value instanceof String string) {
            Date date = ConvertUtils.stringToSmartDate(string);
            dateString = ConvertUtils.dateToString(date, dateFormat);
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert %s object to date string.", getClass().getName()));
        }
        log.debug("Smart class object {} converted to date string with format '{}': {}",
                value, dateFormat, dateString);
        return dateString;
    }

    /**
     * Gets smart value name.
     */
    public String getName() {
        setUp();
        log.debug("Returned smart value name: {}.", name);
        return name;
    }

    /**
     * Returns true if smart value can be converted
     * to collection or false otherwise.
     * @return true/false flag.
     */
    public boolean isCollectable() {
        boolean isCollectable =
                value != null &&
                        (value instanceof Collection ||
                        value instanceof JSONArray ||
                        value.getClass().isArray() ||
                        (value instanceof String &&
                                (ConvertUtils.isJsonArrayString((String) value) ||
                                ConvertUtils.isCsvString((String) value) ||
                                ConvertUtils.isXmlArrayString((String) value))));
        log.debug("""
                Smart value is collectable.
                Result: {}
                Value:
                {}
                """.stripIndent(),
                isCollectable, value);
        return isCollectable;
    }

    /**
     * Returns true if smart value can be converted
     * to map or false otherwise.
     * @return true/false flag.
     */
    public boolean isMappable() {
        boolean isCollectable =
                value != null &&
                        (value instanceof Map<?,?> ||
                         value instanceof JSONObject ||
                         value instanceof Node ||
                         ConvertUtils.isPojoObject(value) ||
                         (value instanceof String &&
                                 (ConvertUtils.isJsonObjectString((String) value) ||
                                 ConvertUtils.isXmlNodeString((String) value) ||
                                 ConvertUtils.isXmlDocumentString((String) value))));
        log.debug("""
                Smart value is collectable.
                Result: {}
                Value:
                {}
                """.stripIndent(),
                isCollectable, value);
        return isCollectable;
    }

    public boolean isNumeric() {
        return value != null &&
                (value instanceof Number ||
                (value instanceof String &&
                        ConvertUtils.isNumberString((String) value)));
    }

    /**
     * Saves object value to the file.
     * @param value The value.
     */
    void setAndSaveValue(Object value) {
        setValue(value);
        setUp();
        valuesMap.put(name, this);
        saveValueToFile();
        log.debug("Value is saved and set to: {}", value);
    }

    /**
     * Validates the value.
     */
    void validateValue() {
        try {
            if (keywordIsEmpty()) {
                throw new RuntimeException("Keyword string is empty.");
            } else if (templateDoesNotContainKeywordPlaceholder()) {
                throw new RuntimeException(String.format("""
                                Smart value template does not contain keyword placeholder.
                                Template:
                                %s
                                Placeholder:
                                %s
                                """.stripIndent(),
                        valueTemplate, KEYWORD_PLACEHOLDER));
            } else if (templateContainsMoreThanOneKeywordPlaceholder()) {
                throw new RuntimeException(String.format("""
                                Smart value template contains more than one keyword placeholder.
                                Template:
                                %s
                                Placeholder:
                                %s
                                """.stripIndent(),
                        valueTemplate, KEYWORD_PLACEHOLDER));
            } else if (valueContainsMoreThanOneKeyword()) {
                throw new RuntimeException(String.format("""
                                Smart value contain more then one keyword.
                                Value:
                                %s
                                Keyword:
                                %s
                                """.stripIndent(),
                        valueString, keywordString));
            } else if (valueDoesNotContainKeyword()) {
                throw new RuntimeException(String.format("""
                                Smart value does not contain keyword.
                                Value:
                                %s
                                Keyword:
                                %s
                                """.stripIndent(),
                        valueString, keywordString));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Cannot validate value string: %s", valueString), e);
        }
    }

    private void validateClassFields() {
        int keywordsCount = 0;
        int keywordPlaceholdersCount = 0;

        for (SmartValue fieldValue : fieldValuesMap.values()) {

            if (keyword != null) {
                keywordsCount += TextUtils.getNumberOfKeywordsInString(
                        fieldValue.toString(), keywordString);
                keywordPlaceholdersCount += TextUtils.getNumberOfKeywordsInString(
                        fieldValue.getValueTemplate(), KEYWORD_PLACEHOLDER);
            }
        }
        if (keyword != null) {
            if (keywordsCount == 0) {
                throw new RuntimeException(String.format("""
                                None class field contains keyword.
                                Class name:
                                Class value:
                                %s
                                Keyword:
                                %s
                                """.stripIndent(),
                        className, value, keywordString));
            } else if (keywordsCount > 1) {
                throw new RuntimeException(String.format("""
                                Class fields values contain more than one keyword.
                                Class name:
                                Class value:
                                %s
                                Keyword:
                                %s
                                """.stripIndent(),
                        className, value, keywordString));
            } else if (keywordPlaceholdersCount == 0) {
                throw new RuntimeException(String.format("""
                                None class field value contain keyword placeholder.
                                Class name:
                                Class value:
                                %s
                                Keyword placeholder:
                                %s
                                """.stripIndent(),
                        className, value, KEYWORD_PLACEHOLDER));
            } else if (keywordPlaceholdersCount > 1) {
                throw new RuntimeException(String.format("""
                                Class fields values contain more than one keyword placeholder.
                                Class name:
                                Class value:
                                %s
                                Keyword placeholder:
                                %s
                                """.stripIndent(),
                        className, value, KEYWORD_PLACEHOLDER));
            }
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
        } catch (Exception e) {
            result = false;
        }
        log.debug("Smart value is valid: {}", result);
        return result;
    }

    int numberOfKeywordsInValue() {
        if (keywordString == null || valueString == null || valueString.isEmpty()) {
            return 0;
        }
        return TextUtils.getNumberOfKeywordsInString(valueString, keywordString);
    }

    int numberOfKeywordPlaceholdersInTemplate() {
        if (value == null || valueTemplate == null) {
            return 0;
        }
        return TextUtils.getNumberOfKeywordsInString(valueTemplate, KEYWORD_PLACEHOLDER);
    }

    boolean keywordIsEmpty() {
        if (keyword != null) {
            return keywordString.isEmpty();
        }
        return false;
    }

    boolean valueContainsMoreThanOneKeyword() {
        if (keyword != null && value != null) {
            return numberOfKeywordsInValue() > 1;
        }
        return false;
    }

    boolean valueDoesNotContainKeyword() {
        if (keyword != null && value != null) {
            return numberOfKeywordsInValue() == 0;
        }
        return false;
    }

    boolean templateContainsMoreThanOneKeywordPlaceholder() {
        if (keyword != null && value != null) {
            return numberOfKeywordPlaceholdersInTemplate() > 1;
        }
        return false;
    }

    boolean templateDoesNotContainKeywordPlaceholder() {
        if (keyword != null && value != null) {
            return numberOfKeywordPlaceholdersInTemplate() == 0;
        }
        return false;
    }

    private void setUpValue() {

        if (keyword != null) {
            keywordString = ConvertUtils.objectToString(keyword);
        } else {
            keywordString = null;
        }
        if (createdFromTemplate) {
            createdFromTemplate = false;
            keywordString = ConvertUtils.objectToString(keyword);
            replaceKeywordPlaceholderWithValue();
            value = ConvertUtils.stringToObject(smartType, valueString);
        }
        else {
            valueString = ConvertUtils.objectToString(value);
            valueTemplate = valueString;
            replaceKeywordPlaceholderWithValue();
            replaceKeywordValueWithPlaceholder();
            smartType = SmartType.fromObject(value);
        }
        if (className != null) {
            setFieldValues();
            setClassValueFromFieldValues();
            validateClassFields();
        }
        if (format == null && value != null) {

            if (value instanceof FormattedValue formattedValue) {
                format = formattedValue.getFormat();
            }
        } else {
            validateValue();
        }
    }

    private void setFieldValues() {

        try {
            Field[] fields = value.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(value);
                SmartValue fieldSmartValue = new SmartValue(fieldValue);
                fieldSmartValue.setKeyword(keyword);
                fieldValuesMap.put(fieldName, fieldSmartValue);
            }
        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot create class value from field values.
                            Class name: %s
                            Field values:
                            %s
                            """.stripIndent(),
                    className, fieldValuesMap), e);
        }
    }

    void setParent(SmartObject parent) {
        this.parent = parent;
    }

    private void setUp() {

        if (name == null && !createdFromTemplate) {
            if (parent != null) {
                validateParent();
                parentName = parent.getClass().getSimpleName();
                fieldName = ClassUtils.getObjectFieldName(parent, this);
                name = String.format("%s.%s", parentName, fieldName);

                if (!valueSet) {
                    setUpValueFromFile();
                }
            }
        }
        setUpValue();
    }

    void setUpValueFromFile() {

        try {
            if (valuesMap.containsKey(name)) {
                SmartValue smartValue = valuesMap.get(name);
                smartValue.setKeyword(keyword);
                setValue(smartValue.getValue());
            } else {
                readValueFromFile();
                SmartValue tempValue = new SmartValue(SOME_VALUE);
                SmartType validValueType;
                boolean isValueValid = false;

                if (value == null && Config.getInstance().getDebugMode()) {

                    while (tempValue.toString().equals(SOME_VALUE)) {
                        String promptMessage;

                        if (keyword == null) {
                            promptMessage = String.format("""
                                    UNDEFINED DATA VALUE
                                                            
                                    Enter %s value.
                                                                
                                    Click OK to save.
                                    OR just click OK ro select it on the page.
                                    OR click CANCEL to exit the test.
                                    """.stripIndent(), name);
                        } else {
                            promptMessage = String.format("""
                                    UNDEFINED DATA VALUE
                                                            
                                    Enter %s value with keyword.
                                    Keyword: '%s'
                                                                
                                    Click OK to save.
                                    OR just click OK ro select it on the page.
                                    OR click CANCEL to exit the test.
                                    """.stripIndent(), name, keywordString);
                        }
                        tempValue.setValue(WebUtils.showPrompt(promptMessage, SOME_VALUE));

                        if (tempValue.toString().isEmpty()) {
                            WebDriverFactory.hardSystemExit();
                        }
                        if (tempValue.toString().equals(SOME_VALUE)) {
                            WebElement element = WebUtils.selectWebElement("DATA SOURCE ELEMENT");
                            tempValue.setValue(WebUtils.getElementValueOrText(element));
                        }
                        String stringValue;

                        if (tempValue.validValue()) {
                            validValueType = tempValue.getSmartType();
                            isValueValid = true;
                            stringValue = tempValue.toString();
                            stringValue = WebUtils.showPrompt(String.format("""
                                    VALID DATA VALUE
                                                                
                                    %s data value is valid.
                                                                         
                                    Click OK to save.
                                    OR click CANCEL to exit the test.
                                    """.stripIndent(), name), stringValue);

                            if (stringValue.isEmpty()) {
                                WebDriverFactory.hardSystemExit();
                            } else {
                                if (!tempValue.toString().equals(stringValue)) {
                                    isValueValid = false;
                                    tempValue.setValue(ConvertUtils.stringToObject(
                                            validValueType, stringValue));
                                }
                            }
                        } else {
                            String message = null;

                            if (tempValue.valueDoesNotContainKeyword()) {
                                message = String.format("%s data value does not contain the keyword.",
                                        name);
                            } else if (tempValue.valueContainsMoreThanOneKeyword()) {
                                message = String.format("%s data value contains more than one keyword.",
                                        name);
                            }
                            WebUtils.showAlert(String.format("""
                                            INVALID DATA VALUE
                                                                    
                                            %s
                                            Value: '%s'
                                            Keyword: '%s'
                                                                        
                                            Click OK to update the data value.
                                            """.stripIndent(), message,
                                    tempValue.value,
                                    keywordString));
                        }
                        if (tempValue.toString().isEmpty()) {
                            WebDriverFactory.hardSystemExit();
                        } else if (isValueValid) {
                            setValue(tempValue.getValue());
                            valuesMap.put(name, this);
                            saveValueToFile();
                            break;
                        }
                        tempValue.setValue(SOME_VALUE);
                    }
                }
                if (getSmartType() == null) {
                    throw new RuntimeException(String.format(
                            "Smart class %s is undefined.", name));
                }
            }
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot setup smart data object field value.
                            Field name: %s
                            """.stripIndent(),
                    name), e);
        }
    }

    private void saveValueToFile() {
        String filePath = null;
        String format = getFormat();
        SmartType type = getSmartType();
        String valueTemplate = getValueTemplate();
        String fileName = String.format("%s.json", parentName);
        String jsonString;
        JSONObject valueJson = new JSONObject();
        JSONObject json;

        try {
            SmartType smartType = getSmartType();
            JSONObject typeJson = getJsonFromSmartType(smartType);
            filePath = String.format("%s/%s", DATA_OBJECTS_FOLDER_PATH, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                jsonString = FileSystemUtils.readFile(filePath);
                json = new JSONObject(jsonString);
            } else {
                json = new JSONObject();
            }
            valueJson.put(TYPE, typeJson);

            if (type.getFieldTypesMap() != null) {
                JSONObject classJson = new JSONObject();
                JSONObject fieldValuesJson = getJsonFromFieldsValues(this);
                classJson.put(FIELD_VALUES, fieldValuesJson);
            } else {
                valueJson.put(VALUE, valueTemplate);

                if (format != null) {
                    valueJson.put(FORMAT, format);
                }
            }
            json.put(fieldName, valueJson);
            jsonString = json.toString(JSON_LAYOUT_SPACES);
            FileSystemUtils.createFile(filePath, jsonString);
            log.debug("""
                            Smart value is saved to file.
                            Name: {}
                            Type: {}
                            Format: {}
                            Value: {}
                            File: {}
                            """.stripIndent(),
                    name, type, format, valueTemplate, filePath);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Smart value is saved to file.
                            Name: {}
                            Type: {}
                            Format: {}
                            Value: {}
                            File: {}
                            """.stripIndent(),
                    name, type, format, valueTemplate, filePath), e);
        }
    }

    private void readValueFromFile() {
        String filePath = null;
        String jsonString = null;

        try {
            String fileName = String.format("%s.json", parentName);
            readValueFromFile(fileName, valuesMap);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot read smart class from file.
                            File: %s
                            Content: %s
                            """.stripIndent(),
                    filePath, jsonString), e);
        }
    }

    private void validateParent() {
        if (parent == null) {
            throw new SmartRuntimeException("""
                    ///////////////////////////////////////////////////////////////////////////
                    Please @SmartValue annotation to initialize your data object like this:
                                        
                    @SuppressWarnings("unused")
                    @SmartElement
                    @Getter
                    public class YourDataObject extends SmartData {
                                                
                        private SmartType yourDataField1;
                        private SmartType yourDataField2;
                    }
                    ///////////////////////////////////////////////////////////////////////////
                    """.stripIndent());
        }
    }

    private static JSONObject getJsonFromSmartType(SmartType smartType) {

        try {
            Class<?> objectClass = smartType.getObjectClass();
            Class<?> keyClass = smartType.getKeyClass();
            SmartType valueSmartType = smartType.getValueSmartType();
            JSONObject typeJson = new JSONObject();

            typeJson.put(OBJECT_CLASS, objectClass.getName());

            if (valueSmartType != null) {

                if (keyClass != null) {
                    typeJson.put(KEY_CLASS, keyClass.getName());
                }
                typeJson.put(VALUE_TYPE, getJsonFromSmartType(valueSmartType));
            }
            else if (smartType.getFieldTypesMap() != null) {
                Map<String, SmartType> fieldTypesMap = smartType.getFieldTypesMap();
                JSONObject fieldTypesJson = getJsonFromFieldTypesMap(fieldTypesMap);
                typeJson.put(FIELD_TYPES, fieldTypesJson);
            }
            log.debug("""
                            Smart type converted to JSON object.
                            Smart class:
                            {}
                            JSON:
                            {}
                            """.stripIndent(),
                    smartType, typeJson);
            return typeJson;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot converted smart type to to JSON object.
                            Type:
                            %s
                            """.stripIndent(),
                    smartType), e);
        }
    }

    private static JSONObject getJsonFromFieldTypesMap(Map<String, SmartType> fieldTypesMap) {

        try {
            JSONObject fieldTypesJson = new JSONObject();

            for (String fieldName : fieldTypesMap.keySet()) {
                SmartType fileldSmartType = fieldTypesMap.get(fieldName);
                JSONObject fieldTypeJson = getJsonFromSmartType(fileldSmartType);
                fieldTypesJson.put(fieldName, fieldTypeJson);
            }
            log.debug("""
                            Field types map converted to JSON object.
                            Smart class:
                            {}
                            JSON:
                            {}
                            """.stripIndent(),
                    fieldTypesMap, fieldTypesJson);
            return fieldTypesJson;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot converted field types map to JSON object.
                            Field types:
                            %s
                            """.stripIndent(),
                    fieldTypesMap), e);
        }
    }

    private static SmartType getSmartTypeFromJson(JSONObject typeJson) {
        SmartType smartType;

        try {
            Class<?> objectClass = Class.forName((typeJson.getString(OBJECT_CLASS)));

            if (typeJson.has(VALUE_TYPE)) {
                SmartType valueSmartType = getSmartTypeFromJson(typeJson.getJSONObject(VALUE_TYPE));

                if (typeJson.has(KEY_CLASS)) {
                    Class<?> keyClass = Class.forName(typeJson.getString(KEY_CLASS));
                    smartType = SmartType.fromMapClass(objectClass, keyClass, valueSmartType);
                } else {
                    smartType = SmartType.fromCollectionClass(objectClass, valueSmartType);
                }
            } else if (typeJson.has(FIELD_TYPES)) {
                JSONObject fieldTypesJson = typeJson.getJSONObject(FIELD_TYPES);
                SmartType fieldsTypesMapType = getSmartTypeFromJson(fieldTypesJson);
                Map<String, SmartType> fieldTypesMap =
                        ConvertUtils.jsonObjectToMap(fieldsTypesMapType, fieldTypesJson);
                smartType = SmartType.fromPojoClass(objectClass, fieldTypesMap);
            } else {
                smartType = SmartType.fromClass(objectClass);
            }
            log.debug("""
                            JSON type converted to smart class.
                            JSON:
                            {}
                            Type:
                            {}
                            """.stripIndent(),
                    typeJson, smartType);
            return smartType;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot get smart class from type JSON.
                            JSON:
                            %s
                            """.stripIndent(),
                    typeJson), e);
        }
    }

    private static SmartValue getClassSmartValueFomValueJson(JSONObject valueJson) {
        try {
            String className = valueJson.getString(OBJECT_CLASS);
            JSONObject fieldValuesJson = valueJson.getJSONObject(FIELD_VALUES);
            Map<String, SmartValue> fieldValuesMap = new HashMap<>();
            Iterator<String> fieldNames = fieldValuesJson.keys();

            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                Object fieldValue = fieldValuesJson.get(fieldName);
                SmartValue fieldSmartValue = new SmartValue(fieldValue);
                fieldValuesMap.put(fieldName, fieldSmartValue);
            }
            SmartValue classSmartValue = new SmartValue(className, fieldValuesMap);
            log.debug("""
                            Value JSON converted to class smart value.
                            Value JSON:
                            {}
                            Smart value:
                            {}
                            """.stripIndent(),
                    valueJson, classSmartValue);
            return classSmartValue;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot get smart value.
                            Class JSON:
                            %s
                            """.stripIndent(),
                    valueJson), e);
        }
    }

    private JSONObject getJsonFromFieldsValues(SmartValue smartValue) {

        try {
            JSONObject fieldValuesJson = new JSONObject();
            Object value = smartValue.getValue();
            Field[] fields = value.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(value);
                SmartValue fieldSmartValue = new SmartValue(fieldValue);
                fieldSmartValue.setKeyword(smartValue.getKeyword());
                String fieldValueTemplate = fieldSmartValue.getValueTemplate();
                fieldValuesJson.put(fieldName, fieldValueTemplate);
            }
            log.debug("""
                            Class fields JSON array returned.
                            Smart value:
                            {}
                            Fields:
                            {}
                            """.stripIndent(),
                    smartValue, fieldValuesJson);
            return fieldValuesJson;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot get class fields JSON array from class smart value.
                            Smart value:
                            %s
                            """.stripIndent(),
                    smartValue), e);
        }
    }

    private void replaceKeywordPlaceholderWithValue() {
        if (keywordString != null && valueTemplate != null) {
            valueString = valueTemplate.replace(KEYWORD_PLACEHOLDER, keywordString);
        }
    }

    private void replaceKeywordValueWithPlaceholder() {
        if (keywordString != null && valueString != null) {
            valueTemplate = valueString.replace(keywordString, KEYWORD_PLACEHOLDER);
        }
    }

    private static SmartValue getSmartValueFromJson(SmartType smartType, JSONObject valueJson) {
        SmartValue smartValue;

        try {
            if (smartType.getFieldTypesMap() != null) {
                smartValue = getClassSmartValueFomValueJson(valueJson);
            } else {
                String valueTemplate = valueJson.getString(VALUE);
                String format = null;

                if (valueJson.has(FORMAT)) {
                    format = valueJson.getString(FORMAT);
                }

                if (format == null) {
                    smartValue = new SmartValue(smartType, valueTemplate);
                } else {
                    smartValue = new SmartValue(smartType, valueTemplate, format);
                }
            }
            log.debug("""
                            JSON value object converted to smart value.
                            Value type:
                            {}
                            JSON object:
                            {}
                            Smart value:
                            {}
                            """.stripIndent(),
                    smartType, valueJson, smartValue);
            return smartValue;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert JSON value object to smart value.
                            Value type:
                            %s
                            JSON object:
                            %s
                            """.stripIndent(),
                    smartType, valueJson), e);
        }
    }

    private static void readValueFromFile(String fileName, Map<String, SmartValue> valuesMap) {
        String filePath = String.format("%s/%s", DATA_OBJECTS_FOLDER_PATH, fileName);

        if (!FileSystemUtils.fileExists(filePath)) {
            log.debug("Data object file does not exist: {}", filePath);
            return;
        }
        String parentName = FileSystemUtils.getFileNameWithoutExtension(fileName);
        String fileContent = FileSystemUtils.readFile(filePath);

        if (!fileContent.trim().isEmpty()) {
            JSONObject json = new JSONObject(fileContent);

            for (String fieldName : json.keySet()) {
                String valueName = String.format("%s.%s", parentName, fieldName);
                JSONObject valueJson = json.getJSONObject(fieldName);
                JSONObject typeJson = valueJson.getJSONObject(TYPE);
                SmartType smartType = getSmartTypeFromJson(typeJson);
                SmartValue smartValue = getSmartValueFromJson(smartType, valueJson);

                valuesMap.put(valueName, smartValue);
                log.debug("""
                                Smart value is read from fle asynchronously.
                                File: {}
                                Name: {}
                                Type:
                                {}
                                Value:
                                {}
                                """.stripIndent(),
                        filePath, valueName, smartType, smartValue);
            }
        }
    }
}
