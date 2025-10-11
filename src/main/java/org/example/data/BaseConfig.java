package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConvertUtils;
import org.example.utils.DataValidator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public abstract class BaseConfig {
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
        DataValidator.notBlank(propertyName, "propertyName");
        String propertyValue = System.getProperty(propertyName);

        if (propertyValue == null) {
            propertyValue = System.getenv(propertyName);
        }

        if (propertyValue == null) {
            propertyValue = getConfigProperties().getProperty(propertyName);
        }

        if (propertyValue == null) {
            throw new SmartRuntimeException(String.format(
                    "configuration property '%s' is undefined.", propertyName));
        }
        log.debug("{} property {}={}", getClass().getSimpleName(), propertyName, propertyValue);
        return propertyValue;
    }

    protected long getLongProperty(String propertyName) {
        long longValue = Integer.parseInt(getStringProperty(propertyName));
        log.debug("Config long value: {}", longValue);
        return longValue;
    }

    protected int getPercentageProperty(String propertyName) {
        String propertyValue = getStringProperty(propertyName);

        if (!propertyValue.trim().endsWith("%")) {
            throw new SmartValidationException(String.format(
                    "Percentage configuration property '%s' value '%s' should end with %s",
                    propertyName, propertyValue, "%"));
        }
        String percentageValue = propertyValue.substring(0, propertyValue.indexOf("%"));
        long longValue = Integer.parseInt(percentageValue);
        DataValidator.range(longValue, 0, 100, "percentage");
        log.debug("Config percentage value: {}", longValue);
        return (int) longValue;
    }

    protected double getDoubleProperty(String propertyName) {
        double doubleValue = Double.parseDouble(getStringProperty(propertyName));
        log.debug("Config double value: {}", doubleValue);
        return doubleValue;
    }

    protected boolean getBooleanProperty(String propertyName) {
        boolean booleanValue = ConvertUtils.stringToBoolean(
                getStringProperty(propertyName));
        log.debug("Config boolean value: {}", booleanValue);
        return booleanValue;
    }

    protected String getSubValue(String value, int index) {
        String[] subValues = value.split(VALUES_DELIMITER);
        if (subValues.length <= index) {
            throw new SmartRuntimeException(String.format(
                    "Config value '%s' doesn't have the part %d.", value, index + 1));
        }
        String subValue = subValues[index];
        log.debug("Config string sub value value: {}", subValue);
        return subValue;
    }

    protected int getIntegerSubValue(String value, int index) {
        String subValue = getSubValue(value, index);
        int integerValue = Integer.parseInt(subValue);
        log.debug("Config integer sub value value: {}", integerValue);
        return integerValue;
    }

    protected Properties getConfigProperties() {
        if (configProperties == null) {
            configProperties = new Properties();
            try {
                configProperties.load(new FileInputStream(filePath));
            } catch (IOException e) {
                throw new SmartRuntimeException(
                        "Cannot initialize config properties file.", e);
            }
        }
        log.debug("Config properties: {}", configProperties);
        return configProperties;
    }
}
