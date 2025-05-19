package Presentation;

import BusinessLogic.OrderBLL;
import Model.Client;
import Model.Product;
import Model.Order;
import Model.Bill;
import DataAcces.ClientDAO;
import DataAcces.ProductDAO;
import DataAcces.OrderDAO;
import DataAcces.BillDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 */

public class OrderGUI extends JFrame {
    private JComboBox<String> clientBox;
    private JComboBox<String> productBox;
    private JTextField quantityField;
    private ClientDAO clientDAO = new ClientDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private BillDAO billDAO = new BillDAO();
    private OrderBLL orderBLL = new OrderBLL();

    private List<Client> clients;
    private List<Product> products;

    public OrderGUI(ProductGUI productGUI) {
        setTitle("Create Order");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        clientBox = new JComboBox<>();
        productBox = new JComboBox<>();
        quantityField = new JTextField();

        JButton orderBtn = new JButton("Place Order");

        add(new JLabel("Select Client:"));
        add(clientBox);
        add(new JLabel("Select Product:"));
        add(productBox);
        add(new JLabel("Quantity:"));
        add(quantityField);
        add(new JLabel());
        add(orderBtn);

        loadData();

        orderBtn.addActionListener(e -> {
            String selectedClientName = (String) clientBox.getSelectedItem();
            String selectedProductName = (String) productBox.getSelectedItem();

            Client client = clients.stream()
                    .filter(c -> c.getName().equals(selectedClientName))
                    .findFirst()
                    .orElse(null);

            Product product = products.stream()
                    .filter(p -> p.getName().equals(selectedProductName))
                    .findFirst()
                    .orElse(null);

            if (client == null || product == null) return;

            int qty;

            try {
                qty = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity");
                return;
            }

            if (product.getQuantity() < qty) {
                JOptionPane.showMessageDialog(this, "Under-stock: Not enough products.");
            } else {
                Order order = new Order(0, client.getId(), product.getId(), qty);
                orderBLL.validate(order);
                Order insertedOrder = orderDAO.insert(order);

                Bill bill = new Bill(
                        0,
                        insertedOrder.getId(),
                        client.getName(),
                        product.getName(),
                        qty,
                        product.getPrice() * qty
                );

                billDAO.insert(bill);
                System.out.println(billDAO.findAll().getLast());

                product.setQuantity(product.getQuantity() - qty);
                productDAO.update(product);

                JOptionPane.showMessageDialog(this, "Order placed successfuly.");

                if (productGUI != null) {
                    productGUI.refreshTable();
                }

                loadData();
            }
        });

        setVisible(true);
    }

    public void loadData() {
        clientBox.removeAllItems();
        productBox.removeAllItems();

        clients = clientDAO.findAll();
        products = productDAO.findAll();

        for (Client c : clients) clientBox.addItem(c.getName());
        for (Product p : products) productBox.addItem(p.getName());
    }
}
