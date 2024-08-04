package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;

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
    private String value;
    private final SmartDataObject parent;
    private String parentName;
    private String fieldName;
    private String name;

    /**
     * Reads asynchronously all data objects from JSON file
     */
    public static ConcurrentMap<String, String> readAllDataObjectsFromFiles() {
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
     * Smart type constructor with parent object parameter.
     */
    public SmartType(SmartDataObject parent) {
        DataValidationUtils.validateNotNull(parent, "parent");
        this.parent = parent;
    }

    /**
     * Smart type constructor with parent object parameter
     * and string value.
     * @param value The value.
     */
    public SmartType(SmartDataObject parent, String value) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(value, "value");
        this.parent = parent;
        this.value = value;
    }

    /**
     * Smart type constructor with parent object parameter
     * and integer value.
     * @param value The value.
     */
    public SmartType(SmartDataObject parent, int value) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(value, "value");
        this.parent = parent;
        this.value = String.valueOf(value);
    }

    /**
     * Smart type constructor with parent object parameter
     * and long value.
     * @param value The value.
     */
    public SmartType(SmartDataObject parent, long value) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(value, "value");
        this.parent = parent;
        this.value = String.valueOf(value);
    }

    /**
     * Smart type constructor with parent object parameter
     * and float value.
     * @param value The value.
     */
    public SmartType(SmartDataObject parent, float value) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(value, "value");
        this.parent = parent;
        this.value = String.valueOf(value);
    }

    /**
     * Smart type constructor with parent object parameter
     * and double value.
     * @param value The value.
     */
    public SmartType(SmartDataObject parent, double value) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(value, "value");
        this.parent = parent;
        this.value = String.valueOf(value);
    }

    /**
     * Smart type constructor with parent object parameter
     * and boolean value.
     * @param value The value.
     */
    public SmartType(SmartDataObject parent, boolean value) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(value, "value");
        this.parent = parent;
        this.value = String.valueOf(value);
    }

    /**
     * Smart type constructor with parent object parameter
     * and date value and its format.
     * @param date The value.
     * @param dateFormat The date format.
     */
    public SmartType(SmartDataObject parent, Date date, String dateFormat) {
        DataValidationUtils.validateNotNull(parent, "parent");
        DataValidationUtils.validateNotNull(date, "value");
        this.parent = parent;
        this.value = ConverterUtils.dateToString(date, dateFormat);
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        if (object instanceof  String) {
            return getValue().equals(object);
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "equals() - Invalid object type: %s",
                    object.getClass().getSimpleName()));
        }
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
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
     * Sets string value.
     * @param value The value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Sets integer value.
     * @param value The value.
     */
    public void setValue(int value) {
        this.value = String.valueOf(value);
    }

    /**
     * Sets long value.
     * @param value The value.
     */
    public void setValue(long value) {
        this.value = String.valueOf(value);
    }

    /**
     * Sets float value.
     * @param value The value.
     */
    public void setValue(float value) {
        this.value = String.valueOf(value);
    }

    /**
     * Sets double value.
     * @param value The value.
     */
    public void setValue(double value) {
        this.value = String.valueOf(value);
    }

    /**
     * Sets boolean value.
     * @param value The value.
     */
    public void setValue(boolean value) {
        this.value = String.valueOf(value);
    }

    /**
     * Sets Date value.
     * @param date The date value.
     * @param dateFormat The date format.
     *
     */
    public void setValue(Date date, String dateFormat) {
        this.value = ConverterUtils.dateToString(date, dateFormat);
    }

    /**
     * Sets string value.
     * @param value The value.
     */
    public void setAndSaveValue(String value) {
        this.value = value;
        valuesMap.put(name, value);
        saveStringValueToFile();
    }


    /**
     * Converts value to integer value.
     * @return The integer value.
     */
    public int toInteger() {
        int result = ConverterUtils.stringToInteger(getValue());
        log.debug("String {} value '{}' converted to integer: {}.",
                name, getValue(), result);
        return result;
    }

    /**
     * Converts value to long value.
     * @return The long value.
     */
    public long toLong() {
        long result = ConverterUtils.stringToLong(getValue());
        log.debug("String {} value '{}' converted to long: {}.",
                name, getValue(), result);
        return result;
    }

    /**
     * Converts value to float value.
     * @return The float value.
     */
    public float toFloat() {
        float result = ConverterUtils.stringToFloat(getValue());
        log.debug("String {} value '{}' converted to float: {}.",
                name, getValue(), result);
        return result;
    }

    /**
     * Converts value to double value.
     * @return The double value.
     */
    public double toDouble() {
        double result = ConverterUtils.stringToDouble(getValue());
        log.debug("String {} value '{}' converted to double: {}.",
                name, getValue(), result);
        return result;
    }

    /**
     * Converts value to boolean value.
     * @return The double value.
     */
    public boolean toBoolean() {
        boolean result = ConverterUtils.stringToBoolean(getValue());
        log.debug("String {} value '{}' converted to boolean: {}.",
                name, getValue(), result);
        return result;
    }

    /**
     * Converts value to date value.
     * @return The date value.
     */
    public Date toDate() {
        Date result = ConverterUtils.stringToDate(getValue());
        log.debug("String {} value '{}' converted to Date: {}.",
                name, getValue(), result);
        return result;
    }

    private String getValue() {
        setUp();
        return value;
    }

    private void setUp() {
        if (name == null) {
            parentName = parent.getName();
            fieldName = ClassUtils.getObjectFieldName(parent, this);
            name = String.format("%s.%s", parentName, fieldName);

            if (value == null) {
                setUpValue();
            }
        }
    }

    private void setUpValue() {

        if (valuesMap.containsKey(name)) {
            value = valuesMap.get(name);
        }
        else {
            readStringValueFromFile();

            if (value == null && Config.getInstance().getDebugMode()) {

                while (value.equals(SOME_VALUE)) {
                    value = WebUtils.showPrompt(String.format("""
                            DATA TYPE
                                                    
                            Please enter %s value and click OK.
                            OR just click OK to select value on the page.
                            OR click CANCEL to exit the test
                            """.stripTrailing(), name), SOME_VALUE);

                    if (value == null) {
                        WebDriverFactory.quiteAllBrowsersAndServers();
                        System.exit(-1);
                    }
                    else if (value.equals(SOME_VALUE)) {
                        WebElement element = WebUtils.selectWebElement("DATA VALUE");
                        value = WebUtils.getElementValueOrText(element);
                        value = WebUtils.showPrompt(String.format("""
                        DATA TYPE
                                                
                        Edit %s value or just click OK to save it.
                        OR click CANCEL to exit the test
                        """.stripTrailing(), name), value);

                        if (value == null) {
                            WebDriverFactory.quiteAllBrowsersAndServers();
                            System.exit(-1);
                        }
                    }
                    saveStringValueToFile();
                    break;
                }
            }
            if (value == null) {
                throw new RuntimeException(String.format(
                        "Smart type %s is undefined.", name));
            }
            valuesMap.put(name, value);
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
            json.put(fieldName, value);
            String jsonString = json.toString();
            FileSystemUtils.createFile(filePath, jsonString);
            log.debug("String {} value '{}' is saved to data object file {}.",
                    fileName, value, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Can not save string %s.%s value %s to data object file: %s",
                    parentName, fieldName, value, filePath), e);
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
                        value = json.get(fieldName).toString();
                        log.debug("Smart string {} value '{}' is read from data object file {}.",
                                fieldName, value, fileName);
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
                    "Can not read smart string %s.%s value from object file file %s",
                    parentName, fieldName, filePath), e);
        }
    }
}
