package org.example.ui.elements;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

/**
 * Search input smart element class.
 */
@Slf4j
public class SearchInput extends TextInput {

    /**
     * The Search input constructor with auto selector.
     */
    public SearchInput() {
    }

    /**
     * The Search input constructor by its selector.
     * @param by The element selector.
     */
    public SearchInput(By by) {
        super(by);
    }
}
