package BusinessLogic;

import BusinessLogic.validators.ProductNameValidator;
import BusinessLogic.validators.ProductPriceValidator;
import BusinessLogic.validators.ProductQuantityValidator;
import BusinessLogic.validators.Validator;
import Model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class ProductBLL {

    private List<Validator<Product>> validators;

    public ProductBLL() {
        validators = new ArrayList<>();
        validators.add(new ProductNameValidator());
        validators.add(new ProductPriceValidator());
        validators.add(new ProductQuantityValidator());
    }

    public void validate(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
    }
}
