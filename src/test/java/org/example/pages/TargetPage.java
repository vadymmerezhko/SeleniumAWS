package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.data.TargetPageOutput;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.elements.Field;
import org.example.ui.pages.SmartPage;

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
