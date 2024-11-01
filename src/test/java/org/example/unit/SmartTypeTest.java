package org.example.unit;

import org.example.data.Null;
import org.example.data.SmartType;
import org.example.enums.Platform;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.unit.supplemental.classes.NestedPojoClass;
import org.example.unit.supplemental.classes.PojoClass;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.*;

public class SmartTypeTest {

    @Test
    public void testFromClassWithValidClass() {
        Class<String> stringClass = String.class;
        SmartType smartType = SmartType.fromClass(stringClass);

        Assert.assertNotNull(smartType);
        Assert.assertEquals(smartType.getObjectClass(), stringClass);
    }

    @Test
    public void testFromCollectionClassWithValidCollection() {
        Class<List> listClass = List.class;
        SmartType valueSmartType = SmartType.fromClass(String.class);
        SmartType smartType = SmartType.fromCollectionClass(listClass, valueSmartType);

        Assert.assertNotNull(smartType);
        Assert.assertEquals(smartType.getObjectClass(), listClass);
        Assert.assertEquals(smartType.getValueSmartType(), valueSmartType);
    }

    @Test
    public void testFromMapClassWithValidMap() {
        Class<Map> mapClass = Map.class;
        Class<String> keyClass = String.class;
        SmartType valueSmartType = SmartType.fromClass(Integer.class);
        SmartType smartType = SmartType.fromMapClass(mapClass, keyClass, valueSmartType);

        Assert.assertNotNull(smartType);
        Assert.assertEquals(smartType.getObjectClass(), mapClass);
        Assert.assertEquals(smartType.getKeyClass(), keyClass);
        Assert.assertEquals(smartType.getValueSmartType(), valueSmartType);
    }

    @Test
    public void testFromArrayValueSmartTypeWithValidArray() {
        SmartType valueSmartType = SmartType.fromClass(Integer.class);
        SmartType smartType = SmartType.fromArrayValueSmartType(valueSmartType);

        Assert.assertNotNull(smartType);
        Assert.assertTrue(smartType.isArray());
        Assert.assertEquals(smartType.getValueSmartType(), valueSmartType);
    }

    @Test
    public void testFromPojoClassWithValidPojo() {
        Class<PojoClass> pojoClass = PojoClass.class;
        Map<String, SmartType> fieldsMap = new HashMap<>();
        fieldsMap.put("name", SmartType.fromClass(String.class));
        fieldsMap.put("age", SmartType.fromClass(Integer.class));

        SmartType smartType = SmartType.fromPojoClass(pojoClass, fieldsMap);

        Assert.assertNotNull(smartType);
        Assert.assertEquals(smartType.getObjectClass(), pojoClass);
        Assert.assertEquals(smartType.getFieldTypesMap(), fieldsMap);
    }

    @Test
    public void testFromEnumClassWithValidEnum() {
        Class<Platform> enumClass = Platform.class;
        SmartType smartType = SmartType.fromEnumClass(enumClass);

        Assert.assertNotNull(smartType);
        Assert.assertEquals(smartType.getObjectClass(), enumClass);
    }

    @Test
    public void testFromObjectWithValidObject() {
        String testObject = "Test String";
        SmartType smartType = SmartType.fromObject(testObject);

        Assert.assertNotNull(smartType);
        Assert.assertEquals(smartType.getObjectClass(), testObject.getClass());
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromClassWithNullClass() {
        SmartType.fromClass(null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFromCollectionClassWithNullCollectionClass() {
        SmartType.fromCollectionClass(null, SmartType.fromClass(String.class));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFromCollectionClassWithNullValueSmartType() {
        SmartType.fromCollectionClass(List.class, null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromMapClassWithNullMapClass() {
        SmartType.fromMapClass(null, String.class, SmartType.fromClass(Integer.class));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromMapClassWithNullKeyClass() {
        SmartType.fromMapClass(Map.class, null, SmartType.fromClass(Integer.class));
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromArrayValueSmartTypeWithNullValueSmartType() {
        SmartType.fromArrayValueSmartType(null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromPojoClassWithNullPojoClass() {
        SmartType.fromPojoClass(null, new HashMap<>());
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromPojoClassWithNullFieldsMap() {
        SmartType.fromPojoClass(PojoClass.class, null);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromEnumClassWithNullEnumClass() {
        SmartType.fromEnumClass(null);
    }

    @Test
    public void testFromObjectWithNullObject() {
        SmartType result = SmartType.fromObject(null);
        SmartType expected = SmartType.fromClass(Null.class);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWithLongObject() {
        SmartType result = SmartType.fromObject(123L);
        SmartType expected = SmartType.fromClass(Long.class);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWithListObject() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        SmartType result = SmartType.fromObject(list);
        SmartType valueType = SmartType.fromClass(String.class);
        SmartType expected = SmartType.fromCollectionClass(ArrayList.class, valueType);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWithArrayObject() {
        Double[] array = {1.1, 2.2, 3.3};
        SmartType result = SmartType.fromObject(array);
        SmartType valueType = SmartType.fromClass(Double.class);
        SmartType expected = SmartType.fromArrayValueSmartType(valueType);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWithMapObject() {
        Map<String,Boolean> map = new HashMap<>();
        map.put("true", true);
        map.put("false", false);
        SmartType result = SmartType.fromObject(map);
        SmartType valueType = SmartType.fromClass(Boolean.class);
        SmartType expected = SmartType.fromMapClass(HashMap.class, String.class, valueType);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWitIntegerObject() {
        Integer integer = 123;
        SmartType expected = SmartType.fromObject(333);
        SmartType result = SmartType.fromObject(integer);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWitEnumObject() {
        SmartType expected = SmartType.fromEnumClass(Platform.class);
        SmartType result = SmartType.fromObject(Platform.WINDOWS);

        Assert.assertEquals(result, expected);
    }

    @Test
    public void testFromObjectWitPojoObject() {
        PojoClass pojo = ConvertUtilsTest.createPojoObject();
        Map<String, SmartType> fieldsMap = new HashMap<>();
        fieldsMap.put("name", SmartType.fromClass(String.class));
        fieldsMap.put("value", SmartType.fromClass(Integer.class));
        fieldsMap.put("stringArray", SmartType.fromArrayValueSmartType(
                SmartType.fromClass(String.class)));
        fieldsMap.put("integerList", SmartType.fromCollectionClass(
                List.class, SmartType.fromClass(Integer.class)));
        fieldsMap.put("stringBooleanMap", SmartType.fromMapClass(
                Map.class, String.class, SmartType.fromClass(Boolean.class)));
        Map<String, SmartType> nestedPojoFieldsMap = new HashMap<>();
        nestedPojoFieldsMap.put("platform", SmartType.fromEnumClass(Platform.class));
        nestedPojoFieldsMap.put("date", SmartType.fromClass(LocalDate.class));
        fieldsMap.put("nestedPojoObject", SmartType.fromPojoClass(NestedPojoClass.class, nestedPojoFieldsMap));
        SmartType expected = SmartType.fromPojoClass(PojoClass.class, fieldsMap);
        SmartType result = SmartType.fromObject(pojo);

        Assert.assertEquals(result, expected);
    }
}
