package org.example.utils;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;
import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.exceptions.SmartRuntimeException;
import org.json.*;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Float.POSITIVE_INFINITY;
import static org.apache.commons.lang3.ObjectUtils.isArray;
import static org.example.constants.Settings.*;
import static org.example.utils.DataValidationUtils.FULL_CLASS_NAME_REGEX;

/**
 * Convert utils class.
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class ConvertUtils {
    private static final String ESCAPED_QUOTE = "\"\"";
    public static final String US_INTEGER_NUMBER_STRING = "^[+-]?\\d{1,3}(,\\d{3})*$";
    public static final String SCIENTIFIC_NUMBER_STRING_REGEX =
            "^[+-]?(\\d{1,3}(,\\d{3})*|\\d+)(\\.\\d+)?[eE][+-]?\\d+$";

    // Map for converting various numeral systems to Arabic numerals
    // Map for converting numerals from various languages to Arabic numerals
    private static final Map<String, Integer> NUMERAL_MAP = new HashMap<>();
    private static final Map<String, Long> UNIT_MAP = new HashMap<>();

    static {
        // Mapping Chinese, Japanese, and Korean numerals
        NUMERAL_MAP.put("零", 0); NUMERAL_MAP.put("一", 1); NUMERAL_MAP.put("二", 2); NUMERAL_MAP.put("三", 3);
        NUMERAL_MAP.put("四", 4); NUMERAL_MAP.put("五", 5); NUMERAL_MAP.put("六", 6); NUMERAL_MAP.put("七", 7);
        NUMERAL_MAP.put("八", 8); NUMERAL_MAP.put("九", 9); NUMERAL_MAP.put("壱", 1); NUMERAL_MAP.put("弐", 2);
        NUMERAL_MAP.put("参", 3); NUMERAL_MAP.put("영", 0); NUMERAL_MAP.put("일", 1); NUMERAL_MAP.put("이", 2);
        NUMERAL_MAP.put("삼", 3); NUMERAL_MAP.put("사", 4); NUMERAL_MAP.put("오", 5); NUMERAL_MAP.put("육", 6);
        NUMERAL_MAP.put("칠", 7); NUMERAL_MAP.put("팔", 8); NUMERAL_MAP.put("구", 9);

        // Mapping Arabic-Indic numerals
        NUMERAL_MAP.put("٠", 0); NUMERAL_MAP.put("١", 1); NUMERAL_MAP.put("٢", 2); NUMERAL_MAP.put("٣", 3);
        NUMERAL_MAP.put("٤", 4); NUMERAL_MAP.put("٥", 5); NUMERAL_MAP.put("٦", 6); NUMERAL_MAP.put("٧", 7);
        NUMERAL_MAP.put("٨", 8); NUMERAL_MAP.put("٩", 9);

        // Mapping Devanagari numerals (used in Hindi, Marathi, Nepali)
        NUMERAL_MAP.put("०", 0); NUMERAL_MAP.put("१", 1); NUMERAL_MAP.put("२", 2); NUMERAL_MAP.put("३", 3);
        NUMERAL_MAP.put("४", 4); NUMERAL_MAP.put("५", 5); NUMERAL_MAP.put("६", 6); NUMERAL_MAP.put("७", 7);
        NUMERAL_MAP.put("८", 8); NUMERAL_MAP.put("९", 9);

        // Mapping Tamil numerals
        NUMERAL_MAP.put("௦", 0); NUMERAL_MAP.put("௧", 1); NUMERAL_MAP.put("௨", 2); NUMERAL_MAP.put("௩", 3);
        NUMERAL_MAP.put("௪", 4); NUMERAL_MAP.put("௫", 5); NUMERAL_MAP.put("௬", 6); NUMERAL_MAP.put("௭", 7);
        NUMERAL_MAP.put("௮", 8); NUMERAL_MAP.put("௯", 9);

        // Mapping Bengali numerals
        NUMERAL_MAP.put("০", 0); NUMERAL_MAP.put("১", 1); NUMERAL_MAP.put("২", 2); NUMERAL_MAP.put("৩", 3);
        NUMERAL_MAP.put("৪", 4); NUMERAL_MAP.put("৫", 5); NUMERAL_MAP.put("৬", 6); NUMERAL_MAP.put("৭", 7);
        NUMERAL_MAP.put("৮", 8); NUMERAL_MAP.put("৯", 9);

        // Mapping Thai numerals
        NUMERAL_MAP.put("๐", 0); NUMERAL_MAP.put("๑", 1); NUMERAL_MAP.put("๒", 2); NUMERAL_MAP.put("๓", 3);
        NUMERAL_MAP.put("๔", 4); NUMERAL_MAP.put("๕", 5); NUMERAL_MAP.put("๖", 6); NUMERAL_MAP.put("๗", 7);
        NUMERAL_MAP.put("๘", 8); NUMERAL_MAP.put("๙", 9);

        // Mapping units for Chinese, Japanese, Korean, and other Asian languages
        UNIT_MAP.put("十", 10L); UNIT_MAP.put("百", 100L); UNIT_MAP.put("千", 1000L);
        UNIT_MAP.put("万", 10000L); UNIT_MAP.put("億", 100000000L); UNIT_MAP.put("亿", 100000000L);
        UNIT_MAP.put("조", 1000000000000L); UNIT_MAP.put("만", 10000L); UNIT_MAP.put("억", 100000000L);
    }

    // The most used language locales
    private static final ULocale[] MOST_COMMON_LOCALES = {
            ULocale.US, // US - English
            new ULocale("zh_CN"), // China - Mandarin
            new  ULocale("hi_IN"), // India - Hindi
            new ULocale("es_ES"), // Spain - Spanish
            new ULocale("ar_EG"), // Egypt - Arabic
            new ULocale("fa_IR"), // Iran - Persian
            new ULocale("bn_BD"), // India - Bengali
            new ULocale("pt_BR"), // Portugal - Portuguese
            new ULocale("ru_RU"), // Russia - Russian
            new ULocale("ur_PK"), // Pakistan - Urdu
            ULocale.GERMANY, // Germany - German
            ULocale.FRANCE, // France - French
            new ULocale("it_IT"), // Italy - Italian
            new ULocale("nl_NL"), // Netherlands - Dutch
            new ULocale("tr_TR"), // Turkey - Turkish
            new ULocale("he_IL"), // Israel - Hebrew
            new ULocale("uk_UA"), // Ukraine - Ukrainian
            new ULocale("et_EE"), // Estonia - Estonian
            new ULocale("lv_LV"), // Latvia - Latvian
            new ULocale("lt_LT"), // Lithuania - Lithuanian
            new ULocale("be_BY"), // Belarus - Belarusian
            new ULocale("kk_KZ"), // Kazakhstan - Kazakh
            new ULocale("ka_GE"), // Georgia - Georgian
            new ULocale("hy_AM"), // Armenia - Armenian
            new ULocale("az_AZ"), // Azerbaijan - Azerbaijani
            new ULocale("tt_RU"),  // Tatarstan - Tatar
            new ULocale("ja_JP"), // Japan - Japanese
            new ULocale("ko_KR"), // South Korea - Korean
            new ULocale("vi_VN"), // Vietnam - Vietnamese
            new ULocale("th_TH"), // Thailand - Thai
            new ULocale("my_MM"), // Myanmar - Burmese
            new ULocale("id_ID"), // Indonesia - Indonesian
            new ULocale("ph_PH"), // Philippines - Filipino
            new ULocale("mn_MN"), // Mongolia - Mongolian
            new ULocale("lo_LA"), // Laos - Lao
            new ULocale("km_KH"), // Cambodia - Khmer
            new ULocale("zh_TW"), // Taiwan - Traditional Chinese
            new ULocale("zh_HK"), // Hong Kong - Traditional Chinese
            new ULocale("en_AU"), // Australia - English
            new ULocale("en_NZ")  // New Zealand - English
    };

    // Locales for Arabic-Indic languages
    private static final ULocale[] ARABIC_INDIAN_LOCALES = {
            new ULocale("ar"),           // General Arabic
            new ULocale("ar_EG"),        // Egypt
            new ULocale("ar_SA"),        // Saudi Arabia
            new ULocale("ar_AE"),        // United Arab Emirates
            new ULocale("ar_MA"),        // Morocco
            new ULocale("ar_JO"),        // Jordan
            new ULocale("ar_IQ"),        // Iraq
            new ULocale("ar_SY"),        // Syria
            new ULocale("fa"),           // General Persian
            new ULocale("fa_IR"),        // Iran
            new ULocale("fa_AF"),        // Dari, Afghanistan
            new ULocale("ur"),           // General Urdu
            new ULocale("ur_PK"),        // Pakistan
            new ULocale("ur_IN"),        // India
            new ULocale("ps"),           // General Pashto
            new ULocale("ps_AF"),        // Afghanistan
            new ULocale("ps_PK"),        // Pakistan
            new ULocale("ckb"),          // General Kurdish (Sorani)
            new ULocale("ckb_IQ"),       // Iraq
            new ULocale("ckb_IR"),       // Iran
            new ULocale("sd"),           // General Sindhi
            new ULocale("sd_PK"),        // Pakistan
            new ULocale("sd_IN"),        // India
            new ULocale("ks"),           // General Kashmiri
            new ULocale("ks_IN"),        // India
            new ULocale("ks_PK"),        // Pakistan
            new ULocale("pa_Arab"),      // Punjabi (Shahmukhi script)
            new ULocale("pa_Arab_PK")    // Pakistan
    };

    private static final String[] TIME_FORMAT_VALUES =
            {"H", "h", "m", "S", "s", "A", "a", "Z", "z", "X", "x", ":", "."};
    private static final String[] DATE_FORMAT_VALUES =
            {"Y", "y", "M", "D", "d", "E", "L", "F", "W", "w", "u", "G", "-", "/"};
    private static final String[] DATE_FORMATS = {
            // Date and time with time zone:
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", // ISO 8601 with milliseconds and time zoe like +0200
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", // ISO 8601 with timezone offset
            "yyyy-MM-dd'T'HH:mm:ss:SSSZ", // ISO 8601 with milliseconds and time zoe like +0200
            "yyyy-MM-dd'T'HH:mm:ss:SSSXXX", // ISO 8601 with timezone offset
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ssa",
            "yyyy-MM-dd'T'HH:mma",
            "yyyy-MM-dd'T'HH:mm:ss a",
            "yyyy-MM-dd'T'HH:mm a",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",

            // Date and Time formats with seconds:
            "yyyy-MM-dd hh:mm:ss a", // With AM/PM
            "yyyy-MM-dd hh:mm:ssa", // With AM/PM
            "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "dd MMM yyyy HH:mm:ss",
            "dd MMMM yyyy HH:mm:ss",
            "MMM dd, yyyy HH:mm:ss",
            "MMMM dd, yyyy HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssX",

            // Date and Time formats with seconds and day like 5 or 05:
            "yyyy-MM-d hh:mm:ss a", // With AM/PM
            "yyyy-MM-d hh:mm:ssa", // With AM/PM
            "yyyy-MM-d HH:mm:ss",
            "MM/d/yyyy HH:mm:ss",
            "d-MM-yyyy HH:mm:ss",
            "yyyy/MM/d HH:mm:ss",
            "d MMM yyyy HH:mm:ss",
            "d MMMM yyyy HH:mm:ss",
            "MMM d, yyyy HH:mm:ss",
            "MMMM d, yyyy HH:mm:ss",

            // Date and Time formats with seconds and month like 5 or 05:
            "yyyy-M-dd hh:mm:ss a", // With AM/PM
            "yyyy-M-dd hh:mm:ssa", // With AM/PM
            "yyyy-M-dd HH:mm:ss",
            "M/dd/yyyy HH:mm:ss",
            "d-M-yyyy HH:mm:ss",
            "yyyy/M/dd HH:mm:ss",

            // Date and Time formats with seconds and month and day like 5 or 05:
            "yyyy-M-d hh:mm:ss a", // With AM/PM
            "yyyy-M-d hh:mm:ssa", // With AM/PM
            "yyyy-M-d HH:mm:ss",
            "M/d/yyyy HH:mm:ss",
            "d-M-yyyy HH:mm:ss",
            "yyyy/M/d HH:mm:ss",

            // Date and Time formats without year and with seconds:
            "EEE, dd MMM yyyy HH:mm:ss z", // RFC 822 date
            "EEE, dd MMM yyyy HH:mm:ssz", // With time zone like PST
            "EEE, dd MMMM yyyy HH:mm:ss z", // With time zone like PST
            "EEE, dd MMMM yyyy HH:mm:ssz", // With time zone like PST
            "EEE, dd MMMM yyyy HH:mm:ss Z", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mm:ssZ", // With time zoe like +0200
            "MM/dd/yyyy HH:mm:ss Z", // With time zone like +0200
            "MM/dd/yyyy HH:mm:ssZ", // With time zone like +0200
            "dd-MM-yyyy HH:mm:ss Z", // With time zone like +0200
            "dd-MM-yyyy HH:mm:ssZ", // With time zone like +0200
            "yyyy-MM-dd HH:mm:ss Z",  // ISO 8601 timezone like +0200
            "yyyy-MM-dd HH:mm:ssZ", // With time zone like +0200
            "yyyy-MM-dd hh:mm:ss a Z",  // With AM/PM and timezone like +0200
            "yyyy-MM-dd hh:mm:ssa Z", // With time zone like +0200
            "yyyy-MM-dd hh:mm:ssaZ", // With time zone like +0200
            "yyyy-MM-dd'T'HH:mm:ss z", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mm:ssz", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mm:ssZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mm:ssX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mm:ssXXX", // With time zoe like +02:00
            "MM-dd'T'HH:mm:ssX",
            "MM-dd'T'HH:mm:ss z", // With time zoe like PST
            "MM-dd'T'HH:mm:ssz", // With time zoe like PST
            "MM-dd'T'HH:mm:ssZ", // With time zoe like +0200
            "MM-dd'T'HH:mm:ssX", // With time zoe like +02
            "MM-dd'T'HH:mm:ssXXX", // With time zoe like +02:00
            "MM-dd hh:mm:ss a", // With AM/PM
            "MM-dd hh:mm:ssa", // With AM/PM
            "MM-dd HH:mm:ss",
            "MM/dd HH:mm:ss",
            "dd-MM HH:mm:ss",
            "dd MMM HH:mm:ss",
            "MMM dd HH:mm:ss",
            "MMMM dd HH:mm:ss",

            // With month like 05 or 5
            "M-dd hh:mm:ss a", // With AM/PM
            "M-dd hh:mm:ssa", // With AM/PM
            "M-dd HH:mm:ss",
            "M/dd HH:mm:ss",
            "dd-M HH:mm:ss",

            // With day like 05 or 5
            "MM-d hh:mm:ss a", // With AM/PM
            "MM-d hh:mm:ssa", // With AM/PM
            "MM-d HH:mm:ss",
            "MM/d HH:mm:ss",
            "d-MM HH:mm:ss",
            "d MMM HH:mm:ss",
            "MMM d HH:mm:ss",
            "MMMM d HH:mm:ss",

            // With month and day like 05 or 5
            "M-d hh:mm:ss a", // With AM/PM
            "M-d hh:mm:ssa", // With AM/PM
            "M-d HH:mm:ss",
            "M/d HH:mm:ss",
            "d-M HH:mm:ss",

            // Date and Time with timezone and without seconds:
            "EEE, dd MMM yyyy HH:mm z", // With time zoe like PST
            "EEE, dd MMMM yyyy HH:mm z", // With time zoe like PST
            "EEE, dd MMM yyyy HH:mm Z", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mm Z", // With time zoe like +0200
            "EEE, dd MMM yyyy HH:mmz", // With time zoe like PST
            "EEE, dd MMMM yyyy HH:mmz", // With time zoe like PST
            "EEE, dd MMM yyyy HH:mmZ", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mmZ", // With time zoe like +0200
            "EEE, dd MMMM yyyy HH:mmX", // With time zoe like +02
            "EEE, dd MMMM yyyy HH:mmXXX", // With time zoe like +02:00
            "MM/dd/yyyy HH:mm Z", // With time zoe like +0200
            "dd-MM-yyyy HH:mm Z", // With time zoe like +0200
            "yyyy-MM-dd HH:mm Z", // With time zoe like +0200
            "yyyy-MM-dd hh:mm a Z", // With time zoe like +0200
            "yyyy-MM-dd hh:mma Z", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mmXXX", // With time zoe like +02:00
            "yyyy-MM-dd hh:mm aZ", // With time zoe like +0200
            "yyyy-MM-dd hh:mmaZ", // With time zoe like +0200
            "MM/dd/yyyy HH:mmZ", // With time zoe like +0200
            "dd-MM-yyyy HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mmXXX", // With time zoe like +02:00

            // Date and time with time zone:
            "yyyy-MM-dd'T'HH:mm z", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mmz", // With time zoe like PST
            "yyyy-MM-dd'T'HH:mmZ", // With time zoe like +0200
            "yyyy-MM-dd'T'HH:mmX", // With time zoe like +02
            "yyyy-MM-dd'T'HH:mmXXX", // With time zoe like +02:00

            // Date and Time formats without year and without seconds:
            "MM-dd'T'HH:mmX",
            "MM-dd'T'HH:mm z", // With time zoe like PST
            "MM-dd'T'HH:mmz", // With time zoe like PST
            "MM-dd'T'HH:mmZ", // With time zoe like +0200
            "MM-dd'T'HH:mmX", // With time zoe like +02
            "MM-dd'T'HH:mmXXX", // With time zoe like +02:00

            // With week day like "Mon" or "MON" prefix
            "EEE, MM-dd hh:mm a", // With AM/PM
            "EEE, MM-dd hh:mma", // With AM/PM
            "EEE, MM-dd HH:mm",
            "EEE, MM/dd HH:mm",
            "EEE, dd-MM HH:mm",
            "EEE, dd MMM HH:mm",
            "EEE, MMM dd HH:mm",
            "EEE, MMMM dd HH:mm",

            // With week day like "Monday" or "MONDAY" prefix
            "EEEE, MM-dd hh:mm a", // With AM/PM
            "EEEE, MM-dd hh:mma", // With AM/PM
            "EEEE, MM-dd HH:mm",
            "EEEE, MM/dd HH:mm",
            "EEEE, dd-MM HH:mm",
            "EEEE, dd MMM HH:mm",
            "EEEE, MMM dd HH:mm",
            "EEEE, MMMM dd HH:mm",

            // With week day like "Mon" or "MON" suffix
            "MM-dd hh:mm a EEE", // With AM/PM
            "MM-dd hh:mma EEE", // With AM/PM
            "MM-dd HH:mm EEE",
            "MM/dd HH:mm EEE",
            "dd-MM HH:mm EEE",
            "dd MMM HH:mm EEE",
            "MMM dd HH:mm EEE",
            "MMMM dd HH:mm EEE",

            // With week day like "Monday" or "MONDAY" suffix
            "MM-dd hh:mm a EEEE", // With AM/PM
            "MM-dd hh:mma EEEE", // With AM/PM
            "MM-dd HH:mm EEEE",
            "MM/dd HH:mm EEEE",
            "dd-MM HH:mm EEEE",
            "dd MMM HH:mm EEEE",
            "MMM dd HH:mm EEEE",
            "MMMM dd HH:mm EEEE",

            "MM-dd hh:mm a", // With AM/PM
            "MM-dd hh:mma", // With AM/PM
            "MM-dd HH:mm",
            "MM/dd HH:mm",
            "dd-MM HH:mm",
            "dd MMM HH:mm",
            "MMM dd HH:mm",
            "MMMM dd HH:mm",

            // With day like 05 or 5
            "MM-d hh:mma", // With AM/PM
            "MM-d hh:mm a", // With AM/PM
            "MM-d HH:mm",
            "MM/d HH:mm",
            "d-MM HH:mm",
            "d MMM HH:mm",
            "MMM d HH:mm",
            "MMMM d HH:mm",

            // With mont like 05 or 5
            "M-dd hh:mma", // With AM/PM
            "M-dd hh:mm a", // With AM/PM
            "M-dd HH:mm",
            "M/dd HH:mm",
            "dd-M HH:mm",

            // With mont and day like 05 or 5
            "M-d hh:mma", // With AM/PM
            "M-d hh:mm a", // With AM/PM
            "M-d HH:mm",
            "M/d HH:mm",
            "d-M HH:mm",

            // Date and Time formats without seconds:
            "yyyy-MM-dd hh:mm a", // With AM/PM
            "yyyy-MM-dd hh:mma", // With AM/PM
            "yyyy-MM-dd HH:mm",
            "MM/dd/yyyy HH:mm",
            "dd-MM-yyyy HH:mm",
            "yyyy/MM/dd HH:mm",
            "dd MMM yyyy HH:mm",
            "dd MMMM yyyy HH:mm",
            "MMM dd, yyyy HH:mm",
            "MMMM dd, yyyy HH:mm",

            // With day like 05 or 5
            "yyyy-MM-d hh:mm a", // With AM/PM
            "yyyy-MM-d hh:mma", // With AM/PM
            "yyyy-MM-d HH:mm",
            "MM/d/yyyy HH:mm",
            "d-MM-yyyy HH:mm",
            "yyyy/MM/d HH:mm",
            "d MMM yyyy HH:mm",
            "d MMMM yyyy HH:mm",
            "MMM d, yyyy HH:mm",
            "MMMM d, yyyy HH:mm",

            // With month like 05 or 5
            "yyyy-M-dd hh:mm a", // With AM/PM
            "yyyy-M-dd hh:mma", // With AM/PM
            "yyyy-M-dd HH:mm",
            "M/dd/yyyy HH:mm",
            "dd-M-yyyy HH:mm",
            "yyyy/M/dd HH:mm",

            // With month and day like 05 or 5
            "yyyy-M-d hh:mm a", // With AM/PM
            "yyyy-M-d hh:mma", // With AM/PM
            "yyyy-M-d HH:mm",
            "M/d/yyyy HH:mm",
            "d-M-yyyy HH:mm",
            "yyyy/M/d HH:mm",

            // Simple Date formats:
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd-MM-yyyy",
            "dd-MM-yyyy",
            "yyyy/MM/dd",
            "yyyyMMdd",
            "dd MMM yyyy",
            "dd MMMM yyyy",
            "MMM dd, yyyy",
            "MMMM dd, yyyy",
            "MM-dd",
            "MM/dd",
            "dd-MM",
            "dd MMM",
            "dd MMMM",
            "MMM dd",
            "MMMM dd",
            "MMM",
            "MMMM",
            "E",
            "EEEE",

            // With day like 05 or 5
            "yyyy-MM-d",
            "MM/d/yyyy",
            "d-MM-yyyy",
            "d-MM-yyyy",
            "yyyy/MM/d",
            "d MMM yyyy",
            "d MMMM yyyy",
            "MMM d, yyyy",
            "MMMM d, yyyy",
            "MM-d",
            "MM/d",
            "dd-MM",
            "d MMM",
            "d MMMM",
            "MMM d",
            "MMMM d",

            // With month like 05 or 5
            "yyyy-M-dd",
            "M/dd/yyyy",
            "dd-M-yyyy",
            "dd-M-yyyy",
            "yyyy/M/dd",
            "M-dd",
            "M/dd",
            "dd-M",

            // With month and day like 05 or 5
            "yyyy-M-d",
            "M/d/yyyy",
            "d-M-yyyy",
            "d-M-yyyy",
            "yyyy/M/d",
            "M-d",
            "M/d",
            "dd-M",

            // Time-only formats:
            "HH:mm:ss.SSSZ",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSS Z",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSS z",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSSz",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSSX",  // Time with milliseconds and timezone like +02.
            "HH:mm:ss.SSS X",  // Time with milliseconds and timezone like +02.
            "HH:mm:ss.SSSXXX",  // Time with milliseconds and timezone like +02:00.
            "HH:mm:ss.SSS XXX",  // Time with milliseconds and timezone like +02:00.
            "HH:mm:ss.SSS Z",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSSZ",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss.SSS z",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss.SSSz",  // Time with milliseconds and timezone like PST.
            "hh:mm:ss.SSS a", // With AM/PM and milliseconds.
            "hh:mm:ss.SSSa", // With AM/PM and milliseconds.
            "HH:mm:ss.SSS", // With milliseconds
            "HH:mm:ss:SSSZ",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss:SSS Z",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss:SSS z",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss:SSSz",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss:SSSX",  // Time with milliseconds and timezone like +02.
            "HH:mm:ss:SSS X",  // Time with milliseconds and timezone like +02.
            "HH:mm:ss:SSSXXX",  // Time with milliseconds and timezone like +02:00.
            "HH:mm:ss:SSS XXX",  // Time with milliseconds and timezone like +02:00.
            "HH:mm:ss:SSS Z",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss:SSSZ",  // Time with milliseconds and timezone like +0200.
            "HH:mm:ss:SSS z",  // Time with milliseconds and timezone like PST.
            "HH:mm:ss:SSSz",  // Time with milliseconds and timezone like PST.
            "hh:mm:ss:SSS a", // With AM/PM and milliseconds.
            "hh:mm:ss:SSSa", // With AM/PM and milliseconds.
            "HH:mm:ss:SSS", // With milliseconds
            "hh:mm:ss a Z",  // Time with timezone and AM/PM.
            "hh:mm:ssa Z",  // Time with timezone and AM/PM.
            "hh:mm:ss aZ",  // Time with timezone and AM/PM.
            "hh:mm:ssaZ",  // Time with timezone and AM/PM.
            "HH:mm:ssZ",  // Time with timezone like +0200.
            "HH:mm:ss Z",  // Time with timezone like +0200.
            "hh:mm:ss a X",  // Time with timezone like +02 and AM/PM
            "hh:mm:ssa X",  // Time with timezone like +02 and AM/PM
            "hh:mm:ss aX",  // Time with timezone like +02 and AM/PM
            "hh:mm:ssaX",  // Time with timezone like +02 and AM/PM
            "HH:mm:ssX",  // Time with timezone like +02.
            "HH:mm:ss X",  // Time with timezone like +02
            "hh:mm:ss a XXX",  // Time with timezone like +02.00 and AM/PM
            "hh:mm:ssa XXX",  // Time with timezone like +02.00 and AM/PM
            "hh:mm:ss aXXX",  // Time with timezone like +02.00 and AM/PM
            "hh:mm:ssaXXX",  // Time with timezone like +02.00 and AM/PM
            "HH:mm:ssXXX",  // Time with timezone like +02.00
            "HH:mm:ss XXX",  // Time with timezone like +02.00
            "HH:mm:ss Z",  // Time with timezone like +0200.
            "HH:mm:ssZ",  // Time with timezone like +0200.
            "HH:mm:ss z",  // Time with timezone like PST.
            "HH:mm:ssz",  // Time with timezone like PST.
            "hh:mm:ss a Z",  // Time with timezone like +0200 and AM/PM.
            "hh:mm:ss a z",  // Time with timezone like PST and AM/PM.
            "hh:mm:ssa Z",  // Time with timezone like +0200 and AM/PM.
            "hh:mm:ssa z",  // Time with timezone like PST and AM/PM.
            "hh:mm:ss a", // With AM/PM.
            "hh:mm:ssa", // With AM/PM.
            "HH:mm:ss a", // With AM/PM.
            "HH:mm:ssa", // With AM/PM.
            "HH:mm:ss",

            // Simple time:
            "hh:mm a", // With AM/PM.
            "hh:mma", // With AM/PM.
            "HH:mm a", // With AM/PM.
            "HH:ma", // With AM/PM.
            "hh:m a", // With AM/PM.
            "hh:ma", // With AM/PM.
            "HH:m a", // With AM/PM.
            "HH:ma", // With AM/PM.
            "HH:mm",
            "hh:mm",
            "HH:m",
            "hh:m",

            // Hour only time:
            "HH a", // Hour with AM/PM like 02PM
            "HHa", // Hour with AM/PM like 02PM
            "hh a", // Hour with AM/PM like 2 PM
            "hha", // Hour with AM/PM like 2PM
    };

    private static final Map<Character, Character> letterToDigitMap = createLetterToDigitMap();

    private ConvertUtils() {
    }

    /**
     * Escapes JavaScript.
     * @param jsonString The input to escape.
     * @return The escaped script.
     */
    public static String escapeJavaScript(String jsonString) {
        DataValidationUtils.validateNotNull(jsonString, "jsonString");
        StringBuilder escapedString = new StringBuilder();

        for (char c : jsonString.toCharArray()) {
            switch (c) {
                case '\'' -> escapedString.append("\\'");
                case '\"' -> escapedString.append("\\\"");
                case '\\' -> escapedString.append("\\\\");
                case '\n' -> escapedString.append("\\n");
                case '\r' -> escapedString.append("\\r");
                case '\t' -> escapedString.append("\\t");
                case '\b' -> escapedString.append("\\b");
                case '\f' -> escapedString.append("\\f");
                case '<' -> escapedString.append("\\u003C");
                case '>' -> escapedString.append("\\u003E");
                case '&' -> escapedString.append("\\u0026");
                case '=' -> escapedString.append("\\u003D");
                case '-' -> escapedString.append("\\u002D");
                default -> {
                    if (c < 32 || c > 126) {
                        escapedString.append(String.format("\\u%04x", (int) c));
                    } else {
                        escapedString.append(c);
                    }
                }
            }
        }
        log.debug("""
                JavaScript string is escaped.
                Source:
                {}
                Target:
                {}
                """.stripIndent(),
                jsonString, escapedString);
        return escapedString.toString();
    }

    /**
     * Convert string to CSV field value.
     * Escapes CSV field value.
     *
     * @param string The string value.
     * @return The escaped CSV field value.
     */
    public static String escapeCSVField(String string) {
        DataValidationUtils.validateNotNull(string, "string");
        // Escape " with ""
        String csvFieldValue = string.replace("\"", ESCAPED_QUOTE);
        // Enclose the field in double quotes
        csvFieldValue = String.format("\"%s\"", csvFieldValue);
        log.debug("""
                        String converted to CSV field value.
                        CSV:
                        {}
                        String:
                        {}
                        """.stripIndent(),
                csvFieldValue, string);
        return csvFieldValue;
    }

    /**
     * Converts CSV field to string.
     * Replaces CSV string double quotes escape.
     *
     * @param csvString The CSV string.
     * @return The string without CSV escape.
     */
    public static String csvFieldValueToString(String csvString) {
        DataValidationUtils.validateNotNull(csvString, "csvScript");
        String string = csvString.replace(ESCAPED_QUOTE, "\"");
        string = trimQuotes(string);
        log.debug("""
                CSV field value converted to normal string.
                CSV;
                String:
                {}
                """.stripIndent(),
                csvString, string);
        return string;
    }

    /**
     * Escapes XML string.
     * @param xmlString The XML string.
     * @return The escaped XML string.
     */
    public static String escapeXmlString(String xmlString) {
        DataValidationUtils.validateNotNull(xmlString, "xmlString");

        try {
            String result = HtmlUtils.htmlEscape(xmlString);
            log.debug("""
                    XML string is escaped.
                    Source:
                    {}
                    Target:
                    {}
                    """.stripIndent(),
                    xmlString, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot escape XML string.
                    XML string:
                    %s
                    """.stripIndent(),
                    xmlString));
        }
    }

    /**
     * Unescapes XML string.
     * @param xmlString The XML string.
     * @return The escaped XML string.
     */
    public static String unescapeXmlString(String xmlString) {
        DataValidationUtils.validateNotNull(xmlString, "xmlString");

        try {
            String result = HtmlUtils.htmlUnescape(xmlString);
            log.debug("""
                    XML string is escaped.
                    Source:
                    {}
                    Target:
                    {}
                    """.stripIndent(),
                    xmlString, result);
            return result;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot escape XML string.
                    XML string:
                    %s
                    """.stripIndent(),
                    xmlString));
        }
    }

    /**
     * Converts string value to boolean value.
     * @param string The string value.
     * @return The boolean value.
     */
    public static boolean stringToBoolean(String string) {
        DataValidationUtils.validateNotBlank(string, "string");
        boolean result;

        switch (string.trim()) {
            case BOOLEAN_TRUE_VALUE -> result = true;
            case BOOLEAN_FALSE_VALUE -> result = false;
            default -> throw new SmartRuntimeException(String.format(
                    "Invalid boolean format: %s.", string));
        }
        log.debug("{} string converted to boolean {}.", string, result);
        return result;
    }

    /**
     * Converts string value to date value.
     * @param dateString The string value.
     * @return The date value or null if cannot convert.
     */
    public static SmartDate stringToSmartDate(String dateString) {
        DataValidationUtils.validateNotBlank(dateString, "dateString");

        try {
            SmartDate smartDate;
            Date date = null;
            String dateFormat = null;

            if (isNumberString(dateString)) {
                 long milliseconds = (long) stringToNumber(dateString);

                 // Check that milliseconds value is not day or year value
                 // up to the year 3000
                 if (milliseconds >= 1 && milliseconds <= 3000) {
                     throw new SmartRuntimeException(String.format("""
                                    Cannot convert string to smart date.
                                    String: {}
                                    """.stripIndent(),
                             dateString));
                 }
                 smartDate = SmartDate.fromMilliseconds(milliseconds);
            }
            else {
                // Replace day suffixes
                dateString = removeOrdinalSuffix(dateString);
                // Replace double spaces with one space
                dateString = dateString.replaceAll("\\s{2,}", " ");

                for (String format : DATE_FORMATS) {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat(format);
                        date = formatter.parse(dateString);
                        dateFormat = format;
                        break;
                    } catch (ParseException e) {
                        // Ignore exception
                    }
                }
                if (date == null) {
                    throw new SmartRuntimeException(String.format("""
                                    Cannot convert string to smart date.
                                    String: {}
                                    """.stripIndent(),
                            dateString));
                }
                smartDate = new SmartDate(date, dateFormat);
            }
            log.debug("""
                String converted to smart date.
                String: {}
                Smart local date: {}
                """.stripIndent(),
                smartDate);
            return smartDate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart date.
                    String: %s
                    """.stripIndent(),
                    dateString), e);
        }
    }

    /**
     * Converts date time format with time to  date format without time.
     * @param dateTimeFormat The date time format.
     * @return The date format.
     */
    public static String dateTimeFormatToDateFormat(String dateTimeFormat) {
        String dateFormat = dateTimeFormat;

        for (String timeFormatValue : TIME_FORMAT_VALUES) {
            dateFormat = dateFormat.replace(timeFormatValue, "");
        }
        return dateFormat.trim();
    }

    /**
     * Converts date time format with time to time format without time.
     * @param dateTimeFormat The date time format.
     * @return The time format.
     */
    public static String dateTimeFormatToTimeFormat(String dateTimeFormat) {
        String timeFormat = dateTimeFormat;

        for (String timeFormatValue : DATE_FORMAT_VALUES) {
            timeFormat = timeFormat.replace(timeFormatValue, "");
        }
        return timeFormat.trim();
    }

    /**
     * Converts string value to smart local date value.
     * @param dateString The date string value.
     * @return The smart local date value.
     */
    public static SmartLocalDate stringToSmartLocalDate(String dateString) {
        DataValidationUtils.validateNotBlank(dateString, "dateString");

        try {
            SmartDate smartDate = stringToSmartDate(dateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
            LocalDate localDate = LocalDate.parse(smartDate.toString(), formatter);
            String dateFormat = ConvertUtils.dateTimeFormatToDateFormat(smartDate.getFormat());
            SmartLocalDate smartLocalDate =
                    new SmartLocalDate(localDate, dateFormat);
            log.debug("""
                String converted to smart local date.
                String: {}
                Smart local date: {}
                """.stripIndent(),
            dateString, smartLocalDate);
            return smartLocalDate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart local date.
                    String: %s
                    """.stripIndent(),
                    dateString), e);
        }
    }

    /**
     * Converts string value to smart local date time value.
     * @param dateTimeString The date time string value.
     * @return The smart local date time value.
     */
    public static SmartLocalDateTime stringToSmartLocalDateTime(String dateTimeString) {
        try {
            SmartDate smartDate = stringToSmartDate(dateTimeString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
            LocalDateTime localDateTime = LocalDateTime.parse(smartDate.toString(), formatter);
            SmartLocalDateTime smartLocalDateTime =
                    new SmartLocalDateTime(localDateTime, smartDate.getFormat());
            log.debug("""
                String converted to smart local date time.
                String: {}
                Smart local date time: {}
                """.stripIndent(),
                    dateTimeString, smartLocalDateTime);
            return smartLocalDateTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart local date time.
                    String: %s
                    """.stripIndent(),
                    dateTimeString), e);
        }
    }

    /**
     * Converts string value to smart local time value.
     * @param timeString The time string value.
     * @return The smart local time value.
     */
    public static SmartLocalTime stringToSmartLocalTime(String timeString) {
        try {
            SmartDate smartDate = stringToSmartDate(timeString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(smartDate.getFormat());
            LocalTime localTime = LocalTime.parse(smartDate.toString(), formatter);
            String timeFormat = dateTimeFormatToTimeFormat(smartDate.getFormat());
            SmartLocalTime smartLocalTime = new SmartLocalTime(localTime, timeFormat);
            log.debug("""
               Cannot convert string to smart local time.
                String: {}
                Smart local time: {}
                """.stripIndent(),
                    timeString, smartLocalTime);
            return smartLocalTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    String converted to smart local time.
                    String: {}
                    """.stripIndent(),
                    timeString), e);
        }
    }

    /**
     * Converts string value to JSON object value.
     * @param string The string value.
     * @return The JSON object value.
     */
    public static JSONObject stringToJsonObject(String string) {
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONObject jsonObject;

            if (isXmlNodeString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = new JSONObject(string);
            }
            log.debug("{} string converted to JSON object {}.", string, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Invalid JSON object format: %s.", string));
        }
    }

    /**
     * Converts string value to JSON array value.
     * @param string The string value.
     * @return The JSON array value.
     */
    public static JSONArray stringToJsonArray(String string) {
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONArray jsonArray;

            if (isJsonArrayString(string)) {
                jsonArray = new JSONArray(string);
            }
            else if (isXmlArrayString(string)) {
                jsonArray = xmlStringToJsonArray(string);
            }
            else if (isCsvString(string)) {
                jsonArray = csvStringToJsonArray(string);
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert string to JSON array: %s", string));
            }
            log.debug("{} string converted to JSON array {}.", string, jsonArray);
            return jsonArray;
        }
        catch (JSONException | NullPointerException e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to JSON array: %s", string), e);
        }
    }

    /**
     * Converts string value to XML document value.
     * @param string The string value.
     * @return The XML document value.
     */
    public static Document stringToXmlDocument(String string) {
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            Document document;

            if (isJsonObjectString(string)) {
                JSONObject jsonObject = stringToJsonObject(string);
                document = jsonObjectToXmlDocument(jsonObject);
            }
            else {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(new InputSource(new StringReader(string)));
            }
            log.debug("{} string converted to XML document:\n{}.", string, document);
            return document;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to XML document:\n%s.", string));
        }
    }

    /**
     * Converts file path string to file object.
     *
     * @param filePath The file path string;
     * @return The file;
     */
    public static File stringToFile(String filePath) {
        DataValidationUtils.validateFilePathFormat(filePath, "filePath");

        try {
            File file = new File(filePath);
            log.debug("""
                    File path converted to File object.
                    File path:
                    {}
                    File object:
                    {}
                    """.stripIndent(),
                    filePath, file);
            return file;
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to file object: %s.", filePath));
        }
    }

    /**
     * Converts URL string to URL object.
     * @param urlString The file path string;
     * @return The URL object;
     */
    public static URL stringToURL(String urlString) {
        DataValidationUtils.validateNotBlank(urlString, "urlString");

        try {
            URL url = new URL(urlString);
            log.debug("""
                    URL string converted to URL object.
                    URL string:
                    {}
                    URL object:
                    {}
                    """.stripIndent(),
                    urlString, url);
            return url;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert URL string to URL object:\n%s.", urlString));
        }
    }

    /**
     * Converts URI string to URI object.
     * @param uriString The URI string;
     * @return The URI object;
     */
    public static URI stringToURI(String uriString) {
        DataValidationUtils.validateNotBlank(uriString, "urlString");

        try {
            URI uri;
            try {
                uri = new URI(uriString);
            }
            catch (URISyntaxException e) {
                // Workaround - convert Windows path to UNIX path format
                uriString = uriString.replace("\\", "/");
                // Try to add file protocol for file path URI
                uriString = String.format("file://%s", uriString);
                uri = new URI(uriString);
            }
            log.debug("""
                    URI string converted to URI object.
                    URI string:
                    {}
                    URI object:
                    {}
                    """.stripIndent(),
                    uriString, uri);
            return uri;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert URI string to URI object:\n%s.", uriString));
        }
    }

    /**
     * Converts file path string to path object.
     * @param filePath The file path string;
     * @return The file;
     */
    public static Path stringToPath(String filePath) {
        DataValidationUtils.validateFilePathFormat(filePath, "filePath");

        try {
            Path path = Paths.get(filePath);
            log.debug("File path string '{}' converted to path object: {}",
                    filePath, path);
            return path;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to path object: %s.", filePath));
        }
    }

    /**
     * Converts string value to XML node value.
     * @param string The string value.
     * @return The XML node value.
     */
    public static Node stringToXmlNode(String string) {
        DataValidationUtils.validateNotBlank(string, "xmlString");

        try {
            Node node;
            if (isJsonObjectString(string)) {
                JSONObject jsonObject = stringToJsonObject(string);
                node = jsonObjectToXmlNode(jsonObject);
            }
            else {
                // Add XML header if not present
                if (!string.trim().startsWith("<?xml")) {
                    string = String.format("%s\n%s", XML_HEADER, string);
                }
                Document document = stringToXmlDocument(string);
                node = document.getDocumentElement();
            }
            log.debug("{} string converted to XML node:\n{}.", string, node);
            return node;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string to XML Node:\n%s.", string));
        }
    }

    /**
     * Converts date to local date.
     * @param date The date.
     * @return The local date.
     */
    public static LocalDate dateToLocalDate(Date date) {
        DataValidationUtils.validateNotNull(date, "date");

        try {
            LocalDate localDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())  // Specify time zone (system default)
                    .toLocalDate();  // Convert to LocalDate
            log.debug("Date '{}' converted to local date: {}", date, localDate);
            return localDate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert date '%s' to local date.'",
                    date), e);
        }
    }

    /**
     * Converts date to local date time.
     * @param date The date.
     * @return The local date time.
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        DataValidationUtils.validateNotNull(date, "date");

        try {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(
                    date.toInstant(), ZoneId.systemDefault());
            log.debug("Date '{}' converted to local date time: {}", date, localDateTime);
            return localDateTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert date '%s' to local date time.'",
                    date), e);
        }
    }

    /**
     * Converts date to local time.
     * @param date The date.
     * @return The local time.
     */
    public static LocalTime dateToLocalTime(Date date) {
        DataValidationUtils.validateNotNull(date, "date");

        try {
            Instant instant = date.toInstant();
            // Convert Instant to LocalTime using the system's default time zone
            LocalTime localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime();
            log.debug("Date '{}' converted to local time: {}", date, localTime);
            return localTime;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert date '%s' to local time.'",
                    date), e);
        }
    }

    /**
     * Converts local date object to date.
     * @param localDate The date.
     * @return The date.
     */
    public static Date localDateToDate(LocalDate localDate) {
        DataValidationUtils.validateNotNull(localDate, "localDate");

        try {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            log.debug("Local date {} converted to date: '{}'.",
                    localDate, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date %s to date.'",
                    localDate), e);
        }
    }

    /**
     * Converts local date time object to date.
     * @param localDateTime The date.
     * @return The date.
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        DataValidationUtils.validateNotNull(localDateTime, "LocalDateTime");

        try {
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            log.debug("Local date time {} converted to date: '{}'.",
                    localDateTime, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date %s to date.'",
                    localDateTime), e);
        }
    }

    /**
     * Converts local time object to date.
     * @param localTime The local time.
     * @return The date.
     */
    public static Date localTimeToDate(LocalTime localTime) {
        DataValidationUtils.validateNotNull(localTime, "localTime");

        try {
            LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            log.debug("Local time {} converted to date: '{}'.",
                    localTime, date);
            return date;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local time %s to date.'",
                    localTime));
        }
    }

    /**
     * Converts local date object to string by date format.
     * @param localDate  The local date.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public static String localDateToString(LocalDate localDate, String dateFormat) {
        DataValidationUtils.validateNotNull(localDate, "localDate");
        DataValidationUtils.validateNotBlank(dateFormat, dateFormat);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            String dataString = localDate.format(formatter);
            log.debug("Local date object {} with format '{}' converted to date string: '{}'.",
                    localDate, dateFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date object %s to string with date format '%s'",
                    localDate, dateFormat));
        }
    }

    /**
     * Converts local date time object to string by date time format.
     * @param localDateTime The local date time.
     * @param dateTimeFormat The date time format.
     * @return The date time string.
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String dateTimeFormat) {
        DataValidationUtils.validateNotNull(localDateTime, "localDate");
        DataValidationUtils.validateNotBlank(dateTimeFormat, dateTimeFormat);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
            String dataString = localDateTime.format(formatter);
            log.debug("Local date time object {} with format '{}' converted to date string: '{}'.",
                    localDateTime, dateTimeFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local date time object %s to string with date format '%s'",
                    localDateTime, dateTimeFormat));
        }
    }

    /**
     * Converts local time object to string by time format.
     * @param localTime  The date.
     * @param timeFormat The time format.
     * @return The time string.
     */
    public static String localTimeToString(LocalTime localTime, String timeFormat) {
        DataValidationUtils.validateNotNull(localTime, "localTime");
        DataValidationUtils.validateNotBlank(timeFormat, timeFormat);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
            String dataString = localTime.format(formatter);
            log.debug("Local time object {} with format '{}' converted to date string: '{}'.",
                    localTime, timeFormat, dataString);
            return dataString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert local time object %s to string with date format '%s'",
                    localTime, timeFormat));
        }
    }

    /**
     * Converts date object to string by date format.
     * @param date The date.
     * @param dateFormat The date format.
     * @return The date string.
     */
    public static String dateToString(Date date, String dateFormat) {
        DataValidationUtils.validateNotNull(date, "date");
        DataValidationUtils.validateNotBlank(dateFormat, "dateFormat");

        try {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String dateString = formatter.format(date);
            log.debug("Date object {} with format '{}' converted to date string: '{}'.",
                    date, dateFormat, dateString);
            return dateString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert Date object %s to string with date format '%s'",
                    date, dateFormat));
        }
    }

    /**
     * Converts XML node object to string.
     * @param xmlNode The XML document.
     * @return The XML string.
     */
    public static String xmlNodeToString(Node xmlNode) {
        DataValidationUtils.validateNotNull(xmlNode, "xml");

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Set the output properties to format the XML with a 4-space indentation
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            // Omit the XML declaration (header)
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            DOMSource domSource = new DOMSource(xmlNode);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            String xmlString = writer.toString();

            log.debug("XML node converted to XML string: {}.", xmlString);
            return normalizeLineSeparators(xmlString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert XML document object to string:\n%s",
                    xmlNode), e);
        }
    }

    /**
     * Converts XML document object to string.
     * @param document The XML document.
     * @return The XML string.
     */
    public static String xmlDocumentToString(Document document) {
        DataValidationUtils.validateNotNull(document, "document");

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Set the output properties to format the XML with a 4-space indentation
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(domSource, result);
            String xmlString = writer.toString();

            // Add new line after XML header if needed
            xmlString = xmlString.replace("?><", "?>\r\n<");
            log.debug("XML node converted to XML string: {}.", xmlString);
            return normalizeLineSeparators(xmlString);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert XML document object to string:\n%s",
                    document), e);
        }
    }


    /**
     * Converts JSON object to string.
     * @param jsonObject The XML document.
     * @return The JSON array string.
     */
    public static String jsonObjectToString(JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            String jsonString = jsonObject.toString(JSON_LAYOUT_SPACES);
            log.debug("JSON object converted to string: {}.", jsonString);
            return jsonString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert JSON object to string:\n%s",
                    jsonObject), e);
        }
    }

    /**
     * Converts JSON array to string.
     * @param jsonArray The JSON array.
     * @return The JSON array string.
     */
    public static String jsonArrayToString(JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            return jsonArray.toString(JSON_LAYOUT_SPACES);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert JSON array to string:\n%s", jsonArray), e);
        }
    }

    /**
     * Converts Object to string.
     * Throws exception if Object has default toString() method.
     * @param object The object.
     * @return The object string.
     */
    public static <T> String objectToString(Object object) {
        String string = null;

        try {
            if (object == null || object == JSONObject.NULL) {
                string = NULL_VALUE_STRING;
            }
            else if (isNaN(object)) {
                string = NAN_VALUE_STRING;
            }
            else if (isFloatPositiveInfinite(object) || isDoublePositiveInfinite(object)) {
                string = POSITIVE_INFINITY_VALUE_STRING;
            }
            else if (isFloatNegativeInfinite(object) || isDoubleNegativeInfinite(object)) {
                string = NEGATIVE_INFINITY_VALUE_STRING;
            }
            else if (object instanceof SmartValue smartValue) {
                string = smartValue.toString();
            }
            else if (object instanceof StringBuffer stringBuffer) {
                string = stringBuffer.toString();
            }
            else if (object instanceof String stringValue) {
                string = stringValue;
            }
            else if (object instanceof Enum enumValue) {
                string = String.valueOf(enumValue);
            }
            if (string != null) {
                log.debug("Object {} converted to string: {}", object, string);
                return string;
            }
            String clasName = object.getClass().getName();

            if (object instanceof JSONObject jsonObject) {
                string = ConvertUtils.jsonObjectToString(jsonObject);
            }
            else if (object instanceof JSONArray jsonArray) {
                string = ConvertUtils.jsonArrayToString(jsonArray);
            }
            else if (object instanceof Document document) {
                string = ConvertUtils.xmlDocumentToString(document);
            }
            else if (object instanceof Node node) {
                string = ConvertUtils.xmlNodeToString(node);
            }
            else if (object instanceof Collection collection) {
                JSONArray jsonArray = collectionToJsonArray(collection);
                string = jsonArrayToString(jsonArray);
            }
            else if (object instanceof Map map) {
                JSONObject jsonObject = mapToJasonObject(map);
                string = jsonObjectToString(jsonObject);
            }
            else if (object instanceof Temporal temporal) {
                string = temporal.toString();
            }
            else if (object instanceof Color color) {
                string = colorToString(color);
            }
            else if (object instanceof Record record) {
                string = recordToString(record);
            }
            else if (isArray(object)) {
                string = arrayToString((T[]) object);
            }
            else if (isPojoObject(object)) {
                string = pojoObjectToString(object);
            }
            else if (object.toString().startsWith(String.format("%s@", clasName))) {
                throw new SmartRuntimeException(String.format(
                        "You need to override default toString() method of the class: %s",
                        object.getClass().getName()));
            }
            else {
                string = String.valueOf(object);
            }
            if (string == null) {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert object to string.
                        Object:
                        {}
                        """.stripIndent(), object));
            }
            log.debug("""
                            "Object converted to string.
                            Object:
                            {}
                            String:
                            {}
                            """.stripIndent(),
                    object, string);
            return normalizeLineSeparators(string);
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to string.
                    Object:
                    %s
                    """.stripIndent(),
                    object), e);
        }
    }

    /**
     * Converts string to enum value.
     * @param type enum type.
     * @param string The string - enum name or index.
     * Enum name is trimmed and converted to upper case.
     * @return The enum value.
     * @param <T> The enum type.
     */
    public static <T extends Enum<T>> T stringToEnumValue(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            T enumValue;
            Class<T> enumClass = (Class<T>) type.getObjectClass();
            string = string.trim();

            if (isNumberString(string)) {
                Number index = stringToNumber(string);
                T[] enumConstants = enumClass.getEnumConstants();
                enumValue = enumConstants[(int) index];
            }
            else {
                enumValue = Enum.valueOf(enumClass, string.toUpperCase());
            }
            log.debug("""
                    String converted to enum value by enum class name.
                    Enum class name:
                    {}
                    Enum name:
                    {}
                    Enum value: {}
                    """.stripIndent(),
                    enumClass.getName(),
                    string, enumValue);
            return enumValue;
        }
        catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert string to enum value.
                    String: %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts integer index to enum value.
     * @param type enum type.
     * @param index The index.
     * @return The enum value.
     * @param <T> The enum type.
     */
    public static <T extends Enum<T>> T indexToEnumValue(SmartType type, int index) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateMin(index, 0, "index");

        try {
            Class<?> enumClass = type.getObjectClass();
            T[] enumConstants = (T[]) enumClass.getEnumConstants();
            T enumValue = enumConstants[index];
            log.debug("""
                    Index converted to enum value by enum class name.
                    Enum class name:
                    {}
                    Index:
                    {}
                    Enum value: {}
                    """.stripIndent(),
                    enumClass.getName(),
                    index, enumValue);
            return enumValue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert index to enum value.
                    Index: %d
                    """.stripIndent(),
                    index), e);
        }
    }

    /**
     * Converts POJO class to map.
     * @param pojObject The POJO object.
     * @return the map.
     */
    public static Map<String,Object> pojoObjectToMap(Object pojObject) {
        DataValidationUtils.validateNotNull(pojObject, "pojObject");

        try {
            Map<String,Object> map = new HashMap<>();

            for (Field field : pojObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(pojObject);
                String fieldName = field.getName();

                map.put(fieldName, value);
            }
            log.debug("""
                    POJO object converted to map.
                    POJO:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    pojObject, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert POJO object to JSON.
                    POJO:
                    %s
                    """.stripIndent(),
                    pojObject), e);
        }
    }

    /**
     * Converts POJO class to JSON object.
     * @param pojObject The POJO object.
     * @return the JSON object.
     */
    public static JSONObject pojoObjectToJsonObject(Object pojObject) {
        DataValidationUtils.validateNotNull(pojObject, "pojObject");

        try {
            JSONObject jsonObject = new JSONObject();

            for (Field field : pojObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(pojObject);
                String fieldName = field.getName();

                if (isJsonTypeObject(value)) {

                    if (value == null) {
                        jsonObject.put(fieldName, JSONObject.NULL);
                    }
                    else {
                        jsonObject.put(fieldName, value);
                    }
                }
                else {
                    if (isPojoObject(value)) {
                        // Recursive call for nested POJO
                        jsonObject.put(fieldName, pojoObjectToJsonObject(value));
                    }
                    else {
                        String valueString = objectToString(value);
                        jsonObject.put(fieldName, valueString);
                    }
                }
            }
            log.debug("""
                POJO object converted to JSON object.
                POJO:
                {}
                JSON:
                {}
                """.stripIndent(),
                    pojObject, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert POJO object to JSON.
                    POJO:
                    %s
                    """.stripIndent(),
                    pojObject), e);
        }
    }

    /**
     * Converts object to object or throws exception if cannot covert.
     * @param targetType   The target object type.
     * @param sourceObject The object to convert.
     * @return The result object.
     */
    public static <T> T objectToObject(SmartType targetType, Object sourceObject) {
        DataValidationUtils.validateNotNull(targetType, "targetType");

        try {
            T targetObject;
            Class<?> targetObjectClass = targetType.getObjectClass();

            // Check if source object type and target object type are the same
            if (targetType.equals(SmartType.fromObject(sourceObject))) {
                targetObject = (T) sourceObject;
            }
            // Check base class of the source object is the same as target object type
            else if (sourceObject != null &&
                    ((sourceObject instanceof Number && targetObjectClass == Number.class) ||
                    (sourceObject instanceof Temporal && targetObjectClass == Temporal.class) ||
                    (sourceObject instanceof File && targetObjectClass == File.class) ||
                    (sourceObject instanceof Document && targetObjectClass == Document.class) ||
                    (sourceObject instanceof Node && targetObjectClass == Node.class) ||
                    (sourceObject instanceof Enum && targetObjectClass == Enum.class) ||
                    (sourceObject instanceof SmartValue && targetObjectClass == SmartValue.class) ||
                    (sourceObject instanceof SmartTemporal && targetObjectClass == SmartTemporal.class) ||
                    (sourceObject instanceof SmartObject && targetObjectClass == SmartObject.class) ||
                    (!targetType.isArray() && targetObjectClass == Object.class))) {
                targetObject = (T) sourceObject;
            }
            // Convert collection source object to target object
            else if (sourceObject instanceof Collection collection) {
                targetObject = (T) collectionToObject(targetType, collection);
            }
            // Convert map source object to target object
            else if (sourceObject instanceof Map map) {
                targetObject = (T) mapToObject(targetType, map);
            }
            // Convert record source object to target map
            else if (Map.class.isAssignableFrom(targetObjectClass) &&
                    sourceObject instanceof Record record) {
                targetObject = (T) recordToMap(record);
            }
            // Convert pojo to map
            else if (Map.class.isAssignableFrom(targetObjectClass) &&
                    isPojoObject(sourceObject)) {
                targetObject = (T) pojoObjectToMap(sourceObject);
            }
            else if (File.class.isAssignableFrom(targetObjectClass)) {
                targetObject = (T) objectToFile(sourceObject);
            }
            else if (Path.class.isAssignableFrom(targetObjectClass)) {
                targetObject = (T) objectToPath(sourceObject);
            }
            else if (URI.class == targetObjectClass) {
                targetObject = (T) objectToURI(sourceObject);
            }
            else if (URL.class == targetObjectClass) {
                targetObject = (T) objectToURL(sourceObject);
            }
            else {
                String stringValue = objectToString(sourceObject);
                targetObject = stringToObject(targetType, stringValue);
                log.debug("""
                        Source object converted to target object.
                        Source:
                        class: {}
                        value:
                        {}
                        Target:
                        class: {}
                        value:
                        {}
                        """.stripIndent(),
                        sourceObject != null ? sourceObject.getClass().getName() : null,
                        sourceObject,
                        targetObject.getClass().getName(),
                        targetObject);
            }
            return targetObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert source object to target object.
                            Source:
                            Class: %s
                            Source object:
                            %s
                            Target type:
                            %s
                            """.stripIndent(),
                    sourceObject != null ? sourceObject.getClass().getName() : null,
                    sourceObject, targetType));
        }
    }

    /**
     * Converts collection to target object by target smart type.
     * @param targetType The target smart type.
     * @param collection The collection.
     * @return The target object.
     * @param <T> The collection type.
     */
    public static <T> Object collectionToObject(SmartType targetType, Collection<T> collection) {
        DataValidationUtils.validateNotNull(targetType, "targetType");
        DataValidationUtils.validateNotNull(collection, "collection");

        try {
            Object targetobject;
            Class<?> objectClass = targetType.getObjectClass();
            SmartType collectionType = SmartType.fromObject(collection);
            SmartType valueType = targetType.getValueSmartType();
            T[] array = collectionToArray(valueType, collection);

            if (targetType.equals(collectionType)) {
                targetobject = collection;
            }
            else if (List.class.isAssignableFrom(objectClass)) {
                targetobject = arrayToList(array);
            }
            else if (Set.class.isAssignableFrom(objectClass)) {
                List<T> list = arrayToList(array);
                targetobject = new HashSet<>(list);
            }
            else if (Queue.class.isAssignableFrom(objectClass)) {
                List<T> list = arrayToList(array);
                targetobject = new LinkedList<>(list);
            }
            else if (Vector.class.isAssignableFrom(objectClass)) {
                List<T> list = arrayToList(array);
                targetobject = new Vector<>(list);
            }
            else  if (JSONArray.class.isAssignableFrom(objectClass)) {
                targetobject = arrayToJsonArray(array);
            }
            else if (targetType.isArray()) {
               targetobject = array;
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert collection to target object.
                    Collection class: %s
                    Collection:
                    %s
                    Target type:
                    %s
                    """.stripIndent(),
                        collection.getClass().getName(),
                        targetType));
            }
            log.debug("""
                    Collection converted to target object.
                    Collection class: {}
                    Collection :
                    {}
                    Target type:
                    {}
                    Object:
                    {}
                    """.stripIndent(),
                    collection.getClass().getName(),
                    collection, targetobject);
            return targetobject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert collection to target object.
                    Collection class: %s
                    Collection:
                    %s
                    Target type:
                    %s
                    """.stripIndent(),
                    collection.getClass().getName(),
                    collection, targetType), e);
        }
    }

    /**
     * Converts collection to target object by target smart type.
     * @param targetType The target smart type.
     * @param map The collection.
     * @return The target object.
     * @param <K> The map key type.
     * @param <V> The map value type.
     */
    public static <K,V> Object mapToObject(SmartType targetType, Map<K,V> map) {
        DataValidationUtils.validateNotNull(targetType, "targetType");
        DataValidationUtils.validateNotNull(map, "collection");

        try {
            Class<?> targetObjectClass = targetType.getObjectClass();
            Object targetObject;

            if (Map.class.isAssignableFrom(targetObjectClass)) {
                Class<?> targetKeyClass = targetType.getKeyClass();
                SmartType targetKeyType = SmartType.fromClass(targetKeyClass);
                SmartType targetValueType = targetType.getValueSmartType();
                Map<Object,Object> targetMap = (Map<Object,Object>)
                        targetObjectClass.getDeclaredConstructor().newInstance();

                for (K key : map.keySet()) {
                    V value = map.get(key);
                    Object targetKey = objectToObject(targetKeyType, key);
                    Object targetValue = objectToObject(targetValueType, value);

                    targetMap.put(targetKey, targetValue);
                }
                targetObject = targetMap;
            }
            else if (JSONObject.class.isAssignableFrom(targetObjectClass)) {
                targetObject = mapToJasonObject(map);
            }
            else if (Document.class.isAssignableFrom(targetObjectClass)) {
                targetObject = mapToXmlDocument(map);
            }
            else if (Node.class.isAssignableFrom(targetObjectClass)) {
                targetObject = mapToXmlNode(map);
            }
            else if (SmartType.isPojoClass(targetObjectClass)) {
                JSONObject jsonObject = mapToJasonObject(map);
                String jsonString = jsonObjectToString(jsonObject);
                targetObject = stringToPojoObject(targetType, jsonString);
            }
            else if (targetObjectClass == String.class) {
                JSONObject jsonObject = mapToJasonObject(map);
                targetObject = jsonObjectToString(jsonObject);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert map to target object.
                    Map class: %s
                    Map:
                    %s
                    Target type:
                    %s
                    """.stripIndent(),
                        map.getClass().getName(),
                        map, targetType));
            }
            log.debug("""
                    Collection converted to target object.
                    Collection class: {}
                    Collection :
                    {}
                    Target type:
                    {}
                    Object:
                    {}
                    """.stripIndent(),
                    map.getClass().getName(),
                    map, targetObject);
            return targetObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert map to target object.
                    Map class: %s
                    Map:
                    %s
                    Target type:
                    %s
                    """.stripIndent(),
                    map.getClass().getName(),
                    map, targetType), e);
        }
    }

    /**
     * Converts collection to array by value smart type.
     * @param valueType The value smart type.
     * @param collection The collection.
     * @return The array.
     * @param <T> The array type.
     */
    public static <T> T[] collectionToArray(SmartType valueType, Collection<?> collection) {
        DataValidationUtils.validateNotNull(valueType, "valueType");
        DataValidationUtils.validateNotNull(collection, "collection");

        try {
            Class<?> valueClass = valueType.getObjectClass();
            List<?> list = new ArrayList<>(collection);
            int rowsLength  = getCollectionRowsLength(collection);
            int columnsLength = getCollectionColumnsLength(collection);
            T[] array;
            int limit;

            if (rowsLength > 1) {
                array = (T[]) Array.newInstance(valueClass, rowsLength, columnsLength);
                limit = rowsLength;
            }
            else {
                array = (T[]) Array.newInstance(valueClass, columnsLength);
                limit = columnsLength;
            }
            for (int i = 0; i < limit; i++) {
                Object element = list.get(i);

                if (element instanceof JSONArray elemntJsonArray) {
                    array[i] = (T) jsonArrayToArray(elemntJsonArray);
                }
                else if (element instanceof JSONObject elementJsonObject) {
                    array[i] = (T) objectToMap(elementJsonObject);
                }
                else if (element == null) {
                    array[i] = null;
                }
                else if (element.getClass().isArray()) {
                    List<?> subList = objectToList(element);
                    Object[] subArray = collectionToArray(valueType, subList);
                    array[i] = (T) subArray;
                }
                else if (element instanceof Collection subCollection) {
                    array[i] = (T) collectionToArray(valueType, subCollection);
                }
                else {
                    array[i] = objectToObject(valueType, element);
                }
            }
            log.debug("""
                    Collection converted to target array.
                    Collection class: {}
                    Collection :
                    {}
                    Target type:
                    {}
                    Object:
                    {}
                    """.stripIndent(),
                    collection.getClass().getName(),
                    collection, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                        Cannot convert collection to target object.
                        Collection class: %s
                        Collection:
                        %s
                        Target type:
                        %s
                        """.stripIndent(),
                collection.getClass().getName(),
                collection, valueType), e);
        }
    }

    /**
     * Converts Java POJO class object to string.
     * @param object The object.
     * @param <T> The object type.
     * @return The string.
     */
    public static <T> String pojoObjectToString(T object) {
        try {
            JSONObject jsonObject = pojoObjectToJsonObject(object);
            String string = jsonObjectToString(jsonObject);
            log.debug("""
                            Java POJO class object  converted to string.
                            Object:
                            {}
                            String:
                            {}
                            """.stripIndent(),
                    string, object);
            return string;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert class object to string.
                            Object:
                            %s
                            """.stripIndent(),
                    object), e);
        }
    }

    /**
     * Coverts string to Java POJO class object.
     * @param string The string.
     * @param type   The class type.
     * @return The Java POJO class object.
     */
    public static <T> T stringToPojoObject(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");
        String className = type.getObjectClass().getName();

        try {
            JSONObject jsonObject;

            if (isXmlNodeString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else if (isJsonObjectString(string)) {
                jsonObject = new JSONObject(string);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert string to Java POJO class object.
                    Invalid string format - neither JSON object or XML.
                    String:
                    %s
                    POJO class: %s
                    """.stripIndent(),
                    string, className));
            }

            if (jsonObject.isEmpty()) {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert string to Java POJO class object.
                    No data to initialize POJO object.
                    String:
                    %s
                    POJO class: %s
                    """.stripIndent(),
                    string, className));
            }
            // Load the class by name
            Class<?> customClass = Class.forName(className);
            // Create an instance of the class using the default constructor
            Object object = customClass.getDeclaredConstructor().newInstance();
            Map<String, SmartType> fieldTypesMap = type.getFieldTypesMap();
            Field[] fields = customClass.getDeclaredFields();

            // Iterate over the fields of the class
            for (Field field : fields) {
                // Make private fields accessible
                field.setAccessible(true);
                String fieldName = field.getName();

                // Set the field value if the JSON has the key
                if (jsonObject.has(fieldName)) {
                    Object jsonValue = jsonObject.get(fieldName);
                    SmartType fieldType = fieldTypesMap.get(fieldName);
                    Object targetObject = objectToObject(fieldType, jsonValue);
                    field.set(object, targetObject);
                }
                else {
                    String firstKey;
                    Object firstValue;
                    Iterator<String> keys = jsonObject.keys();

                    if (keys.hasNext()) {
                        firstKey = keys.next();
                        firstValue = jsonObject.get(firstKey);

                        if (firstValue instanceof JSONObject childJson &&
                                childJson.has(fieldName)) {
                            Object targetObject = childJson.get(fieldName);
                            field.set(object, targetObject);
                        }
                        else {
                            throw new SmartRuntimeException(String.format("""
                                Cannot convert JSON object to POJO object.
                                Field name is not present in JSON object.
                                Field name: %s
                                JSON:
                                %s
                                POJO class:
                                """.stripIndent(),
                                fieldName, jsonObject,
                                className));
                        }
                    }
                }
            }
            log.debug("""
                    String converted to Java POJO class object.
                    String:
                    {}
                    POJO class: {}
                    Object:
                    {}
                    """.stripIndent(),
                    string,
                    className,
                    object);
            return (T) object;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to Java POJO class object.
                    String:
                    %s
                    POJO class: %s
                    """.stripIndent(),
                    string, className), e);
        }
    }

    /**
     * Converts CSV string to object.
     * @param csvString The CSV string.
     * @return The object.
     */
    public static Object csvStringToObject(String csvString) {
        DataValidationUtils.validateNotNull(csvString, "csvString");

        try {
            JSONArray jsonArray = csvStringToJsonArray(csvString);
            SmartValue smartValue = new SmartValue(jsonArrayToString(jsonArray));
            log.debug("""
                            CVS string converted to object.
                            CVS:
                            {}
                            Smart value:
                            {}
                            """.stripIndent(),
                    csvString, smartValue);
            return smartValue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to CSV object.
                            String:
                            %s
                            """.stripIndent(),
                    csvString), e);
        }
    }

    /**
     * Converts string to smart value.
     * @param type   The target object type.
     * @param string The string.
     * @return The smart value.
     */
    public static SmartValue stringToSmartValue(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotNull(string, "string");

        try {
            Object object = stringToObject(type, string);
            SmartValue smartValue = new SmartValue(object);
            log.debug("""
                            String converted to smart value.
                            String:
                            {}
                            Smart value:
                            {}
                            """.stripIndent(),
                    string, smartValue);
            return smartValue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to smart value.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts object to CSV string.
     * @param object The string.
     * @return The smart value.
     */
    public static String objectToCsvString(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            JSONArray jsonRows = objectToJsonArray(object);
            StringBuffer stringBuffer = new StringBuffer();
            int rowsSize = jsonRows.length();
            int columnsSize = 0;

            for (int i = 0; i < rowsSize; i++) {

                if (jsonRows.get(i) instanceof JSONArray jsonColumns) {

                    if (columnsSize == 0) {
                        columnsSize = jsonColumns.length();
                    }
                    if (jsonRows.getJSONArray(0).length() != columnsSize) {
                        throw new SmartRuntimeException(String.format("""
                                        Object rows have different size.
                                        Objet:
                                        %s
                                        """.stripIndent(),
                                object,
                                jsonRows));
                    }
                    for (int j = 0; j < columnsSize; j++) {
                        if (j > 0) {
                            stringBuffer.append(",");
                        }
                        Object jsonValue = jsonColumns.get(j);

                        if (isCsvNoneStringObject(jsonValue)) {
                            stringBuffer.append(jsonValue);
                        }
                        else {
                            String callValue = objectToString(jsonValue);
                            stringBuffer.append(String.format("%s", escapeCSVField(callValue)));
                        }
                    }
                    stringBuffer.append("\n");
                }
                else {
                    if (i > 0) {
                        stringBuffer.append(",");
                    }
                    String cellValue = jsonRows.getString(i);
                    stringBuffer.append(String.format("\"%s\"", escapeCSVField(cellValue)));
                }
            }
            log.debug("""
                            Object converted to CSV string.
                            Object:
                            {}
                            CSV string:
                            {}
                            """.stripIndent(),
                    object, stringBuffer);
            return stringBuffer.toString();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert object to CSV string.
                            Object:
                            %s
                            """.stripIndent(),
                    object), e);
        }
    }

    /**
     * Converts string to object by object smart type.
     * @param type   The type name.
     * @param string The string.
     * @return The object.
     */
    public static <T> T stringToObject(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "value");

        try {
            T object;
            Class<?> objectClass = type.getObjectClass();

            if (objectClass == SmartValue.class) {
                object = (T) stringToSmartValue(type, string);
            }
            else if (objectClass == String.class) {
                object = (T) string;
            }
            else if (objectClass == StringBuffer.class) {
                object = (T) stringToStringBuffer(string);
            }
            else if (objectClass == Character.class) {
                object = (T)(Character) stringToCharacter(string);
            }
            else if (objectClass == Short.class) {
                BigDecimal bigDecimal = new BigDecimal(string);
                object = (T)(Short) bigDecimal.shortValue();
            }
            else if (objectClass == Integer.class) {
                BigDecimal bigDecimal = new BigDecimal(string);
                object = (T)(Integer) bigDecimal.intValue();
            }
            else if (objectClass == Long.class) {
                BigDecimal bigDecimal = new BigDecimal(string);
                object = (T)(Long) bigDecimal.longValue();
            }
            else if (objectClass == BigInteger.class) {
                BigDecimal bigDecimal = new BigDecimal(string);
                object = (T) bigDecimal.toBigInteger();
            }
            else if (objectClass == Float.class) {
                object = (T) stringToFloat(string);
            }
            else if (objectClass == Double.class) {
                object = (T) stringToDouble(string);
            }
            else if (objectClass == BigDecimal.class) {
                object = (T) new BigDecimal(string);
            }
            else if (objectClass == Boolean.class) {
                object = (T)(Boolean) ConvertUtils.stringToBoolean(string);
            }
            else if (objectClass == LocalDate.class) {
                object = (T) stringToSmartLocalDate(string).getLocalDate();
            }
            else if (objectClass == LocalDateTime.class) {
                object = (T) stringToSmartLocalDateTime(string).getLocalDateTime();
            }
            else if (objectClass == LocalTime.class) {
                object = (T) stringToSmartLocalTime(string).getLocalTime();
            }
            else if (objectClass == SmartDate.class) {
                object = (T) stringToSmartDate(string);
            }
            else if (objectClass == SmartLocalDate.class) {
                // Convert with smart date to exclude time values
                SmartDate smartDate = stringToSmartDate(string);
                object = (T)  smartDate.toSmartLocalDate();
            }
            else if (objectClass == SmartLocalDateTime.class) {
                object = (T) stringToSmartLocalDateTime(string);
            }
            else if (objectClass == SmartLocalTime.class) {
                // Convert with smart date to exclude date values
                SmartDate smartDate = stringToSmartDate(string);
                object = (T) smartDate.toSmartLocalTime();
            }
            else if (objectClass == SmartNumber.class) {
                object = (T) SmartNumber.fromString(string);
            }
            else if (objectClass == SmartCurrency.class) {
                object = (T) SmartCurrency.fromString(string);
            }
            else if (objectClass == SmartTelephoneNumber.class) {
                object = (T) SmartTelephoneNumber.fromString(string);
            }
            else if (objectClass == File.class) {
                object = (T) stringToFile(string);
            }
            else if (objectClass == java.net.URL.class) {
                object = (T) stringToURL(string);
            }
            else if (objectClass == java.net.URI.class) {
                object = (T) stringToURI(string);
            }
            else {
                if (type.isArray()) {
                    object = (T) ConvertUtils.stringToArray(type.getValueSmartType(), string);
                }
                else if (List.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToList(type.getValueSmartType(), string);
                }
                else if (Set.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToSet(type.getValueSmartType(), string);
                }
                else if (Queue.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToQueue(type.getValueSmartType(), string);
                }
                else if (Vector.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToVector(type.getValueSmartType(), string);
                }
                else if (Map.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToMap(type, string);
                }
                else if (JSONObject.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToJsonObject(string);
                }
                else if (JSONArray.class.isAssignableFrom(objectClass)) {
                    object = (T) stringToJsonArray(string);
                }
                else if (Document.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToXmlDocument(string);
                }
                else if (Node.class.isAssignableFrom(objectClass)) {
                    object = (T) ConvertUtils.stringToXmlNode(string);
                }
                else if (Path.class.isAssignableFrom(objectClass)) {
                    object = (T) stringToPath(string);
                }
                else if (Date.class.isAssignableFrom(objectClass)) {
                    object = (T) SmartDate.fromString(string).toDate();
                }
                else if (Color.class.isAssignableFrom(objectClass)) {
                    object = (T) stringToColor(string);
                }
                else if (objectClass.isEnum()) {
                    object = (T) stringToEnumValue(type, string);
                }
                else if (objectClass.isRecord()) {
                    object = stringToRecord(objectClass, string);
                }
                // For POJO class
                else if (SmartType.isPojoClass(objectClass)) {
                    object = stringToPojoObject(type, string);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                            Cannot convert string to object.
                            String:
                            %s
                            Target type:
                            %s
                            """.stripIndent(),
                            string, type));
                }
            }
            DataValidationUtils.validateNotNull(object, "object");
            log.debug("""
                    String converted to object.
                    String:
                    {}
                    Target type:
                    {}
                    Object:
                    {}
                    """.stripIndent(),
                    string, type, object);
            return object;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to object.
                            String:
                            %s
                            Target type:
                            %s
                            """.stripIndent(),
                    string, type), e);
        }
    }

    /**
     * Converts string to string buffer.
     * @param string The string.
     * @return The string buffer.
     */
    public static StringBuffer stringToStringBuffer(String string) {
        DataValidationUtils.validateNotEmpty(string, "string");
        return new StringBuffer(string);
    }

    /**
     * Converts CVS string to JSON array.
     * @param csvString The string.
     * @return The string buffer.
     */
    public static JSONArray csvStringToJsonArray(String csvString) {
        DataValidationUtils.validateNotEmpty(csvString, "csvString");

        try {
            JSONArray jsonArray = new JSONArray();
            String[] lines = TextUtils.splitMultilineString(csvString);

            if (lines.length < 2) {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert CSV string to JSON array.
                        CSV string should have at least two rows
                        with equal number of columns.
                        CSV string:
                        {}
                        """.stripIndent(),
                        csvString));
            }
            int columnsSize = 0;

            // Iterate over the rest of the lines
            for (String line : lines) {

                // Skip empty or blank line
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> fieldValues = parseCsvRowString(line);

                if (columnsSize == 0) {
                    columnsSize = fieldValues.size();

                    if (columnsSize < 2) {
                        throw new SmartRuntimeException(String.format("""
                                    Cannot convert CSV string to JSON array.
                                    CSV line does not have a delimiter.
                                    CSV line:
                                    {}
                                    """.stripIndent(),
                                line));
                    }
                }
                if (fieldValues.size() != columnsSize) {
                    throw new SmartRuntimeException(String.format("""
                                    Cannot convert CSV string to JSON array.
                                    CSV rows have different number of columns.
                                    CSV:
                                    {}
                                    """.stripIndent(),
                            csvString));
                }
                JSONArray rowJsonArray = new JSONArray();

                for (String fieldValue : fieldValues) {
                    fieldValue = fieldValue.trim();

                    if (isNumberString(fieldValue)) {
                        Number number = stringToNumber(fieldValue);
                        rowJsonArray.put(number);
                    }
                    else if (isBooleanString(fieldValue)) {
                        boolean bool = stringToBoolean(fieldValue);
                        rowJsonArray.put(bool);
                    }
                    else if (fieldValue.equals("null")) {
                        rowJsonArray.put(JSONObject.NULL);
                    }
                    else {
                        // Replace escaped come placeholder back escaped coma.
                        String string = csvFieldValueToString(fieldValue);
                        rowJsonArray.put(string);
                    }
                }
                jsonArray.put(rowJsonArray);
            }
            log.debug("""
                    CSV string converted to JSON array.
                    CSV string:
                    {}
                    JSON array:
                    {}
                    """.stripIndent(),
                    csvString, jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert CSV string to JSON array.
                            CSV string:
                            %s
                            """.stripIndent(),
                    csvString), e);
        }
    }

    /**
     * Converts string to char.
     * @param charString The char string.
     * @return The char.
     */
    public static char stringToCharacter(String charString) {
        DataValidationUtils.validateNotEmpty(charString, "charString");
        DataValidationUtils.validateMax(charString.length(), 1, "charString");
        return charString.charAt(0);
    }

    /**
     * Converts collection to JSON array.
     * @param collection The collection.
     * @return The JSON array.
     */
    public static <T> JSONArray collectionToJsonArray(Collection<T> collection) {
        DataValidationUtils.validateNotNull(collection, "collection");

        try {
            JSONArray jsonArray = new JSONArray(collection);
            log.debug("""
                    Collection converted to JSON array.
                    Collection:
                    {}
                    JSON array:
                    {}
                    """.stripIndent(),
                    collection, jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert collection to JSON array:\n'%s'", collection));
        }
    }

    /**
     * Converts map to JSON object.
     * @param map The map.
     * @return The JSON object.
     */
    public static <K, V> JSONObject mapToJasonObject(Map<K, V> map) {
        DataValidationUtils.validateNotNull(map, "map");

        try {
            JSONObject jsonObject = new JSONObject(map);
            log.debug("""
                    Map converted to JSON object.
                    Map:
                    {}
                    JSON:
                    {}
                    """.stripIndent(),
                    map, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert map to JSON object:\n'%s'", map));
        }
    }

    /**
     * Converts map to XML document
     * @param map The map.
     * @return The XML document.
     */
    public static <K,V> Document mapToXmlDocument(Map<K,V> map) {
        DataValidationUtils.validateNotNull(map, "map");

        try {
            String rootName = getParameterName(0);
            JSONObject jsonObject = mapToJasonObject(map);
            Document xml = jsonObjectToXmlDocument(jsonObject);
            Document document = updateXmlRootName(xml, rootName);
            log.debug("""
                    Map converted to XML document.
                    Map:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    map, document);
            return document;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert map to XML document:\n'%s'", map));
        }
    }

    /**
     * Converts map to XML node.
     * @param map The map.
     * @return The XML node.
     */
    public static <K,V> Node mapToXmlNode(Map<K,V> map) {
        DataValidationUtils.validateNotNull(map, "map");

        String rootName = getParameterName(0);
        Document xml = mapToXmlDocument(map);
        Document document = updateXmlRootName(xml, rootName);
        Node xmlNode = document.getDocumentElement();
        log.debug("""
                    Map converted to XML node.
                    Map:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                map, xmlNode);
        return xmlNode;
    }

    /**
     * Converts record to XML document
     * @param record The record.
     * @return The XML document.
     */
    public static Document recordToXmlDocument(Record record) {
        DataValidationUtils.validateNotNull(record, "record");

        try {
            String rootName = getParameterName(0);
            JSONObject jsonObject = recordToJsonObject(record);
            Document xml = jsonObjectToXmlDocument(jsonObject);
            Document document = updateXmlRootName(xml, rootName);
            log.debug("""
                    Record converted to XML document.
                    Record:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    record, document);
            return document;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert record to XML document:\n'%s'", record));
        }
    }

    /**
     * Converts record to XML node.
     * @param record The record.
     * @return The XML node.
     */
    public static Node recordToXmlNode(Record record) {
        DataValidationUtils.validateNotNull(record, "record");

        String rootName = getParameterName(0);
        Document xml = recordToXmlDocument(record);
        Document document = updateXmlRootName(xml, rootName);
        Node xmlNode = document.getDocumentElement();
        log.debug("""
                Record converted to XML node.
                Record:
                {}
                XML:
                {}
                """.stripIndent(),
                record, xmlNode);
        return xmlNode;
    }

    /**
     * Converts POJO object to XML document
     * @param pojo The pojo object.
     * @return The XML document.
     */
    public static Document pojoObjectToXmlDocument(Object pojo) {
        DataValidationUtils.validateNotNull(pojo, "pojo");

        try {
            String rootName = getParameterName(0);
            JSONObject jsonObject = pojoObjectToJsonObject(pojo);
            Document xml = jsonObjectToXmlDocument(jsonObject);
            Document document = updateXmlRootName(xml, rootName);
            log.debug("""
                    POJO object converted to XML document.
                    POJO:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    pojo, document);
            return document;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert POJO object to XML document:\n'%s'", pojo));
        }
    }

    /**
     * Converts POJO object to XML node.
     * @param pojo The pojo.
     * @return The XML node.
     */
    public static Node pojoObjectToXmlNode(Object pojo) {
        DataValidationUtils.validateNotNull(pojo, "pojo");

        String rootName = getParameterName(0);
        JSONObject jsonObject = pojoObjectToJsonObject(pojo);
        Document xml = jsonObjectToXmlDocument(jsonObject);
        Document document = updateXmlRootName(xml, rootName);
        Node xmlNode = document.getDocumentElement();
        log.debug("""
                Record converted to XML node.
                Record:
                {}
                XML:
                {}
                """.stripIndent(),
                pojo, xmlNode);
        return xmlNode;
    }

    /**
     * Converts XML array to array.
     * @param xmlNode The XML array node.
     * @param <T> The array type.
     * @return The array.
     */
    public static <T> T[] xmlArrayNodeToArray(Node xmlNode) {
        DataValidationUtils.validateNotNull(xmlNode, "xmlNode");

        try {
            String xmlString = xmlNodeToString(xmlNode);
            JSONArray jsonArray = xmlStringToJsonArray(xmlString);
            T[] array;

            if (jsonArray.length() == 0) {
                // Crete an empty array for empty JSON array
                array = (T[]) Array.newInstance(Object.class, 0);
            }
            else {
                array = jsonArrayToArray(jsonArray);
            }
            log.debug("""
                    XML array node converted to array.
                    XML array:
                    {}
                    Array:
                    {}
                    """.stripIndent(),
                    xmlNode, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert XML array node to array.
                    XML array:
                    %s
                    """.stripIndent(),
                    xmlNode), e);
        }
    }

    /**
     * Converts JSON array to array.
     * @param jsonArray The JSON array.
     * @param <T>       The array type.
     * @return The array.
     */
    public static <T> T[] jsonArrayToArray(JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            SmartType valueType = SmartType.getJsonArrayValueSmartType(jsonArray);

            if (JSONObject.class.isAssignableFrom(valueType.getObjectClass())) {
                SmartType mapValueType = SmartType.getJsonObjectValueSmartType(jsonArray.getJSONObject(0));
                valueType = SmartType.fromMapClass(HashMap.class, String.class, mapValueType);
            }

            Class<?> valueClass = valueType.getObjectClass();
            int rowsLength  = getJsonRowsLength(jsonArray);
            int columnsLength = getJsonArrayColumnsLength(jsonArray);
            T[] array;
            int limit;

            if (rowsLength > 1) {
                array = (T[]) Array.newInstance(valueClass, rowsLength, columnsLength);
                limit = rowsLength;
            }
            else {
                array = (T[]) Array.newInstance(valueClass, columnsLength);
                limit = columnsLength;
            }
            for (int i = 0; i < limit; i++) {
                Object element = jsonArray.get(i);

                if (element == null || element == JSONObject.NULL) {
                    array[i] = null;
                }
                else if (element instanceof JSONArray elemntJsonArray) {
                    Object[] subArray = jsonArrayToArray(elemntJsonArray);
                    array[i] = (T) subArray;
                }
                else if (element instanceof JSONObject elementJsonObject) {
                    array[i] = (T) objectToMap(elementJsonObject);
                }
                else if (element == JSONObject.NULL) {
                    array[i] = null;
                }
                else if (element.getClass().isArray()) {
                    array[i] = (T) element;
                }
                else if (element instanceof Collection collection) {
                    array[i] = (T) collection.toArray();
                }
                else {
                    array[i] = (T) element;
                }
            }
            log.debug("""
                    JSON array converted to array.
                    JSON array:
                    {}
                    Array:
                    {}
                    """.stripIndent(),
                    jsonArray, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON array to array.
                    JSON array:
                    %s
                    """.stripIndent(),
                    jsonArray), e);
        }
    }

    /**
     * Converts array to JSON array.
     * @param array The array.
     * @param <T>   The array type.
     * @return The JSON array.
     */
    public static <T> JSONArray arrayToJsonArray(T[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            JSONArray jsonArray = new JSONArray();

            for (T element : array) {

                if (element == null) {
                    jsonArray.put(element);
                }
                else if (element instanceof String string) {
                    if (isXmlNodeString(string)) {
                        JSONObject xmlJson = xmlStringToJsonObject(string);
                        jsonArray.put(xmlJson);
                    }
                    else if (isJsonObjectString(string)) {
                        JSONObject jsObject = stringToJsonObject(string);
                        jsonArray.put(jsObject);
                    }
                    else {
                        jsonArray.put(string);
                    }
                }
                else if (isJsonValue(element)) {
                    jsonArray.put(element);
                }
                else if (element instanceof Record record) {
                    JSONObject recordJson = recordToJsonObject(record);
                    jsonArray.put(recordJson);
                }
                else if (isPojoObject(element)) {
                    JSONObject pojoJson = pojoObjectToJsonObject(element);
                    jsonArray.put(pojoJson);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                            Cannot convert array element to JSON array element.
                            Array element:
                            %s
                            """.stripIndent(),
                            element));
                }
            }
            log.debug("""
                    Array converted to JSON array.
                    Array:
                    {}
                    JSON array:
                    {}
                    """.stripIndent(),
                    array,
                    jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert array to JSON array.
                            Array:
                            %s
                            """.stripIndent(),
                            array), e);
        }
    }

    /**
     * Converts object to JSON array.
     * @param object The object.
     * @return The JSON array.
     * @param <T> The collection element type.
     */
    public static <T> JSONArray objectToJsonArray(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        JSONArray jsonArray;

        try {
            if (object.getClass() == JSONArray.class) {
                jsonArray = (JSONArray) object;
            }
            else if (object instanceof Collection collection) {
                jsonArray = collectionToJsonArray(collection);
            }
            else if (isArray(object)) {
                jsonArray = arrayToJsonArray((T[]) object);
            }
            else if (object instanceof String string) {

                if (isJsonArrayString(string) || isXmlArrayString(string)) {
                    jsonArray = stringToJsonArray(string);
                }
                else if (isCsvString(string)) {
                    jsonArray = csvStringToJsonArray(string);
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Cannot convert string to JSON array: %s", string));
                }
            }
            else {
                throw new SmartRuntimeException(String.format("""
                                Cannot convert object to JSON array.
                                Object class: %s
                                Object:
                                %s
                                """.stripIndent(),
                                object.getClass().getName(),
                                object));
            }
            log.debug("""
                    Object is converted to JSON array.
                    Object class: {}
                    Object:
                    {}
                    JSON array:
                    {}
                    """.stripIndent(),
                    object.getClass().getName(),
                    object, jsonArray);
            return jsonArray;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                                Cannot convert object to JSON array.
                                Object class: %s
                                Object:
                                %s
                                """.stripIndent(),
                    object.getClass().getName(),
                    object), e);
        }
    }

    /**
     * Converts object to JSON object.
     * @param object The object.
     * @return The JSON array.
     */
    public static JSONObject objectToJsonObject(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        JSONObject jsonObject;

        try {
            if (object.getClass() == JSONObject.class) {
                jsonObject = (JSONObject) object;
            }
            else if (object instanceof Map map) {
               jsonObject = mapToJasonObject(map);
            }
            else if (object instanceof Node xmlNode) {
                jsonObject = xmlNodeToJsonObject(xmlNode);
            }
            else if (object instanceof String string) {
                jsonObject = stringToJsonObject(string);
            }
            else if (object instanceof Record record) {
                jsonObject = recordToJsonObject(record);
            }
            else if (isPojoObject(object)) {
                jsonObject = pojoObjectToJsonObject(object);
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert object to JSON array:\n%s", object));
            }
            log.debug("Object is converted to JSON array:\n{}", jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to JSON array:\n%s", object), e);
        }
    }

    /**
     * Converts object to XML node object.
     * @param object The object.
     * @return The XML node object.
     */
    public static Node objectToXmlNode(Object object) {
        DataValidationUtils.validateNotNull(object, "object");
        Node xmlNode;

        try {
            String rootName = getParameterName(0);

            if (object instanceof Node node) {
                xmlNode = node;
            }
            else if (object instanceof String string) {

                if (isXmlNodeString(string)) {
                    xmlNode = stringToXmlNode(string);
                }
                else if (isJsonObjectString(string)) {
                    xmlNode = stringToXmlNode(string);
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Cannot convert string to XML node:\n%s", string));
                }
            }
            else if (object instanceof Map map) {
                xmlNode = mapToXmlNode(map);
            }
            else if (object instanceof JSONObject jsonObject) {
                xmlNode = jsonObjectToXmlNode(jsonObject);
            }
            else if (object instanceof JSONArray jsonArray) {
                xmlNode = jsonArrayToXmlNode(jsonArray);
            }
            else if (object instanceof Record record) {
                xmlNode = recordToXmlNode(record);
            }
            else if (isPojoObject(object)) {
                xmlNode = pojoObjectToXmlNode(object);
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert %s type to XML node",
                        object.getClass().getName()));
            }
            if (!(object instanceof String) && !(object instanceof Node)) {
                Document document = xmlNodeToXmlDocument(xmlNode);
                xmlNode = updateXmlRootName(document, rootName).getDocumentElement();
            }
            log.debug("Object is converted to XML node:\n{}", xmlNode.toString());
            return xmlNode;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to XML node:\n%s", object), e);
        }
    }

    /**
     * Converts an XML Node to XML document.
     * @param node The XML Node.
     * @return The XML document containing the given node as the root.
     */
    public static Document xmlNodeToXmlDocument(Node node) {
        DataValidationUtils.validateNotNull(node, "node");

        try {
            // Create a new DocumentBuilder
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // Create a new Document
            Document document = documentBuilder.newDocument();

            // Import the node into the new Document
            Node importedNode = document.importNode(node, true);

            // Append the imported node as the root element
            document.appendChild(importedNode);
            log.debug("""
                    XML node converted to XML document.
                    Node:
                    {}
                    Document:
                    {}
                    """.stripIndent(),
                    node, document);
            return document;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert XML node to XML Document:\n%s", node));
        }
    }

    /**
     * Converts a string to a Java Record object.
     * @param recordClass The Record class type.
     * @param string      The string (in JSON or XML format).
     * @return The Java Record object.
     */
    public static <T> T stringToRecord(Class<?> recordClass, String string) {
        DataValidationUtils.validateNotNull(recordClass, "recordClass");
        DataValidationUtils.validateNotBlank(string, "string");

        if (!recordClass.isRecord()) {
            throw new SmartRuntimeException(String.format(
                    "Provided class is not a Record: %s", recordClass.getName()));
        }
        try {
            JSONObject jsonObject;

            if (isXmlNodeString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = new JSONObject(string);
            }
            T record = createRecordInstanceFromJson(recordClass, jsonObject);
            log.debug("""
                    String converted to record.
                    String:
                    {}
                    Record class: {}
                    Record:
                    {}
                    """.stripIndent(),
                    string, recordClass.getName(), record);
            return record;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to Java Record object.
                    String:
                    %s
                    Record class: %s
                    """.stripIndent(),
                    string, recordClass.getName()), e);
        }
    }

    /**
     * Converts record object to JSON object.
     * @param record The record.
     * @return The JSON object.
     */
    public static JSONObject recordToJsonObject(Record record) {
        DataValidationUtils.validateNotNull(record, "record");

        try {
            JSONObject jsonObject = new JSONObject();
            RecordComponent[] components = record.getClass().getRecordComponents();

            for (RecordComponent component : components) {
                String name = component.getName();
                Object value = component.getAccessor().invoke(record);
                jsonObject.put(name, value);
            }
            log.debug("""
                    Record is converted to JSON object.
                    Record class: {}
                    Record:
                    {}
                    JSON:
                    {}
                    """.stripIndent(),
                    record.getClass().getName(),
                    record, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert record to JSON object.
                    Record class: %s
                    Record:
                    %s
                    """.stripIndent(),
                    record.getClass().getName(),
                    record), e);
        }
    }

    /**
     * Converts record object to map.
     * @param record The record.
     * @return The map.
     */
    public static Map<String, Object> recordToMap(Record record) {
        DataValidationUtils.validateNotNull(record, "record");

        try {
            Map<String, Object> map = new HashMap<>();
            RecordComponent[] components = record.getClass().getRecordComponents();

            for (RecordComponent component : components) {
                String name = component.getName();
                Object value = component.getAccessor().invoke(record);
                map.put(name, value);
            }
            log.debug("""
                    Record is converted to map.
                    Record class: {}
                    Record:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    record.getClass().getName(),
                    record, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert record to map.
                    Record class: %s
                    Record:
                    %s
                    """.stripIndent(),
                    record.getClass().getName(),
                    record), e);
        }
    }

    /**
     * Converts record object to string.
     * @param record The record object.
     * @return The string.
     */
    public static String recordToString(Record record) {
        DataValidationUtils.validateNotNull(record, "record");

        try {
            JSONObject jsonObject = recordToJsonObject(record);
            String string = jsonObjectToString(jsonObject);
            log.debug("""
                    Record is converted to string.
                    Record class: {}
                    Record:
                    {}
                    String:
                    {}
                    """.stripIndent(),
                    record.getClass().getName(),
                    record, string);
            return string;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert record to string.
                    Record class{ %s
                    Record:
                    %s
                    """.stripIndent(),
                    record.getClass().getName(), record), e);
        }
    }

    /**
     * Converts object to enum value
     * @param type The enum type.
     * @param object The object.
     * @param <T> The enum type.
     * @return The enum value..
     */
    public static <T extends Enum<T>> T objectToEnumValue(SmartType type, Object object) {
        DataValidationUtils.validateNotNull(type, "enumClassNme");
        DataValidationUtils.validateNotNull(object, "object");
        T enumValue;

        try {
            try {
                enumValue = (T) object;
            }
            catch (ClassCastException e) {

                if (object instanceof String string) {
                      enumValue = stringToEnumValue(type, string.toUpperCase());
                }
                else if (object instanceof Integer index) {
                    enumValue = indexToEnumValue(type, index);
                }
                else {
                    throw new SmartRuntimeException(String.format(
                            "Cannot convert object to enum value: %s", object));
                }
            }
            log.debug("Object is converted to enum value: {}", enumValue);
            return enumValue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert object to enum value: %s", object), e);
        }
    }

    /**
     * Converts array to string.
     * @param array The array.
     * @param <T>   The array type.
     * @return The array string.
     */
    public static <T> String arrayToString(T[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            JSONArray jsonArray = arrayToJsonArray(array);
            String arrayString = jsonArrayToString(jsonArray);
            log.debug("Array {} converted to string '{}'", array, arrayString);
            return arrayString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert array to string: %s", Arrays.toString(array)));
        }
    }

    /**
     * Converts string in JSON array format to array.
     * @param valueType The value type.
     * @param string    The array string.
     * @param <T>       The array type.
     * @return The array.
     */
    public static <T> T[] stringToArray(SmartType valueType, String string) {
        DataValidationUtils.validateNotNull(valueType, "valueType");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONArray jsonArray;

            if (isCsvString(string)) {
                jsonArray = csvStringToJsonArray(string);
            }
            else if (isJsonArrayString(string)) {
                jsonArray = stringToJsonArray(string);
            }
            else if (isXmlArrayString(string)) {
                jsonArray = xmlStringToJsonArray(string);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert string to array.
                    Invalid format - neither CSV or JSON.
                    String:
                    %s
                    """.stripIndent(),
                        string));
            }
            Class<?> valueClass = valueType.getObjectClass();

            // Normalize immutable collection and map classes
            valueClass = normalizeClass(valueClass);
            // Create array from value class
            T[] array = null;

            if (jsonArray.length() == 0) {
                array = (T[]) Array.newInstance(valueClass, 0);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.get(i);
                T element;

                if (object instanceof JSONArray subJsonArray) {
                    if (array == null) {
                        array = (T[]) Array.newInstance(Integer.class,
                                jsonArray.length(), subJsonArray.length());
                    }
                    element = (T) jsonArrayToArray(subJsonArray);
                }
                else {
                    if (array == null) {
                        array = (T[]) Array.newInstance(valueClass, jsonArray.length());
                    }
                    element = objectToObject(valueType, object);
                }
                array[i] = element;
            }
            log.debug("""
                    String converted to array.
                    String:
                    {}
                    Array:
                    {}
                    """.stripIndent(),
                    string, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to array.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON array format to list
     * or synchronized list.
     * @param valueType The value type.
     * @param string The string.
     * @param <T> The list element type.
     * @return The list.
     */
    public static <T> List<T> stringToList(SmartType valueType, String string) {
        DataValidationUtils.validateNotNull(valueType, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            List<T> list;
            T[] array = null;
            // Workaround for XML array converted to JSON object
            if (isJsonObjectString(string)) {
                JSONObject jsonObject = stringToJsonObject(string);
                // Check the JSON object contains only one element - JSON array
                // Get all keys from the JSONObject
                var keys = jsonObject.keys();

                // Check if there’s only one key and its value is a JSONArray
                if (keys.hasNext()) {
                    String key = keys.next();
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    array = jsonArrayToArray(jsonArray);
                }
                if (keys.hasNext()) {
                    throw new SmartRuntimeException(String.format("""
                            Cannot convert string to list. Invalid JSON format.
                            String:
                            %s
                            """.stripIndent(),
                            string));
                }
            }
            else {
                array = stringToArray(valueType, string);
            }
            list = arrayToList(array);
            log.debug("""
                    String converted to List.
                    String:
                    {}
                    List:
                    {}
                    """.stripIndent(),
                    string, list);
            return list;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to list.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON array format to set
     * or synchronized set.
     * @param valueType The value type.
     * @param string The string.
     * @param <T> The set element type.
     * @return The set.
     */
    public static <T> Set<T> stringToSet(SmartType valueType, String string) {
        DataValidationUtils.validateNotNull(valueType, "valueType");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            List<T> list = stringToList(valueType, string);
            Set<T> set = new HashSet<>(list);
            log.debug("""
                    String converted to set.
                    String:
                    {}
                    Set:
                    {}
                    """.stripIndent(),
                    string, set);
            return set;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to set.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON array format to queue
     * or synchronized queue.
     * @param valueType The value type.
     * @param string The string.
     * @param <T> The queue element type.
     * @return The queue.
     */
    public static <T> Queue<T> stringToQueue(SmartType valueType, String string) {
        DataValidationUtils.validateNotNull(valueType, "valueType");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            List<T> list = stringToList(valueType, string);
            Queue<T> queue = new LinkedList<>(list);
            log.debug("""
                    String converted to queue.
                    String:
                    {}
                    Queue:
                    {}
                    """.stripIndent(),
                    string, queue);
            return queue;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to queue.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON array format to vector.
     * @param valueType The value type.
     * @param string The string.
     * @param <T> The vector element type.
     * @return The vector.
     */
    public static <T> Vector<T> stringToVector(SmartType valueType, String string) {
        DataValidationUtils.validateNotNull(valueType, "valueType");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            List<T> list = stringToList(valueType, string);
            Vector<T> vector = new Vector<>(list);
            log.debug("""
                    String converted to vector.
                    String:
                    {}
                    Queue:
                    {}
                    """.stripIndent(),
                    string, vector);
            return vector;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to vector.
                    String:
                    %s
                    """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Converts string in JSON object format to map.
     * @param type      The target type.
     * @param string    The string.
     * @param <K>       The key type.
     * @param <V>       The value type.
     * @return The map.
     */
    public static <K,V> Map<K,V> stringToMap(SmartType type, String string) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotBlank(string, "string");

        try {
            JSONObject jsonObject;

            if (isXmlNodeString(string)) {
                jsonObject = xmlStringToJsonObject(string);
            }
            else {
                jsonObject = stringToJsonObject(string);
            }
            Map<K,V> map = jsonObjectToMap(type, jsonObject);

            if (ConcurrentMap.class.isAssignableFrom(type.getObjectClass())) {
                map = new ConcurrentHashMap<>(map);
            }
            log.debug("""
                    String converted to map.
                    String:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    string, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert string to map.
                            String:
                            %s
                            """.stripIndent(),
                    string), e);
        }
    }

    /**
     * Convers JSON object to map.
     * @param type The target type.
     * @param jsonObject The JSON object.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return The map.
     */
    public static <K,V> Map<K,V> jsonObjectToMap(SmartType type, JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(type, "type");
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            Class<K> keyClass = (Class<K>) type.getKeyClass();
            Class<V> valueClass = (Class<V>) type.getValueSmartType().getObjectClass();
            Map<K,V> map = createMapFromClasses(keyClass, valueClass);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String stringKey = keys.next();
                K key = objectToObject(SmartType.fromClass(type.getKeyClass()), stringKey);
                Object jsonValue = jsonObject.get(stringKey);
                SmartType elementType = SmartType.fromClass(valueClass);
                V value = objectToObject(elementType, jsonValue);

                map.put(key, value);
            }
            log.debug("""
                    JSON converted to map.
                    JSON:
                    {}
                    Target type:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    jsonObject, type, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert JSON object to map:\n%s", jsonObject), e);
        }
    }

    /**
     * Converts XML string to JSON object.
     * @param xmlString XML string.
     * @return The JSON object.
     */
    public static JSONObject xmlStringToJsonObject(String xmlString) {
        DataValidationUtils.validateNotBlank(xmlString, "xmlString");

        try {
            xmlString = escapeXmlString(xmlString);
            xmlString = unescapeXmlString(xmlString);
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            Iterator<String> keys = jsonObject.keys();

            // Check if the JSON objet has a root element and return its child JSON object.
            if (keys.hasNext()) {
                String rootKey = keys.next();
                jsonObject = jsonObject.getJSONObject(rootKey);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert XML string to JSON object.
                        XML string:
                        %s
                        """.stripIndent(),
                        xmlString));
            }
            log.debug("""
                    XML string converted to JSON object.
                    String:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    xmlString, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert XML string to JSON object.
                            XML string:
                            %s
                            """.stripIndent(),
                    xmlString), e);
        }
    }

    /**
     * Converts XML array string to JSON array.
     * @param xmlString XML string.
     * @return The JSON object.
     */
    public static JSONArray xmlStringToJsonArray(String xmlString) {
        DataValidationUtils.validateNotBlank(xmlString, "xmlString");

        try {
            if (ConvertUtils.isXmlArrayString(xmlString)) {
                Node xmlNode = stringToXmlNode(xmlString);
                JSONObject jsonObject = xmlNodeToJsonObject(xmlNode);
                String rootName = jsonObject.keys().next();
                JSONArray jsonArray;
                Object element = jsonObject.get(rootName);

                if (element instanceof String && ((String)element).isEmpty()) {
                    // Create empty json array for empty XML array string
                    jsonArray = new JSONArray();
                }
                else {
                    JSONObject itemsJson = jsonObject.getJSONObject(rootName);
                    String itemName = itemsJson.keys().next();
                    Object itemObject = itemsJson.get(itemName);

                    if (itemObject instanceof JSONArray) {
                        jsonArray = (JSONArray) itemObject;
                    }
                    else {
                        // Return an empty JSON array and add the only item
                        jsonArray = new JSONArray();
                        jsonArray.put(itemObject);
                    }
                }
                log.debug("""
                        XML array string converted to JSON array.
                        XML string:
                        {}
                        JSON array:
                        {}
                        """.stripIndent(),
                        xmlString, jsonArray);
                return jsonArray;
            }
            else {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert XML array string to JSON array.
                        XML string:
                        %s
                        """.stripIndent(),
                        xmlString));
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                        Cannot convert XML array string to JSON array.
                        XML string:
                        %s
                        """.stripIndent(),
                    xmlString), e);
        }
    }

    /**
     * Converts XML node to JSON object.
     * @param xmlNode XML string.
     * @return The JSON object.
     */
    public static JSONObject xmlNodeToJsonObject(Node xmlNode) {
        DataValidationUtils.validateNotNull(xmlNode, "xmlNode");

        try {
            String xmlString = xmlNodeToString(xmlNode);
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            log.debug("""
                    XML node converted to JSON object.
                    XML:
                    {}
                    JSON:
                    {}
                    """.stripIndent(),
                    xmlNode, jsonObject);
            return jsonObject;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert XML node to JSON object.
                            XML node:
                            %s
                            """.stripIndent(),
                    xmlNode), e);
        }
    }

    /**
     * Normalizes a number string by converting Arabic-Indic numerals and separators
     * by country - language lacale.
     * to Western equivalents to ensure proper parsing.
     * @param numberString The input number string.
     * @param locale The Locale
     * @return A normalized number string.
     */
    private static String normalizeNumberString(String numberString, ULocale locale) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");
        StringBuilder normalized = new StringBuilder();
        List<ULocale> arabicIndianLocales = Arrays.asList(ARABIC_INDIAN_LOCALES);
        String normalizedNumberString = numberString;

        if (arabicIndianLocales.contains(locale)) {

            for (char c : numberString.toCharArray()) {

                if (c >= '\u0660' && c <= '\u0669') {
                    // Arabic-Indic digits (٠١٢٣٤٥٦٧٨٩)
                    normalized.append((char) (c - '\u0660' + '0'));
                }
                else if (c >= '\u06F0' && c <= '\u06F9') {
                    // Persian digits (۰۱۲۳۴۵۶۷۸۹)
                    normalized.append((char) (c - '\u06F0' + '0'));
                }
                else if (c == '٫' || c == ',') {
                    // Arabic/Persian decimal separator
                    normalized.append('.');
                }
                else if (c == '٬' || c == ' ') {
                    // Arabic/Persian thousand separator (and non-breaking spaces)
                    normalized.append(',');
                }
                else {
                    // Append other characters as is
                    normalized.append(c);
                }
            }
            normalizedNumberString = normalized.toString();
            log.debug("""
                    Number string is normalized.
                    """.stripIndent(),
                    numberString,
                    normalizedNumberString);
        }
        // Ensure that minus signs are preserved during normalization
        if (numberString.startsWith("-") && !normalizedNumberString.startsWith("-")) {
            normalizedNumberString = "-" + normalizedNumberString;  // Restore the minus sign if it was stripped
        }
        log.debug("""
                Number string is normalized.
                Input: {}
                Output: {}
                """.stripIndent(),
                numberString,
                normalizedNumberString);
        return normalizedNumberString;
    }

    /**
     * Converts any language number string to a standardized Java number string
     * using Western digits and standard separators.
     * @param numberString The input number string in any language or format.
     * @return The standardized number string according to Java conventions.
     * @throws RuntimeException if the number cannot be parsed.
     */
    public static String localeNumberStringToJavaNumberString(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");
        String javaNumberString = null;
        numberString = numberString.trim();

        // Iterate over most common locales to parse the number
        for (ULocale locale : MOST_COMMON_LOCALES) {

            if (numberString.toLowerCase().matches(SCIENTIFIC_NUMBER_STRING_REGEX)) {
                // Return scientific format number string as is
                javaNumberString = numberString;
                break;
            }
            // Normalize the number string to replace localized digits and separators
            String normalizedString = normalizeNumberString(numberString, locale);

            try {
                // Parse the normalized string to a number
                Number number = numberStringToNumber(normalizedString, locale);
                // Validate parsed number
                validateParsedNumber(numberString, number, locale);
                // Convert number to Java number string
                javaNumberString = getJavaNumberStringFomNumber(number);
                break;
            }
            catch (Exception ignored) {
                // Continue to the next locale if parsing fails
            }
        }
        if (javaNumberString == null) {
            // Iterate all other available locales to parse the number
            ULocale[] availableLocales = ULocale.getAvailableLocales();
            List<ULocale> otherLocales = Arrays.asList(availableLocales);
            otherLocales.remove(MOST_COMMON_LOCALES);

            for (ULocale locale : otherLocales) {
                // Normalize the number string to replace localized digits and separators
                String normalizedString = normalizeNumberString(numberString, locale);

                try {
                    // Parse the normalized string to a number
                    Number number = numberStringToNumber(normalizedString, locale);
                    // Validate parsed number
                    validateParsedNumber(numberString, number, locale);
                    // Convert number to Java number string
                    javaNumberString = getJavaNumberStringFomNumber(number);
                    break;
                }
                catch (Exception ignored) {
                    // Continue to the next locale if parsing fails
                }
            }
        }
        // Replace thousands delimiters and replace coma decimal point with point
        // as work around for some countries like Lithuania and in scientific number format
        if (javaNumberString == null ||
            javaNumberString.toLowerCase().matches(SCIENTIFIC_NUMBER_STRING_REGEX)) {
            // Translate to English for some countries like Chana, Japan and Korea
            javaNumberString = languageNumberStringToJavaNumberString(numberString);
        }
        try {
            // Validate Java number string
            new BigDecimal(javaNumberString);
        }
        catch (NumberFormatException e) {
            // Do not throw SmartRuntimeException not to print error log
            throw new RuntimeException(String.format(
                    "Cannot convert locale number string to Java number string: %s",
                    numberString));
        }
        log.debug("""
                Locale number string converted to Java number string.
                Input: {}
                Output: {}
                """.stripIndent(),
                numberString,
                javaNumberString);
            return javaNumberString;
    }

    /**
     * Converts a number string from various languages to a standard Java number string.
     * @param numberString The number string in various languages
     * (e.g., "一百二十三万四千五百六十七点八九" or "일백이십삼만사천오백육십칠점팔구").
     * @return The numeric representation as a Java number string
     * (e.g., "1234567.89").
     */
    public static String languageNumberStringToJavaNumberString(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");
        String javaNumberString;

        // Check if the input is already an English number string
        if (numberString.matches("^[-\\d,\\.\\s]+([eE][-+]?\\d+)?$")) {
            // Remove thousand delimiters and return the number string as is
            javaNumberString = removeThousandDelimiters(numberString);
            // Replace coma decimal point with point
            javaNumberString = javaNumberString.replace(",", ".");
            log.debug("""
                    Language number string converted to Java string.
                    Language string: {}
                    Java string: {}
                    """.stripIndent(),
                    numberString,
                    javaNumberString);
            return javaNumberString;
        }

        double result = 0;
        long sectionValue = 0;
        long currentValue = 0;

        for (int i = 0; i < numberString.length(); i++) {
            String ch = String.valueOf(numberString.charAt(i));

            // Check if the character is a numeral
            if (NUMERAL_MAP.containsKey(ch)) {
                currentValue = NUMERAL_MAP.get(ch);
            }
            else if (UNIT_MAP.containsKey(ch)) {
                long unitValue = UNIT_MAP.get(ch);

                if (unitValue >= 10000) {
                    // Large unit (e.g., 万, 亿, 만, 억) - multiply the current section value
                    sectionValue = (sectionValue + currentValue) * unitValue;
                    result += sectionValue;
                    sectionValue = 0;  // Reset section after applying large unit
                }
                else {
                    // Smaller unit (e.g., 十, 百, 千, 십, 백, 천) - accumulate within the section
                    sectionValue += currentValue * unitValue;
                }
                currentValue = 0;  // Reset current value after applying unit
            }
            else if (ch.equals("点") || ch.equals("점") || ch.equals(".")) {
                // Handle decimal part separately
                result += sectionValue + currentValue;  // Add remaining values before decimal
                String decimalPart = convertDecimalPart(numberString.substring(i + 1));
                javaNumberString = String.format("%.0f", result) + decimalPart;  // Combine integer and decimal parts
                javaNumberString = formatJavaNumberString(javaNumberString);
                log.debug("""
                        Language number string converted to Java string.
                        Language string: {}
                        Java string: {}
                        """.stripIndent(),
                        numberString,
                        javaNumberString);
                return javaNumberString;  // Return formatted number string
            }
            else {
                throw new RuntimeException(String.format(
                        "Cannot convert language number string to Java number string: %s",
                        numberString));
            }
        }
        // Add any remaining values
        result += sectionValue + currentValue;

        // Convert the result to a string format to match Java number format expectations
        javaNumberString = String.valueOf(result);
        javaNumberString = formatJavaNumberString(javaNumberString);
        log.debug("""
                Language number string converted to Java string.
                Language string: {}
                Java string: {}
                """.stripIndent(),
                numberString,
                javaNumberString);
        return javaNumberString;
    }

    /**
     * Converts country - language specific number string to a number.
     * Throws an exception if the string cannot be converted to a number.
     * @param numberString The number string.
     * @return The number.
     */
    public static Number localeNumbStringToNumber(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");

        try {
            Number number;
            String javaNumberString;

            if (numberString.trim().matches(US_INTEGER_NUMBER_STRING)) {
                javaNumberString = numberString.trim().replace(",", "");
            }
            else {
                javaNumberString = localeNumberStringToJavaNumberString(numberString);
            }
            // Convert to positive infinity
            if (javaNumberString.equals(POSITIVE_INFINITY_VALUE_SYMBOL)) {
                number = Double.POSITIVE_INFINITY;
            }
            // Convert to negative infinity
            else if (javaNumberString.equals(NEGATIVE_INFINITY_VALUE_SYMBOL)) {
                number = Double.NEGATIVE_INFINITY;
            }
            // Convert to number with decimal point or scientific format
            else if (javaNumberString.contains(".") ||
                    javaNumberString.matches(SCIENTIFIC_NUMBER_STRING_REGEX)) {
                number = Double.parseDouble(javaNumberString);

                if (Double.isInfinite((double) number)) {
                    if ((double) number == Float.POSITIVE_INFINITY ||
                            (double) number == Float.NEGATIVE_INFINITY) {
                        number = new BigDecimal(javaNumberString);
                    }
                }
            }
            // Convert to number without decimal point
            else {
                try {
                    number = Integer.parseInt(javaNumberString);
                }
                catch (NumberFormatException e) {
                    try {
                        number = Long.parseLong(javaNumberString);
                    }
                    catch (NumberFormatException nfe) {
                        number = new BigInteger(javaNumberString);
                    }
                }
            }
            log.debug("""
                    Country - language specific number string converted to a number.
                    String: {}
                    Number: {}
                    """.stripIndent(), numberString, number);
            return number;
        }
        catch (Exception e) {
            // Do not throw SmartRuntimeException not to add error logs
            // when calling isNumberString() method
            throw new RuntimeException(String.format(
                    "Cannot convert country - language specific number string to a number: %s",
                    numberString), e);
        }
    }

    /**
     * Converts a number string in any numeric system
     * or format to a standardized number string
     * using the provided Locale from ICU4J library.
     * @param numberString The input number string in any format or encoding.
     * @param locale The Locale to use for formatting.
     * @return The standardized number string according to the provided Locale.
     */
    public static String localeStringToNumberString(String numberString, ULocale locale) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");

        try {
            // Create a number format based on the locale using ICU4J
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            // Parse the number string using the ICU4J number format
            Number number = numberFormat.parse(numberString);

            // Format the number back to a string in a standard format
            String resultNumberString = objectToString(number);
            log.debug("""
                    Number string converted to standard Java number string.
                    Input: {}
                    Output: {}
                    """.stripIndent(),
                    numberString, resultNumberString);
            return resultNumberString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert number string to number.
                    String: %s
                    Locale: %s
                    """.stripIndent(),
                    numberString, locale));
        }
    }

    /**
     * Converts locale number string to a number format string
     * based on the provided ULocale.
     * @param numberString The number string in any encoding.
     * @param uLocale The ULocale representing the locale-specific number format.
     * @return The formatted number string where all numeric digits
     * and the "-" sign are replaced with "#".
     */
    public static String localeNumberStringToFormat(String numberString, ULocale uLocale) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");
        StringBuilder formatBuilder = new StringBuilder();

        // Iterate through each character in the number string
        for (char c : numberString.toCharArray()) {
            // Check if the character is a digit in the locale
            if (Character.isDigit(c) || c == '-' || isLocaleSpecificDigit(c, uLocale)) {
                // Replace digits and "-" with "#"
                formatBuilder.append('#');
            } else {
                // Keep other delimiters (e.g., ',', '٫', spaces)
                formatBuilder.append(c);
            }
        }

        String formatString = formatBuilder.toString();
        log.debug("Number string '{}' converted to format: {}", numberString, formatString);
        return formatString;
    }

    /**
     * Converts number string to number.
     * Works only for US locale number format like "1,234.56".
     * It returns null if string cannot be converted to number.
     * @param numberString The number string.
     * @return The number.
     */
    public static Number stringToNumber(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");
        Number number = stringToNumberValue(numberString);

        if (number == null) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert number string to number.
                    Number string:
                    %s
                    """.stripIndent(),
                    numberString));
        }
        return number;
    }

    /**
     * Converts number string to number value.
     * Works with any country - language specific number format.
     * It does not convert to short and float number types,
     * because these types have low precision and are rarely used.
     * It returns null if string cannot be converted to number.
     * @param numberString The number string.
     * @return The number.
     */
    public static Number stringToNumberValue(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");

        try {
            Number number = localeNumbStringToNumber(numberString);
            log.debug("""
                    Number string converted to number.
                    Number string: %s
                    Number: %s
                    """.stripIndent(),
                    numberString, number);
            return number;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts currency number string to big decimal value.
     * @param numberString The number string.
     * @return The big decimal value,
     * or throws an exception if number has more than 2 numbers after decimal point.
     */
    public static BigDecimal currencyNumberStringToBigDecimal(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");

        try {
            // Remove comas from number string and trim it
            Number number = stringToNumber(numberString);
            BigDecimal value = new BigDecimal(String.valueOf(number));
            log.debug("String '{}' converted to big decimal value: {}", numberString, value);
            return value;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert string '%s' to big decimal value.", numberString));
        }
    }

    /**
     * Converts JSON object to XMl document with
     * custom root name and custom item name.
     * @param jsonObject The JSON object.
     * @return The XML document.
     */
    public static Document jsonObjectToXmlDocument(JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            // Use parameter name as XML root name.
            String rootName = getParameterName(0);
            // Convert JSON object to XML string and wrap it into root tags
            String xmlString = jsonObjectToXmlString(jsonObject, rootName);

            // Convert the built XML string to an XML Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDocument = builder.parse(new InputSource(new StringReader(xmlString)));
            log.debug("""
                    JSON object converted to XML document.
                    JSON:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    jsonObject, xmlDocument);
            return xmlDocument;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON object to XML document.
                    JSON:
                    %s
                    """.stripIndent(),
                    jsonObject), e);
        }
    }

    /**
     * Converts JSON array to XMl document with
     * custom root name and custom item name.
     * @param jsonArray The JSON array.
     * @return The JSON document.
     */
    public static Document jsonArrayToXmlDocument(JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            // Use parameter name as XML root name.
            String rootName = getParameterName(0);
            String itemName = TextUtils.pluralToSingular(rootName);

            // Convert JASON array to string and wrap XML string into root tags
            String xmlString =  jsonArrayToXmlString(jsonArray, rootName, itemName);

            // Convert the XML string to an XML Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            log.debug("""
                    JSON array converted to XML document.
                    JSON:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    jsonArray, document);
            return document;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON array to XML document.
                    JSON:
                    %s
                    """.stripIndent(),
                    jsonArray), e);
        }
    }

    /**
     * Converts JSON object to XMl node.
     * Root name is taken from parameter name passed tho this method.
     * @param jsonObject The JSON object.
     * @return The XML node.
     */
    public static Node jsonObjectToXmlNode(JSONObject jsonObject) {
        DataValidationUtils.validateNotNull(jsonObject, "jsonObject");

        try {
            String rootName = getParameterName(0);
            Document xml = jsonObjectToXmlDocument(jsonObject);
            Document document = updateXmlRootName(xml, rootName);
            Node xmlNode = document.getDocumentElement();
            log.debug("""
                    JSON object converted to XML node.
                    JSON:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    jsonObject, xmlNode);
            return xmlNode;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON object to XML node.
                    JSON:
                    %s
                    """.stripIndent(),
                    jsonObject), e);
        }
    }

    /**
     * Converts JSON array to XMl node with
     * custom root name and custom item name.
     * @param jsonArray The JSON object.
     * @param rootName The XML root name.
     * @param itemName The XML item name.
     * @return The XML node.
     */
    public static Node jsonArrayToXmlNode(JSONArray jsonArray, String rootName, String itemName) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");
        DataValidationUtils.validateNotBlank(rootName, "rootName");
        DataValidationUtils.validateNotBlank(itemName, "itemName");

        try {
            Node xmlNode = jsonArrayToXmlDocument(jsonArray).getDocumentElement();
            log.debug("""
                    JSON object converted to XML node.
                    JSON:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    jsonArray, xmlNode);
            return xmlNode;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON object to XML node.
                    JSON:
                    %s
                    """.stripIndent(),
                    jsonArray), e);
        }
    }

    /**
     * Converts JSON array to XMl node with
     * default root name and default item name.
     * @param jsonArray The JSON object.
     * @return The XML node.
     */
    public static Node jsonArrayToXmlNode(JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        return jsonArrayToXmlNode(jsonArray, DEFAULT_XML_ROOT_NAME, DEFAULT_XML_ITEM_NAME);
    }

    /**
     * Normalizes lineSeparators
     * @param string The input string.
     * @return The normalized string.
     */
    public static String normalizeLineSeparators(String string) {
        DataValidationUtils.validateNotNull(string, "string");

        return string.replace("\r\n", "\n");
    }

    public static String normalizeStringEncoding(String string) {
        DataValidationUtils.validateNotNull(string, "string");

        byte[] encoded = string.getBytes();
        String normalizedString = new String(encoded, StandardCharsets.UTF_8);
        log.debug("""
                String encoding is normalized.
                String:
                {}
                Normalized:
                {}
                """.stripIndent(),
                string, normalizedString);
        return normalizedString;
    }

    /**
     * Returns true if object is POJO object,
     * or false otherwise.
     * @param object The object.
     * @return The true/false flag.
     */
    public static boolean isPojoObject(Object object) {
        boolean result;
        Class<?> objectClass = null;

        if (object == null) {
            result = false;
        }
        else {
            objectClass = object.getClass();
            result = SmartType.isPojoClass(objectClass);
        }
        log.debug("""
                Is object class POJO?
                Class: {}
                Result: {}
                Object:
                {}
                """.stripIndent(),
                objectClass, result, object);
        return result;
    }

    /**
     * Converts a number string to a number format
     * by detecting the appropriate ULocale.
     * @param numberString The number string.
     * @return The format of the number string.
     */
    public static String numberStringToFormat(String numberString) {
        DataValidationUtils.validateNotBlank(numberString, "numberString");

        try {
            String formatString = null;
            numberString = numberString.trim();

            // Iterate through all available ULocales to find the correct one
            for (ULocale locale : ULocale.getAvailableLocales()) {
                DecimalFormat originalFormat = new DecimalFormat("",
                        DecimalFormatSymbols.getInstance(locale.toLocale()));

                try {
                    // Attempt to parse the number string using the current locale
                    originalFormat.parse(numberString);

                    // Use localeNumberStringToFormat to convert the number string to the desired format
                    formatString = localeNumberStringToFormat(numberString, locale);

                    // If formatting succeeds, break the loop
                    break;
                }
                catch (ParseException ignored) {
                    // Continue to the next locale if parsing fails
                }
            }
            if (formatString == null) {
                // Replace digits with #
                formatString = replaceAllDigitsWithDies(formatString);
            }
            if (formatString.equals(numberString)) {
                throw new SmartRuntimeException(String.format(
                        "Cannot convert number string %s to number format.", numberString));
            }

            return formatString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert number string %s to number format.", numberString), e);
        }
    }

    /**
     * Converts number to a formatted locale string based on the provided locale.
     * @param number The number to format.
     * @param uLocale The ULocale representing the locale-specific number format.
     * @return The formatted number string.
     */
    public static String numberToLocaleString(Number number, ULocale uLocale) {
        DataValidationUtils.validateNotNull(number, "number");
        DataValidationUtils.validateNotNull(uLocale, "uLocale");

        // Get the ICU4J NumberFormat for the specified ULocale
        NumberFormat numberFormat = NumberFormat.getInstance(uLocale);
        // Format the number into a string
        String formattedNumber = numberFormat.format(number);
        log.debug("Number '{}' formatted to '{}' using locale '{}'", number, formattedNumber, uLocale);
        return formattedNumber;
    }


    /**
     * Converts a Number to formatted string
     * based on the provided number format.
     * @param number The number to format.
     * @param format The format pattern.
     * @return The formatted number string.
     */
    public static String numberToFormattedString(Number number, String format) {
        DataValidationUtils.validateNotNull(number, "number");
        DataValidationUtils.validateNotBlank(format, "format");

        try {
            // Create a DecimalFormat instance with the provided format pattern
            DecimalFormat decimalFormat = new DecimalFormat(format);

            // Format the number to a string
            String numberString = decimalFormat.format(number);
            String code = currencyStringToCodeOrSymbol(numberString);

            if (!code.isEmpty()) {
                // Remove currency code or symbol to validate the number
                String currencyNumberString = numberString.replace(code, "");
                DataValidationUtils.validateNumberString(currencyNumberString, "currencyNumberString");
            }
            else {
                // Validate number string
                DataValidationUtils.validateNumberString(numberString, "numberString");
            }
            log.debug("""
                    Number converted to formatted number string.
                    Number: {}
                    Format: {}
                    String: {}
                    """.stripIndent(),
                    number, numberString);
            return numberString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert number %s to formatted number string.",
                    number), e);
        }
    }

    /**
     * Converts currency string to symbol or code.
     * @param currencyString The currency string
     * (e.g., "$1,234.56" or "USD1,234.56").
     * @return The currency symbol or code.
     */
    public static String currencyStringToCodeOrSymbol(String currencyString) {
        DataValidationUtils.validateNotBlank(currencyString, "currencyString");

        // Pattern to match any characters before the first numeric digit
        Pattern prefixPattern = Pattern.compile("^[^0-9]+");
        // Pattern to match any characters after the numeric part
        Pattern suffixPattern = Pattern.compile("[^0-9]+$");

        // Matcher to find prefix before the number
        Matcher prefixMatcher = prefixPattern.matcher(currencyString);
        // Matcher to find suffix after the number
        Matcher suffixMatcher = suffixPattern.matcher(currencyString);
        String codeOrSymbol = "";

        if (prefixMatcher.find(0) && suffixMatcher.find(0)) {
            throw new SmartRuntimeException(String.format(
                    "Wrong currency string format: %s", currencyString));
        }
        // If a prefix is found
        if (prefixMatcher.find(0)) {
            codeOrSymbol = prefixMatcher.group().trim();
        }
        else {
            // If the prefix is not found and a suffix is found
            if (suffixMatcher.find(0)) {
                codeOrSymbol = suffixMatcher.group().trim();
            }
        }
        if (codeOrSymbol.length() > 3) {
            throw new SmartRuntimeException(String.format(
                    "Currency code or symbol is too long: %s", codeOrSymbol));
        }
        log.debug("Currency string '{}' converted to currency code or symbol: {}",
                currencyString, codeOrSymbol);
        return codeOrSymbol;
    }

    /**
     * Converts a currency code (e.g., "USD")
     * to a regional currency symbol (e.g., "US$").
     * @param currencyCode The currency code
     * (e.g., "USD", "CAD").
     * @return The regional currency symbol
     * (e.g., "US$", "CA$"), or empty string if not found.
     */
    public static String currencyCodeToRegionalSymbol(String currencyCode) {
        DataValidationUtils.validateNotBlank(currencyCode, "currencyCode");
        DataValidationUtils.validateMax(currencyCode.length(), 3, "currencyCodeLength");

        try {
            String regionalSymbol = "";

            // Iterate through all available locales to find the corresponding currency
            for (Locale locale : Locale.getAvailableLocales()) {
                try {
                    Currency currency = Currency.getInstance(locale);

                    // Check if the currency code matches
                    if (currency != null && currency.getCurrencyCode().equals(currencyCode)) {
                        // Get the currency symbol for the locale
                        regionalSymbol = currency.getSymbol(locale);
                        break;
                    }
                } catch (Exception e) {
                    // Skip locales that don't have a currency
                }
            }
            // Take regional code from currency code first 2 chars and currency symbol
            // if regional code length less than 3
            if (regionalSymbol.length() < 3) {
                regionalSymbol = String.format("%s%s",
                        currencyCode.substring(0, 2),
                        currencyCodeToSymbol(currencyCode));
            }
            log.debug("Currency code '{}' converted to currency regional symbol: {}",
                    currencyCode, regionalSymbol);
            // Return empty string if no matching currency code is found
            return regionalSymbol;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert currency code '%s' to currency symbol.", currencyCode), e);
        }
    }

    /**
     * Converts a currency code to a currency symbol based on the locale.
     * @param currencyCode The 3-letter currency code
     * (e.g., "USD", "EUR").
     * (e.g., Locale.US for USD).
     * @return The currency symbol
     * (e.g., "$", "€") or throws an exception.
     */
    public static String currencyCodeToSymbol(String currencyCode) {
        DataValidationUtils.validateNotBlank(currencyCode, "currencyCode");
        DataValidationUtils.validateMax(currencyCode.length(), 3, "currencyCodeLength");

        try {
            currencyCode = currencyCode.trim();
            String currencySymbol = currencyCode;
            Currency currency = null;

            // Get the Currency instance for the given currency code
            try {
                currency = Currency.getInstance(currencyCode);
            }
            catch (IllegalArgumentException iae) {
                // Ignore exception to return the same code if currency is not found by code
            }
            if (currency != null) {
                // Iterate through all available locales to find the symbol
                for (Locale locale : Locale.getAvailableLocales()) {
                    try {
                        if (currency.equals(Currency.getInstance(locale))) {
                            String countryCode = locale.getCountry();

                            if (currencyCode.startsWith(countryCode)) {
                                currencySymbol = currency.getSymbol(locale).replace(countryCode, "");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // Skip locales that do not support this currency
                    }
                }
            }
            log.debug("Currency code '{}' converted to currency symbol: {}",
                    currencyCode, currencySymbol);
            return currencySymbol;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert currency code '%s' to currency symbol.", currencyCode), e);
        }
    }

    /**
     * Converts a currency regional symbol to a currency code based on the locale.
     * @param currencyRegionalSymbol The 3-letter currency regional symbol code
     * (e.g., "US$", "EU€").
     * @return The currency code
     * (e.g., "USD", "EUR") or returns empty string.
     */
    public static String currencyRegionalSymbolToCode(String currencyRegionalSymbol) {
        DataValidationUtils.validateNotBlank(currencyRegionalSymbol, "currencyRegionalSymbol");
        DataValidationUtils.validateMax(currencyRegionalSymbol.length(), 3,
                "currencyRegionalSymbolLength");

        try {
            // Return empty string if no code is found.
            String currencyCode = "";

            // Iterate through all available locales to find matching symbol
            for (Locale locale : Locale.getAvailableLocales()) {
                try {
                    Currency currency = Currency.getInstance(locale);
                    if (currency != null) {
                        // Get the currency symbol for this locale
                        String symbol = currency.getSymbol(locale);

                        // Check if the provided currency symbol matches
                        if (currencyRegionalSymbol.equals(symbol) ||
                            currencyRegionalSymbol.startsWith(locale.getCountry())) {
                            currencyCode = currency.getCurrencyCode();
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Ignore invalid locales or currency lookup issues
                }
            }
            log.debug("Currency regional symbol '{}' converted to currency code: {}",
                    currencyRegionalSymbol, currencyCode);
            return currencyCode;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert currency regional symbol '%s' to currency code.",
                    currencyRegionalSymbol), e);
        }
    }

    /**
     * Converts currency string to currency format by locale.
     * Currency string like "$1,234.56" or to
     * currency format like "¤#,###.##",
     * or like "USD 1,234.56" to
     * currency format like "CCC #,###.##",
     * or like "US$ 1,234.56" to
     * currency format like "SSS #,###.##".
     * @param currencyString The currency string
     * (e.g., "$1,234.56" or "USD 1,234.56").
     * @return The currency format string
     * (e.g., "¤#,###.##" or "CCC#,###.##"),
     * or just number format if currency code or symbol is not present.
     */
    public static String currencyStringToFormat(String currencyString) {
        DataValidationUtils.validateNotBlank(currencyString, "currencyString");

        try {
            String format;
            // Get currency code or symbol string
            String codeOrSymbol = currencyStringToCodeOrSymbol(currencyString);

            // Validate maximum code or symbol length - 3
            DataValidationUtils.validateMax(codeOrSymbol.length(), 3, "codeOrSymbolLenght");

            String numberFormat = numberStringToFormat(currencyString.replace(codeOrSymbol, ""));
            String numberString = currencyString.replace(codeOrSymbol, "").trim();

            if (!codeOrSymbol.isEmpty()) {

                // Check if the code is a currency code (like "USD", "EUR")
                if (codeOrSymbol.matches("^[A-Z]{3}$")) {
                    format = currencyString.replace(codeOrSymbol, CURRENCY_CODE_FORMAT)
                            .replace(numberString, numberFormat);
                }
                // Check if the code is a currency regional symbol (like "US$", "EU€")
                else if (codeOrSymbol.matches("^[A-Z]{2}[^A-Za-z]$")) {
                    format = currencyString.replace(codeOrSymbol, CURRENCY_REGIONAL_SYMBOL_FORMAT);
                    format = format.replace(numberString, numberFormat);
                }
                // Handle currency symbols
                else {
                    format = currencyString.replace(codeOrSymbol, CURRENCY_SYMBOL_FORMAT)
                            .replace(numberString, numberFormat);
                }
            } else {
                format = numberFormat;
            }
            // Workaround for values that ends with ".0"
            format = format.replace(".0", "").replace("0", "");

            log.debug("Currency string '{}' converted to currency format string: {}",
                    currencyString, format);
            return format;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert currency string '%s' to currency format.",
                    currencyString), e);
        }
    }

    /**
     * Converts a format string (e.g., "¤#,###.##" or "CCC #,###.##",
     * "#,###.## ¤" or "#,###.## CCC").
     * into a formatted currency string.
     * @param value The currency value.
     * @param code The currency symbol or currency code
     * (e.g., "$", "USD").
     * @param format The format string
     * (e.g., "¤#,###.##", "CCC #,###.##", "#,###.## ¤" or "#,###.## CCC").
     * @return The formatted currency string
     * (e.g., "$1,234.56", "USD 1,234.56", "1,234.56€" or "1,234.56").
     */
    public static String currencyValueToFormatedString(Number value, String code, String format) {
        DataValidationUtils.validateNotNull(value, "money");
        DataValidationUtils.validateNotBlank(code, "code");
        DataValidationUtils.validateNotBlank(format, "format");

        try {
            // Determine if the code is a symbol or currency code
            boolean isSymbol = format.contains("¤");

            // Remove the currency placeholder (¤ or CCC) from the format string for DecimalFormat
            String numberFormat = format.replace("¤", "").replace("CCC", "").trim();

            // Create a DecimalFormat for the numeric part of the format
            DecimalFormat decimalFormat = new DecimalFormat(numberFormat);
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());  // Use the default locale
            decimalFormat.setDecimalFormatSymbols(symbols);

            // Format the number
            String formattedNumber = decimalFormat.format(value);
            String currencyString;

            // Replace the placeholder with the actual currency symbol or code
            if (isSymbol) {
                currencyString = format.replace("¤", code).replace(numberFormat, formattedNumber);  // Replace ¤ with currency symbol
            } else {
                currencyString = format.replace("CCC", code).replace(numberFormat, formattedNumber);  // Replace CCC with currency code
            }
            log.debug("""
                            Currency value format, symbol or code and currency value
                            converted to currency string.
                            Value: {}
                            Symbol or code: {}
                            Format: {}
                            String: {}
                            """.stripIndent(),
                    value, code, format, currencyString);
            return currencyString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert currency value %s to formatted string with format '%s'.",
                    value, format), e);
        }
    }

    /**
     * Converts a phone number string to a format pattern
     * that can be used to format other phone numbers.
     * @param phoneNumber The phone number string.
     * @return The phone number format pattern.
     */
    public static String phoneNumberToFormat(String phoneNumber) {
        DataValidationUtils.validateNotBlank(phoneNumber, "phoneNumber");

        try {
            // Validate input
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                throw new IllegalArgumentException("Phone number cannot be null or empty.");
            }
            // Remove all digits from the phone number to get the formatting characters
            StringBuilder format = new StringBuilder();

            for (char ch : phoneNumber.toCharArray()) {
                if (Character.isDigit(ch)) {
                    format.append("#");  // Replace digits with placeholders
                } else {
                    format.append(ch);  // Keep non-digit characters (like spaces, dashes, parentheses)
                }
            }
            log.debug("""
                    Phone number string converted to phone number format.
                    Number string: %s
                    Number: %s
                    """.stripIndent(),
                    phoneNumber, format);
            return format.toString();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert phone number string to phone number format.
                    Phone number:
                    %s
                    """.stripIndent(),
                    phoneNumber), e);
        }
    }

    /**
     * Converts a phone number string to a number.
     * Removes all non-numeric characters before converting,
     * and converts letters to digits.
     * All letters should be uppercase except extension letter 'x'.
     * @param phoneNumberString The phone number string.
     * @return The phone number.
     */
    public static Number phoneNumberStingToNumber(String phoneNumberString) {
        DataValidationUtils.validateNotBlank(phoneNumberString, "phoneNumberString");

        // Convert letters to digits and remove all non-numeric characters
        StringBuilder cleanedPhoneNumber = new StringBuilder();
        for (char c : phoneNumberString.toCharArray()) {

            if (Character.isDigit(c)) {
                cleanedPhoneNumber.append(c);
            }
            else if (letterToDigitMap.containsKey(c)) {
                cleanedPhoneNumber.append(letterToDigitMap.get(c));
            }
        }
        // Validate the cleaned phone number
        String finalPhoneNumber = cleanedPhoneNumber.toString();
        DataValidationUtils.validateNotBlank(finalPhoneNumber, "cleanedPhoneNumber");
        DataValidationUtils.validateNumberString(finalPhoneNumber, "cleanedPhoneNumber");

        // Convert to BigInteger for long phone numbers
        Number phoneNumber = new BigInteger(finalPhoneNumber);

        // Validate phone length including extension number is between 8 and 21 digits
        DataValidationUtils.validateMin(phoneNumber, MIN_PHONE_NUMBER, "phoneNumber");
        DataValidationUtils.validateMax(phoneNumber, MAX_PHONE_NUMBER, "phoneNumber");
        log.debug("""
                Phone number string converted to number.
                String: {}
                Number: {}
                """.stripIndent(),
                phoneNumberString, phoneNumber);
        return phoneNumber;
    }

    /**
     * Formats a number into a phone number string
     * based on a given phone number format.
     * @param number The number to format
     * (e.g., 1234567890).
     * @param format The phone format
     * (e.g., "(###) ###-####").
     * @return The formatted phone number string.
     */
    public static String phoneNumberFormattedString(Number number, String format) {
        DataValidationUtils.validateNotNull(number, "number");
        DataValidationUtils.validateMin(number, MIN_PHONE_NUMBER, "number");
        DataValidationUtils.validateMax(number, MAX_PHONE_NUMBER, "number");
        DataValidationUtils.validateNotBlank(format, "format");

        String numberString = number.toString();
        StringBuilder phoneNumberString = new StringBuilder();
        int numberIndex = 0;

        for (char ch : format.toCharArray()) {
            if (ch == '#') {
                phoneNumberString.append(numberString.charAt(numberIndex));
                numberIndex++;
            }
            else {
                // Append non-numeric characters like (), -, or spaces
                phoneNumberString.append(ch);
            }
            // If we have used all digits from the numberString, break
            if (numberIndex >= numberString.length()) {
                break;
            }
        }
        log.debug("""
                    Phone number string converted to phone number format.
                    Number string: %s
                    Number: %s
                    """.stripIndent(),
                phoneNumberString, format);
        return phoneNumberString.toString();
    }

    /**
     * Converts array to list.
     * It converts collection, array, JSON array elements
     * to list elements.
     * It also converts JSON object elements to map elements.
     * If you need to keep element types as is please use Arras.asList().
     * It also converts JSON object and XML node elements
     * to map elements.
     * @param array The array.
     * @param <T> The list type.
     * @return The list.
     */
    public static <T> List<T> arrayToList(Object[] array) {
        DataValidationUtils.validateNotNull(array, "array");

        try {
            List<T> list = new ArrayList<>();

            for (Object element : array) {
                if (element == null) {
                    list.add(null);
                }
                else if (element.getClass().isArray()) {
                    list.add((T) arrayToList((Object[]) element));
                }
                else if (element instanceof Collection collection) {
                    list.add((T) collection);
                }
                else if (element instanceof JSONObject) {
                    list.add((T) objectToMap(element));
                }
                else if (element instanceof JSONArray) {
                    list.add((T) objectToList(element));
                }
                else if (element instanceof Node xmlNode) {
                    JSONObject jsonObject = xmlNodeToJsonObject(xmlNode);
                    list.add((T) objectToMap(jsonObject));
                }
                else {
                    list.add((T) element);
                }
            }
            log.debug("""
                    Array converted to list.
                    Array:
                    {}
                    List:
                    {}
                    """.stripIndent(),
                    array, list);
            return list;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert array to list.
                    Array:
                    %s
                    """.stripIndent(),
                    array), e);
        }
    }

    /**
     * Converts object to array.
     * It can convert collection, JSON array,
     * JSON array string and CSV string.
     * It converts collection and JSON array elements
     * to array elements.
     * It also converts:
     * JSON object string elements to JSON object elements;
     * XML document string to XML document;
     * XML node string to XML node.
     * @param object The object.
     * @param <T> The list type.
     * @return The list.
     */
    public static  <T> T[] objectToArray(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            T[] array;

            if (object instanceof String string) {

                if (isCsvString(string)) {
                    JSONArray jsonArray = csvStringToJsonArray(string);
                    array = jsonArrayToArray(jsonArray);
                }
                else if (isJsonArrayString(string)) {
                    JSONArray jsonArray = stringToJsonArray(string);
                    array = jsonArrayToArray(jsonArray);
                }
                else if (isXmlArrayString(string)) {
                    Node xmlArrayNode = stringToXmlNode(string);
                    array = xmlArrayNodeToArray(xmlArrayNode);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                    Cannot convert string to array.
                    Object class:
                    Object:
                    %s
                    """.stripIndent(),
                            object.getClass().getName(),
                            object));
                }
            }
            else if (object.getClass().isArray()) {
                SmartType valueType = SmartType.getArrayValueSmartType((Object[]) object);
                SmartType arrayType = SmartType.fromArrayValueSmartType(valueType);
                array = objectToObject(arrayType, object);
            }
            else if (object instanceof JSONArray jsonArray) {
                array = jsonArrayToArray(jsonArray);
            }
            else if (object instanceof Collection collection) {
                SmartType valueType = SmartType.getCollectionValueSmartType(collection);
                array = collectionToArray(valueType, collection);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert object to array.
                    Object class:
                    Object:
                    %s
                    """.stripIndent(),
                        object.getClass().getName(),
                        object));
            }
            log.debug("""
                    Object converted to array.
                    Object class: {}
                    Object value:
                    {}
                    Array:
                    {}
                    """.stripIndent(),
                    object.getClass().getName(),
                    object, array);
            return array;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to array.
                    Object class:
                    Object:
                    %s
                    """.stripIndent(),
                    object.getClass().getName(),
                    object), e);
        }
    }

    /**
     * Converts object to list.
     * It can convert collection, array, JSON array,
     * JSON array string and CSV string.
     * It converts collection, array and JSON array elements
     * to list elements.
     * It also converts JSON object and XML node elements
     * to map elements.
     * @param object The object.
     * @param <T> The list type.
     * @return The list.
     */
     public static  <T> List<T> objectToList(Object object) {
            DataValidationUtils.validateNotNull(object, "object");

        try {
            List<T> list;

            if (object instanceof Collection collection) {
                list = new ArrayList<T>(collection);
            }
            else if (object instanceof JSONArray jsonArray) {
                T[] array = jsonArrayToArray(jsonArray);
                list = arrayToList(array);
            }
            else if (object.getClass().isArray()) {
                list = arrayToList((T[]) object);
            }
            else if (object instanceof String string) {
                JSONArray jsonArray;

                if (isJsonArrayString(string)) {
                    // Try to convert JSON array string to JSONn array
                    jsonArray = stringToJsonArray(string);
                    T[] array = jsonArrayToArray(jsonArray);
                    list = Arrays.asList(array);
                }
                else if (isCsvString(string)) {
                    T[] array = objectToArray(string);
                    list = ConvertUtils.arrayToList(array);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                                Cannot convert string to list.
                                String:
                                %s
                                """.stripIndent(),
                                string));
                }
            }
            else {
                throw new SmartRuntimeException(String.format("""
                        Cannot convert object to list.
                        Object class: %s
                        Object value:
                        %s
                        """.stripIndent(),
                        object.getClass().getName(),
                        object));
            }
            log.debug("""
                    Object converted to list.
                    Object class: {}
                    Object value:
                    {}
                    List:
                    {}
                    """.stripIndent(),
                    object.getClass().getName(),
                    object, list);
            return list;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to list.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                    object.getClass().getName(),
                    object), e);
        }
    }

    /**
     * Converts object to map.
     * It can convert other map, JSON object, XML node,
     * JSON object string and XML string.
     * It converts collection, array and JSON array elements
     * to list elements.
     * It also converts JSON object and XML node elements
     * to map elements.
     * @param object The object.
     * @param <K> The map key type.
     * @param <V> The map value type.
     * @return The list.
     */
    public static  <K,V> Map<K,V> objectToMap(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            Map<K,V> map;

            if (object instanceof Map objectMap) {
                map = objectMap;
            }
            else if (object instanceof JSONObject jsonObject) {
                map = new HashMap<>();
                Iterator<String> keys = jsonObject.keys();

                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObject.get(key);

                    if (value instanceof JSONObject) {
                        map.put((K) key, (V) objectToMap(value));
                    }
                    else if (value instanceof JSONArray) {
                        map.put((K) key, (V) objectToList(value));
                    }
                    else if (value == JSONObject.NULL) {
                        map.put((K) key, null);
                    }
                    else {
                        map.put((K) key, (V) value);
                    }
                }
            }
            else if (object instanceof Node node) {
                JSONObject jsonObject = xmlNodeToJsonObject(node);
                map = objectToMap(jsonObject);
                // Remove XML root node
                String rootKey = (String) map.entrySet().iterator().next().getKey();
                map = (Map<K, V>) map.get(rootKey);
            }
            else if (object instanceof String string) {
                if (isJsonObjectString(string)) {
                    // Try to convert JSON object string to map
                    JSONObject jsonObject = ConvertUtils.stringToJsonObject(string);
                    map = objectToMap(jsonObject);
                }
                else if (isXmlNodeString(string)) {
                    // Try to convert XML string to map
                    Node xmlNode = stringToXmlNode(string);
                    map = objectToMap(xmlNode);
                }
                else {
                    throw new SmartRuntimeException(String.format("""
                    Cannot convert string to map.
                    String:
                    %s
                    """.stripIndent(),
                    string));
                }
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert object to map.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                        object.getClass(), object));
            }
            log.debug("""
                    Object converted to map.
                    Object class: {}
                    Object value:
                    {}
                    Map:
                    {}
                    """.stripIndent(),
                    object.getClass(), object, map);
            return map;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to map.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                    object.getClass(), object), e);
        }
    }

    /**
     * Converts object to file.
     * @param object The object
     * @return The file.
     */
    public static File objectToFile(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            File file;

            if (object instanceof File) {
                file = (File) object;
            }
            else if (object instanceof Path path) {
                file = path.toFile();
            }
            else if (object instanceof URI uri) {
                file = new File(uri);
            }
            else if (object instanceof URL url) {
                file = new File(url.toURI());
            }
            else if (object instanceof String string) {
                DataValidationUtils.validateFilePathFormat(string, "filePath");
                file = new File(string);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert object to file.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                        object.getClass(), object));
            }
            log.debug("""
                    Object converted to file.
                    Object class: {}
                    Object value:
                    {}
                    File:
                    {}
                    """.stripIndent(),
                    object.getClass(), object, file);
            return file;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to file.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                    object.getClass(), object), e);
        }
    }

    /**
     * Converts object to path.
     * @param object The object
     * @return The path.
     */
    public static Path objectToPath(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            Path path = objectToFile(object).toPath();
            log.debug("""
                    Object converted to path.
                    Object class: {}
                    Object value:
                    {}
                    Path:
                    {}
                    """.stripIndent(),
                    object.getClass(), object, path);
            return path;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to path.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                    object.getClass(), object), e);
        }
    }

    /**
     * Converts object to URI.
     * @param object The object
     * @return The URI.
     */
    public static URI objectToURI(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            URI uri;

            if (object instanceof File file) {
                uri = file.toURI();
            }
            else if (object instanceof Path path) {
                uri = path.toUri();
            }
            else if (object instanceof URI) {
                uri = (URI) object;
            }
            else if (object instanceof URL url) {
                uri = url.toURI();
            }
            else if (object instanceof String string) {
                uri = new URI(string);
            }
            else {
                throw new SmartRuntimeException(String.format("""
                    Cannot convert object to uri.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                        object.getClass(), object));
            }
            log.debug("""
                    Object converted to uri.
                    Object class: {}
                    Object value:
                    {}
                    URI:
                    {}
                    """.stripIndent(),
                    object.getClass(), object, uri);
            return uri;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to URI.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                    object.getClass(), object), e);
        }
    }

    /**
     * Converts object to URL.
     * @param object The object
     * @return The URL.
     */
    public static URL objectToURL(Object object) {
        DataValidationUtils.validateNotNull(object, "object");

        try {
            URL url = objectToURI(object).toURL();
            log.debug("""
                    Object converted to uri.
                    Object class: {}
                    Object value:
                    {}
                    URI:
                    {}
                    """.stripIndent(),
                    object.getClass(), object, url);
            return url;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert object to URI.
                    Object class: %s
                    Object value:
                    %s
                    """.stripIndent(),
                    object.getClass(), object), e);
        }
    }

    /**
     * Converts full class name string to the class package name string.
     * @param fullClassName The full class name.
     * @return The class package name.
     */
    public static String fullClassNameToPackageName(String fullClassName) {
        DataValidationUtils.validateNotBlank(fullClassName, "fullClassName");
        DataValidationUtils.validateMatches(fullClassName, FULL_CLASS_NAME_REGEX, "fullClassName");

        String simpleClassName;
        // Extract the simple class name by finding the last '.' in the full class name
        int lastDotIndex = fullClassName.lastIndexOf('.');

        // If there's no '.' in the class name, return it as-is (already simple)
        if (lastDotIndex == -1) {
            throw new SmartRuntimeException(String.format(
                    "Invalid class format. No package name: %s", fullClassName));
        }
        else {
            // Return the part after the last '.'
            simpleClassName = fullClassName.substring(0, lastDotIndex);
        }
        log.debug("Full class name {} converted to the class package name: {}",
                fullClassName, simpleClassName);
        return simpleClassName;
    }

    /**
     * Converts a Color object to a hex string compatible with HTML color input.
     * @param color The Color object to convert.
     * @return A hex color string in the format "#RRGGBB".
     */
    public static String colorToString(Color color) {
        DataValidationUtils.validateNotNull(color, "color");

        String colorString = String.format(
                "#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue())
                .toLowerCase();
        log.debug("""
                Color converted to string.
                Color: {}
                String: {}
                """.stripIndent(),
                color, colorString);
        return colorString;
    }

    /**
     * Converts a hex color string to a Color object.
     * @param colorString The hex color string in the format "#RRGGBB".
     * @return A Color object representing the hex color.
     * @throws IllegalArgumentException if the input string is not a valid hex color.
     */
    public static Color stringToColor(String colorString) {
        DataValidationUtils.validateColorFormat(colorString, "colorString");

        try {
            int red = Integer.parseInt(colorString.substring(1, 3), 16);
            int green = Integer.parseInt(colorString.substring(3, 5), 16);
            int blue = Integer.parseInt(colorString.substring(5, 7), 16);
            Color color = new Color(red, green, blue);
            log.debug("Color string '{}' converted to color: {}", colorString, color);
            return color;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert string to color object.
                    String: %s
                    """.stripIndent(),
                    colorString), e);
        }
    }


    /**
     * Returns true for JSON object
     * or false otherwise.
     * @param object The object.
     * @return true/false flag.
     */
    public static boolean isJsonTypeObject(Object object) {
        boolean result;
        if (object == null) {
            result = true;
        }
        else {
            Class<?> objectClass = object.getClass();

            result = objectClass.isPrimitive() ||
                    objectClass == String.class ||
                    objectClass == Boolean.class ||
                    objectClass == Byte.class ||
                    objectClass == Character.class ||
                    objectClass.isArray() ||
                    objectClass.isEnum() ||
                    object instanceof Number ||
                    object instanceof JSONArray ||
                    object instanceof JSONObject ||
                    object instanceof Collection ||
                    object instanceof Map;
        }
        log.debug("""
                Object type is JSON type?
                Result: {}
                Object:
                {}
                """.stripIndent(),
                result, object);
        return result;
    }

    /**
     * Returns true string is XML node string
     * or false otherwise.
     * @param string The object.
     * @return true/false flag.
     */
    public static boolean isXmlNodeString(String string) {
        try {
            string = escapeXmlString(string);
            string = unescapeXmlString(string);

            // Initialize a document builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.parse(new ByteArrayInputStream(string.getBytes()));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true object is XML document.
     * or false otherwise.
     * @param string The object.
     * @return true/false flag.
     */
    public static boolean isXmlDocumentString(String string) {
        string = escapeXmlString(string);
        string = unescapeXmlString(string);

        return  (string.trim().startsWith("<?xml") &&
            isXmlNodeString(string));
    }

    /**
     * Returns true if sting is XML document or XML node
     * and all nodes under the root node has the same name
     * or false otherwise.
     * @param string The sating
     * @return true/false flag.
     */
    public static boolean isXmlArrayString(String string) {
        DataValidationUtils.validateNotBlank(string, "string");
        boolean result;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(string.getBytes()));
            Node rootNode = document.getDocumentElement();

            if (rootNode == null) {
                return false;
            }
            NodeList childNodes = rootNode.getChildNodes();
            String commonNodeName = null;

            if (childNodes.getLength() == 0) {
                // Return true for empty XML document or node
                result = true;
            }
            else {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node child = childNodes.item(i);

                    // Only check element nodes
                    if (child.getNodeType() == Node.ELEMENT_NODE) {

                        if (commonNodeName == null) {
                            commonNodeName = child.getNodeName();
                        } else if (!commonNodeName.equals(child.getNodeName())) {
                            // Found a node with a different name
                            commonNodeName = null;
                            break;
                        }
                    }
                }
                result = commonNodeName != null;
            }
        }
        catch (Exception e) {
            result = false;
        }
        log.debug("XML string is an XML array: {}\nXML:\n{}", result, string);
        return result;
    }

    /**
     * Returns true if string is JSON object string.
     * or false otherwise.
     * @param string The string.
     * @return true/false flag.
     */
    public static boolean isJsonObjectString(String string) {

        if (isXmlNodeString(string)) {
            return false;
        }
        try {
            new JSONObject(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if string is JSON array string.
     * or false otherwise.
     * @param string The string.
     * @return true/false flag.
     */
    public static boolean isJsonArrayString(String string) {

        if (isXmlNodeString(string)) {
            return false;
        }
        try {
            new JSONArray(string);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if string is CSV string.
     * or false otherwise.
     * @param string The string.
     * @return true/false flag.
     */
    public static boolean isCsvString(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        string = string.trim();

        if (string.startsWith("[") || string.startsWith("{") ||
           (string.endsWith("]") || string.endsWith("}"))) {
            return false;
        }
        try {
            csvStringToJsonArray(string);
            return true;
        }
        catch (SmartRuntimeException e) {
            return false;
        }
    }

    /**
     * Returns true if string is a number string,
     * or false otherwise.
     * @param sting The string.
     * @return true/false flag.
     */
    public static boolean isNumberString(String sting) {
        return stringToNumberValue(sting) != null;
    }

    private static boolean isCsvNoneStringObject(Object object) {
        boolean result;

        if (object == null) {
            result = true;
        }
        else if (object instanceof String) {
            result = false;
        }
        else {
            Class<?> objectClass = object.getClass();

            result = objectClass.isPrimitive() ||
                    objectClass == Boolean.class ||
                    objectClass == Byte.class ||
                    objectClass == Character.class ||
                    object instanceof Number;
        }
        log.debug("""
                Object type is not a CSV string type?
                Result: {}
                Object:
                {}
                """.stripIndent(),
                result, object);
        return result;
    }

    private static <K,V> Map<K,V> createMapFromClasses(Class<K> keyClass, Class<V> valueClass) {
        Map<K, V> map = new HashMap<>();
        log.debug("""
                Map created from:
                Key class: {}
                Value class: {}
                Map:
                {}
                """.stripIndent(),
                keyClass.getName(),
                valueClass.getName(),
                map);
        return map;
    }

    private static <T> Collection<T> jsonArrayToCollection(SmartType type, JSONArray jsonArray) {
        DataValidationUtils.validateNotNull(jsonArray, "jsonArray");

        try {
            Collection<T> collection;
            Class<?> objectClass = type.getObjectClass();

            if (List.class.isAssignableFrom(objectClass)) {
                collection = new ArrayList<>();
            }
            else if (Set.class.isAssignableFrom(objectClass)) {
                collection = new HashSet<>();
            }
            else if (Queue.class.isAssignableFrom(objectClass)) {
                collection = new LinkedList<>();
            }
            else if (Vector.class.isAssignableFrom(objectClass)) {
                collection = new Vector<>();
            }
            else {
                throw new SmartRuntimeException(String.format(
                        "Unsupported collection type: %s", type));
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.get(i);
                T elementObject = objectToObject(type.getValueSmartType(), object);
                collection.add(elementObject);
            }
            log.debug("""
                    JSON array converted to collection.
                    Collection class: {}
                    JSON array:
                    {}
                    Collection:
                    {}
                    """.stripIndent(),
                    objectClass.getName(), jsonArray, collection);
            return collection;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                            Cannot convert JSON array to collection.
                            JSON array:
                            {}
                            """.stripIndent(),
                    jsonArray), e);
        }
    }

    private static String removeOrdinalSuffix(String dateString) {
        // Regular expression to match ordinal suffixes (1st, 2nd, 3rd, 4th, etc.)
        return dateString.replaceAll("(?<=\\d)(st|nd|rd|th)", "");
    }

    private static boolean isNaN(Object object) {
        return ((object.getClass() == Float.class && Float.isNaN((Float) object)) ||
                (object.getClass() == Double.class && Double.isNaN((Double) object)));
    }

    private static boolean isFloatPositiveInfinite(Object object) {
        return (object.getClass() == Float.class &&
                object.equals(POSITIVE_INFINITY));
    }

    private static boolean isFloatNegativeInfinite(Object object) {
        return (object.getClass() == Float.class &&
                object.equals(Float.NEGATIVE_INFINITY));
    }

    private static boolean isDoublePositiveInfinite(Object object) {
        return (object.getClass() == Double.class &&
                object.equals(Double.POSITIVE_INFINITY));
    }

    private static boolean isDoubleNegativeInfinite(Object object) {
        return (object.getClass() == Double.class &&
                object.equals(Double.NEGATIVE_INFINITY));
    }

    private static String trimQuotes(String string) {
        String trimmedString = string.trim();

        if (trimmedString.startsWith("\"") && trimmedString.endsWith("\"")) {
            return trimmedString.substring(1, trimmedString.length() - 1);
        } else {
            return trimmedString;
        }
    }

    private static List<String> parseCsvRowString(String csvRow) {
        DataValidationUtils.validateNotNull(csvRow, "csvRow");

        char[] delimiters = {',', ';', '|', ':', '\t'};
        List<String> values = new ArrayList<>();

        if (csvRow.isEmpty()) {
            return values;
        }
        boolean inQuotes = false;
        int length = csvRow.length();
        int valueStartIndex = 0;

        for (char delimiter : delimiters) {

            for (int i = 0; i < length; i++) {
                char previousCharValue = ' ';
                char nextCharValue = ' ';
                char charValue = csvRow.charAt(i);

                if (i > 0) {
                    previousCharValue = csvRow.charAt(i - 1);
                }
                else if (i < length - 1) {
                    nextCharValue = csvRow.charAt(i + 1);
                }

                if (charValue == '"' && nextCharValue != '"' && previousCharValue != '"') {
                    inQuotes = !inQuotes;
                }
                if (charValue == delimiter && !inQuotes) {
                    String value = csvRow.substring(valueStartIndex, i);
                    values.add(value);
                    valueStartIndex = i + 1;
                }
            }
            String value = csvRow.substring(valueStartIndex);
            values.add(value);
            return values;
        }
        throw new SmartRuntimeException(String.format("""
                        CSV row does not contain valid delimiters.
                        CSV:
                        %s
                        Valid delimiters:
                        %s
                        """.stripIndent(),
                csvRow, String.valueOf(delimiters)));
    }

    private static void collectionLog(
            Class<?> collectionClass,
            Class<?> elemnetKeyClass,
            SmartType elementValueType,
            Collection<?> collection) {
        log.debug("""
                {} created from:
                Object class: {}
                Key class: {}
                Value type: {}
                {}:
                {}
                """.stripIndent(),
                collectionClass.getSimpleName(),
                collectionClass.getName(),
                elemnetKeyClass != null ? elemnetKeyClass.getName() : null,
                elementValueType,
                collectionClass.getSimpleName(),
                collection);
    }

    private static <T> T createRecordInstanceFromJson(Class<?> recordClass, JSONObject jsonObject) throws Exception {
        Constructor<?> canonicalConstructor = recordClass.getDeclaredConstructors()[0];
        RecordComponent[] recordComponents = recordClass.getRecordComponents();
        Object[] constructorArgs = new Object[recordComponents.length];

        // Iterate over the record components (fields)
        for (int i = 0; i < recordComponents.length; i++) {
            String fieldName = recordComponents[i].getName();

            // Set the value from the JSON object if present
            if (jsonObject.has(fieldName)) {
                constructorArgs[i] = jsonObject.get(fieldName);
            }
            else {
                String firstKey;
                Object firstValue;
                Iterator<String> keys = jsonObject.keys();

                if (keys.hasNext()) {
                    firstKey = keys.next();
                    firstValue = jsonObject.get(firstKey);

                    if (firstValue instanceof JSONObject childJson &&
                            childJson.has(fieldName)) {
                        constructorArgs[i] = childJson.get(fieldName);
                    }
                    else {
                        throw new SmartRuntimeException(String.format("""
                                Cannot convert JSON object to record object.
                                Field name is not present in JSON object.
                                Field name: %s
                                JSON:
                                %s
                                """.stripIndent(),
                                fieldName, jsonObject));
                    }
                }
            }
        }
        // Instantiate the Record using its canonical constructor
        return (T) canonicalConstructor.newInstance(constructorArgs);
    }

    private static boolean isJsonValue(Object object) {

        return object == null ||
                object.getClass().isPrimitive() ||
                object instanceof String ||
                object instanceof Number ||
                object instanceof Boolean ||
                object instanceof JSONArray ||
                object instanceof JSONObject ||
                object instanceof Collection ||
                object.getClass().isArray() ||
                object instanceof Map ||
                object instanceof Character;
    }

    private static String jsonObjectToXmlString(JSONObject jsonObject, String rootName) {

        try {
            StringBuilder xmlString = new StringBuilder();

            // Start with XML root open tag
            xmlString.append("<").append(rootName).append(">");
            // Traverse JSONObject keys and handle JSONArray separately
            jsonObject.keys().forEachRemaining(keyName -> {
                Object value = jsonObject.get(keyName);

                if (value instanceof JSONArray elementJsonArray) {
                    String itemName = TextUtils.pluralToSingular(keyName);
                    xmlString.append(jsonArrayToXmlString(elementJsonArray, keyName, itemName));
                }
                else if (value instanceof JSONObject elementJsonObject) {
                    xmlString.append(jsonObjectToXmlString(elementJsonObject, keyName));
                }
                else if (value instanceof Collection collection) {
                    JSONArray jsonArray = collectionToJsonArray(collection);
                    String itemName = TextUtils.pluralToSingular(keyName);
                    xmlString.append(jsonArrayToXmlString(jsonArray, keyName, itemName));
                }
                else if (value instanceof Map map) {
                    JSONObject jsonMap = mapToJasonObject(map);
                    xmlString.append(jsonObjectToXmlString(jsonMap, keyName));
                }
                else {
                    // Convert regular JSONObject fields
                    String fieldXml = XML.toString(value, keyName);
                    xmlString.append(fieldXml);
                }
            });
            // End with XML root close tag
            xmlString.append("</").append(rootName).append(">");
            String xml = xmlString.toString();
            log.debug("""
                    JSON object converted to XML string.
                    JSON:
                    {}
                    XML:
                    {}
                    """.stripIndent(),
                    jsonObject, xml);
            return xml;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON object to XML string.
                    JSON:
                    %s
                    """.stripIndent(),
                    jsonObject), e);
        }
    }

    private static String jsonArrayToXmlString(JSONArray jsonArray, String arrayName, String itemName) {
        try {
            StringBuilder xmlString = new StringBuilder();
            Set<String> subArraysNames = new HashSet<>();
            // Start with array name open tag
            xmlString.append("<").append(arrayName).append(">");

            // Convert each element in JSONArray to XML with custom itemName
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);

                if (value instanceof JSONObject itemJsonObject) {
                    // Nested JSON objects will have the same name
                    xmlString.append(jsonObjectToXmlString(itemJsonObject, itemName));
                }
                else if (value instanceof JSONArray itemJsonArray) {
                    String subArrayName = getJsonArrayPutPluralParameterName(arrayName);
                    String subItemName = TextUtils.pluralToSingular(subArrayName);

                    // Check if array with this name already has been added.
                    if (!subArraysNames.contains(subArrayName)) {
                        xmlString.append(jsonArrayToXmlString(itemJsonArray, subArrayName, subItemName));
                    }
                    else {
                        int number = 1;
                        subArrayName = DEFAULT_XML_ITEMS_NAME;

                        while (true) {
                            if (!subArraysNames.contains(subArrayName)) {
                                xmlString.append(jsonArrayToXmlString(itemJsonArray, subArrayName, subItemName));
                                break;
                            }
                            // Add number to sub array name at the end to make it unique
                            subArrayName += number;
                        }
                    }
                    subArraysNames.add(subArrayName);
                }
                else {
                    // Convert regular JSONObject fields
                    String itemXml = XML.toString(value, itemName);
                    xmlString.append(itemXml);
                }
            }
            // End with array name close tag
            xmlString.append("</").append(arrayName).append(">");
            return xmlString.toString();
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format("""
                    Cannot convert JSON array to XML string without root tags.
                    JSON:
                    %s
                    """.stripIndent(),
                    jsonArray), e);
        }
    }

    private static String getParameterName(int depthIndex) {
        try {
            String fullTestMethodName = ClassUtils.getFullMethodNameFromStackTrace(depthIndex + 2);
            String fullMethodName = ClassUtils.getFullMethodNameFromStackTrace(depthIndex + 1);
            Class<?> clazz = ClassUtils.getClassFromFullMethodName(fullTestMethodName);
            String methodName = ClassUtils.getMethodNameFromFullMethodName(fullMethodName);
            String parameterName = ClassUtils.getMethodParameterName(clazz, fullTestMethodName, methodName, 0);
            log.debug("Parameter name: {}", parameterName);
            return parameterName;
        }
        catch (Exception e) {
            return DEFAULT_XML_ITEM_NAME;
        }
    }

    private static String getJsonArrayPutPluralParameterName(String arrayName) {
        try {
            String fullTestMethodName = ClassUtils.getFullMethodNameFromStackTrace(6);
            Class<?> clazz = ClassUtils.getClassFromFullMethodName(fullTestMethodName);
            String methodName = String.format("%s.put", arrayName);
            String parameterName = ClassUtils.getSingleMethodParameterName(
                    clazz, methodName, "JSONArray");
            log.debug("Parameter name: {}", parameterName);
            return parameterName;
        }
        catch (Exception e) {
            return DEFAULT_XML_ITEMS_NAME;
        }
    }

    private static Document updateXmlRootName(Document document, String newRootName) {
        // Get the original root element
        Element oldRoot = document.getDocumentElement();
        // Create a new root element with the new name
        Element newRoot = document.createElement(newRootName);

        // Move all child nodes from the old root to the new root
        NodeList childNodes = oldRoot.getChildNodes();
        while (childNodes.getLength() > 0) {
            Node child = childNodes.item(0);
            newRoot.appendChild(child);  // This will remove the child from the old root
        }
        // Replace the old root with the new root in the document
        document.removeChild(oldRoot);
        document.appendChild(newRoot);
        return document;
    }

    private static boolean isLocaleSpecificDigit(char c, ULocale uLocale) {
        // Get the digits for the locale using ICU4J's ULocale-based formatting
        char[] digits = com.ibm.icu.text.DecimalFormatSymbols.getInstance(uLocale).getDigits();

        // Check if the character matches any of the locale-specific digits
        for (char digit : digits) {
            if (digit == c) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBooleanString(String sting) {
        return sting.equals(BOOLEAN_TRUE_VALUE) ||
               sting.equals(BOOLEAN_FALSE_VALUE);
    }

    private static Map<Character, Character> createLetterToDigitMap() {
        Map<Character, Character> map = new HashMap<>();
        map.put('A', '2'); map.put('B', '2'); map.put('C', '2');
        map.put('D', '3'); map.put('E', '3'); map.put('F', '3');
        map.put('G', '4'); map.put('H', '4'); map.put('I', '4');
        map.put('J', '5'); map.put('K', '5'); map.put('L', '5');
        map.put('M', '6'); map.put('N', '6'); map.put('O', '6');
        map.put('P', '7'); map.put('Q', '7'); map.put('R', '7');
        map.put('S', '7'); map.put('T', '8'); map.put('U', '8');
        map.put('V', '8'); map.put('W', '9'); map.put('X', '9');
        map.put('Y', '9'); map.put('Z', '9');
        return map;
    }

    private static Class<?> normalizeClass(Class<?> objectClass) {

        if (List.class.isAssignableFrom(objectClass)) {
            return ArrayList.class;
        }
        else if (Set.class.isAssignableFrom(objectClass)) {
             return HashSet.class;
        }
        else if (Queue.class.isAssignableFrom(objectClass)) {
            return LinkedList.class;
        }
        else if (Vector.class.isAssignableFrom(objectClass)) {
            return Vector.class;
        }
        else if (Map.class.isAssignableFrom(objectClass)) {
            return HashMap.class;
        }
        else {
            return objectClass;
        }
    }

    private static int getJsonArrayColumnsLength(JSONArray jsonArray) {
        int length = 0;

        for (int i = 0; i < jsonArray.length(); i++) {

            if (jsonArray.get(i) instanceof JSONArray subJsonArray) {

                if (length == 0) {
                    length = subJsonArray.length();
                }
                else {
                    if (length != subJsonArray.length()) {
                        throw new SmartRuntimeException(
                                "JSON array table contains rows with different length.");
                    }
                }
            }
            else {
                length = jsonArray.length();
            }
        }
        return length;
    }

    private static int getCollectionColumnsLength(Collection<?> collection) {
        int length = 0;

        for (Object element : collection) {

            if (element instanceof Collection subCollection) {

                if (length == 0) {
                    length = subCollection.size();
                }
                else {
                    if (length != subCollection.size()) {
                        throw new SmartRuntimeException(
                                "Collection table contains rows with different length.");
                    }
                }
            }
            else {
                length = collection.size();
            }
        }
        return length;
    }

    private static void validateParsedNumber(String numberString, Number number, ULocale locale) {
        // No need to validate number for scientific format like 12 or 1E12
        // or positive or negative infinity number string
        if (numberString.equals(POSITIVE_INFINITY_VALUE_SYMBOL) ||
            numberString.equals(NEGATIVE_INFINITY_VALUE_SYMBOL)) {
            return;
        }
        // Convert number to US locale number string with maximal precision
        DecimalFormat decimalFormat = new DecimalFormat(
                "", DecimalFormatSymbols.getInstance(Locale.US));
        // Format number to US number string and remove thousands comas
        String javaNumberString = decimalFormat.format(number).replace(",", "");

        // No validation is needed for positive and negative number string
        if (javaNumberString.equals(POSITIVE_INFINITY_VALUE_SYMBOL) ||
            javaNumberString.equals(NEGATIVE_INFINITY_VALUE_SYMBOL)) {
            return;
        }
        // Remove all delimiters from new number and initial number string and get number
        // of digits in the number string as string length
        int initialNumberStringDigitsLength = removeNumberDelimiters(numberString, locale).length();
        // Replace decimal point and minus for negative numbers to compare just digits length
        int newNumberStringDigitLength = javaNumberString
                .replace(".", "")
                .replace("-", "")
                .length();

        // Check if converted number string has the same number of digits as input number string
        if (newNumberStringDigitLength != initialNumberStringDigitsLength) {
            throw new RuntimeException(String.format("""
                    Error validating parsed number.
                    Number string: {}
                    Number: {}
                    """.stripIndent(),
                    numberString, number));
        }
        // Create a DecimalFormat with by format and locale-specific symbols
        DecimalFormat originalFormat = new DecimalFormat("", DecimalFormatSymbols.getInstance(locale.toLocale()));
        // Format the number to a string using the specified locale and pattern
        String newNumberString = originalFormat.format(number);

        if (!newNumberString.equals(numberString)) {
            throw new RuntimeException(String.format("""
                    Error validating parsed number.
                    Number string: {}
                    Number: {}
                    """.stripIndent(),
                    numberString, number));
        }
    }

    private static String removeNumberDelimiters(String numberString, ULocale locale) {
        // Obtain DecimalFormatSymbols for the specified ULocale
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale.toLocale());
        // Get the locale-specific decimal and grouping separators
        char decimalSeparator = symbols.getDecimalSeparator();
        char groupingSeparator = symbols.getGroupingSeparator();
        // Build a regex pattern using String.format()
        String regex = String.format("[\\%c\\%c+\\-]", decimalSeparator, groupingSeparator);

        // Remove all matching delimiters and signs
        return numberString.replaceAll(regex, "");
    }

    public static String removeThousandDelimiters(String numberString) {
        // Regular expression to match common thousands separators including various spaces and commas
        // The regex targets commas, normal spaces, and any Unicode space-like characters
        String regex = "(?<=\\d)[,\\s\\u00A0\\u202F\\u2000-\\u200A\\u205F\\u3000٬](?=\\d{3})";

        // Remove thousand delimiters using the regex
        return numberString.replaceAll(regex, "");
    }

    private static int getDigitCharacterLengths(int digit, ULocale locale) {
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        String localizedDigit = numberFormat.format(digit);
        return localizedDigit.length();
    }

    private static Number numberStringToNumber(String numberString, ULocale locale) {
        // Create DecimalFormat with the specified ULocale and maximal precision
        DecimalFormat decimalFormat = new DecimalFormat("",
                DecimalFormatSymbols.getInstance(locale.toLocale()));

        // Parse the normalized string to a number
        try {
            return decimalFormat.parse(numberString);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getJavaNumberStringFomNumber(Number number) {
        // Convert the parsed number back to a standardized Java string format
        // with maximal precision
        int scale = new BigDecimal(String.valueOf(number)).scale();
        String format = String.format("#.%s", "#".repeat(scale));
        DecimalFormat decimalFormat = new DecimalFormat(format,
                DecimalFormatSymbols.getInstance(Locale.US));
        // Format the number
        String formattedNumber = decimalFormat.format(number);
        return formattedNumber.replace(",", "");
    }

    private static Float stringToFloat(String string) {
        Float object;

        if (string.equals(POSITIVE_INFINITY_VALUE_SYMBOL) ||
                string.equals(POSITIVE_INFINITY_VALUE_STRING)) {
            object = POSITIVE_INFINITY;
        }
        else if (string.equals(NEGATIVE_INFINITY_VALUE_SYMBOL) ||
                string.equals(NEGATIVE_INFINITY_VALUE_STRING)) {
            object = Float.NEGATIVE_INFINITY;
        }
        else {
            object = Float.parseFloat(string);
        }
        return object;
    }

    private static Double stringToDouble(String string) {
        Double object;

        if (string.equals(POSITIVE_INFINITY_VALUE_SYMBOL) ||
                string.equals(POSITIVE_INFINITY_VALUE_STRING)) {
            object = Double.POSITIVE_INFINITY;
        }
        else if (string.equals(NEGATIVE_INFINITY_VALUE_SYMBOL) ||
                string.equals(NEGATIVE_INFINITY_VALUE_STRING)) {
            object = Double.NEGATIVE_INFINITY;
        }
        else {
            object = Double.parseDouble(string);
        }
        return object;
    }

    private static int getJsonRowsLength(JSONArray jsonArray) {
        if (jsonArray.isEmpty()) {
            return 1;
        }
        int rowsLength = 0;

        for (Object element : jsonArray) {
            if (element instanceof JSONArray) {
                rowsLength++;
            }
            else {
                rowsLength = 1;
            }
        }
        return rowsLength;
    }

    private static int getCollectionRowsLength(Collection<?> collection) {
        if (collection.isEmpty()) {
            return 1;
        }
        int rowsLength = 0;

        for (Object element : collection) {

            if (element != null &&
                (element instanceof JSONArray ||
                element instanceof Collection ||
                element.getClass().isArray())) {
                rowsLength++;
            }
            else {
                rowsLength = 1;
            }
        }
        return rowsLength;
    }

    private static String convertDecimalPart(String decimalPart) {
        StringBuilder decimalValue = new StringBuilder(".");
        for (char ch : decimalPart.toCharArray()) {
            String numeral = String.valueOf(ch);
            if (NUMERAL_MAP.containsKey(numeral)) {
                decimalValue.append(NUMERAL_MAP.get(numeral));
            } else {
                break;  // Stop if we encounter a non-numeric character
            }
        }
        return decimalValue.toString();
    }

    private static String formatJavaNumberString(String numberString) {
        // Remove all thousand delimiters if any
        numberString = removeThousandDelimiters(numberString);
        // Replace comma decimal delimiter with point
        numberString = numberString.replace(",", ".");
        return numberString;
    }

    private static String replaceAllDigitsWithDies(String formatNumberString) {
        return formatNumberString.replaceAll("[0-9]", "#");
    }
}


