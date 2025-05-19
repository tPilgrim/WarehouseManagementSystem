package BusinessLogic.validators;

import Model.Product;

public class ProductNameValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {
        String name = product.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Numele produsului e gol.");
        }
        if (!name.matches("[A-Za-z ]+")) {
            throw new IllegalArgumentException("Numaele trbuie sa contina doar litere si spatii.");
        }
    }
}

