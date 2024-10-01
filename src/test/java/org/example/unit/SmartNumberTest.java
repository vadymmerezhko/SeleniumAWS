package org.example.unit;

import com.ibm.icu.util.ULocale;
import org.example.data.SmartNumber;
import org.example.exceptions.SmartRuntimeException;
import org.example.exceptions.SmartValidationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

public class SmartNumberTest {

    @Test
    public void testParseWithInteger() {
        String numberString = "1234";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1234);
        Assert.assertEquals(smartNumber.getFormat(), "#");
    }

    @Test
    public void testParseWithIntegerAndThousandComa() {
        String numberString = "1,234";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1234);
        Assert.assertEquals(smartNumber.getFormat(), "#,###");
    }

    @Test
    public void testParseWithIntegerAndThousandComas() {
        String numberString = "1,234,567";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1234567);
        Assert.assertEquals(smartNumber.getFormat(), "#,###,###");
    }

    @Test
    public void testParseWithIntegerAndDecimalPoint() {
        String numberString = "123.456";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 123.456);
        Assert.assertEquals(smartNumber.getFormat(), "#.###");
    }

    @Test
    public void testParseWithIntegerAndThousandComaAndDecimalPoint() {
        String numberString = "1,234.567";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1234.567);
        Assert.assertEquals(smartNumber.getFormat(), "#,###.###");
    }

    @Test
    public void testParseWithNegativeIntegerAndThousandComaAndDecimalPoint() {
        String numberString = "-1,234.567";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), -1234.567);
        Assert.assertEquals(smartNumber.getFormat(), "##,###.###");
    }

    @Test
    public void testParseWithArabicNumberString() {
        String numberString = "١٬٢٣٤٬٥٦٧٫٨٩";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1234567.89);
        Assert.assertEquals(smartNumber.getFormat(), "#٬###٬###٫##");
    }

    @Test
    public void testParseWithWithExponent() {
        String numberString = "1e6";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1000000.0);
        Assert.assertEquals(smartNumber.getFormat(), "0.###E0");
    }

    @Test
    public void testParseWithDecimalPointAndExponent() {
        String numberString = "1.123e6";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1123000.0);
        Assert.assertEquals(smartNumber.getFormat(), "0.###E0");
    }

    @Test
    public void testParseWithThousandComaDecimalPointAndExponent() {
        String numberString = "1,234.567e6";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), 1.234567E9);
        Assert.assertEquals(smartNumber.getFormat(), "0.###E0");
    }

    @Test
    public void testParseWithMaxInteger() {
        String numberString = String.valueOf(Integer.MAX_VALUE);  // Maximum value for Integer: 2147483647
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), Integer.valueOf(numberString));
        Assert.assertEquals(smartNumber.getFormat(), "#");
    }

    @Test
    public void testParseWithLong() {
        String numberString = "9223372036854775807";  // Maximum value for Long
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), Long.valueOf(numberString));
        Assert.assertEquals(smartNumber.getFormat(), "#");
    }

    @Test
    public void testParseWithBigInteger() {
        // Maximum BigInteger value
        String numberString = "12345678901234567890123456789012345678901234567890123456789001234567890";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), new BigInteger(numberString));
        Assert.assertEquals(smartNumber.getFormat(), "#");
    }

    @Test
    public void testParseWithDouble() {
        String numberString = "1.7976931348623157E308";  // Max Double value
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), Double.valueOf(numberString));
        Assert.assertEquals(smartNumber.getFormat(), "0.###E0");
    }

    @Test
    public void testParseWithBigDecimal() {
        String numberString = "9.999e+100000000";  // Max BigDecimal value
        SmartNumber smartNumber = SmartNumber.fromString(numberString);

        Assert.assertNotNull(smartNumber);
        Assert.assertEquals(smartNumber.getNumber(), new BigDecimal(numberString));
        Assert.assertEquals(smartNumber.getFormat(), "0.###E0");
    }

    @Test
    public void testToStringWithIntegerNumber() {
        SmartNumber smartNumber = SmartNumber.fromString("1,234");
        String result = smartNumber.toFormattedString();

        Assert.assertNotNull(result);
        Assert.assertEquals(smartNumber.getNumber(), 1234);
        Assert.assertEquals(smartNumber.getFormat(), "#,###");
        Assert.assertEquals(result, "1,234");
    }

    @Test
    public void testToStringWithFloatNumber() {
        SmartNumber smartNumber = SmartNumber.fromString("1,234.567");
        String result = smartNumber.toFormattedString();

        Assert.assertNotNull(result);
        Assert.assertEquals(smartNumber.getNumber(), 1234.567);
        Assert.assertEquals(smartNumber.getFormat(), "#,###.###");
        Assert.assertEquals(result, "1,234.567");
    }

    @Test
    public void testToStringWithNegativeFloatNumber() {
        SmartNumber smartNumber = SmartNumber.fromString("-1,234,567.89");
        String result = smartNumber.toFormattedString();

        Assert.assertNotNull(result);
        Assert.assertEquals(smartNumber.getNumber(), -1234567.89);
        Assert.assertEquals(smartNumber.getFormat(), "##,###,###.##");
        Assert.assertEquals(result, "-1,234,567.89");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToStringWithInvalidNumberString() {
        SmartNumber.fromString("Invalid");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testToStringWithBlankSting() {
        SmartNumber.fromString("  ");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testToStringWithNullSting() {
       SmartNumber.fromString(null);
    }

    @Test
    public void testConvertToNumberStringFrenchEncodingWithDelimitersLocale() {
        // French number with thousand separators and decimal delimiters
        String numberString = "1 234 567,89";
        SmartNumber smartNumber = SmartNumber.fromStringByLocale(
                numberString, ULocale.forLocale(Locale.FRANCE));
        String result = smartNumber.toFormattedString();

        Assert.assertNotNull(result);
        Assert.assertEquals(smartNumber.getNumber(), 1234567.89);
        Assert.assertEquals(smartNumber.getFormat(), "# ### ###,##");
        Assert.assertEquals(result, numberString);
    }

    @Test
    public void testSmartNumberEqualsWithSameObject() {
        SmartNumber smartNumber = SmartNumber.fromStringByLocale("12345.67", ULocale.US);

        Assert.assertEquals(smartNumber, smartNumber,
                "The same object should be equal to itself.");
    }

    @Test
    public void testSmartNumberEqualsWithEqualNumbers() {
        SmartNumber smartNumber1 = SmartNumber.fromStringByLocale("12345.67", ULocale.US);
        SmartNumber smartNumber2 = SmartNumber.fromStringByLocale("12345.67", ULocale.US);

        Assert.assertEquals(smartNumber2, smartNumber1,
                "Two SmartNumber objects with the same value should be equal.");
    }

    @Test
    public void testSmartNumberEqualsWithDifferentNumbers() {
        SmartNumber smartNumber1 = SmartNumber.fromStringByLocale("12345.67", ULocale.US);
        SmartNumber smartNumber2 = SmartNumber.fromStringByLocale("54321.89", ULocale.US);

        Assert.assertNotEquals(smartNumber2, smartNumber1, "Two SmartNumber objects with different values should not be equal.");
    }

    @Test
    public void testSmartNumberEqualsWithNullObject() {
        SmartNumber smartNumber = SmartNumber.fromStringByLocale("12345.67", ULocale.US);

        Assert.assertNotNull(smartNumber, "SmartNumber should not be equal to null.");
    }

    @Test
    public void testConvertToNumberStringGermanyEncodingWithDelimitersLocale() {
        // Arabic number with thousand separators and decimal delimiters
        String numberString = "1.234.567,89";
        SmartNumber smartNumber = SmartNumber.fromStringByLocale(numberString, ULocale.GERMANY);
        String formattedString = smartNumber.toFormattedString();
        String resultString = smartNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(smartNumber.getNumber(), 1234567.89);
        Assert.assertEquals(smartNumber.getFormat(), "#.###.###,##");
        Assert.assertEquals(formattedString, numberString);
        Assert.assertEquals(resultString, "1234567.89");
    }

    @Test
    public void testSmartNumberEqualsWithInvalidComparisonObject() {
        SmartNumber smartNumber = SmartNumber.fromStringByLocale("12345.67", ULocale.US);
        String invalidObject = "Invalid Object";

        Assert.assertNotEquals(invalidObject, smartNumber);
    }

    @Test
    public void testSmartNumberEqualsWithNullSmartNumberValue() {
        SmartNumber smartNumber = null;  // Null SmartNumber
        SmartNumber comparisonSmartNumber = SmartNumber.fromStringByLocale(
                "12345.67", ULocale.US);

        // Expecting SmartRuntimeException because smartNumber is null
        Assert.assertNotEquals(smartNumber, comparisonSmartNumber);
    }

    @Test
    public void testSetValueWithValidInteger() {
        SmartNumber smartNumber = SmartNumber.fromString("9,999,999");
        Integer value = 123;
        smartNumber.setValue(value);

        Assert.assertEquals(smartNumber.getNumber(), value,
                "SmartNumber should correctly set an Integer value.");
        Assert.assertEquals(smartNumber.toFormattedString(), "123");
        Assert.assertEquals(smartNumber.toString(), "123");
    }

    @Test
    public void testSetValueWithValidDouble() {
        String numberString = "1,234,567.89";
        SmartNumber smartNumber = SmartNumber.fromString(numberString);
        Double value = 9999999.99;
        smartNumber.setValue(value);

        Assert.assertEquals(smartNumber.getNumber(), value,
                "SmartNumber should correctly set a Double value.");
        Assert.assertEquals(smartNumber.toFormattedString(), "9,999,999.99");
        Assert.assertEquals(smartNumber.toString(), "9999999.99");
    }

    @Test
    public void testSetValueWithValidBigDecimal() {
        SmartNumber smartNumber = SmartNumber.fromString("1,234,567.89");
        BigDecimal value = new BigDecimal("123456789.123456789");
        smartNumber.setValue(value);

        Assert.assertEquals(smartNumber.getNumber(), value,
                "SmartNumber should correctly set a BigDecimal value.");
        Assert.assertEquals(smartNumber.toFormattedString(), "123,456,789.12");
        Assert.assertEquals(smartNumber.toString(), "123456789.123456789");
    }

    @Test
    public void testSetValueWithValidBigInteger() {
        SmartNumber smartNumber = SmartNumber.fromString("9,999,999.00");
        BigInteger value = new BigInteger("123456789");
        smartNumber.setValue(value);

        Assert.assertEquals(smartNumber.getNumber(), value,
                "SmartNumber should correctly set a BigInteger value.");
        Assert.assertEquals(smartNumber.toFormattedString(), "123,456,789");
        Assert.assertEquals(smartNumber.toString(), "123456789");
    }

    @Test
    public void testSetFormatWithValidPattern() {
        SmartNumber smartNumber = SmartNumber.fromString("9,999,999");
        String format = "#,###,###.00";
        smartNumber.setFormat(format);

        Assert.assertEquals(smartNumber.getFormat(), format,
                "SmartNumber should correctly set the format.");
        Assert.assertEquals(smartNumber.toFormattedString(), "9,999,999.00");
        Assert.assertEquals(smartNumber.toString(), "9999999");
    }

    @Test
    public void testSetFormatWithCurrencyPattern() {
        SmartNumber smartNumber = SmartNumber.fromString("9,999,999.99");
        String format = "¤#,##0.00";
        smartNumber.setFormat(format);

        Assert.assertEquals(smartNumber.getFormat(), format,
                "SmartNumber should correctly set the currency format.");
        Assert.assertEquals(smartNumber.toFormattedString(), "$9,999,999.99");
        Assert.assertEquals(smartNumber.toString(), "9999999.99");
    }

    @Test
    public void testSetFormatWithScientificNotation() {
        SmartNumber smartNumber = SmartNumber.fromString("1,234,567.89");
        String format = "0.###E0";
        smartNumber.setFormat(format);

        Assert.assertEquals(smartNumber.getFormat(), format,
                "SmartNumber should correctly set the scientific notation format.");
        Assert.assertEquals(smartNumber.toFormattedString(), "1.235E6");
        Assert.assertEquals(smartNumber.toString(), "1234567.89");
    }
}
