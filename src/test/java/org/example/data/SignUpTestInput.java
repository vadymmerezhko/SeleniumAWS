package org.example.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Signup test input data record.
 * @param textInput The text input.
 * @param textareaInput The text area input.
 * @param dropdownSelectedOption The dropdown option to select.
 * @param dataListSelectOption The data list option to select.
 * @param filePath The file path.
 * @param checkbox1Value The first checkbox value.
 * @param checkbox2Value The second checkbox value.
 * @param radiobutton1Value The first radio button value.
 * @param radiobutton2Value The second radio button value.
 * @param color The color value in "#RRGGBB" format like "#0088ff".
 * @param date The data value in "mm/DD/YYYY" format like "05/23/1970".
 * @param range The range value.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record SignUpTestInput(String textInput,
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
