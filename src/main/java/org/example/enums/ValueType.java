package org.example.enums;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Value type enum.
 */
@Slf4j
public enum ValueType {
    STRING("String"),
    STRING_BUFFER("StringBuffer"),
    CVS_STRING("CvsString"),
    CHARACTER("Character"),
    SHORT("Short"),
    INTEGER("Integer"),
    LONG("Long"),
    BIG_INTEGER("BigInteger"),
    FLOAT("Float"),
    DOUBLE("Double"),
    BIG_DECIMAL("BigDecimal"),
    BOOLEAN("Boolean"),
    DATE("Date"),
    SMART_DATE("SmartDate"),
    LOCAL_DATE("LocalDate"),
    LOCAL_TIME("LocalTime"),
    LOCAL_DATE_TIME("LocalDateTime"),
    SMART_LOCAL_DATE("SmartLocalDate"),
    SMART_LOCAL_TIME("SmartLocalTime"),
    SMART_LOCAL_DATE_TIME("SmartLocalDateTime"),
    LIST("List"),
    SET("Set"),
    QUEUE("Queue"),
    VECTOR("Vector"),
    ARRAY("Array"),
    MAP("Map"),
    JSON_OBJECT("JSONObject"),
    JSON_ARRAY("JSONArray"),
    XML_NODE("Node"),
    FILE("File"),
    URL("URL"),
    URI("URI"),
    PATH("Path"),
    POSITIVE_INFINITY("Infinity"),
    NEGATIVE_INFINITY("-Infinity"),
    NAN("NaN"),
    NULL("null"),
    CLASS("Class"),
    ENUM("Enum"),
    SMART_VALUE("SmartValue");

    private final String name;
    private boolean isCollection = false;

    ValueType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns true if value type is collection,
     * or false otherwise.
     * @return The true/false flag.
     */
    public boolean isCollection() {
        return isCollection;
    }

    public static ValueType fromString(String name) {

       for (ValueType valueType : ValueType.values()) {
           if (valueType.name.equals(name)) {
               setCollectionFlag(valueType);
               log.debug("Value type enum returned: {}", valueType);
               return valueType;
           }
       }
       return CLASS;
    }

    public static ValueType fromClass(Class<?> valueClass) {
        ValueType valueType;

        if (valueClass.equals(ArrayList.class)) {
            valueType = LIST;
        }
        else if (valueClass.equals(HashMap.class)) {
            valueType = MAP;
        }
        else if (valueClass.equals(HashSet.class)) {
            valueType = SET;
        }
        else if (valueClass.equals(LinkedList.class)) {
            valueType = QUEUE;
        }
        else if (valueClass.equals(Vector.class)) {
            valueType = VECTOR;
        }
        else {
            valueType = fromString(valueClass.getSimpleName());
        }
        setCollectionFlag(valueType);
        log.debug("Value type enum returned: {}", valueType);
        return valueType;
    }

    private static void setCollectionFlag(ValueType valueType) {

        if (valueType == LIST || valueType == SET ||
            valueType == QUEUE || valueType == VECTOR) {
            valueType.isCollection = true;
        }
    }
}
