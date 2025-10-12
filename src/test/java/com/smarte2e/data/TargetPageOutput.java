package com.smarte2e.data;

import lombok.Getter;
import com.smarte2e.annotations.SmartValueField;


@SuppressWarnings("unused")
@SmartValueField
@Getter
public class TargetPageOutput extends SmartData {
    private SmartValue header;
    private SmartValue status;
}
