package org.example.data;

import lombok.Getter;
import org.example.annotations.SmartValueField;


@SuppressWarnings("unused")
@SmartValueField
@Getter
public class LoginPageInput extends SmartData {
    private SmartValue userName;
    private SmartValue password;
}
