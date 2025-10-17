package com.smarte2e.data;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.exceptions.SmartRuntimeException;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Smart data object class.
 */
@Slf4j
public abstract class SmartData extends SmartObject {
    private String name = getClass().getSimpleName();
    protected String dataSetName;

    /**
     * Constructs data object without data set name.
     */
    SmartData() {
        dataSetName = "";
        SmartDataInitializer.initialize(this);
        log.debug("Smart data object without set name is created: {}", name);
    }

    /**
     * Gets data object name without data set name.
     * @return The name.
     */
    public String getName() {
        log.debug("SmartData name is returned: {}", name);
        return name;
    }

    /**
     * Gets data set name.
     * @return The data set name.
     */
    public String getDataSetName() {
        log.debug("SmartData data set name is returned: {}", dataSetName);
        return dataSetName;
    }

    /**
     * Sets data set name.
     */
    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
        name = dataSetName + getName();
        log.debug("SmartData data set name is set: {}", dataSetName);
    }

    @Override
    public boolean equals(Object actual) {

        if (actual == this) {
            return true;
        }
        else if (actual instanceof SmartData smartData) {
            try {
                SmartAssert.assertData(this, smartData);
                return true;
            }
            catch (AssertionError e) {
                return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {

        try {
            StringBuffer stringBuffer = new StringBuffer();
            Field[] fields = getClass().getDeclaredFields();

            stringBuffer.append(this.getClass().getName());
            stringBuffer.append("\n");

            for (Field field : fields) {
                field.setAccessible(true);
                stringBuffer.append(field.getName());
                stringBuffer.append(":\n");
                stringBuffer.append(field.get(this).toString());
            }
            log.debug("""
                    Smart object converted to string.
                    Smart object:
                    {}
                    String:
                    {}
                    """.stripIndent(),
                    this, stringBuffer);
            return stringBuffer.toString();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot get smart object string.
                    Smart object:
                    %s
                    """.stripIndent(),
                    this), e);
        }
    }
}
