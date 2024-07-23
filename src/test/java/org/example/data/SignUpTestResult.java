package org.example.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * The signup test result data record.
 * @param textInput The actual text value.
 * @param textareaInput The actual textarea value.
 * @param dropdownSelectedOption The actual dropdown selected option.
 * @param dataListSelectOption The actual selected data list option.
 * @param filePath The actual file path.
 * @param checkbox1Value The actual value of the first checkbox.
 * @param checkbox2Value The actual value of the second checkbox.
 * @param radiobutton1Value The actual value of the first radio button.
 * @param radiobutton2Value The actual value of the second radio button.
 * @param color The actual selected color value in format "#RRGGBB" like "#0088FF".
 * @param date The actual selected data value in format "mm/DD/YYYY" like "05/23/1970".
 * @param range The actual selected range value.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record SignUpTestResult(String textInput,
                               String textareaInput,
                               String dropdownSelectedOption,
                               String dataListSelectOption,
                               String filePath,
                               boolean checkbox1Value,
                               boolean checkbox2Value,
                               boolean radiobutton1Value,
                               boolean radiobutton2Value,
                               String color,
                               String date,
                               int range) {
}
