package BusinessLogic.validators;

import Model.Product;

public class ProductPriceValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Pretul produsului nu poate fi negativ.");
        }
    }
}
