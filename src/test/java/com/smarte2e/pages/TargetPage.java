package com.smarte2e.pages;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.data.TargetPageOutput;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.ui.elements.Field;
import com.smarte2e.ui.pages.SmartPage;

@Slf4j
@SuppressWarnings("unused")
public class TargetPage extends SmartPage {

    private final Field header = new Field();
    private final Field status = new Field();

    public TargetPageOutput getOutputData() {
        try {
            TargetPageOutput output = new TargetPageOutput();
            setAllOutputs(output);

            log.debug("Target page output data is returned: {}", output);
            return output;
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Getting Target page output data failed.", e);
        }
    }
}
