package org.example.data;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConvertUtils;
import org.example.utils.DataValidator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.List;

/**
 * Smart value type. Encapsulates:
 * Object type of the value object.
 * Key value type for Map object type;
 * Value type for Map and Collections object types like:
 * List
 * Set
 * Queue
 * Array
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class SmartType extends SmartObject {

    private static final Class<?>[] COMMON_CLASSES = {
            SmartValue.class, SmartTemporal.class, SmartObject.class,
            Number.class, Collection.class, Map.class, Document.class, Node.class,
            JSONObject.class, JSONArray.class, JsonElement.class, Date.class,
            Temporal.class, File.class, Enum.class, Object.class };

    @Getter
    private final Class<?> objectClass;
    @Getter
    private Class<?> keyClass;
    @Getter
    private SmartType valueSmartType;
    @Getter
    private Map<String, SmartType> fieldTypesMap;
    private boolean isArray;

    /**
     * Creates smart type from object class.
     * @param objectClass The object class.
     * @return The smart type.
     * @param <T> The object type.
     */
    public static <T> SmartType fromClass(Class<T> objectClass) {
        DataValidator.notNull(objectClass, "objectClass");

        SmartType smartType = new SmartType(objectClass);
        log.debug("""
                Smart type object is created from object class.
                Class: {}
                Smart type:
                {}
                """.stripIndent(),
                objectClass.getName(), smartType);
        return smartType;
    }

    /**
     * Creates collection smart type from collection class and value class.
     * @param collectionClass The collection class.
     * @param valueSmartType The value smart type.
     * @return The smart type.
     * @param <T> The object type.
     */
    public static <T> SmartType fromCollectionClass(Class<T> collectionClass, SmartType valueSmartType) {
        DataValidator.notNull(collectionClass, "collectionClass");
        DataValidator.notNull(valueSmartType, "valueSmartType");

        SmartType smartType = new SmartType(collectionClass, valueSmartType);
        log.debug("""
                Smart type object is created from collection class and value smart type.
                Class: {}
                Value smart type:
                {}
                Smart type:
                {}
                """.stripIndent(),
                collectionClass.getName(),
                valueSmartType, smartType);
        return smartType;
    }

    /**
     * Creates collection smart type from map class, key class and value smart type.
     * @param mapClass The map class.
     * @param keyClass The key class.
     * @param valueSmartType The value smart type.
     * @return The smart type.
     * @param <T> The object type.
     */
    public static <T,K> SmartType fromMapClass(Class<T> mapClass, Class<K> keyClass, SmartType valueSmartType) {
        DataValidator.notNull(mapClass, "collectionClass");
        DataValidator.notNull(valueSmartType, "valueSmartType");

        SmartType smartType = new SmartType(mapClass, keyClass, valueSmartType);
        log.debug("""
                Smart type object is created from collection class and value smart type.
                Class: {}
                Value smart type:
                {}
                Smart type:
                {}
                """.stripIndent(),
                mapClass.getName(),
                valueSmartType, smartType);
        return smartType;
    }


    /**
     * Creates smart type from array class and value smart type.
     * @return The smart type.
     */
    public static SmartType fromArrayValueSmartType(SmartType valueSmartType) {
        DataValidator.notNull(valueSmartType, "valueSmartType");

        SmartType smartType = new SmartType(Object.class, valueSmartType);
        smartType.isArray = true;
                log.debug("""
                Smart type object is created from array value smart type.
                Value type: {}
                Smart type:
                {}
                """.stripIndent(),
                valueSmartType, smartType);
        return smartType;
    }

    /**
     * Creates smart type from POJO class and fields map.
     * @param pojoClass The POJO class.
     * @param fieldsMap The fields string:smart type map.
     * @return The smart type.
     * @param <T> The object type.
     */
    public static <T> SmartType fromPojoClass(Class<T> pojoClass, Map<String, SmartType> fieldsMap) {
        DataValidator.notNull(pojoClass, "pojoClass");
        DataValidator.notNull(fieldsMap, "fieldsMap");

        SmartType smartType = new SmartType(pojoClass, fieldsMap);
        log.debug("""
                Smart type object is created from POJO class.
                Class: {}
                Field types map:
                {}
                Smart type:
                {}
                """.stripIndent(),
                pojoClass.getName(),
                fieldsMap, smartType);
        return smartType;
    }

    /**
     * Creates smart type from enum class.
     * @param enumClass The enum class.
     * @return The smart type.
     * @param <T> The object type.
     */
    public static <T> SmartType fromEnumClass(Class<T> enumClass) {
        DataValidator.notNull(enumClass, "enumClass");

        SmartType smartType = new SmartType(enumClass);
        log.debug("""
                Smart type object is created from enum class.
                Class: {}
                Smart type:
                {}
                """.stripIndent(),
                enumClass.getName(),
                smartType);
        return smartType;
    }

    /**
     * Gets smart type from object;
     * @param object The object;
     * @return The smart type.
     */
    public static <T> SmartType fromObject(Object object) {
        SmartType type;

        if (object == null || object == JSONObject.NULL) {
            type = new SmartType(Null.class);
        }
        else {
            Class<?> objectClass = object.getClass();

            try {
                if (object instanceof Collection collection) {
                    type = fromCollection(collection);
                }
                else if (ObjectUtils.isArray(object)) {
                    type = fromArray((T[]) object);
                }
                else if (object instanceof Map map) {
                    type = fromMap(map);
                }
                else if (object instanceof Color color) {
                    type = fromClass(objectClass);
                }
                else if (isPojoClass(objectClass)) {
                    type = fromPojoObject(object);
                }
                else {
                    type = fromClass(objectClass);
                }
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                    Cannot get smart type from object.
                    Object:
                    %s
                    """.stripIndent(),
                        object));
            }
        }
        log.debug("""
                Object smart type returned.
                Object:
                {}
                Type:
                {}
                """.stripIndent(),
                object, type);
        return type;
    }

    /**
     * Returns true if object class is POJO class,
     * or false otherwise.
     * @param objectClass The object class.
     * @return The true/false flag.
     */
    public static boolean isPojoClass(Class<?> objectClass) {
        boolean result;

        if (objectClass == null) {
            result = false;
        }
        else {
            result = !(objectClass.isPrimitive() ||
                    objectClass.isArray() ||
                    objectClass.isRecord() ||
                    objectClass.isEnum() ||
                    objectClass.isAnnotation() ||
                    objectClass.isInterface() ||
                    objectClass.isLocalClass() ||
                    objectClass.isSynthetic() ||
                    Number.class.isAssignableFrom(objectClass) ||
                    Boolean.class.isAssignableFrom(objectClass) ||
                    String.class.isAssignableFrom(objectClass) ||
                    Character.class.isAssignableFrom(objectClass) ||
                    StringBuffer.class.isAssignableFrom(objectClass) ||
                    Collection.class.isAssignableFrom(objectClass) ||
                    Map.class.isAssignableFrom(objectClass) ||
                    JSONObject.class.isAssignableFrom(objectClass) ||
                    JSONArray.class.isAssignableFrom(objectClass) ||
                    Node.class.isAssignableFrom(objectClass) ||
                    Date.class.isAssignableFrom(objectClass) ||
                    File.class.isAssignableFrom(objectClass) ||
                    URL.class.isAssignableFrom(objectClass) ||
                    URI.class.isAssignableFrom(objectClass) ||
                    Path.class.isAssignableFrom(objectClass) ||
                    Temporal.class.isAssignableFrom(objectClass) ||
                    Color.class.isAssignableFrom(objectClass) ||
                    SmartObject.class.isAssignableFrom(objectClass) ||
                    SmartTemporal.class.isAssignableFrom(objectClass) ||
                    SmartType.class.isAssignableFrom(objectClass) ||
                    Modifier.isAbstract(objectClass.getModifiers()) ||
                    objectClass == Class.class);
        }

        // Log the class and result
        log.debug("""
                Is object class POJO?
                Class: {}
                Result: {}
                """.stripIndent(), objectClass, result);
        return result;
    }

    /**
     * Constructs smart value type with object class.
     * @param objectClass The object type.
     */
    private SmartType(Class<?> objectClass) {
        DataValidator.notNull(objectClass, "objectClass");

        if (objectClass == JSONObject.NULL.getClass()) {
            objectClass = Null.class;
        }
        this.objectClass = objectClass;
        log.debug("""
            Smart value type created with:
            Object type: {}
            """.stripIndent(),
                objectClass);
    }

    /**
     * Constructs smart value type for collections
     * with object class and value (element) smart type.
     * @param objectClass The object type.
     * @param valueSmartType The value type.
     */
    private SmartType(Class<?> objectClass, SmartType valueSmartType) {
        DataValidator.notNull(objectClass, "objectClass");
        DataValidator.notNull(valueSmartType, "valueSmartType");

        this.objectClass = objectClass;
        this.valueSmartType = valueSmartType;
        log.debug("""
            Smart value type created with:
            Object type: {}
            Value type:
            """.stripIndent(),
                objectClass, valueSmartType);
    }

    /**
     * Constructs smart value type for maps
     * with object class, key class and value smart type.
     * key class and object value type.
     * @param objectClass The object type.
     * @param valueSmartType The value type.
     */
   private SmartType(Class<?> objectClass, Class<?> keyClass, SmartType valueSmartType) {
       DataValidator.notNull(objectClass, "objectClass");
       DataValidator.notNull(keyClass, "keyClass");
       DataValidator.notNull(valueSmartType, "valueSmartType");

        this.objectClass = objectClass;
        this.keyClass = keyClass;
        this.valueSmartType = valueSmartType;
        log.debug("""
            Smart value type created with:
            Object type: {}
            Key type: {}
            Value type: {}
            """.stripIndent(),
                objectClass, keyClass, valueSmartType);
    }

    /**
     * Constructs smart value type for Java POJO class
     * with object class and class field smart types.
     * @param pojoClass The class name
     * @param fieldTypesMap The field types.
     */
    private SmartType(Class<?> pojoClass, Map<String, SmartType> fieldTypesMap) {
        DataValidator.notNull(pojoClass, "pojoClass");
        DataValidator.notNull(fieldTypesMap, "fieldTypesMap");

        this.objectClass = pojoClass;
        this.fieldTypesMap = fieldTypesMap;
        log.debug("""
            Smart value type created with:
            Class name: {}
            Object type:
            {}
            Fields:
            {}
            """.stripIndent(),
            pojoClass, objectClass, fieldTypesMap);
    }

    /**
     * Returns true if smart type is array type,
     * or false otherwise.
     * @return The true/false flag.
     */
    public boolean isArray() {
        return isArray;
    }

    private static <T> SmartType fromCollection(Collection<T> collection) {
        DataValidator.notNull(collection, "collection");

        SmartType valueSmartType = getCollectionValueSmartType(collection);
        SmartType collectionSmartType = fromCollectionClass(collection.getClass(), valueSmartType);
        log.debug("""
                Collection smart type returned.
                Collection:
                {}
                Type: {}
                """.stripIndent(),
                collection, collectionSmartType);
        return collectionSmartType;
    }

    private static <K,V> SmartType fromMap(Map<K, V> map) {
        DataValidator.notNull(map, "map");

        Class<?> objectClass = map.getClass();
        Class<K> keyClass = getMapKeyClass(map);
        SmartType valueSmartType = getMapValueSmartType(map);
        SmartType mapSmartType = new SmartType(objectClass, keyClass, valueSmartType);
        log.debug("""
                Map smart type returned.
                Collection:
                {}
                Type: {}
                """.stripIndent(),
                map, mapSmartType);
        return mapSmartType;
    }

    private static <T> SmartType fromArray(T[] array) {
        DataValidator.notNull(array, "array");

        SmartType valueSmartType = getArrayValueSmartType(array);
        SmartType arraySmartType = fromArrayValueSmartType(valueSmartType);
        log.debug("""
                Array smart object type returned.
                Array:
                {}
                Type: {}
                """.stripIndent(),
                array, arraySmartType);
        return arraySmartType;
    }

    private static SmartType fromPojoObject(Object object) {
        Map<String, SmartType> fieldTypes = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        Class<?> pojoClass = object.getClass();

        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue;

            try {
                fieldValue = field.get(object);
            }
            catch (IllegalAccessException e) {
                throw new SmartRuntimeException(e);
            }
            SmartType fieldType = fromObject(fieldValue);
            String fieldName = field.getName();
            fieldTypes.put(fieldName, fieldType);
        }
        SmartType classSmartType = new SmartType(pojoClass, fieldTypes);
        log.debug("""
                Class objet smart type returned.
                Class name: {}
                Object:
                {}
                Type:
                {}
                """.stripIndent(),
                pojoClass, object, classSmartType);
        return classSmartType;
    }

    /**
     * Compares smart type with other object.
     * @param object The object.
     * @return The true if equals or false otherwise.
     */
    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("The actual smart type object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Two smart type objects are compared. They are the same object.");
            return true;
        }
        if (object instanceof SmartType actual) {

            boolean result =
                    getObjectClass() == actual.getObjectClass() &&
                    getKeyClass() == actual.getKeyClass() &&
                    (getValueSmartType() == actual.getValueSmartType() ||
                    getValueSmartType().equals(actual.getValueSmartType()) &&
                    (getFieldTypesMap() == actual.getFieldTypesMap() ||
                     getFieldTypesMap().equals(actual.getFieldTypesMap())));

            log.debug(String.format("""
                    Smart type equals() called.
                    Result: {}
                    Expected:
                    {}
                    Actual:
                    {}
                    """.stripIndent(),
                    result, this, actual));
            return result;
        }
        else {
            return false;
        }
    }

    /**
     * Converts smart type to string value.
     * @return The string value.
     */
    @Override
    public String toString() {
        return String.format("""
                Smart type class: %s
                Object class: %s
                Key class: %s
                Value type:
                %s
                Field types:
                %s
                """.stripIndent(),
                this.getClass().getName(),
                objectClass.getName(),
                keyClass != null ? keyClass.getName() : null,
                valueSmartType,
                fieldTypesMap);
    }

    /**
     * Converts smart type to hash code.
     * @return The has code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(objectClass, valueSmartType, keyClass, fieldTypesMap);
    }

    /**
     * Gets collection value smart type.
     * @param collection The collection.
     * @return The smart type.
     * @param <T> The value type.
     */
    public static <T,K> SmartType getCollectionValueSmartType(Collection<T> collection) {
        DataValidator.notNull(collection, "collection");
        SmartType valueType = null;
        List<T> list = new ArrayList<>(collection);
        Object element;

        for (T t : list) {
            Class<?> valueClass;
            Class<?> subKeyClass = null;
            SmartType subValueType = null;
            element = t;

            if (valueType == null) {
                valueType = getElementType(element);
                continue;
            }
            valueClass = valueType.getObjectClass();

            if (element instanceof Collection<?> collectionElement) {
                subValueType = getCollectionValueSmartType(collectionElement);
            } else if (element instanceof JSONArray jsonArrayElement) {
                subValueType = getJsonArrayValueSmartType(jsonArrayElement);
            } else if (element instanceof JSONObject jsonObjectElement) {
                subKeyClass = String.class;
                subValueType = getJsonObjectValueSmartType(jsonObjectElement);
            }
            if (subKeyClass != null) {
                valueType = SmartType.fromMapClass(valueClass, subKeyClass, subValueType);
            } else if (subValueType != null) {
                if (Collection.class.isAssignableFrom(valueType.getObjectClass())) {
                    valueType = subValueType;
                } else {
                    valueType = getCommonValueSmartType(valueType, subValueType);
                }
            } else {
                valueType = getCommonValueSmartType(valueType, SmartType.fromObject(element));
            }
        }
        if (valueType == null) {
            valueType = SmartType.fromClass(Object.class);
        }
        log.debug("""
                Collection value type is returned.
                Collection class: {}
                Collection:
                {}
                Type:
                {}
                """.stripIndent(),
                collection.getClass().getName(),
                collection, valueType);
        return valueType;
    }

    /**
     * Gets JSON array value smart type.
     * @param jsonArray The collection.
     * @return The smart type.
     */
    public static SmartType getJsonArrayValueSmartType(JSONArray jsonArray) {
        DataValidator.notNull(jsonArray, "jsonArray");
        SmartType valueType = null;
        Object element;

        for (int i = 0; i < jsonArray.length(); i++) {
            Class<?> valueClass;
            Class<?> subKeyClass = null;
            SmartType subValueType = null;
            element = jsonArray.get(i);

            if (valueType == null) {

                if (element instanceof JSONArray jsonArrayElement) {
                    valueType = getJsonArrayValueSmartType(jsonArrayElement);
                }
                else {
                    valueType = getElementType(element);
                }
                continue;
            }
            valueClass = valueType.getObjectClass();

            if (element instanceof JSONArray jsonArrayElement) {
                subValueType = getJsonArrayValueSmartType(jsonArrayElement);
            }
            else if (element instanceof JSONObject jsonObjectElement) {
                subKeyClass = String.class;
                subValueType = getJsonObjectValueSmartType(jsonObjectElement);
            }
            if (subKeyClass != null) {
                valueType = SmartType.fromMapClass(valueClass, subKeyClass, subValueType);
            }
            else if (subValueType != null) {
                valueType = getCommonValueSmartType(valueType, subValueType);
            }
            else {
                valueType = getCommonValueSmartType(valueType, SmartType.fromObject(element));
            }
        }
        if (valueType == null) {
            valueType = SmartType.fromClass(Object.class);
        }
        log.debug("""
                JSON array value type is returned.
                JSON array:
                {}
                Type:
                {}
                """.stripIndent(),
                jsonArray, valueType);
        return valueType;
    }

    /**
     * Gets array value smart type.
     * @param array The array.
     * @return The value smart type.
     * @param <T> The arrays type.
     */
    public static <T> SmartType getArrayValueSmartType(T[] array) {
        DataValidator.notNull(array, "array");
        List<T> list = Arrays.asList(array);
        SmartType valueType = getCollectionValueSmartType(list);
        log.debug("""
                Array value type is returned.
                Array:
                {}
                Type:
                {}
                """.stripIndent(),
                array, valueType);
        return valueType;
    }

    /**
     * Gets map key class.
     * @param map The map.
     * @return The key class.
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static <K,V>  Class<K> getMapKeyClass(Map<K,V> map) {
        DataValidator.notNull(map, "map");
        SmartType keyType = getCollectionValueSmartType(map.keySet());
        Class<K> keyClass = (Class<K>) keyType.getObjectClass();
        log.debug("""
                Map key class is returned.
                Map class:
                Map:
                {}
                Key class: {}
                """.stripIndent(),
                map.getClass().getName(),
                map, keyClass.getName());
        return keyClass;
    }

    /**
     * Gets map value smart type.
     * @param map The map.
     * @return The smart type.
     * @param <K> The key type.
     * @param <V> The value type.
     */
    public static <K,V> SmartType getMapValueSmartType(Map<K,V> map) {
        DataValidator.notNull(map, "map");

        SmartType valueType = getCollectionValueSmartType(map.values());
        log.debug("""
                Map value type is returned.
                Map class: {}
                Map:
                {}
                Type:
                {}
                """.stripIndent(),
                map.getClass().getName(),
                map, valueType);
        return valueType;
    }

    /**
     * Gets JSON object value smart type.
     * @param jsonObject The JSON object.
     * @return The smart type.
     * @param <V> The value type.
     */
    public static <V> SmartType getJsonObjectValueSmartType(JSONObject jsonObject) {
        DataValidator.notNull(jsonObject, "jsonObject");

        Map<String, V> map = ConvertUtils.objectToMap(jsonObject);
        SmartType valueType = getCollectionValueSmartType(map.values());
        log.debug("""
                Map value type is returned.
                Map class: {}
                Map:
                {}
                Type:
                {}
                """.stripIndent(),
                map.getClass().getName(),
                map, valueType);
        return valueType;
    }

    private static <T> SmartType getElementType(T element) {
        SmartType elementType;

        if (element instanceof Collection collectionElement) {
            Class<?> elementClass = collectionElement.getClass();
            SmartType subValueType = getCollectionValueSmartType(collectionElement);
            elementType = SmartType.fromCollectionClass(elementClass, subValueType);
        }
        else if (element instanceof Map mapElement) {
            Class<?> elementClass = mapElement.getClass();
            Class<?> subKeyClass = getMapKeyClass(mapElement);
            SmartType subValueType = getMapValueSmartType(mapElement);
            elementType = SmartType.fromMapClass(elementClass, subKeyClass, subValueType);
        }
        else {
            elementType = SmartType.fromObject(element);
        }
        return elementType;
    }

    private static Class<?> getCommonParentClass(Class<?> objectClass1, Class<?> objectClass2) {
        Class<?> parentClass = null;

        if (objectClass1.isAssignableFrom(objectClass2)) {
            parentClass = objectClass1;
        }
        else if (objectClass2.isAssignableFrom(objectClass1)) {
            parentClass = objectClass2;
        }
        else {
            // Check the element is instance of the common base class
            for (Class<?> commonClass : COMMON_CLASSES) {

                if (commonClass.isAssignableFrom(objectClass1) &&
                        commonClass.isAssignableFrom(objectClass2)) {
                    parentClass = commonClass;
                    break;
                }
            }
        }
        return parentClass;
    }

    private static SmartType getCommonValueSmartType(SmartType objectType1, SmartType objectType2) {
        Class<?> objectClass1 = objectType1.getObjectClass();
        Class<?> objectClass2 = objectType2.getObjectClass();
        return SmartType.fromClass(getCommonParentClass(objectClass1, objectClass2));
    }
}
