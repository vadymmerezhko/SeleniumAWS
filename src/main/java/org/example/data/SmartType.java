package org.example.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.ValueType;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.temporal.Temporal;
import java.util.*;

import static org.apache.commons.lang3.ObjectUtils.isArray;
import static org.example.enums.ValueType.*;

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
public final class SmartType {
    @Getter
    private final ValueType objectType;
    @Getter
    private ValueType keyType;
    @Getter
    private SmartType valueSmartType;
    @Getter
    private String className;
    @Getter
    private Map<String, SmartType> fieldTypesMap;

    /**
     * Class to get collection element type (class).
     * @param <T> Element type.
     */
    static class GenericCollection<T> {
        private final Collection<T> collection;

        public GenericCollection(Collection<T> collection) {
            this.collection = collection;
        }

        public Class<?> getElementClass() {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType paramType) {
                Type[] typeArguments = paramType.getActualTypeArguments();

                if (typeArguments.length > 0) {
                    return (Class<?>) typeArguments[0];
                }
            }
            // Default case if type cannot be determined
            return Object.class;
        }
    }

    /**
     * Class to get map element type (class).
     * @param <K> The element key type.
     * @param <V> The element value type.
     */
    static class GenericMap<K, V> {
        private final Map<K, V> map;
        private final Class<K> keyClass;
        private final Class<V> valueClass;

        @SuppressWarnings("unchecked")
        public GenericMap(Map<K, V> map) {
            this.map = map;

            if (!map.isEmpty()) {
                Map.Entry<K, V> firstEntry = map.entrySet().iterator().next();
                this.keyClass = (Class<K>) firstEntry.getKey().getClass();
                this.valueClass = (Class<V>) firstEntry.getValue().getClass();
            } else {
                this.keyClass = null;
                this.valueClass = null;
            }
        }

        public Class<K> getKeyClass() {
            return keyClass;
        }

        public Class<V> getValueClass() {
            return valueClass;
        }

        public void put(K key, V value) {
            map.put(key, value);
        }

        public V get(K key) {
            return map.get(key);
        }

        public Map<K, V> getMap() {
            return map;
        }
    }

    /**
     * Gets smart type from object;
     * @param object The object;
     * @return The smart type.
     */
    public static SmartType fromObject(Object object) {
        SmartType type;

        if (object == null) {
            type = new SmartType(NULL);
        }
        else {
            Class<?> objectClass = object.getClass();

            try {
                if (object instanceof Collection) {
                    type = getCollectionType(object);
                }
                else if (isArray(object)) {
                    type = getArrayType(object);
                }
                else if (object instanceof Map) {
                    type = getMapType(object);
                }
                else if (objectClass == String.class ||
                        objectClass == StringBuffer.class ||
                        objectClass == SmartValue.class ||
                        objectClass == Boolean.class ||
                        object instanceof Number ||
                        object instanceof Date ||
                        object instanceof Temporal ||
                        object instanceof SmartDateInterface ||
                        object instanceof Path ||
                        object instanceof Enum ||
                        objectClass == JSONObject.class ||
                        objectClass == JSONArray.class ||
                        objectClass == File.class ||
                        objectClass == java.net.URL.class ||
                        objectClass == java.net.URI.class ||
                        objectClass == Character.class) {
                    String className = object.getClass().getSimpleName();
                    type = new SmartType(ValueType.fromString(className));
                }
                else {
                    type = getClassType(object);
                }
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                    Cannot get smart type from object.
                    Object:
                    {}
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
     * Constructs smart value type with object type.
     * @param objectType The object type.
     */
    public SmartType(ValueType objectType) {
        DataValidationUtils.validateNotNull(objectType, "objectType");

        this.objectType = objectType;
        log.debug("""
            Smart value type created with:
            Object type: {}
            """.stripIndent(),
            objectType);
    }

    /**
     * Constructs smart value type with object type
     * and object value type.
     * @param objectType The object type.
     * @param valueSmartType The value type.
     */
    public SmartType(ValueType objectType, SmartType valueSmartType) {
        DataValidationUtils.validateNotNull(objectType, "objectType");
        DataValidationUtils.validateNotNull(valueSmartType, "valueSmartType");

        this.objectType = objectType;
        this.valueSmartType = valueSmartType;
        log.debug("""
            Smart value type created with:
            Object type: {}
            Value type:
            """.stripIndent(),
            objectType, valueSmartType);
    }

    /**
     * Constructs smart value type with object type
     * and object value type.
     * @param objectType The object type.
     * @param valueSmartType The value type.
     */
   public SmartType(ValueType objectType, ValueType keyType, SmartType valueSmartType) {
       DataValidationUtils.validateNotNull(objectType, "objectType");
       DataValidationUtils.validateNotNull(keyType, "keyType");
       DataValidationUtils.validateNotNull(valueSmartType, "valueSmartType");

        this.objectType = objectType;
        this.keyType = keyType;
        this.valueSmartType = valueSmartType;
        log.debug("""
            Smart value type created with:
            Object type: {}
            Key type: {}
            Value type: {}
            """.stripIndent(),
            objectType, keyType, valueSmartType);
    }

    /**
     * Constructs smart value type for Java POJO class
     * and class field types.
     * @param className The class name
     * @param fieldTypesMap The field types.
     */
    public SmartType(String className, Map<String, SmartType> fieldTypesMap) {
        DataValidationUtils.validateNotBlank(className, "className");
        DataValidationUtils.validateNotNull(fieldTypesMap, "fieldTypesMap");

        this.className = className;
        this.objectType = CLASS;
        this.fieldTypesMap = fieldTypesMap;
        log.debug("""
            Smart value type created with:
            Class name: {}
            Object type:
            {}
            Fields:
            {}
            """.stripIndent(),
            className, objectType, fieldTypesMap);
    }

    /**
     * Constructs smart value type by enum value.
     * and class field types.
     * @param enumValue The enum value.
     */
    public SmartType(Enum enumValue) {
        DataValidationUtils.validateNotNull(enumValue, "enumValue");

        this.className = enumValue.getClass().getName();
        this.objectType = CLASS;
        log.debug("""
            Smart value type created with:
            Class name: {}
            Object type:
            {}
            Fields:
            {}
            """.stripIndent(),
                className, objectType, fieldTypesMap);
    }


    private static <T> SmartType getCollectionType(Object collectionObject) {
        DataValidationUtils.validateNotNull(collectionObject, "collectionObject");

        Collection<T> collection = (Collection<T>) collectionObject;
        ValueType objectType = ValueType.fromClass(collection.getClass());
        GenericCollection<T> genericCollection = new GenericCollection<>(collection);
        ValueType valueType = ValueType.fromClass(genericCollection.getElementClass());
        SmartType valueSmartType = fromSmartType(new SmartType(valueType));
        SmartType collectionSmartType = new SmartType(objectType, valueSmartType);
        log.debug("""
                Collection smart type returned.
                Collection:
                {}
                Type: {}
                """.stripIndent(),
                collection, collectionSmartType);
        return collectionSmartType;
    }

    private static <K,V> SmartType getMapType(Object mapObject) {
        DataValidationUtils.validateNotNull(mapObject, "mapObject");

        Map<K,V> map = (Map<K,V>) mapObject;
        ValueType objectType = ValueType.fromClass(map.getClass());
        GenericMap<K, V> genericMap = new GenericMap<>(map);
        ValueType keyType = ValueType.fromClass(genericMap.getKeyClass());
        ValueType valueType = ValueType.fromClass(genericMap.getValueClass());
        SmartType valueSmartType = fromSmartType(new SmartType(valueType));
        SmartType mapSmartType = new SmartType(objectType, keyType, valueSmartType);
        log.debug("""
                Map smart type returned.
                Collection:
                {}
                Type: {}
                """.stripIndent(),
                map, mapSmartType);
        return mapSmartType;
    }

    private static <T> SmartType getArrayType(Object arrayObject) {
        DataValidationUtils.validateNotNull(arrayObject, "arrayObject");

        T[] array = (T[]) arrayObject;
        ValueType objectType = ValueType.fromClass(array.getClass());
        Class<?> elementClass = array.getClass().getComponentType();
        ValueType valueType = ValueType.fromClass(elementClass);
        SmartType valueSmartType = fromSmartType(new SmartType(valueType));
        SmartType arraySmartType = new SmartType(objectType, valueSmartType);
        log.debug("""
                Array smart object type returned.
                Array:
                {}
                Type: {}
                """.stripIndent(),
                array, arraySmartType);
        return arraySmartType;
    }


    private static SmartType fromSmartType(SmartType sourceSmartType) {
        SmartType targetSmartType;
        ValueType sourceObjectType = sourceSmartType.getObjectType();
        ValueType sourceKeyType = sourceSmartType.getKeyType();
        SmartType valueSmartType = sourceSmartType.getValueSmartType();

        if (sourceObjectType.isCollection() ||
                sourceObjectType == MAP ||
                sourceObjectType == ValueType.ARRAY) {

            valueSmartType = fromSmartType(valueSmartType);

            if (sourceObjectType == MAP) {
                targetSmartType = new SmartType(sourceObjectType, sourceKeyType, valueSmartType);
            }
            else {
                targetSmartType = new SmartType(sourceObjectType, valueSmartType);
            }
        }
        else {
            targetSmartType = sourceSmartType;
        }
        log.debug("""
                Source smart type converted to target smart type..
                Source:
                {}
                Target:
                {}
                """.stripIndent(),
                sourceSmartType, targetSmartType);
        return targetSmartType;
    }

    private static SmartType getClassType(Object object) {
        Map<String, SmartType> fieldTypes = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        String className = object.getClass().getName();

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
        SmartType classSmartType = new SmartType(className, fieldTypes);
        log.debug("""
                Class objet smart type returned.
                Class name: {}
                Object:
                {}
                Type:
                {}
                """.stripIndent(),
                className, object, classSmartType);
        return classSmartType;
    }

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

            boolean result = getObjectType().equals(actual.objectType) &&
                    getObjectType().equals(actual.getObjectType()) &&
                    (getKeyType() != null && getKeyType().equals(actual.getKeyType())) &&
                    (getValueSmartType() != null && getValueSmartType().equals(actual.valueSmartType)) &&
                    (getClassName() != null && getClassName().equals(actual.getClassName())) &&
                    (getFieldTypesMap() != null && getFieldTypesMap().equals(actual.fieldTypesMap));

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

    @Override
    public String toString() {
        return String.format("""
                Smart type class: %s
                Object type: %s
                Value type: %s
                Key type: %s
                Field types:
                %s
                """.stripIndent(),
                this.getClass().getName(),
                objectType,
                valueSmartType,
                keyType,
                fieldTypesMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectType, valueSmartType,
                keyType, className, fieldTypesMap);
    }
}
