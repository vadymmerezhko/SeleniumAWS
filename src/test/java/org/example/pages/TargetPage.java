package org.example.pages;

import lombok.Getter;
import org.example.ui.elements.Field;
import org.example.ui.pages.SmartPage;

@Getter
public class TargetPage extends SmartPage {

    private final Field header = new Field();
    private final Field status = new Field();
}
