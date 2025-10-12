package com.smarte2e.unit;

import com.smarte2e.data.SmartTelephoneNumber;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.exceptions.SmartValidationException;
import com.smarte2e.utils.ConvertUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigInteger;

public class SmartTelephoneNumberTest {

    @Test
    public void testFromStringWithValidPhoneNumber() {
        String validNumberString = "+1234567890";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(validNumberString);

        Assert.assertNotNull(smartTelephoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getNumber().longValue(), 1234567890L);
        Assert.assertEquals(smartTelephoneNumber.getFormat(), "+##########");
    }

    @Test
    public void testToStringWithValidPhoneNumber() {
        String numberString = "+(123)456-7890";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithAllDashDelimiters() {
        String numberString = "+123-456-7890";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithCountryCode() {
        String numberString = "+123-456-789-0123";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithCountryCodeWithSpaces() {
        String numberString = "+1 456 789 0123";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidShortestPhoneNumberWithoutDelimiters() {
        String numberString = "100000";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidLongestPhoneNumberWithoutDelimiters() {
        String numberString = "99999999999999999999";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testToStringWithValidPhoneNumberWithMaximalPhoneExtension() {
        String numberString = "+987(456)-789-0123 #12345";
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString(numberString);
        String formattedString = smartTelephoneNumber.toString();

        Assert.assertNotNull(formattedString);
        Assert.assertEquals(formattedString, ConvertUtils.phoneNumberFormattedString(
                smartTelephoneNumber.getNumber(), smartTelephoneNumber.getFormat()));
    }

    @Test
    public void testFromNumberWithValidPhoneNumber() {
        Number phoneNumber = 1234567890L; // Valid phone number (10 digits)
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromNumber(phoneNumber);

        Assert.assertNotNull(smartTelephoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getNumber(), phoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getFormat(), "##########");
    }

    @Test
    public void testFromNumberWithValidMinPhoneNumber() {
        Number phoneNumber = 100000L; // Minimal valid phone number (6 digits)
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromNumber(phoneNumber);

        Assert.assertNotNull(smartTelephoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getNumber(), phoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getFormat(), "######");
    }

    @Test
    public void testFromNumberWithValidMaxPhoneNumber() {
        // Max valid phone number (15 digits) and 5 digits of extension
        Number phoneNumber = new BigInteger("99999999999999999999");
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromNumber(phoneNumber);

        Assert.assertNotNull(smartTelephoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getNumber(), phoneNumber);
        Assert.assertEquals(smartTelephoneNumber.getFormat(), "####################");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFromNumberWithInvalidShortPhoneNumber() {
        Number phoneNumber = 99999L; // Too short (5 digits)
        SmartTelephoneNumber.fromNumber(phoneNumber);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testFromNumberWithInvalidLongPhoneNumber() {
        // Too long number - 21 digits
        Number phoneNumber = new BigInteger("999999999999999999999");
        SmartTelephoneNumber.fromNumber(phoneNumber);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromNumberWithNullPhoneNumber() {
        SmartTelephoneNumber.fromNumber(null);
    }

    @Test
    public void testToStringWithValidPhoneNumberAndFormat() {
        SmartTelephoneNumber smartTelephoneNumber =
                SmartTelephoneNumber.fromString("123-456-7890");

        String expected = "123-456-7890"; // Expected formatted string
        Assert.assertEquals(smartTelephoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithValidPhoneNumberWithoutCountryCode() {
        // Assuming SmartPhoneNumber has a constructor where format can be null
        SmartTelephoneNumber smartTelephoneNumber =
                SmartTelephoneNumber.fromString("+1(123)456-7890");

        String expected = "+1(123)456-7890"; // Expected string without formatting
        Assert.assertEquals(smartTelephoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithValidPhoneNumberWithoutExtensionNumber() {
        // Assuming SmartPhoneNumber has a constructor where format can be null
        SmartTelephoneNumber smartTelephoneNumber =
                SmartTelephoneNumber.fromString("+987(123)456-7890 #12345");

        // TODO: # is removed before number extension
        String expected = "+987(123)456-7890 12345"; // Expected string without formatting
        Assert.assertEquals(smartTelephoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithShortPhoneNumberAndFormat() {
        SmartTelephoneNumber smartTelephoneNumber =
                SmartTelephoneNumber.fromString("123-456");

        String expected = "123-456"; // Expected formatted string
        Assert.assertEquals(smartTelephoneNumber.toString(), expected);
    }

    @Test
    public void testToStringWithShortPhoneNumberWithoutDelimiters() {
        SmartTelephoneNumber smartTelephoneNumber =
                SmartTelephoneNumber.fromString("123456");

        String expected = "123456"; // Expected formatted string
        Assert.assertEquals(smartTelephoneNumber.toString(), expected);
    }

    @Test
    public void testEqualsWithSameObject() {
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString("123-456-7890");

        Assert.assertEquals(smartTelephoneNumber, smartTelephoneNumber);
    }

    @Test
    public void testEqualsWithEqualNumber() {
        SmartTelephoneNumber smartTelephoneNumber1 = SmartTelephoneNumber.fromString("+1(123)-456-7890");
        SmartTelephoneNumber smartTelephoneNumber2 = SmartTelephoneNumber.fromString("1 123-456-7890");

        Assert.assertEquals(smartTelephoneNumber1, smartTelephoneNumber2);
    }

    @Test
    public void testEqualsWithDifferentNumber() {
        SmartTelephoneNumber smartTelephoneNumber1 = SmartTelephoneNumber.fromString("+111(245)-456-7890");
        SmartTelephoneNumber smartTelephoneNumber2 = SmartTelephoneNumber.fromString("1 123-456-7890");

        Assert.assertNotEquals(smartTelephoneNumber1, smartTelephoneNumber2);
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString("+1(245)-456-7890");

        Assert.assertNotEquals(smartTelephoneNumber, new Object());
    }

    @Test
    public void testEqualsWithNullObject() {
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString("+1(245)-456-7890");

        Assert.assertNotEquals(smartTelephoneNumber, null);
    }

    @Test
    public void testEqualsWithTheSameNumber() {
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString("987-654-321");

        Long sameNumber = 987654321L;
        Assert.assertNotEquals(smartTelephoneNumber,sameNumber);
    }

    @Test
    public void testEqualsWithNonNumberObject() {
        SmartTelephoneNumber smartTelephoneNumber = SmartTelephoneNumber.fromString("+1(245)-456-7890");

        String nonNumberObject = "+1(245)-456-7890"; // Non-number object
        Assert.assertNotEquals(smartTelephoneNumber, nonNumberObject);
    }
}
