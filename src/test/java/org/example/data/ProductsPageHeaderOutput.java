package org.example.data;

import lombok.Getter;
import lombok.Setter;
import org.example.annotations.SmartValueField;


@SuppressWarnings("unused")
@SmartValueField
@Getter @Setter
public class ProductsPageHeaderOutput extends SmartData {
    private SmartValue header;
    private SmartValue productsField;
}
