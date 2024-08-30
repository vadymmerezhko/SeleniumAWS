package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Smart data object class.
 */
@Slf4j
public abstract class SmartData extends SmartObject {
    private final String name = getClass().getSimpleName();

    public String getName() {
        return name;
    }

    public void initialize() {
        try {
            Field[] fields = getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(this);

                if (fieldObject instanceof SmartValue) {
                    ((SmartValue) fieldObject).setParent(this);
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Cannot initialize %s data object fields.");
        }
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
