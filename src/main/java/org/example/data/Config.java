package org.example.data;

import org.example.enums.BrowserName;
import org.example.enums.TestMode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The configuration file class.
 * See README.md fi;e for more details.
 */
public class Config {
    private static final String TESTNG_FILE_PROP_NAME = "testngFile";
    private static final String TREAD_COUNT_PROP_NAME = "threadCount";
    private static final String TEST_MODE_PROP_NAME = "testMode";
    private static final String BROWSER_PROP_NAME = "browser";
    private static final String HEADLESS_PROP_NAME = "headless";
    private static final String REMOTE_HOST = "remoteHost";
    private static final String EMULATORS = "emulators";
    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String START_DATE = "startDate";
    private static final String VALUES_DELIMITER = ":";

    private static final Map<String, String> stringPropertyMap = new HashMap<>();
    private static final Map<String, Integer> integerPropertyMap = new HashMap<>();
    private static final Map<String, Boolean> booleanPropertyMap = new HashMap<>();

    private final String filePath;
    private Properties configProperties;

    /**
     * Config class constructor by the config file path.
     * @param filePath The config file path.
     */
    public Config(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the TestNG file name.
     * @return The TestNG file name.
     */
    synchronized public String getTestngFile() {
        return getStringProperty(TESTNG_FILE_PROP_NAME);
    }

    /**
     * Returns the maximal thread count.
     * @return The maximal tread count.
     */
    synchronized public int getThreadCount() {
        return getIntegerProperty(TREAD_COUNT_PROP_NAME);
    }

    /**
     * Returns the test mode name.
     * @return The test mode name.
     */
    synchronized public TestMode getTestMode() {
        return TestMode.fromString(getStringProperty(TEST_MODE_PROP_NAME));
    }

    /**
     * Returns remote Selenium server URL.
     * @return The remote host URL.
     */
    synchronized public String getRemoteHost() {
        return getStringProperty(REMOTE_HOST);
    }

    /**
     * Returns the AWS access key.
     * @return The AWS access key.
     */
    synchronized public String getAccessKey() {
        return getStringProperty(ACCESS_KEY);
    }

    /**
     * Returns the AWS secret key.
     * @return The AWS secret key.
     */
    synchronized public String getSecretKey() {
        return getStringProperty(SECRET_KEY);
    }

    /**
     * Returns test start date.
     * @return The test start date.
     */
    synchronized public String getStartDate() {
        return getStringProperty(START_DATE);
    }

    /**
     * Returns Appium emulator name by its index.
     * @param index The emulator index.
     * @return The emulator name.
     */
    synchronized public String getEmulator(int index) {
        String emulators = getStringProperty(EMULATORS);
        String[] emulatorsArray = emulators.split(VALUES_DELIMITER);

        if (emulatorsArray.length <= index) {
            throw new RuntimeException("Wrong emulator index: " + index);
        }

        return emulatorsArray[index];
    }

    /**
     * Returns the browser name.
     * @return The browser name.
     */
    synchronized public BrowserName getBrowserName() {
        String browser = getStringProperty(BROWSER_PROP_NAME);
        return BrowserName.fromString(getSubValue(browser, 0));
    }

    /**
     * Returns the browser version.
     * @return The browser version.
     */
    synchronized public String getBrowserVersion() {
        String browser = getStringProperty(BROWSER_PROP_NAME);
        return getSubValue(browser, 1);
    }

    /**
     * Returns true/false headless flag.
     * @return The headless flag.
     */
    synchronized public boolean getHeadless() {
        return getBooleanProperty(HEADLESS_PROP_NAME);
    }

    private String getStringProperty(String propertyName) {
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
