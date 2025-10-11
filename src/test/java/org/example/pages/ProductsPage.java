package org.example.pages;

import lombok.extern.slf4j.Slf4j;
import org.example.data.ProductsPageHeaderOutput;
import org.example.exceptions.SmartRuntimeException;
import org.example.ui.elements.Field;
import org.example.ui.pages.SmartPage;

@Slf4j
@SuppressWarnings({"unused"})
public class ProductsPage extends SmartPage {

    private final Field header = new Field();
    private final Field productsField = new Field();

    public ProductsPageHeaderOutput getHeaderOutputData() {
        try {
            ProductsPageHeaderOutput output = new ProductsPageHeaderOutput();
            // Set output values for header and productsField
            setAllOutputs(output);
            log.debug("Product page header output data is returned: {}", output);
            return output;
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Getting Product page header output data failed.", e);
        }
    }
}
