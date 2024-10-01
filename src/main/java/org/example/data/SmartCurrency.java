package org.example.data;

import com.ibm.icu.util.ULocale;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.ConvertUtils;
import org.example.utils.DataValidationUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import static org.example.constants.Settings.*;

/**
 * Smart number class.
 */
@Slf4j
public final class SmartCurrency extends SmartObject implements FormattedValue {
    private String currencyString;
    @Getter
    private BigDecimal value;
    @Getter
    private long units;
    @Getter
    private long subunits;
    @Getter
    private String code;
    @Getter
    private String symbol;
    @Getter
    private String regionalSymbol;
    @Getter
    private String format;

    /**
     * Parses currency string to smart number
     * like "$1,234.56", "USD 1,234.56", "1,234.56€" or "1,234.56 EUR"
     * to smart currency object, that keeps:
     * currency string
     * currency code like "USD"
     * currency symbol like "$";
     * currency regional symbol like "US$";
     * currency format string like "CCC #,###.##", or "RRR #,###.##" or "¤#,###.##";
     * currency value like 1234.56;
     * currency units like 1234;
     * currency subunits like 56.
     * @param currencyString The currency string.
     * @return The smart currency.
     */
    public static SmartCurrency fromString(String currencyString) {
        DataValidationUtils.validateNotBlank(currencyString, "currencyString");

        try {
            SmartCurrency smartCurrency = new SmartCurrency(currencyString, null);
            log.debug("""
                Number string converted to smart currency.
                String: {}
                Code: {}
                Symbol: {}
                Regional symbol: {}
                Value: {}
                Units: {}
                Subunits: {}
                Format: {}
                """.stripIndent(),
                currencyString,
                smartCurrency.getCode(),
                smartCurrency.getSymbol(),
                smartCurrency.getRegionalSymbol(),
                smartCurrency.getValue(),
                smartCurrency.getUnits(),
                smartCurrency.getSubunits(),
                smartCurrency.getFormat());
            return smartCurrency;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot parse currency string to smart currency object.
                    Currency string: %s
                    """.stripIndent(),
                    currencyString), e);
        }
    }

    /**
     * Creates smart currency object from currency value
     * like 1234.56 and locale like ULocale locale = new ULocale("en", "US");.
     * to smart currency object, that keeps:
     * currency string
     * currency code like "USD"
     * currency symbol like "$";
     * currency regional symbol like "US$";
     * currency format string like "CCC #,###.##", or "RRR #,###.##" or "¤#,###.##";
     * currency value like 1234.56;
     * currency units like 1234;
     * currency subunits like 56.
     * @param currencyValue The currency value.
     * @param locale The locale.
     * @return The smart currency.
     */
    public static SmartCurrency fromValue(Number currencyValue, ULocale locale) {
        DataValidationUtils.validateNotNull(currencyValue, "currencyValue");
        DataValidationUtils.validateNotNull(locale, "locale");

        try {
            String currencyString = ConvertUtils.objectToString(currencyValue);
            SmartCurrency smartCurrency = new SmartCurrency(currencyString, locale);
            smartCurrency.setCurrencyCodesFromLocale(locale);
            log.debug("""
                Smart currency object is created from currency number and locale.
                Number: {}
                Code: {}
                Symbol: {}
                Regional symbol: {}
                Value: {}
                Units: {}
                Subunits: {}
                Format: {}
                String: {}
                """.stripIndent(),
                    currencyValue,
                    smartCurrency.getCode(),
                    smartCurrency.getSymbol(),
                    smartCurrency.getRegionalSymbol(),
                    smartCurrency.getValue(),
                    smartCurrency.getUnits(),
                    smartCurrency.getSubunits(),
                    smartCurrency.getFormat(),
                    currencyString);
            log.debug("""
                    Smart currency object is created from currency value and locale.
                    Value: {}
                    Locale: {}
                    """.stripIndent(),
                    currencyValue, locale);
            return smartCurrency;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot create smart currency object from currency value and locale.
                    Currency string: %s
                    """.stripIndent(),
                    currencyValue), e);
        }
    }

    /**
     * Creates smart currency object from currency value, country code and language code.
     * like 1234.56 and Locale.US.
     * to smart currency object, that keeps:
     * currency string
     * currency code like "USD"
     * currency symbol like "$";
     * currency regional symbol like "US$";
     * currency format string like "CCC #,###.##", or "RRR #,###.##" or "¤#,###.##";
     * currency value like 1234.56;
     * currency units like 1234;
     * currency subunits like 56.
     * @param currencyValue The currency number (e.g. 1234.56).
     * @param countryCode The country code.
     * @param languageCode The language code.
     * @return The smart currency.
     */
    public static SmartCurrency fromValue(
            Number currencyValue,
            String countryCode,
            String languageCode) {
        DataValidationUtils.validateNotNull(currencyValue, "currencyValue");
        DataValidationUtils.validateNotBlank(countryCode, "countryCode");
        DataValidationUtils.validateNotBlank(languageCode, "languageCode");

        try {
            ULocale locale = new ULocale(languageCode, countryCode);
            SmartCurrency smartCurrency = fromValue(currencyValue, locale);
            log.debug("""
                    Smart currency object is created from currency value, country code and language code.
                    Value: {}
                    Country code: {}
                    Language code: {}
                    """.stripIndent(),
                    currencyValue,
                    countryCode,
                    languageCode);
            return smartCurrency;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot create smart currency object from currency value, country and language codes..
                    Currency string: %s
                    """.stripIndent(),
                    currencyValue), e);
        }
    }

    SmartCurrency(String currencyString, ULocale locale) {

        try {
            this.currencyString = currencyString.trim();
            String codeOrSymbol = ConvertUtils.currencyStringToCodeOrSymbol(currencyString);

            if (locale == null) {
                // Validate code  or symbol is present.
                DataValidationUtils.validateNotBlank(codeOrSymbol, "codeOrSymbol");
            }

            String numberString = currencyString.replace(codeOrSymbol, "").trim();
            format = ConvertUtils.currencyStringToFormat(currencyString);

            if (format.contains(CURRENCY_SYMBOL_FORMAT)) {
                symbol = codeOrSymbol;
                // Set regional symbol and code value to symbol value
                // because it's not possible to get these fields value
                // just from not unique symbol like USD $, CAD $ and AUS $
                regionalSymbol = symbol;
                code = symbol;
            }
            else if (format.contains(CURRENCY_CODE_FORMAT)) {
                code = codeOrSymbol;
                symbol = ConvertUtils.currencyCodeToSymbol(code);
                regionalSymbol = ConvertUtils.currencyCodeToRegionalSymbol(code);
            }
            else if (format.contains(CURRENCY_REGIONAL_SYMBOL_FORMAT)) {
                regionalSymbol = codeOrSymbol;
                code = ConvertUtils.currencyRegionalSymbolToCode(regionalSymbol);
                symbol = ConvertUtils.currencyCodeToSymbol(code);
            }
            else {
                code = "";
                symbol = "";
                regionalSymbol = "";
            }
            // Using BigDecimal for precise currency parsing
            setValue(ConvertUtils.currencyNumberStringToBigDecimal(numberString));
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("""
                            Cannot create smart currency object from currency string and locale.
                            String: %s
                            Locale: %s
                            """.stripIndent(),
                            currencyString, locale), e);
        }
    }

    /**
     * Sets new currency value.
     * All currency codes remain the same.
     * @param value The value object.
     */
    public void setValue(Number value) {
        this.value = (BigDecimal) value;
        units = this.value.longValue();
        subunits = this.value.subtract(
                BigDecimal.valueOf(units)).multiply(BigDecimal.valueOf(100)).longValue();
        currencyString = numberToCurrencyString();
        log.debug("""
                Smart currency value is updated.
                Value: {}
                Currency string: {}
                """.stripIndent(),
                value, currencyString);
    }

    /**
     * Converts smart currency to string with currency symbol.
     * @return The string with currency symbol.
     */
    public String toSymbolString() {
        String symbolString;

        if (format.contains(CURRENCY_CODE_FORMAT)) {
            symbolString = currencyString.replace(code, symbol);
        }
        else if (format.contains(CURRENCY_REGIONAL_SYMBOL_FORMAT)) {
            symbolString = currencyString.replace(regionalSymbol, symbol);
        }
        else {
            symbolString = currencyString;
        }
        log.debug("Smart currency converted to currency string with currency symbol: {}",
                symbolString);
        return symbolString;
    }

    /**
     * Converts smart currency to string with currency regional symbol.
     * Regional symbol may be empty string.
     * @return The string with currency symbol.
     */
    public String toRegionalSymbolString() {
        String symbolString;

        if (format.contains(CURRENCY_CODE_FORMAT)) {
            symbolString = currencyString.replace(code, regionalSymbol);
        }
        else if (format.contains(CURRENCY_SYMBOL_FORMAT)) {
            symbolString = currencyString.replace(symbol, regionalSymbol);
        }
        else {
            symbolString = currencyString;
        }
        log.debug("Smart currency converted to currency string with currency regional symbol: {}",
                symbolString);
        return symbolString;
    }

    /**
     * Converts smart currency to string with currency code.
     * Currency code may be empty string.
     * @return The string with currency symbol.
     */
    public String toCodeString() {
        String symbolString;

        if (format.contains(CURRENCY_REGIONAL_SYMBOL_FORMAT)) {
            symbolString = currencyString.replace(regionalSymbol, code);
        }
        else if (format.contains(CURRENCY_SYMBOL_FORMAT)) {
            symbolString = currencyString.replace(symbol, code);
        }
        else {
            symbolString = currencyString;
        }
        log.debug("Smart currency converted to currency string with currency code: {}",
                symbolString);
        return symbolString;
    }

    @Override
    public String toString() {
        log.debug("""
                Smart currency value is converted to currency string.
                Symbol: {}
                Regional Symbol: {}
                Code: {}
                Value: {}
                Format: {}
                String: {}
                """.stripIndent(),
                symbol, regionalSymbol, code, value, format,
                currencyString);
        return currencyString;
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart currency equals() called. The actual smart currency object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart currency equals() called. The actual is the same object as expected.");
            return true;
        }
        try {
            boolean result = false;

            if (object instanceof SmartCurrency currency) {
                boolean sameCurrencyCodes = sameCurrencyCodes(currency);
                result = value.compareTo(currency.getValue()) == 0 && sameCurrencyCodes;
            }
            log.debug("""
                    Smart currency equals() called.
                    Expected: {}
                    Actual: {}
                    Result: {}
                    """.stripIndent(),
                    value, object, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Smart currency method equals() failed.
                    Expected: %s
                    Actual: %s
                    """.stripIndent(),
                    this, object), e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, code, symbol, regionalSymbol, format);
    }

    private boolean sameCurrencyCodes(SmartCurrency currency) {
        return code.equals(currency.getCode()) &&
               regionalSymbol.equals(currency.getRegionalSymbol()) &&
               symbol.equals(currency.getSymbol());

    }

    private void setCurrencyCodesFromLocale(ULocale locale) {
        Currency currency = Currency.getInstance(locale.toLocale());
        code = currency.getCurrencyCode();
        regionalSymbol = ConvertUtils.currencyCodeToRegionalSymbol(code);
        symbol = ConvertUtils.currencyCodeToSymbol(code);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale.toLocale());
        // Format the amount using the currency format of the Locale
        currencyString = currencyFormatter.format(value);
        format = ConvertUtils.currencyStringToFormat(currencyString);
    }

    private String numberToCurrencyString() {
        // Create a DecimalFormatSymbols instance to customize currency symbols
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        String currencyFormat = format;

        if (format.contains(CURRENCY_CODE_FORMAT)) {
            currencyFormat = currencyFormat.replace(CURRENCY_CODE_FORMAT, code);
        }
        else if (format.contains(CURRENCY_REGIONAL_SYMBOL_FORMAT)) {
            currencyFormat = currencyFormat.replace(CURRENCY_REGIONAL_SYMBOL_FORMAT, regionalSymbol);
        }
        else if (format.contains(CURRENCY_SYMBOL_FORMAT)) {
                symbols.setCurrencySymbol(symbol);
        }
        // Create a DecimalFormat instance with the given format
        DecimalFormat decimalFormat = new DecimalFormat(currencyFormat, symbols);
        // Format the BigDecimal value into a currency string
        return decimalFormat.format(value);
    }
}
