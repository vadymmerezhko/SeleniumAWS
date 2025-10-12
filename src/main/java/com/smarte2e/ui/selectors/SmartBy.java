package com.smarte2e.ui.selectors;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.data.SmartValue;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.utils.DataValidator;
import com.smarte2e.utils.WebUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

import static com.smarte2e.constants.Settings.*;

/**
 * Smart By class.
 */
@Slf4j
public class SmartBy extends By {

    private By by;
    private SmartByType type;
    private final SmartValue selectorValue = new SmartValue();
    private String elementName;

    /**
     * Returns smart By found by next order:
     * - Algorithm;
     * - OpenAI;
     * - Image.
     * @return The smart By selector.
     */
    public static SmartBy auto() {
        SmartBy smartBy = new SmartBy();
        log.debug("Auto smart By selector is created:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Returns smart By selector for Selenium By selector.
     * @return The smart By selector.
     */
    public static SmartBy selector(By nativeBy) {
        DataValidator.notNull(nativeBy, "nativeBy");

        SmartBy smartBy = new SmartBy(nativeBy);
        log.debug("Smart By selector is created by native selector By:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Returns smart By selector with keyword.
     * By default, XPATH selector by text contains is used for keyword.
     * @param keyword The keyword.
     * @return The smart By selector.
     */
    public static SmartBy keyword(Object keyword) {
        DataValidator.notNull(keyword, "keyword");

        String selctorString = String.format("//*[contains(text(),'%s')]", keyword);
        SmartBy smartBy = new SmartBy(By.xpath(selctorString));
        smartBy.setKeyword(keyword);
        log.debug("Smart By selector is created with keyword:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Returns smart By selector with strict keyword.
     * By default, XPATH selector by text equals is used for keyword.
     * @param keyword The keyword.
     * @return The smart By selector.
     */
    public static SmartBy strictKeyword(Object keyword) {
        DataValidator.notNull(keyword, "keyword");

        String selctorString = String.format("//*[text()='%s']", keyword);
        SmartBy smartBy = new SmartBy(By.xpath(selctorString));
        smartBy.setKeyword(keyword);
        log.debug("Smart By selector is created with strict keyword:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Returns smart By selector found by image.
     * @return The selector.
     */
    public static SmartBy image() {
        SmartBy smartBy = new SmartByImage();
        log.debug("Smart By selector is created by image:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Returns smart By selector found by image.
     * @param imageFilePath The image file path.
     * @return The selector.
     */
    public static SmartBy image(String imageFilePath) {
        DataValidator.filePathFormat(imageFilePath, "imageFilePath");

        // Use selector by link text for PNG image files
        SmartBy smartBy = new SmartBy(By.linkText(imageFilePath));
        log.debug("Smart By selector is created by image:\n{}", smartBy);
        return smartBy;
    }

    /**
     * Constructs empty smart By object.
     */
    SmartBy() {
        super();
        log.debug("Empty smart By selector is created.");
    }

    /**
     * Constructs smart By selector from smart By type and native By selector.
     * @param type The smart By type.
     * @param by The native By selector.
     */
    SmartBy(SmartByType type, By by) {
        super();
        DataValidator.notNull(type, "smartByType");
        this.type = type;
        setBy(by);
        log.debug("Smart By selector is created by native By selector: {}", by);
    }

    /**
     * Constructs smart By selector from native By selector.
     */
    SmartBy(By nativeBy) {
        super();
        setBy(nativeBy);
        type = SmartByParser.fromBy(by).getSmartByType();
        log.debug("Smart By selector is created from native By selector: {}", nativeBy);
    }

    /**
     * Returns smart By type.
     * @return The By selector.
     */
    public SmartByType getSmartByType() {
        log.debug("Smart By Type is returned: {}", type);
        return type;
    }

    /**
     * Returns wrapped native By selector.
     * @return The By selector.
     */
    public By getBy() {
        setUp();
        log.debug("Smart By native By selector is returned: {}", by);
        return by;
    }

    /**
     * Sets wrapped By selector.
     * @param by The By selector.
     */
    public void setBy(By by) {
        DataValidator.notNull(by, "nativeBy");
        this.by = by;
        log.debug("Smart By native By selector is set: {}", by);
    }

    /**
     * Returns keyword object.
     * @return The text.
     */
    public Object getKeyword() {
        Object keyword = selectorValue.getKeyword();
        log.debug("Smart By keyword is returned: {}", by);
        return keyword;
    }

    /**
     * Sets keyword and updates By selector.
     * @param keyword The keyword.
     */
    public void setKeyword(Object keyword) {
        selectorValue.setKeyword(keyword);
        log.debug("Smart By keyword is set: {}", keyword);
    }

    /**
     * Sets element name.
     * @param elementName The element name.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
        log.debug("Smart By element name is set: {}", elementName);
    }

    /**
     * Returns element name.
     * @return The element name.
     */
    public String getElementName() {
        log.debug("Smart By element name is returned: {}", elementName);
        return elementName;
    }

    /**
     * Gets type=value selector string.
     * @return The type=value string.
     */
    public String toSelectorString() {

        try {
            setUp();
            String selectorValue = SmartByParser.selectorValueFromBy(by);

            if (selectorValue != null && selectorValue.endsWith(String.format(": %s", NULL_VALUE_STRING))) {
                log.debug("Undefined smart by selector converted to selector string: null");
                return null;
            }
            String selectorType;

            if (WebUtils.isXpath(selectorValue)) {
                selectorType = SelectorType.XPATH.toString();
            }
            else if (WebUtils.isPngImageSelector(selectorValue)) {
                selectorType = SelectorType.IMAGE.toString();
            }
            else if (WebUtils.isUrl(selectorValue)) {
                selectorType = SelectorType.URL.toString();
            }
            else {
                selectorType = SelectorType.CSS.toString();
            }
            String selectorString = String.format("%s%s%s", selectorType, SELECTOR_DELIMITER, selectorValue);
            log.debug("Smart by selector converted to selector string: {}", selectorString);
            return selectorString;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart by to selector type=value string:\n%s", this), e);
        }
    }

    /**
     * Gets type=value selector template with keyword placeholder (if any).
     * @return The type=value string.
     */
    public String toSelectorTemplate() {

        try {
            String selectorString = toSelectorString();

            if (selectorString == null) {
                log.debug("Undefined smart by selector converted to selector template: null");
                return null;
            }
            SmartValue smartValue = new SmartValue(selectorString, selectorValue.getKeyword());
            String selectorTemplate = smartValue.getValueTemplate();
            log.debug("Smart by selector converted to selector template: {}", selectorTemplate);
            return selectorTemplate;
        }
        catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot convert smart by to selector typ=value template:\n%s", this), e);
        }
    }

    /**
     * Finds web elements.
     * @param context The context.
     * @return The list of found elements.
     */
    @Override
    public List<WebElement> findElements(SearchContext context) {
        setUp();
        DataValidator.notNull(by, "nativeBy");
        List<WebElement> elements =  by.findElements(context);
        log.debug("Web elements found by smart By selector:\n{}", elements);
        return elements;
    }

    /**
     * Converts smart selector object to string.
     * @return The string.
     */
    @Override
    public String toString() {
        String string = by == null ? null : by.toString();
        log.debug("Smart by selector converted to string: {}", string);
        return string;
    }

    /**
     * Compares this smart selector to other object.
     * @param object The object.
     * @return The result: equals - true, otherwise - false.
     */
    @Override
    public boolean equals(Object object) {

        if (object == null) {
            log.debug("Smart selector equals() called. The actual object is null.");
            return false;
        }
        if (this == object) {
            log.debug("Smart selector equals() called. The actual and expected are the same object.");
            return true;
        }
        if (object instanceof SmartBy actualSmartBy) {
            try {
                boolean result = elementName.equals(actualSmartBy.elementName) &&
                        by.equals(actualSmartBy.by) &&
                        type.equals(actualSmartBy.type) &&
                        selectorValue.equals(actualSmartBy.selectorValue);
                log.debug("""
                        Two smart selectors objects are compared.
                        Expected:
                        {}
                        Actual:
                        {}
                        Result:
                        {}
                        """.stripIndent(),
                        this, object, result);
                return result;
            }
            catch (Exception e) {
                throw new SmartRuntimeException(String.format("""
                        Cannot compare two smart element objects.
                        Expected:
                        %s
                        Actual:
                        %s
                        """.stripIndent(),
                        this,
                        object), e);
            }
        }
        log.debug("Smart selector equals() returned false.\n" +
                "The actual object is not a smart selector.");
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementName, by, type, selectorValue);
    }

    private void setUp() {

        if (by != null) {

            if (by instanceof SmartBy smartBy) {

                if (smartBy.by != null &&
                    smartBy.by.toString().endsWith(String.format(": %s", NULL_VALUE_STRING))) {
                    by = null;
                    type = null;
                }
                else {
                    by = smartBy.getBy();
                }
            }
            if (type == null && by != null) {
                    type = SmartByParser.fromBy(by).getSmartByType();
            }
            String selectorValueString = SmartByParser.selectorValueFromBy(by);
            selectorValue.setValue(selectorValueString);
        }
        else {
            type = null;
            selectorValue.setValue(null);
        }
        if (selectorValue.getKeyword() != null) {

            if (selectorValue.getSmartType() != null) {
                by = SmartByParser.fromSelectorValue(selectorValue.toString());
            }
        }
    }
}
