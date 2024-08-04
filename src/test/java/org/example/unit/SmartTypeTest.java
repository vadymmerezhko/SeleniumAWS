package org.example.unit;

import org.example.data.SmartDataObject;
import org.example.data.SmartType;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Date;

public class SmartTypeTest {
    
    static class DummyDataObject extends SmartDataObject {
        SmartType smartType;

        public void setSmartType(SmartType smartType) {
            this.smartType = smartType;
        }
    }

    // Positive test for the string constructor
    @Test
    public void testStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "Some value";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toString(), value);
    }

    // Negative test for the string constructor with null parent
    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringConstructorNullParent() {
        new SmartType(null, "test value");
    }

    // Negative test for the string constructor with null value
    @Test(expectedExceptions = SmartValidationException.class)
    public void testStringConstructorNullValue() {
        SmartDataObject parent = new DummyDataObject();
        new SmartType(parent, null);
    }

    // Test constructors with different data types
    @Test
    public void testIntegerConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        int value = 1;
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toInteger(), value);
    }

    @Test
    public void testIntegerStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "1";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toInteger(), Integer.parseInt(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testIntegerStringConstructorNegative() {
        DummyDataObject parent = new DummyDataObject();
        String value = "invalid";
        SmartType smartType =  new SmartType(parent, value);
        smartType.toInteger();
    }

    @Test
    public void testLongConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        long value = 123456789L;
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toLong(), value);
    }

    @Test
    public void testLongStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "123456789";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toLong(), Long.parseLong(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testLongStringConstructorNegative() {
        DummyDataObject parent = new DummyDataObject();
        String value = "invalid";
        SmartType smartType =  new SmartType(parent, value);
        smartType.toLong();
    }

    @Test
    public void testFloatConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        float value = 123.245f;
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toFloat(), value);
    }

    @Test
    public void testFloatStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "123.456";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toFloat(), Float.parseFloat(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFloatStringConstructorNegative() {
        DummyDataObject parent = new DummyDataObject();
        String value = "invalid";
        SmartType smartType =  new SmartType(parent, value);
        smartType.toFloat();
    }

    @Test
    public void testDoubleConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        double value = 12356788.123456789d;
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toDouble(), value);
    }

    @Test
    public void testDoubleStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "1123456789.123456789";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toDouble(), Double.parseDouble(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDoubleStringConstructorNegative() {
        DummyDataObject parent = new DummyDataObject();
        String value = "invalid";
        SmartType smartType =  new SmartType(parent, value);
        smartType.toDouble();
    }

    @Test
    public void testBooleanConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        boolean value = true;
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toBoolean(), value);
    }

    @Test
    public void testBooleanStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "false";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toBoolean(), Boolean.parseBoolean(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testBooleanStringConstructorNegative() {
        DummyDataObject parent = new DummyDataObject();
        String value = "invalid";
        SmartType smartType =  new SmartType(parent, value);
        smartType.toBoolean();
    }

    @Test
    public void testDateConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        Date date = ConverterUtils.stringToDate("1970-05-23");
        String dateFormat = "yyy-MM-dd";
        SmartType smartType =  new SmartType(parent, date, dateFormat);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toDate(), date);
    }

    @Test
    public void testDateStringConstructorPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "1970-05-23";
        SmartType smartType =  new SmartType(parent, value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toDate(), ConverterUtils.stringToDate(value));
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testDateStringConstructorNegative() {
        DummyDataObject parent = new DummyDataObject();
        String value = "invalid";
        SmartType smartType =  new SmartType(parent, value);
        smartType.toDate();
    }

    @Test
    public void testSetStringPositive() {
        DummyDataObject parent = new DummyDataObject();
        String value = "Some value";
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toString(), value);
    }

    @Test
    public void testSetIntegerPositive() {
        DummyDataObject parent = new DummyDataObject();
        int value = 1;
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toInteger(), value);
    }

    @Test
    public void testSetLongPositive() {
        DummyDataObject parent = new DummyDataObject();
        long value = 123456789L;
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toLong(), value);
    }

    @Test
    public void testSetFloatPositive() {
        DummyDataObject parent = new DummyDataObject();
        float value = 123.456f;
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toFloat(), value);
    }

    @Test
    public void testSetDoublePositive() {
        DummyDataObject parent = new DummyDataObject();
        double value = 123.456f;
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toDouble(), value);
    }

    @Test
    public void testSetBooleanPositive() {
        DummyDataObject parent = new DummyDataObject();
        boolean value = true;
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(value);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toBoolean(), value);
    }

    @Test
    public void testSetDatePositive() {
        DummyDataObject parent = new DummyDataObject();
        Date date = ConverterUtils.stringToDate("1970-05-23");
        String dateFormat = "yyyy-MM-dd";
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(date, dateFormat);
        parent.setSmartType(smartType);
        Assert.assertEquals(smartType.toDate(), date);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetNullDateNegative() {
        DummyDataObject parent = new DummyDataObject();
        Date date = null;
        String dateFormat = "yyyy-MM-dd";
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(date, dateFormat);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSetNullDateFormatNegative() {
        DummyDataObject parent = new DummyDataObject();
        Date date = ConverterUtils.stringToDate("1970-05-23");
        SmartType smartType =  new SmartType(parent);
        smartType.setValue(date, null);
    }
}

