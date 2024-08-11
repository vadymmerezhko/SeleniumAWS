package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;

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
    private SmartDataObject parent;
    private Object value;
    private String keyword;
    private String stringValue;
    private String parentName;
    private String fieldName;
    private String name;

    /**
     * Creates auto smart type.
     * @return Returns smart type instance.
     */
    public static SmartType auto() {
        return new SmartType();
    }

    /**
     * Creates auto smart type with string value.
     * @param value The value.
     * @return Returns smart type instance.
     */
    public static SmartType fromString(String value) {
        return new SmartType(value);
    }

    /**
     * Creates auto smart type with integer value.
     * @param value The value.
     * @return Returns smart type instance.
     */
    public static SmartType fromInteger(int value) {
        return new SmartType(value);
    }

    /**
     * Creates auto smart type with long value.
     * @param value The value.
     * @return Returns smart type instance.
     */
    public static SmartType fromLong(long value) {
        return new SmartType(value);
    }

    /**
     * Creates auto smart type with float value.
     * @param value The value.
     * @return Returns smart type instance.
     */
    public static SmartType fromFloat(float value) {
        return new SmartType(value);
    }

    /**
     * Creates auto smart type with double value.
     * @param value The value.
     * @return Returns smart type instance.
     */
    public static SmartType fromDouble(double value) {
        return new SmartType(value);
    }

    /**
     * Creates auto smart type with boolean value.
     * @param value The value.
     * @return Returns smart type instance.
     */
    public static SmartType fromBoolean(boolean value) {
        return new SmartType(value);
    }

    /**
     * Creates auto smart type with date value and date format.
     * @param date The date.
     * @param dateFormat The date format.
     * @return Returns smart type instance.
     */
    public static SmartType fromDate(Date date, String dateFormat) {
        SmartType smartType = new SmartType(date, dateFormat);
        log.debug("Smart date: '{}'", smartType.stringValue);
        return smartType;
    }

    /**
     * Creates auto smart type string with date value and date format.
     * @param date The date.
     * @return Returns smart type instance.
     */
    public static SmartType fromDate(String date) {
        SmartType smartType = new SmartType(date);
        log.debug("Smart date: '{}'", smartType.stringValue);
        return smartType;
    }

    /**
     * Creates auto smart type string with local date and date format.
     * @param localDate The date.
     * @return Returns smart type instance.
     */
    public static SmartType fromLocalDate(LocalDate localDate, String dateFormat) {
        SmartType smartType = new SmartType(localDate);
        log.debug("Smart local date: '{}'", smartType.stringValue);
        return smartType;
    }

    /**
     * Reads asynchronously all data objects from JSON file
     */
    private static ConcurrentMap<String, String> readAllDataObjectsFromFiles() {
        Set<String> fileNames = FileSystemUtils.getFileNamesInFolder(DATA_OBJECTS_FOLDER_PATH);
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

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
                            String value = json.get(fieldName).toString();

                            map.put(valueName, value);
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
        return map;
    }

    /**
     * Smart type constructor.
     */
    private SmartType() {
        keyword = null;
    }

    /**
     * Smart type constructor with string value.
     */
    private SmartType(String keyword) {
        DataValidationUtils.validateNotNull(keyword, "keyword");
        this.keyword = keyword;
    }

    /**
     * Smart type constructor string value.
     * @param value The value.
     */
    private SmartType(String value, String keyword) {
        this.stringValue = value;
        this.keyword = keyword;
    }

    /**
     * Smart type constructor with parent int value.
     * @param value The value.
     */
    private SmartType(int value) {
        DataValidationUtils.validateNotNull(value, "value");
        this.stringValue = String.valueOf(value);
    }

    /**
     * Smart type constructor with long value.
     * @param value The value.
     */
    private SmartType(long value) {
        DataValidationUtils.validateNotNull(value, "value");
        this.stringValue = String.valueOf(value);
    }

    /**
     * Smart type constructor with float value.
     * @param value The value.
     */
    private SmartType(float value) {
        DataValidationUtils.validateNotNull(value, "value");
        this.stringValue = String.valueOf(value);
    }

    /**
     * Smart type constructor with double value.
     * @param value The value.
     */
    private SmartType(double value) {
        DataValidationUtils.validateNotNull(value, "value");
        this.stringValue = String.valueOf(value);
    }

    /**
     * Smart type constructor with  boolean value.
     * @param value The value.
     */
    private SmartType(boolean value) {
        DataValidationUtils.validateNotNull(value, "value");
        this.stringValue = String.valueOf(value);
    }

    /**
     * Smart type constructor with date value and its format.
     * @param date The value.
     * @param dateFormat The date format.
     */
    private SmartType(Date date, String dateFormat) {
        DataValidationUtils.validateNotNull(date, "value");
        this.stringValue = ConverterUtils.dateToString(date, dateFormat);
    }

    /**
     * Smart type constructor with  local date value and its format.
     * @param localDate The value.
     */
    private SmartType(LocalDate localDate) {
        DataValidationUtils.validateNotNull(localDate, "localDate");
        this.value = ConverterUtils.localDateToDate(localDate);
    }

    @Override
    public String toString() {
        return getStringValue();
    }

    @Override
    public boolean equals(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        if (object instanceof  String) {
            return getStringValue().equals(object);
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "equals() - Invalid object type: %s",
                    object.getClass().getSimpleName()));
        }
    }

    @Override
    public int hashCode() {
        return getStringValue().hashCode();
    }

    /**
     * Gets type name.
     */
    public String getName() {
        setUp();
        log.debug("Returned smart type name: {}.", name);
        return name;
    }

    /**
     * Sets keyword.
     * @param keyword The keyword.
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
        log.debug("Smart type keyword is set: '{}'", keyword);
    }

    /**
     * Gets keyword.
     * @return The keyword.
     */
    public String getKeyword() {
        String keyWord = keyword.toString();
        log.debug("Smart type keyword is set: '{}'", keyword.toString());
        return keyWord;
    }

    /**
     * Sets string value.
     * @param value The value.
     */
    public void setString(String value) {
        this.stringValue = value;
    }

    /**
     * Sets integer value.
     * @param value The value.
     */
    public void setInteger(int value) {
        this.stringValue = String.valueOf(value);
    }

    /**
     * Sets long value.
     * @param value The value.
     */
    public void setLong(long value) {
        this.stringValue = String.valueOf(value);
    }

    /**
     * Sets float value.
     * @param value The value.
     */
    public void setFloat(float value) {
        this.stringValue = String.valueOf(value);
    }

    /**
     * Sets double value.
     * @param value The value.
     */
    public void setDouble(double value) {
        this.stringValue = String.valueOf(value);
    }

    /**
     * Sets boolean value.
     * @param value The value.
     */
    public void setBoolean(boolean value) {
        this.stringValue = String.valueOf(value);
    }

    /**
     * Sets Date value.
     * @param date The date value.
     * @param dateFormat The date format.
     *
     */
    public void setDate(Date date, String dateFormat) {
        this.stringValue = ConverterUtils.dateToString(date, dateFormat);
        value = date;
    }

    /**
     * Sets Date value.
     * @param localDate The date value.
     * @param dateFormat The date format.
     *
     */
    public void setLocalDate(LocalDate localDate, String dateFormat) {
        Date date = ConverterUtils.localDateToDate(localDate);
        this.stringValue = ConverterUtils.dateToString(date, dateFormat);
        value = date;
    }

    /**
     * Sets string value and saves it to the file.
     * @param value The value.
     */
    public void setAndSaveString(String value) {
        this.stringValue = value;
        valuesMap.put(name, value);
        saveStringValueToFile();
    }


    /**
     * Converts value to integer value.
     * @return The integer value.
     */
    public int toInteger() {
        int result = ConverterUtils.stringToInteger(getStringValue());
        log.debug("String {} value '{}' converted to integer: {}.",
                name, getStringValue(), result);
        return result;
    }

    /**
     * Converts value to long value.
     * @return The long value.
     */
    public long toLong() {
        long result = ConverterUtils.stringToLong(getStringValue());
        log.debug("String {} value '{}' converted to long: {}.",
                name, getStringValue(), result);
        return result;
    }

    /**
     * Converts value to float value.
     * @return The float value.
     */
    public float toFloat() {
        float result = ConverterUtils.stringToFloat(getStringValue());
        log.debug("String {} value '{}' converted to float: {}.",
                name, getStringValue(), result);
        return result;
    }

    /**
     * Converts value to double value.
     * @return The double value.
     */
    public double toDouble() {
        double result = ConverterUtils.stringToDouble(getStringValue());
        log.debug("String {} value '{}' converted to double: {}.",
                name, getStringValue(), result);
        return result;
    }

    /**
     * Converts value to boolean value.
     * @return The double value.
     */
    public boolean toBoolean() {
        boolean result = ConverterUtils.stringToBoolean(getStringValue());
        log.debug("String {} value '{}' converted to boolean: {}.",
                name, getStringValue(), result);
        return result;
    }

    /**
     * Converts value to date value.
     * @return The date value.
     */
    public Date toDate() {
        Date result = ConverterUtils.stringToDate(getStringValue());
        log.debug("String {} value '{}' converted to Date: {}.",
                name, getStringValue(), result);
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
        validateParent();

        if (name == null) {
            parentName = parent.getClass().getSimpleName();
            fieldName = ClassUtils.getObjectFieldName(parent, this);
            name = String.format("%s.%s", parentName, fieldName);

            if (stringValue == null) {
                setUpValue();
            }
        }
    }

    private void setUpValue() {

        if (valuesMap.containsKey(name)) {
            stringValue = valuesMap.get(name);
        }
        else {
            readStringValueFromFile();

            if (stringValue == null && Config.getInstance().getDebugMode()) {

                while (stringValue == null || stringValue.equals(SOME_VALUE)) {
                    String message;

                    if (keyword == null) {
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
                                """.stripIndent(), name, this.keyword);
                    }
                    stringValue = WebUtils.showPrompt(message, SOME_VALUE);

                    if (stringValue.isEmpty()) {
                        WebDriverFactory.hardSystemExit();
                    }

                    if (stringValue.equals(SOME_VALUE)) {
                        WebElement element = WebUtils.selectWebElement("DATA VALUE");
                        stringValue = WebUtils.getElementValueOrText(element);
                    }

                    if (validValue()) {
                        stringValue = WebUtils.showPrompt(String.format("""
                            VALID DATA VALUE
                            
                            %s data value is valid.
                                                                 
                            Click OK to save.
                            OR click CANCEL to exit the test.
                            """.stripIndent(), name), stringValue);
                    }
                    else {
                        WebUtils.showAlert(String.format("""
                            INVALID DATA VALUE
                                                    
                            %s data value does not contain the keyword.
                            Keyword: '%s'
                            
                            Click OK to update the data value.
                            """.stripIndent(), name, keyword));
                    }

                    if (validValue()) {
                        replaceKeywordPlaceholderIfDefined();
                        saveStringValueToFile();
                        break;
                    }
                    if (stringValue.isEmpty()) {
                        WebDriverFactory.hardSystemExit();
                    }
                    stringValue = SOME_VALUE;
                }
            }
            if (stringValue == null) {
                throw new RuntimeException(String.format(
                        "Smart type %s is undefined.", name));
            }
            valuesMap.put(name, stringValue);
        }
    }

    private boolean validValue() {
        if (stringValue != null) {
            if (keyword != null) {
                if (numberOfKeywordsInValue() == 1) {
                    return true;
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Keyword value '%s' contains more than one keyword '%s' : %d.",
                            stringValue, keyword, numberOfKeywordsInValue()));
                }
            }
            return true;
        }
        return false;
    }

    public int numberOfKeywordsInValue() {
        if (stringValue == null || keyword == null || stringValue.isEmpty() || keyword.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;

        while ((index = stringValue.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    void validKeyword() {
        if (keyword != null) {
            throw new SmartRuntimeException("Keyword value is empty. Update the keyword.");
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

            if (keyword != null) {
                if (stringValue.contains(keyword)) {
                    stringValue = stringValue.replace(keyword, KEYWORD_PLACEHOLDER);
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Smart type value '%s' does not contain keyword '%s'",
                            stringValue, keyword));
                }
            }
            json.put(fieldName, stringValue);
            String jsonString = json.toString();
            FileSystemUtils.createFile(filePath, jsonString);
            log.debug("String {} value '{}' is saved to data object file {}.",
                    fileName, this.stringValue, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not save string %s.%s value %s to data object file: %s",
                    parentName, fieldName, stringValue, filePath), e);
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
                        stringValue = json.get(fieldName).toString();
                        log.debug("Smart string {} value '{}' is read from data object file {}.",
                                fieldName, stringValue, fileName);
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
        if (keyword != null) {
            if (stringValue.contains(KEYWORD_PLACEHOLDER)) {
                return stringValue.replace(KEYWORD_PLACEHOLDER, keyword);
            }
        }
        return stringValue;
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
}
