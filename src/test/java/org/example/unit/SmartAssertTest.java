package org.example.unit;

import org.example.asserts.SmartAssert;
import org.example.data.SmartDataObject;
import org.example.data.SmartType;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.example.utils.ConverterUtils;
import org.testng.annotations.Test;

import java.util.Date;

public class SmartAssertTest {

    static class DummyDataObject extends SmartDataObject {
        private final SmartType smartType;

        public DummyDataObject(SmartType smartType) {
            this.smartType = smartType;
            initialize();
        }

        @Override
        public String toString() {
            return smartType.toString();
        }
    }

    static class OtherFieldNameDataObject extends SmartDataObject {
        private final SmartType someType;

        public OtherFieldNameDataObject(SmartType smartType) {
            this.someType = smartType;
            initialize();
        }

        @Override
        public String toString() {
            return someType.toString();
        }
    }

    static class EmptyDataObject extends SmartDataObject {
    }

    @Test
    public void testSmartAssertStringPositive() {
        String value = "some value";
        DummyDataObject expected = new DummyDataObject(SmartType.withString(value));
        DummyDataObject actual = new DummyDataObject(SmartType.withString(value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertIntegerPositive() {
        int value = 1;
        DummyDataObject expected = new DummyDataObject(SmartType.withInteger(value));
        DummyDataObject actual = new DummyDataObject(SmartType.withInteger(value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertLongPositive() {
        long value = 123456789L;
        DummyDataObject expected = new DummyDataObject(SmartType.withLong(value));
        DummyDataObject actual = new DummyDataObject(SmartType.withLong(value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertFloatPositive() {
        float value = 123.345f;
        DummyDataObject expected = new DummyDataObject(SmartType.withFloat(value));
        DummyDataObject actual = new DummyDataObject(SmartType.withFloat(value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertDoublePositive() {
        double value = 123456789.123456789d;
        DummyDataObject expected = new DummyDataObject(SmartType.withDouble(value));
        DummyDataObject actual = new DummyDataObject(SmartType.withDouble(value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertBooleanPositive() {
        boolean value = false;
        DummyDataObject expected = new DummyDataObject(SmartType.withBoolean(value));
        DummyDataObject actual = new DummyDataObject(SmartType.withBoolean(value));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertDatePositive() {
        Date date = ConverterUtils.stringToDate("1970-05-23");
        String dateFormat = "yyy-MM-dd";
        DummyDataObject expected = new DummyDataObject(SmartType.withDate(date, dateFormat));
        DummyDataObject actual = new DummyDataObject(SmartType.withDate(date, dateFormat));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertStringFailsPositive() {
        DummyDataObject expected = new DummyDataObject(SmartType.withString("aaa"));
        DummyDataObject actual = new DummyDataObject(SmartType.withString("bbb"));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertIntegerFailsPositive() {
        DummyDataObject expected = new DummyDataObject(SmartType.withInteger(1));
        DummyDataObject actual = new DummyDataObject(SmartType.withInteger(1));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test
    public void testSmartAssertLongFailsPositive() {
        DummyDataObject expected = new DummyDataObject(SmartType.withLong(1L));
        DummyDataObject actual = new DummyDataObject(SmartType.withLong(1L));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertFloatFailsPositive() {
        DummyDataObject expected = new DummyDataObject(SmartType.withFloat(123.456f));
        DummyDataObject actual = new DummyDataObject(SmartType.withFloat(123.1f));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertDoubleFailsPositive() {
        DummyDataObject expected = new DummyDataObject(SmartType.withDouble(123456789.123456789d));
        DummyDataObject actual = new DummyDataObject(SmartType.withDouble(987654321.987654321d));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertBooleanFailsPositive() {
        DummyDataObject expected = new DummyDataObject(SmartType.withBoolean(true));
        DummyDataObject actual = new DummyDataObject(SmartType.withBoolean(false));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testSmartAssertDateFailsPositive() {
        Date expectedDate = ConverterUtils.stringToDate("1970-05-23");
        Date actualDate = ConverterUtils.stringToDate("1974-12-13");
        String dateFormat = "yyyy-MM-dd";
        DummyDataObject expected = new DummyDataObject(SmartType.withDate(expectedDate, dateFormat));
        DummyDataObject actual = new DummyDataObject(SmartType.withDate(actualDate, dateFormat));

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSmartAssertExpectedNullNegative() {
        DummyDataObject actual = new DummyDataObject(SmartType.withString("Some value"));

        SmartAssert.assertDataObjects(null, actual);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSmartAssertActualNullNegative() {
        DummyDataObject expected = new DummyDataObject(SmartType.withString("Some value"));

        SmartAssert.assertDataObjects(expected, null);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSmartAssertTheSameObjectNegative() {
        DummyDataObject dataObject = new DummyDataObject(SmartType.withString("Some value"));

        SmartAssert.assertDataObjects(dataObject, dataObject);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSmartAssertMissedFieldNegative() {
        DummyDataObject expected = new DummyDataObject(SmartType.withString("Some value"));
        EmptyDataObject actual = new EmptyDataObject();

        SmartAssert.assertDataObjects(expected, actual);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testSmartAssertOtherFieldNameNegative() {
        String value = "Some value";
        DummyDataObject expected = new DummyDataObject(SmartType.withString(value));
        OtherFieldNameDataObject actual = new OtherFieldNameDataObject(SmartType.withString(value));

        SmartAssert.assertDataObjects(expected, actual);
    }
}
