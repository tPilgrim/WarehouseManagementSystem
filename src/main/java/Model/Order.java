package Model;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class Order {
    private int id;
    private int client_id;
    private int product_id;
    private int quantity;

    public Order(int id, int client_id, int product_id, int quantity) {
        this.id = id;
        this.client_id = client_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getClient_id() {
        return client_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
