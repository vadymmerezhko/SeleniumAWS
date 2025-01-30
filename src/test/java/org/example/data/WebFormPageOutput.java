package org.example.data;

import lombok.Getter;
import org.example.annotations.SmartValueField;


/**
 * The Web Form output data class.
 */
@SuppressWarnings("unused")
@SmartValueField
@Getter
public class WebFormPageOutput extends SmartData {
    private SmartValue productName;
    private SmartValue description;
    private SmartValue brand;
    private SmartValue model;
    private SmartValue upload;
    private SmartValue available;
    private SmartValue freeDelivery;
    private SmartValue paidReturn;
    private SmartValue freeReturn;
    private SmartValue color;
    private SmartValue deliveryDate;
    private SmartValue weight;
}
