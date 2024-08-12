package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.DATA_OBJECTS_FOLDER_PATH;

/**
 * Smart type class.
 */
@Slf4j
public class SmartType {
    static final ConcurrentMap<String, String> valuesMap = readAllDataObjectsFromFiles();
    private static final String SOME_VALUE = "Some value";
    private static final String KEYWORD_PLACEHOLDER = "#KEYWORD#";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private SmartDataObject parent;
    private Object value;
    private Object keyword;
    private String name;
    private String typeName;
    private String keywordString;
    private String valueString;
    private String parentName;
    private String fieldName;

    /**
     * Reads asynchronously all data objects from JSON file
     */
    private static ConcurrentMap<String, String> readAllDataObjectsFromFiles() {
        Set<String> fileNames = FileSystemUtils.getFileNamesInFolder(DATA_OBJECTS_FOLDER_PATH);
        ConcurrentMap<String, String> valuesMap = new ConcurrentHashMap<>();

        Thread thread = new Thread(() -> {
            try {
                log.debug("Asynchronous data object files reading began.");

                for (String fileName : fileNames) {
                    if (FileSystemUtils.getFileExtension(fileName).equals("json")) {
                        String filePath = String.format("%s/%s", DATA_OBJECTS_FOLDER_PATH, fileName);
                        String parentName = FileSystemUtils.getFileNameWithoutExtension(fileName);
                        String fileContent = FileSystemUtils.readFile(filePath);

                        if (fileContent.trim().isEmpty()) {
                            continue;
                        }
                        JSONObject json = new JSONObject(fileContent);

                        for (String fieldName : json.keySet()) {
                            String valueName = String.format("%s.%s", parentName, fieldName);
                            JSONObject valueJson = (JSONObject) json.get(fieldName);
                            String valueTemplate = valueJson.getString(VALUE);
                            String type = valueJson.getString(TYPE);


                            valuesMap.put(valueName, valueTemplate);
                            log.debug("Smart string {} value {} is asynchronously read from file {}.",
                                    valueName, value, filePath);
                        }
                    }
                    log.debug("Asynchronous reading data objects from files finished.");
                }
            } catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read all data object files from: %s", DATA_OBJECTS_FOLDER_PATH), e);
            }
        });
        thread.start();
        return valuesMap;
    }

    /**
     * Smart type constructor.
     */
    public SmartType() {
        log.debug("Smart object {} is created.", name);
    }

    /**
     * Creates smart type object  constructor from object value with keyword object.
     * @param value The value.
     */
    private SmartType(Object value, Object keyword) {
        DataValidationUtils.validateNotNull(value, "value");
        DataValidationUtils.validateNotNull(keyword, "keyword");

        this.value = value;
        this.keyword = keyword;
        typeName = getTypeName(value);
        valueString = ConverterUtils.objectToSting(value);
        keywordString = ConverterUtils.objectToSting(keyword);
        validateValue();
        validateKeyword();
        log.debug("""
                Smart type object {} created with value and keyword.
                Value:
                {}
                Keyword:
                {}
                """, name, value, keyword);
    }

    /**
     * Creates smart type object  constructor from object value.
     * @param value The smart value.
     */
    private SmartType(Object value) {
        DataValidationUtils.validateNotNull(value, "value");

        this.value = value;
        valueString = ConverterUtils.objectToSting(value);
        typeName = getTypeName(value);
        validateValue();
        log.debug("""
                Smart type object {} created with value.
                Value:
                {}
                """, name, value);
    }

    @Override
    public String toString() {
        setUp();
        String string = getStringValue();
        log.debug("Smart type object {} converted to string:\n{}", name, string);
        return string;
    }

    @Override
    public boolean equals(Object object) {
        boolean result;

        if (!(object instanceof SmartType)) {
            throw new SmartRuntimeException(String.format(
                    "Not a SmartType object type: %s", object.getClass().getName()));
        }
        Object actualValue = ((SmartType) object).value;
        DataValidationUtils.validateTheSameType(value, actualValue, "expectedValue", "actualValue");

        if (actualValue.getClass() != value.getClass()) {
            throw new SmartRuntimeException(String.format(
                    "Expected object type is: %s,\n" +
                    "but actual object type is %sActual object has wrong type: %s", object.getClass().getName()));
        }

        try {
            if (actualValue instanceof Node) {
                result = ComparatorUtils.compareXmlNodes((Node) value, (Node) actualValue);
            }
            else {
                result = value.equals(actualValue);
            }
            log.debug("""
                    Two objects were compared.
                    Expected:
                    {}
                    Actual:
                    {}
                    Result:
                    {}
                    """.stripIndent(),
                    value, object, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot compare two smart type objects.
                            Expected:
                            %s
                            Actual:
                            %s
                            """.stripIndent(),
                    value.toString(),
                    actualValue.toString()), e);
        }
    }

    @Override
    public int hashCode() {
        int hashCode = value.hashCode();
        log.debug("Smart type object {} hash code: {}", name, hashCode);
        return hashCode;
    }

    /**
     * Converts date object to date string with date format.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public String toDateString(String dateFormat) {
        String dateString;

        if (value instanceof Date) {
            dateString = ConverterUtils.dateToString((Date) value, dateFormat);
        }
        else if (value instanceof LocalDate) {
            dateString = ConverterUtils.localDateToString((LocalDate) value, dateFormat);
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert %s object to date string.", value.getClass().getName()));
        }
        log.debug("Smart type object {} converted to date string with format '{}': {}",
                value, dateFormat, dateString);
        return dateString;
    }

    /**
     * Gets smart type name.
     */
    public String getName() {
        setUp();
        log.debug("Returned smart type object name: {}.", name);
        return name;
    }

    /**
     * Sets value object.
     * @param value The value.
     */
    public void setValue(Object value) {
        setUp();
        this.value = value;
        typeName = getTypeName(value);
    }

    /**
     * Sets smart type keyword.
     * @param keyword The keyword.
     */
    public Object setKeyword(Object keyword) {
        this.keyword = keyword;
        keywordString = ConverterUtils.objectToSting(keyword);
        log.debug("Returned smart type keyword: {}.", keyword);
        return keyword;
    }

    /**
     * Gets smart type keyword.
     */
    public Object getKeyword() {
        setUp();
        log.debug("Returned smart type keyword: {}.", keyword);
        return keyword;
    }

    /**
     * Gets smart type keyword.
     */
    protected Object getValue() {
        setUp();
        log.debug("Returned smart type keyword: {}.", value);
        return value;
    }

    /**
     * Gets smart type keyword.
     */
    public String getKeywordString() {
        setUp();
        log.debug("Returned smart type keyword string: {}.", keyword);
        return keywordString;
    }

    /**
     * Sets Date value.
     * @param localDate The date value.
     * @param dateFormat The date format.
     *
     */
    public void setLocalDate(LocalDate localDate, String dateFormat) {
        this.valueString = ConverterUtils.localDateToString(localDate, dateFormat);
        value = localDate;
        typeName = getTypeName(value);
    }

    /**
     * Sets string value and saves it to the file.
     * @param value The value.
     */
    public void setAndSaveString(String value) {
        this.value = value;
        valueString = value;
        typeName = getTypeName(value);
        valuesMap.put(name, value);
        saveStringValueToFile();
    }

    /**
     * Converts value to integer value.
     * @return The integer value.
     */
    public Integer toInteger() {
        setUp();
        Integer result = ConverterUtils.objectToInteger(value);
        log.debug("Smart type object {} converted to integer: {}.", name, result);
        return result;
    }

    /**
     * Converts value to long value.
     * @return The long value.
     */
    public Long toLong() {
        setUp();
        Long result = ConverterUtils.objectToLong(value);
        log.debug("Smart type object {} converted to long: {}.", name, result);
        return result;
    }

    /**
     * Converts value to big integer value.
     * @return The long value.
     */
    public BigInteger toBigInteger() {
        setUp();
        BigInteger result = ConverterUtils.objectToBigInteger(value);
        log.debug("Smart type object {} converted to big integer: {}.", name, result);
        return result;
    }

    /**
     * Converts value to float value.
     * @return The float value.
     */
    public Float toFloat() {
        setUp();
        Float result = ConverterUtils.objectToFloat(value);
        log.debug("Smart type object {} converted to float: {}.", name, result);
        return result;
    }

    /**
     * Converts value to double value.
     * @return The double value.
     */
    public Double toDouble() {
        setUp();
        Double result = ConverterUtils.objectToDouble(value);
        log.debug("Smart type object {} converted to double: {}.", name, result);
        return result;
    }

    /**
     * Converts value to big decimal value.
     * @return The big decimal value.
     */
    public BigDecimal toBigDecimal() {
        setUp();
        BigDecimal result = ConverterUtils.objetToBigDecimal(value);
        log.debug("Smart type object {} converted to big decimal: {}.", name, result);
        return result;
    }

    /**
     * Converts value to boolean value.
     * @return The boolean value.
     */
    public Boolean toBoolean() {
        setUp();
        Boolean result = ConverterUtils.objectToBoolean(value);
        log.debug("Smart type object {} converted to boolean: {}.", name, result);
        return result;
    }

    /**
     * Converts value to date value.
     * @return The date value.
     */
    public Date toDate() {
        setUp();
        Date result = ConverterUtils.objectToDate(value);
        log.debug("Smart type object {} converted to date: {}.", name, result);
        return result;
    }

    /**
     * Converts value to local date value.
     * @return The date value.
     */
    public LocalDate toLocalDate() {
        setUp();
        LocalDate result = ConverterUtils.objectToLocalDate(value);
        log.debug("Smart type object {} converted to local date: {}.", name, result);
        return result;
    }

    void setParent(SmartDataObject parent) {
        this.parent = parent;
    }

    private String getStringValue() {
        setUp();
        return replaceKeywordPlaceholderIfDefined();
    }

    private void setUp() {

        if (name == null) {
            validateParent();
            parentName = parent.getClass().getSimpleName();
            fieldName = ClassUtils.getObjectFieldName(parent, this);
            name = String.format("%s.%s", parentName, fieldName);

            if (valueString == null) {
                setUpValue();
            }
        }
    }

    private void setUpValue() {

        if (valuesMap.containsKey(name)) {
            valueString = valuesMap.get(name);
        }
        else {
            readStringValueFromFile();

            if (valueString == null && Config.getInstance().getDebugMode()) {

                while (valueString == null || valueString.equals(SOME_VALUE)) {
                    String message;

                    if (keywordString == null) {
                        message = String.format("""
                                UNDEFINED DATA VALUE
                                                        
                                Enter %s value.
                                                            
                                Click OK to save.
                                OR just click OK ro select it on the page.
                                OR click CANCEL to exit the test.
                                """.stripIndent(), name);
                    }
                    else {
                        message = String.format("""
                                UNDEFINED DATA VALUE
                                                        
                                Enter %s value with keyword.
                                Keyword: '%s'
                                                            
                                Click OK to save.
                                OR just click OK ro select it on the page.
                                OR click CANCEL to exit the test.
                                """.stripIndent(), name, this.keywordString);
                    }
                    valueString = WebUtils.showPrompt(message, SOME_VALUE);

                    if (valueString.isEmpty()) {
                        WebDriverFactory.hardSystemExit();
                    }

                    if (valueString.equals(SOME_VALUE)) {
                        WebElement element = WebUtils.selectWebElement("DATA VALUE");
                        valueString = WebUtils.getElementValueOrText(element);
                    }

                    if (validValue()) {
                        valueString = WebUtils.showPrompt(String.format("""
                            VALID DATA VALUE
                            
                            %s data value is valid.
                                                                 
                            Click OK to save.
                            OR click CANCEL to exit the test.
                            """.stripIndent(), name), valueString);
                    }
                    else {
                        if (valueDoesNotContainKeyword()) {
                            message = String.format("%s data value does not contain the keyword.",
                                    name);
                        }
                        else if (valueContainsMoreThanOneKeyword()) {
                            message = String.format("%s data value contains more than one keyword.",
                                    name);
                        }
                        WebUtils.showAlert(String.format("""
                                    INVALID DATA VALUE
                                                            
                                    %s
                                    Value: '%s'
                                    Keyword: '%s'
                                                                
                                    Click OK to update the data value.
                                    """.stripIndent(), message, valueString, keywordString));
                    }
                    if (validValue()) {
                        replaceKeywordPlaceholderIfDefined();
                        saveStringValueToFile();
                        break;
                    }
                    if (valueString.isEmpty()) {
                        WebDriverFactory.hardSystemExit();
                    }
                    valueString = SOME_VALUE;
                }
            }
            if (valueString == null) {
                throw new RuntimeException(String.format(
                        "Smart type %s is undefined.", name));
            }
            valuesMap.put(name, valueString);
            value = valueString;
            typeName = getTypeName(value);
        }
    }

    private boolean validValue() {
        if (valueString != null) {
            if (keywordString != null) {
                return  !valueContainsMoreThanOneKeyword() && !valueDoesNotContainKeyword();
            }
            return true;
        }
        return false;
    }

    public int numberOfKeywordsInValue() {
        if (valueString == null || keywordString == null || valueString.isEmpty() || keywordString.isEmpty()) {
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

    void validateKeyword() {
        if (keywordString != null) {
            throw new SmartRuntimeException("Keyword value is empty. Update the keyword.");
        }
    }

    private boolean valueContainsMoreThanOneKeyword() {
        return numberOfKeywordsInValue() > 1;
    }

    private boolean valueDoesNotContainKeyword() {
        return numberOfKeywordsInValue() == 0;
    }

    private void validateValue() {
        if (keywordString != null) {

            if (valueContainsMoreThanOneKeyword()) {
                throw new SmartRuntimeException(String.format(
                        "String value '%s' contains more that one keyword '%s'.",
                        valueString, keywordString));
            }
            else if (valueDoesNotContainKeyword()) {
                throw new SmartRuntimeException(String.format(
                        "String value '%s' does not contain keyword '%s'.",
                        valueString, keywordString));
            }
        }
    }

    private void saveStringValueToFile() {
        String filePath = null;

        try {
            String fileName = String.format("%s.json", parentName);
            JSONObject json;
            filePath = String.format("%s/%s", DATA_OBJECTS_FOLDER_PATH, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);
                json = new JSONObject(jsonString);
            } else {
                json = new JSONObject();
            }
            String valueTemplate = valueString;

            if (keywordString != null) {
                if (valueString.contains(keywordString)) {
                    valueTemplate = valueString.replace(keywordString, KEYWORD_PLACEHOLDER);
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Smart type value '%s' does not contain keyword '%s'",
                            valueString, keywordString));
                }
            }
            JSONObject valueJson = new JSONObject();
            valueJson.put(TYPE, typeName);
            valueJson.put(VALUE, valueTemplate);

            json.put(fieldName, valueJson);
            String jsonString = json.toString();
            FileSystemUtils.createFile(filePath, jsonString);
            log.debug("String {} value '{}' is saved to data object file {}.",
                    fileName, this.valueString, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not save string %s.%s value %s to data object file: %s",
                    parentName, fieldName, valueString, filePath), e);
        }
    }

    private void readStringValueFromFile() {
        String filePath = null;
        try {
            String fileName = String.format("%s.json", parentName);
            filePath = String.format("%s/%s", DATA_OBJECTS_FOLDER_PATH, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                String jsonString = FileSystemUtils.readFile(filePath);

                if (jsonString.trim().isEmpty()) {
                    throw new RuntimeException(String.format(
                            "Data object file %s is empty.", filePath));
                }
                JSONObject json = new JSONObject(jsonString);

                if (json.has(fieldName)) {
                    try {
                        JSONObject valueJson = (JSONObject) json.get(fieldName);
                        String valueTemplate = valueJson.getString(VALUE);
                        typeName = valueJson.getString(TYPE);

                        log.debug("Smart string {} value template '{}' is read from data object file {}.",
                                fieldName, valueTemplate, fileName);

                        if (keywordString != null) {
                            valueString = valueTemplate.replace(KEYWORD_PLACEHOLDER, keywordString);
                        }
                        else {
                            valueString = valueTemplate;
                        }
                        value = valueString;
                        log.debug("Smart string {} value '{}' is read from data object file {}.",
                                fieldName, valueString, fileName);
                    } catch (JSONException e) {
                        throw new SmartRuntimeException(String.format(
                                "Data object file %s has invalid JSON object format: %s",
                                filePath, jsonString));
                    }
                }
            }
            else {
                log.debug("Data object {} file {} does not exist.",
                        parentName, filePath);
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not read smart string %s.%s value from object file file %s.",
                    parentName, fieldName, filePath), e);
        }
    }

    private String replaceKeywordPlaceholderIfDefined() {
        if (keywordString != null) {
            if (valueString.contains(KEYWORD_PLACEHOLDER)) {
                return valueString.replace(KEYWORD_PLACEHOLDER, keywordString);
            }
        }
        return valueString;
    }

    private void validateParent() {
        if (parent == null) {
            throw new SmartRuntimeException("""
                            Please add method initialize(); to data object class constructor like this:
                            
                            public YourDataObject() {
                                super();
                                initialize();
                            }
                            """.stripIndent());
        }
    }

    private String getTypeName(Object object) {
        return object.getClass().getSimpleName();
    }
}
