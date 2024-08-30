package org.example.data;

import lombok.Getter;

public class TargetPageOutput extends SmartData {
    @Getter
    private final SmartValue header = new SmartValue();
    @Getter
    private final SmartValue status = new SmartValue();

    public TargetPageOutput() {
        super();
        initialize();
    }
}
