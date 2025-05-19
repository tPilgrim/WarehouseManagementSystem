package Model;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 */

public record Bill(
        int id,
        int orderId,
        String clientName,
        String productName,
        int quantity,
        double totalPrice
) {}
