package org.example.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {
    private static final String TREAD_COUNT_PROP_NAME = "threadCount";
    private static final String TEST_MODE_PROP_NAME = "testMode";
    private static final String BROWSER_PROP_NAME = "browser";
    private static final String OS_PROP_NAME = "os";
    private static final String HEADLESS_PROP_NAME = "headless";
    private static final String VALUES_DELIMITER = ":";

    private static final Map<String, String> stringPropertyMap = new HashMap<>();
    private static final Map<String, Integer> integerPropertyMap = new HashMap<>();
    private static final Map<String, Boolean> booleanPropertyMap = new HashMap<>();

    private final String filePath;
    private Properties configProperties;

    public Config(String filePath) {
        this.filePath = filePath;
    }

    synchronized public int getThreadCount() {
        return getIntegerProperty(TREAD_COUNT_PROP_NAME);
    }

    synchronized public String getTestMode() {
        return getStringProperty(TEST_MODE_PROP_NAME);
    }

    synchronized public String getBrowserName() {
        String browser = getStringProperty(BROWSER_PROP_NAME);
        return getSubValue(browser, 0);
    }

    synchronized public String getBrowserVersion() {
        String browser = getStringProperty(BROWSER_PROP_NAME);
        return getSubValue(browser, 1);
    }

    synchronized public String getOsName() {
        String browser = getStringProperty(OS_PROP_NAME);
        return getSubValue(browser, 0);
    }

    synchronized public String getOsVersion() {
        String browser = getStringProperty(OS_PROP_NAME);
        String browserVersion = getSubValue(browser, 1);
        if (browserVersion == null) {
            throw new RuntimeException("Browser version is undefined.");
        }
        return browserVersion;
    }

    synchronized public boolean getHeadless() {
        return getBooleanProperty(HEADLESS_PROP_NAME);
    }

    private String getStringProperty(String propertyName) {
        if (!stringPropertyMap.containsKey(propertyName)) {
            String propertyValue = System.getProperty(propertyName);
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

    private int getIntegerProperty(String propertyName) {
        if (!integerPropertyMap.containsKey(propertyName)) {
            int integerValue = Integer.parseInt(getStringProperty(propertyName));
            integerPropertyMap.put(propertyName, integerValue);
            return integerValue;
        }
        return integerPropertyMap.get(propertyName);
    }

    private boolean getBooleanProperty(String propertyName) {
        if (!booleanPropertyMap.containsKey(propertyName)) {
            boolean booleanValue = Boolean.parseBoolean(getStringProperty(propertyName));
            booleanPropertyMap.put(propertyName, booleanValue);
            return booleanValue;
        }
        return booleanPropertyMap.get(propertyName);
    }

    private String getSubValue(String value, int index) {
        String[] subValues = value.split(VALUES_DELIMITER);
        if (subValues.length <= index) {
            return null;
        }
        return subValues[index];
    }

    private Properties getConfigProperties() {
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
