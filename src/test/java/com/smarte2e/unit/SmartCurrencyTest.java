package com.smarte2e.unit;

import com.ibm.icu.util.ULocale;
import com.smarte2e.data.SmartCurrency;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.exceptions.SmartValidationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class SmartCurrencyTest {

    @Test
    public void testParseCurrencyStringToSmartCurrencyWithSymbol() {
        String currencyString = "$1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("1234.56"));
        Assert.assertEquals(smartCurrency.getSymbol(), "$");
        // Assuming the regional symbol for symbol $ is also $.
        // It's not possible to gate region symbol or currency code just from
        // not unique symbol like US $, Canadian S or Australian $.
        Assert.assertEquals(smartCurrency.getCode(), "$");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "$");
    }

    @Test
    public void testParseCurrencyStringWithCode() {
        String currencyString = "USD 1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("1234.56"));
        Assert.assertEquals(smartCurrency.getCode(), "USD");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "US$");
        Assert.assertEquals(smartCurrency.getSymbol(), "$");
        Assert.assertEquals(smartCurrency.getUnits(), 1234);
        Assert.assertEquals(smartCurrency.getSubunits(), 56);
    }

    @Test
    public void testParseCurrencyStringWithRegionalSymbol() {
        String currencyString = "US$ 1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("1234.56"));
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "US$");
        Assert.assertEquals(smartCurrency.getCode(), "USD");
        Assert.assertEquals(smartCurrency.getSymbol(), "$");
        Assert.assertEquals(smartCurrency.getUnits(), 1234);
        Assert.assertEquals(smartCurrency.getSubunits(), 56);
    }

    @Test
    public void testEqualsWithSameValueAndCodes() {
        SmartCurrency currency1 = SmartCurrency.fromString("USD 1,234.56");
        SmartCurrency currency2 = SmartCurrency.fromString("USD 1,234.56");

        Assert.assertEquals(currency2, currency1);
        Assert.assertEquals(currency1.getValue(), currency2.getValue());
        Assert.assertEquals(currency1.getCode(), currency2.getCode());
        Assert.assertEquals(currency1.getSymbol(), currency2.getSymbol());
        Assert.assertEquals(currency1.getRegionalSymbol(), currency2.getRegionalSymbol());
        Assert.assertEquals(currency1.getUnits(), currency2.getUnits());
        Assert.assertEquals(currency1.getSubunits(), currency2.getSubunits());
        Assert.assertEquals(currency1.getFormat(), currency2.getFormat());
        Assert.assertEquals(currency1.toString(), currency2.toString());
        Assert.assertEquals(currency1.hashCode(), currency2.hashCode());
    }

    @Test
    public void testEqualsWithSameValueAndCodesWithDifferentFormat() {
        SmartCurrency currency1 = SmartCurrency.fromString("US$ 1,234.56");
        SmartCurrency currency2 = SmartCurrency.fromString("1234.56 USD");

        Assert.assertEquals(currency2, currency1);
        Assert.assertEquals(currency1.getValue(), currency2.getValue());
        Assert.assertEquals(currency1.getCode(), currency2.getCode());
        Assert.assertEquals(currency1.getSymbol(), currency2.getSymbol());
        Assert.assertEquals(currency1.getRegionalSymbol(), currency2.getRegionalSymbol());
        Assert.assertEquals(currency1.getUnits(), currency2.getUnits());
        Assert.assertEquals(currency1.getSubunits(), currency2.getSubunits());
        Assert.assertNotEquals(currency1.getFormat(), currency2.getFormat());
        Assert.assertNotEquals(currency1.toString(), currency2.toString());
        Assert.assertNotEquals(currency1.hashCode(), currency2.hashCode());
    }

    @Test
    public void testEqualsWithSameValueAndCodesWithRegionalSymbol() {
        SmartCurrency currency1 = SmartCurrency.fromString("US$ 1,234.56");

        Assert.assertEquals(currency1.getValue(), new BigDecimal("1234.56"));
        Assert.assertEquals(currency1.getCode(), "USD");
        Assert.assertEquals(currency1.getSymbol(), "$");
        Assert.assertEquals(currency1.getRegionalSymbol(), "US$");
        Assert.assertEquals(currency1.getUnits(), 1234);
        Assert.assertEquals(currency1.getSubunits(), 56);
        Assert.assertEquals(currency1.getFormat(), "RRR #,###.##");
        Assert.assertEquals(currency1.toString(), "US$ 1,234.56");
    }

    @Test
    public void testEqualsWithSameValueAndCodesWithCurrencyCodePrefix() {
        SmartCurrency currency1 = SmartCurrency.fromString("1,234.56 UAH");

        Assert.assertEquals(currency1.getValue(), new BigDecimal("1234.56"));
        Assert.assertEquals(currency1.getCode(), "UAH");
        Assert.assertEquals(currency1.getSymbol(), "₴");
        Assert.assertEquals(currency1.getRegionalSymbol(), "UA₴");
        Assert.assertEquals(currency1.getUnits(), 1234);
        Assert.assertEquals(currency1.getSubunits(), 56);
        Assert.assertEquals(currency1.getFormat(), "#,###.## CCC");
        Assert.assertEquals(currency1.toString(), "1,234.56 UAH");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testParseInvalidCurrencyStringEmpty() {
        SmartCurrency.fromString("");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testParseInvalidCurrencyStringNull() {
        SmartCurrency.fromString(null);
    }

    @Test
    public void testEqualsWithDifferentValues() {
        SmartCurrency currency1 = SmartCurrency.fromString("USD 1,234.56");
        SmartCurrency currency2 = SmartCurrency.fromString("USD 1,000.00");

        Assert.assertNotEquals(currency2, currency1);
    }

    @Test
    public void testEqualsWithDifferentCurrencyCodes() {
        SmartCurrency currency1 = SmartCurrency.fromString("USD 1,234.56");
        SmartCurrency currency2 = SmartCurrency.fromString("CAD 1,234.56");

        Assert.assertNotEquals(currency2, currency1);
    }

    @Test
    public void testToSymbolStringWithCurrencyCode() {
        // Setup SmartCurrency with currency code format
        String currencyString = "USD 1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toSymbolString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "$ 1,234.56");  // Assuming symbol for USD is $
    }

    @Test
    public void testToSymbolStringWithRegionalSymbol() {
        // Setup SmartCurrency with regional symbol format
        String currencyString = "US$ 1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toSymbolString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "$ 1,234.56");
    }

    @Test
    public void testToSymbolStringWithNoCodeOrRegionalSymbol() {
        // Setup SmartCurrency with no specific format
        String currencyString = "$1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toSymbolString();

        Assert.assertNotNull(result);
        Assert.assertEquals(result, "$1,234.56");
    }

    @Test
    public void testToRegionalSymbolStringWithCurrencyCode() {
        // Setup SmartCurrency with currency code format
        String currencyString = "USD 1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toRegionalSymbolString();

        Assert.assertNotNull(result);
        // Assuming regional symbol for USD is US$
        Assert.assertEquals(result, "US$ 1,234.56");
    }

    @Test
    public void testToRegionalSymbolStringWithCurrencySymbol() {
        // Setup SmartCurrency with currency symbol format
        String currencyString = "$1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toRegionalSymbolString();

        Assert.assertNotNull(result);
        // Assuming the regional symbol for symbol $ is also $.
        // It's not possible to gate region symbol or currency code just from
        // not unique symbol like US $, Canadian S or Australian $.
        Assert.assertEquals(result, "$1,234.56");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testToRegionalSymbolStringWithNoCodeOrSymbol() {
        // Setup SmartCurrency with no currency or symbol
        String currencyString = "1,234.56";

        // Should throw SmartRuntimeException - no currency code r symbol
        SmartCurrency.fromString(currencyString);
    }

    @Test
    public void testToCodeStringWithRegionalSymbol() {
        // Setup SmartCurrency with regional symbol format
        String currencyString = "US$ 1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toCodeString();

        Assert.assertNotNull(result);
        // Assuming code for US$ is USD
        Assert.assertEquals(result, "USD 1,234.56");
    }

    @Test
    public void testToCodeStringWithCurrencySymbol() {
        // Setup SmartCurrency with currency symbol format
        String currencyString = "$1,234.56";
        SmartCurrency smartCurrency = SmartCurrency.fromString(currencyString);
        String result = smartCurrency.toCodeString();

        Assert.assertNotNull(result);
        // Assuming the regional symbol for symbol $ is also $.
        // It's not possible to gate region symbol or currency code just from
        // not unique symbol like US $, Canadian S or Australian $.
        Assert.assertEquals(result, "$1,234.56");
    }

    @Test
    public void testFromValueAndLocaleWithUSLocale() {
        Number currencyNumber = 1234.56;
        ULocale locale = new ULocale("en", "US");  // English, United States
        SmartCurrency smartCurrency = SmartCurrency.fromValue(currencyNumber, locale);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("1234.56"));
        Assert.assertEquals(smartCurrency.getCode(), "USD");
        Assert.assertEquals(smartCurrency.getSymbol(), "$");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "US$");
        Assert.assertEquals(smartCurrency.getUnits(), 1234);
        Assert.assertEquals(smartCurrency.getSubunits(), 56);
        Assert.assertEquals(smartCurrency.getFormat(), "¤#,###.##");
        Assert.assertEquals(smartCurrency.toString(), "$1,234.56");
    }

    @Test
    public void testFromValueAndLocaleWithCanadaLocale() {
        Number currencyNumber = 7890.12;
        // Create ULocale for Japan (Japanese language, Japan country)
        ULocale locale = new ULocale("ja", "JP");
        SmartCurrency smartCurrency = SmartCurrency.fromValue(currencyNumber, locale);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("7890.12"));
        Assert.assertEquals(smartCurrency.getCode(), "JPY");
        Assert.assertEquals(smartCurrency.getSymbol(), "￥");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "JP￥");
        Assert.assertEquals(smartCurrency.getUnits(), 7890);
        Assert.assertEquals(smartCurrency.getSubunits(), 12);
    }

    @Test
    public void testFromValueAndLocaleWithUKLocale() {
        Number currencyNumber = 567.89;
        // Create a ULocale for the United Kingdom
        ULocale locale = new ULocale("en", "GB");
        SmartCurrency smartCurrency = SmartCurrency.fromValue(currencyNumber, locale);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("567.89"));
        Assert.assertEquals(smartCurrency.getCode(), "GBP");
        Assert.assertEquals(smartCurrency.getSymbol(), "£");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "GB£");
        Assert.assertEquals(smartCurrency.getUnits(), 567);
        Assert.assertEquals(smartCurrency.getSubunits(), 89);
    }

    @Test
    public void testFromValueAndLocaleWithGERMANLocale() {
        Number currencyNumber = 567.89;
        // Create a ULocale for Germany
        ULocale locale = new ULocale("de", "DE");
        SmartCurrency smartCurrency = SmartCurrency.fromValue(currencyNumber, locale);

        Assert.assertNotNull(smartCurrency);
        Assert.assertEquals(smartCurrency.getValue(), new BigDecimal("567.89"));
        Assert.assertEquals(smartCurrency.getCode(), "EUR");
        Assert.assertEquals(smartCurrency.getSymbol(), "EUR");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "EUEUR");
        Assert.assertEquals(smartCurrency.getUnits(), 567);
        Assert.assertEquals(smartCurrency.getSubunits(), 89);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromValueAndLocaleWithNullCurrencyNumber() {
        // Create a ULocale for US English
        ULocale locale = new ULocale("en", "US");
        // Should throw SmartValidationException
        SmartCurrency.fromValue(null, locale);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testFromValueAndLocaleWithNullLocale() {
        // Should throw SmartValidationException
        SmartCurrency.fromValue(123, null);
    }

    @Test
    public void testSetValueWithValidBigDecimal() {
        SmartCurrency smartCurrency = SmartCurrency.fromString("USD 9,999,999,999");
        BigDecimal value = new BigDecimal("1234.56");
        smartCurrency.setValue(value);

        Assert.assertEquals(smartCurrency.getValue(), value);
        Assert.assertEquals(smartCurrency.getCode(), "USD");
        Assert.assertEquals(smartCurrency.getSymbol(), "$");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "US$");
        Assert.assertEquals(smartCurrency.getUnits(), 1234);
        Assert.assertEquals(smartCurrency.getSubunits(), 56);
        Assert.assertEquals(smartCurrency.getFormat(), "CCC #,###,###,###");
        Assert.assertEquals(smartCurrency.toString(), "USD 1,235");
    }

    @Test
    public void testSetValueWithValidBigDecimalZero() {
        SmartCurrency smartCurrency = SmartCurrency.fromString("$9,999,999,99");
        BigDecimal value = BigDecimal.ZERO;

        smartCurrency.setValue(value);

        Assert.assertEquals(smartCurrency.getValue(), value,
                "SmartCurrency should correctly set a BigDecimal value of zero.");
    }

    @Test
    public void testSetValueWithValidBigDecimalNegative() {
        SmartCurrency smartCurrency = SmartCurrency.fromString("US$ 9,999,999,99");
        BigDecimal value = new BigDecimal("-12345.67");
        smartCurrency.setValue(value);

        Assert.assertEquals(smartCurrency.getValue(), value,
                "SmartCurrency should correctly set a negative BigDecimal value.");
    }

    @Test
    public void testFromValueWithValidCurrencyAndLocale() {
        Number currencyValue = 1234.56;
        String countryCode = "US";
        String languageCode = "en";
        SmartCurrency smartCurrency = SmartCurrency.fromValue(currencyValue, countryCode, languageCode);

        Assert.assertNotNull(smartCurrency, "The SmartCurrency object should not be null.");
        Assert.assertEquals(String.valueOf(smartCurrency.getValue()), String.valueOf(currencyValue),
                "The currency value should match the expected value.");
        Assert.assertEquals(smartCurrency.getCode(), "USD",
                "The currency code should be 'USD'.");
        Assert.assertEquals(smartCurrency.getSymbol(), "$",
                "The currency symbol should be '$'.");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "US$",
                "The regional currency symbol should be 'US$'.");
    }

    @Test
    public void testFromValueWithValidCurrencyAndDifferentLocale() {
        Number currencyValue = 98765.43;
        String countryCode = "FR";
        String languageCode = "fr";
        SmartCurrency smartCurrency = SmartCurrency.fromValue(currencyValue, countryCode, languageCode);

        Assert.assertNotNull(smartCurrency, "The SmartCurrency object should not be null.");
        Assert.assertEquals(String.valueOf(smartCurrency.getValue()), String.valueOf(currencyValue),
                "The currency value should match the expected value.");
        Assert.assertEquals(smartCurrency.getCode(), "EUR",
                "The currency code should be 'EUR'.");
        Assert.assertEquals(smartCurrency.getSymbol(), "EUR",
                "The currency symbol should be 'EUR'.");
        Assert.assertEquals(smartCurrency.getRegionalSymbol(), "EUEUR",
                "The regional currency symbol should be 'EUEUR'.");
    }


}
