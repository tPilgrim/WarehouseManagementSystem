package BusinessLogic.validators;

import Model.Product;

public class ProductQuantityValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Cantitatea produsului nu poate fi negativa.");
        }
    }
}
