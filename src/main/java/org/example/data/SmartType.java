package org.example.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
    private final Class<?> objectClass;
    @Getter
    private Class<?> keyClass;
    @Getter
    private SmartType valueSmartType;
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
    public static <T> SmartType fromObject(Object object) {
        SmartType type;

        if (object == null) {
            type = new SmartType(Null.class);
        }
        else {
            Class<?> objectClass = object.getClass();

            try {
                if (object instanceof Collection collection) {
                    type = fromCollection(collection);
                }
                else if (isArray(object)) {
                    type = fromArray((T[]) object);
                }
                else if (object instanceof Map map) {
                    type = fromMap(map);
                }
                else if (objectClass == String.class ||
                        objectClass == StringBuffer.class ||
                        objectClass == SmartValue.class ||
                        objectClass == Boolean.class ||
                        object instanceof Number ||
                        object instanceof Temporal ||
                        object instanceof Date ||
                        object instanceof SmartTemporal ||
                        object instanceof Path ||
                        object instanceof Enum ||
                        objectClass == JSONObject.class ||
                        objectClass == JSONArray.class ||
                        objectClass == File.class ||
                        objectClass == java.net.URL.class ||
                        objectClass == java.net.URI.class ||
                        objectClass == Character.class) {
                    type = new SmartType(object.getClass());
                }
                else {
                    type = fromPojoClass(object);
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
     * @param objectClass The object type.
     */
    public SmartType(Class<?> objectClass) {
        DataValidationUtils.validateNotNull(objectClass, "objectClass");

        this.objectClass = objectClass;
        log.debug("""
            Smart value type created with:
            Object type: {}
            """.stripIndent(),
                objectClass);
    }

    /**
     * Constructs smart value type with object type
     * and object value type.
     * @param objectClass The object type.
     * @param valueSmartType The value type.
     */
    public SmartType(Class<?> objectClass, SmartType valueSmartType) {
        DataValidationUtils.validateNotNull(objectClass, "objectClass");
        DataValidationUtils.validateNotNull(valueSmartType, "valueSmartType");

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
     * Constructs smart value type with object type
     * and object value type.
     * @param objectClass The object type.
     * @param valueSmartType The value type.
     */
   public SmartType(Class<?> objectClass, Class<?> keyClass, SmartType valueSmartType) {
       DataValidationUtils.validateNotNull(objectClass, "objectClass");
       DataValidationUtils.validateNotNull(keyClass, "keyClass");
       DataValidationUtils.validateNotNull(valueSmartType, "valueSmartType");

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
     * and class field types.
     * @param pojoClass The class name
     * @param fieldTypesMap The field types.
     */
    public SmartType(Class<?> pojoClass, Map<String, SmartType> fieldTypesMap) {
        DataValidationUtils.validateNotNull(pojoClass, "pojoClass");
        DataValidationUtils.validateNotNull(fieldTypesMap, "fieldTypesMap");

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

    private static <T> SmartType fromCollection(Collection<T> collection) {
        DataValidationUtils.validateNotNull(collection, "collection");

        GenericCollection<T> genericCollection = new GenericCollection<>(collection);
        Class<?> valueClass = genericCollection.getElementClass();
        SmartType valueSmartType = new SmartType(valueClass);
        SmartType collectionSmartType = new SmartType(collection.getClass(), valueSmartType);
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
        DataValidationUtils.validateNotNull(map, "map");

        Class<?> objectClass = map.getClass();
        GenericMap<K, V> genericMap = new GenericMap<>(map);
        Class<K> keyClass = genericMap.getKeyClass();
        Class<V> valueClass = genericMap.getValueClass();
        SmartType valueSmartType = new SmartType(valueClass);
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
        DataValidationUtils.validateNotNull(array, "array");

        Class<?> objectClass = array.getClass();
        Class<?> valueClass = array.getClass().getComponentType();
        SmartType valueSmartType = new SmartType(valueClass);
        SmartType arraySmartType = new SmartType(objectClass, valueSmartType);
        log.debug("""
                Array smart object type returned.
                Array:
                {}
                Type: {}
                """.stripIndent(),
                array, arraySmartType);
        return arraySmartType;
    }

    private static SmartType fromPojoClass(Object object) {
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
                    (getValueSmartType() != null && getValueSmartType().equals(actual.valueSmartType)) &&
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
                Object class: %s
                Key class: %s
                Value type: %s
                Field types:
                %s
                """.stripIndent(),
                this.getClass().getName(),
                objectClass.getName(),
                keyClass.getName(),
                valueSmartType,
                fieldTypesMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectClass, valueSmartType, keyClass, fieldTypesMap);
    }
}
