package org.example.data;

import lombok.Getter;
import org.example.annotations.SmartValueField;


@SuppressWarnings("unused")
@SmartValueField
@Getter
public class TargetPageOutput extends SmartData {
    private SmartValue header;
    private SmartValue status;
}
