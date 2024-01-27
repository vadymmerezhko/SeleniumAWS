package org.example.data;

public record TestResult(String textInput,
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
