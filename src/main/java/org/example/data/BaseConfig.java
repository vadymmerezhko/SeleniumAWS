package org.example.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseConfig {
    private static final Map<String, String> stringPropertyMap = new HashMap<>();
    private static final Map<String, Integer> integerPropertyMap = new HashMap<>();
    private static final Map<String, Boolean> booleanPropertyMap = new HashMap<>();
    protected static final String VALUES_DELIMITER = ":";

    private final String filePath;
    private Properties configProperties;

    /**
     * Config class constructor by the config file path.
     * @param filePath The config file path.
     */
    public BaseConfig(String filePath) {
        this.filePath = filePath;
    }

    protected String getStringProperty(String propertyName) {
        if (!stringPropertyMap.containsKey(propertyName)) {
            String propertyValue = System.getProperty(propertyName);

            if (propertyValue == null) {
                propertyValue = System.getenv(propertyName);
            }

            if (propertyValue == null) {
                propertyValue = getConfigProperties().getProperty(propertyName);
            }

            if (propertyValue == null) {
                throw new RuntimeException(String.format(
                        "configuration property '%s' is undefined.", propertyName));
            }
            stringPropertyMap.put(propertyName, propertyValue);
            return propertyValue;
        }
        return stringPropertyMap.get(propertyName);
    }

    protected int getIntegerProperty(String propertyName) {
        if (!integerPropertyMap.containsKey(propertyName)) {
            int integerValue = Integer.parseInt(getStringProperty(propertyName));
            integerPropertyMap.put(propertyName, integerValue);
            return integerValue;
        }
        return integerPropertyMap.get(propertyName);
    }

    protected boolean getBooleanProperty(String propertyName) {
        if (!booleanPropertyMap.containsKey(propertyName)) {
            boolean booleanValue = Boolean.parseBoolean(getStringProperty(propertyName));
            booleanPropertyMap.put(propertyName, booleanValue);
            return booleanValue;
        }
        return booleanPropertyMap.get(propertyName);
    }

    protected String getSubValue(String value, int index) {
        String[] subValues = value.split(VALUES_DELIMITER);
        if (subValues.length <= index) {
            throw new RuntimeException(String.format(
                    "Config value '%s' doesn't have the part %d.", value, index + 1));
        }
        return subValues[index];
    }

    protected int getIntegerSubValue(String value, int index) {
        String subValue = getSubValue(value, index);
        return Integer.parseInt(subValue);
    }

    protected Properties getConfigProperties() {
        if (configProperties == null) {
            configProperties = new Properties();
            try {
                configProperties.load(new FileInputStream(filePath));
            } catch (IOException e) {
                throw new RuntimeException("Cannot initialize config properties file:\n" + e.getMessage());
            }
        }
        return configProperties;
    }
}
