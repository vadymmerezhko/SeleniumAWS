package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.Config;
import org.example.drivers.factories.WebDriverFactory;
import org.example.enums.ValueType;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.*;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.DATA_OBJECTS_FOLDER_PATH;
import static org.example.enums.ValueType.*;

/**
 * Smart class.
 */
@Slf4j
public final class SmartClass extends SmartValue {
    static final ConcurrentMap<String, SmartValue> valuesMap = readAllDataObjectsFromFiles();
    private static final String SOME_VALUE = "Some value";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String FORMAT = "format";
    private static final String OBJECT_TYPE = "objectType";
    private static final String KEY_TYPE = "keyType";
    private static final String VALUE_TYPE = "valueType";
    private static final String CLASS_TYPE = "class";
    private static final String CLASS_NAME = "className";
    private static final String FIELD_TYPES = "fieldTypes";
    private static final String FIELD_VALUES = "fieldValues";
    private static final String CLASS_VALUE = "classValue";
    private SmartObject parent;
    private String name;
    private String parentName;
    private String fieldName;
    private boolean setUp = false;

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
                    log.debug("Asynchronous reading data objects from files finished.");
                }
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format(
                        "Cannot read all data object files from: %s",
                        DATA_OBJECTS_FOLDER_PATH), e);
            }
        });
        thread.start();
        return valuesMap;
    }

    /**
     * Smart class constructor.
     */
    public SmartClass() {
        super();
        log.debug("Smart object is created. Not initialized yet.");
    }

    /**
     * Creates smart class object  constructor from object value.
     * @param value The smart value.
     */
    private SmartClass(Object value) {
        this(value, null);
    }

    /**
     * Creates smart class object constructor from object value and keyword object.
     * @param value The smart value.
     */
    private SmartClass(Object value, Object keyword) {
        setValue(value);
        setKeyword(keyword);
        setUp();
        log.debug("""
                Smart class object {} created with value and keyword.
                Value:
                {}
                Keyword:
                {}
                """.stripIndent(),
                name, value, keyword);
    }

    @Override
    public int hashCode() {
        setUp();
        return Objects.hash(super.hashCode(), parent, parentName, name);
    }

    /**
     * Gets smart class name.
     */
    public String getName() {
        setUp();
        log.debug("Returned smart class object name: {}.", name);
        return name;
    }

    /**
     * Saves object value to the file.
     * @param value The value.
     */
    public void setAndSaveValue(Object value) {
        setValue(value);
        setUp();
        valuesMap.put(name, this);
        saveValueToFile();
        log.debug("Value is saved and set to: {}", value);
    }

    void setParent(SmartObject parent) {
        this.parent = parent;
    }

    protected void setUp() {

        if (name == null) {
            validateParent();
            parentName = parent.getClass().getSimpleName();
            fieldName = ClassUtils.getObjectFieldName(parent, this);
            name = String.format("%s.%s", parentName, fieldName);
        }
        if (!setUp) {
            setUp = true;
            if (getSmartType() == null) {
                setUpValue();
            }
        }
    }

    private void setUpValue() {

        try {
            if (valuesMap.containsKey(name)) {
                SmartValue smartValue = valuesMap.get(name);
                setValue(smartValue.getValue());
            }
            else {
                readValueFromFile();
                SmartValue tempValue = new SmartValue(SOME_VALUE, getKeyword());
                SmartType validValueType;
                boolean isValueValid = false;

                if (getSmartType() == null && Config.getInstance().getDebugMode()) {

                    while (tempValue.toString().equals(SOME_VALUE)) {
                        String promptMessage;

                        if (tempValue.getKeyword() == null) {
                            promptMessage = String.format("""
                                    UNDEFINED DATA VALUE
                                                            
                                    Enter %s value.
                                                                
                                    Click OK to save.
                                    OR just click OK ro select it on the page.
                                    OR click CANCEL to exit the test.
                                    """.stripIndent(), name);
                        }
                        else {
                            promptMessage = String.format("""
                                    UNDEFINED DATA VALUE
                                                            
                                    Enter %s value with keyword.
                                    Keyword: '%s'
                                                                
                                    Click OK to save.
                                    OR just click OK ro select it on the page.
                                    OR click CANCEL to exit the test.
                                    """.stripIndent(), name, tempValue.getKeywordString());
                        }
                        tempValue.setValue(WebUtils.showPrompt(promptMessage, SOME_VALUE));

                        if (tempValue.toString().isEmpty()) {
                            WebDriverFactory.hardSystemExit();
                        }
                        if (tempValue.toString().equals(SOME_VALUE)) {
                            WebElement element = WebUtils.selectWebElement("DATA SOURCE ELEMENT");
                            tempValue = WebUtils.getElementSmartValue(element);
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
                            }
                            else {
                                if (!tempValue.toString().equals(stringValue)) {
                                    isValueValid = false;
                                    tempValue.setValue(ConverterUtils.stringToObject(validValueType, stringValue));
                                }
                            }
                        }
                        else {
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
                                    tempValue,
                                    tempValue.getKeywordString()));
                        }
                        if (tempValue.toString().isEmpty()) {
                            WebDriverFactory.hardSystemExit();
                        }
                        else if (isValueValid) {
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
        }
        catch (Exception e) {
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
            }
            else {
                json = new JSONObject();
            }
            valueJson.put(TYPE, typeJson);

            if (type.getObjectType() == CLASS) {
                JSONObject classJson = new JSONObject();
                SmartType classSmartType = getSmartType();
                String className = classSmartType.getClassName();
                classJson.put(CLASS_NAME, className);
                JSONObject fieldValuesJson = getJsonFromFieldsValues(this);
                classJson.put(FIELD_VALUES, fieldValuesJson);
            }
            else {
                valueJson.put(VALUE, valueTemplate);

                if (format != null) {
                    valueJson.put(FORMAT, format);
                }
            }
            json.put(fieldName, valueJson);
            jsonString = json.toString(4);
            FileSystemUtils.createFile(filePath, jsonString);
            log.debug("""
                    Smart class value is saved to file.
                    Name: {}
                    Type: {}
                    Format: {}
                    Value: {}
                    File: {}
                    """.stripIndent(),
                    name, type, format, valueTemplate, filePath);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Smart class value is saved to file.
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
            filePath = String.format("%s/%s", DATA_OBJECTS_FOLDER_PATH, fileName);

            if (FileSystemUtils.fileExists(filePath)) {
                jsonString = FileSystemUtils.readFile(filePath);

                if (jsonString.trim().isEmpty()) {
                    throw new RuntimeException(String.format(
                        "Data object file %s is empty.", filePath));
                }
                JSONObject json = new JSONObject(jsonString);

                if (json.has(fieldName)) {
                    JSONObject valueJson = (JSONObject) json.get(fieldName);
                    JSONObject typeJson = valueJson.getJSONObject(TYPE);
                    SmartType smartType = getSmartTypeFromJson(typeJson);

                    SmartValue smartValue = getSmartValueFromJson(smartType, valueJson);
                    valuesMap.put(fieldName, smartValue);
                    setValue(smartValue.getValue());
                    log.debug("""
                            Smart class value is read from file.
                            File: {}
                            Name: {}
                            Type:
                            {}
                            Value:
                            {}
                            """.stripIndent(),
                            name, smartType, getValue(), filePath);
                }
            }
            else {
                log.debug("Data object {} file {} does not exist.",
                        parentName, filePath);
            }
        }
        catch (Exception e) {
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
                    Please add method initialize(); to data object class constructor like this:
                                                
                    public class YourDataObject extends SmartDataObject {
                                                
                        @Getter
                        private final SmartType yourDataField = new SmartType();
                        
                        public YourDataObject() {
                            super();
                            initialize();
                        }
                    }
                    ///////////////////////////////////////////////////////////////////////////
                    """.stripIndent());
        }
    }

    private static JSONObject getJsonFromSmartType(SmartType smartType) {

        try {
            ValueType objectType = smartType.getObjectType();
            ValueType keyType = smartType.getKeyType();
            SmartType valueSmartType = smartType.getValueSmartType();
            JSONObject typeJson = new JSONObject();

            typeJson.put(OBJECT_TYPE, objectType.toString());

            if (valueSmartType != null) {

                if (keyType != null) {
                    typeJson.put(KEY_TYPE, keyType.toString());
                }
                typeJson.append(VALUE_TYPE, getJsonFromSmartType(valueSmartType));
            }
            else if (objectType == CLASS) {
                String className = smartType.getClassName();
                typeJson.put(CLASS_NAME, className);
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
        }
        catch (Exception e) {
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
            ValueType objectType = ValueType.fromString(typeJson.getString(OBJECT_TYPE));

            if (typeJson.has(VALUE_TYPE)) {
                SmartType valueSmartType = getSmartTypeFromJson(typeJson.getJSONObject(VALUE_TYPE));

                if (typeJson.has(KEY_TYPE)) {
                    ValueType keyType = ValueType.fromString(typeJson.getString(KEY_TYPE));
                    smartType = new SmartType(objectType, keyType, valueSmartType);
                } else {
                    smartType = new SmartType(objectType, valueSmartType);
                }
            }
            else if (typeJson.has(CLASS_TYPE)) {
                JSONObject classJson = typeJson.getJSONObject(CLASS_TYPE);
                String className = classJson.getString(CLASS_NAME);
                JSONObject fieldTypesJson = classJson.getJSONObject(FIELD_TYPES);
                SmartType fieldsTypesMapType = getSmartTypeFromJson(fieldTypesJson);
                Map<String, SmartType> fieldTypesMap =
                        ConverterUtils.jsonObjectToMap(fieldsTypesMapType, fieldTypesJson);
                smartType = new SmartType(className, fieldTypesMap);
            }
            else {
                smartType = new SmartType(objectType);
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
        }
        catch (Exception e) {
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
            String className = valueJson.getString(CLASS_NAME);
            JSONObject classJson = valueJson.getJSONObject(CLASS_VALUE);
            JSONObject fieldValuesJson = classJson.getJSONObject(FIELD_VALUES);
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
        }
        catch (Exception e) {
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
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get class fields JSON array from class smart value.
                    Smart value:
                    %s
                    """.stripIndent(),
                    smartValue), e);
        }
    }

    private static SmartValue getSmartValueFromJson(SmartType smartType, JSONObject valueJson) {
        SmartValue smartValue;

        try {
            if (smartType.getObjectType() == CLASS) {
                smartValue = getClassSmartValueFomValueJson(valueJson);
            }
            else {
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
        }
        catch (Exception e) {
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
}
