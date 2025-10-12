package com.smarte2e.data;

import lombok.Getter;
import lombok.Setter;
import com.smarte2e.annotations.SmartValueField;


@SuppressWarnings("unused")
@SmartValueField
@Getter @Setter
public class ProductsPageHeaderOutput extends SmartData {
    private SmartValue header;
    private SmartValue productsField;
}
