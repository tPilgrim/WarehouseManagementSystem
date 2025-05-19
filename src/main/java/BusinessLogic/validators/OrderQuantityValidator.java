package BusinessLogic.validators;

import Model.Order;

public class OrderQuantityValidator implements Validator<Order> {

    @Override
    public void validate(Order order) {
        if (order.getQuantity() < 0) {
            throw new IllegalArgumentException("Cantitatea nu poate fi negativa.");
        }
    }
}
