package BusinessLogic;

import BusinessLogic.validators.*;
import Model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class OrderBLL {
    private List<Validator<Order>> validators;

    public OrderBLL() {
        validators = new ArrayList<>();
        validators.add(new OrderQuantityValidator());
    }

    /**
     * Validates the order using validators.
     *
     * @param order the order to validate
     */
    public void validate(Order order) {
        for (Validator<Order> v : validators) {
            v.validate(order);
        }
    }
}
