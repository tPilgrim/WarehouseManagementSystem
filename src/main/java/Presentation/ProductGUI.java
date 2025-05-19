package Presentation;

import BusinessLogic.ProductBLL;
import Model.Product;
import DataAcces.ProductDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 */

public class ProductGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, priceField, quantityField;
    private ProductDAO productDAO = new ProductDAO();
    private ProductBLL productBLL = new ProductBLL();
    private OrderGUI orderGUI;

    public ProductGUI() {
        this(null);
    }

    public ProductGUI(OrderGUI orderGUI) {
        this.orderGUI = orderGUI;

        setTitle("Product Management");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2));
        nameField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Quantity:"));
        form.add(quantityField);
        add(form, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        add(buttons, BorderLayout.SOUTH);

        refreshTable();

        addBtn.addActionListener(e -> {
            Product p = new Product(0, nameField.getText(),
                    Integer.parseInt(quantityField.getText()),
                    Double.parseDouble(priceField.getText()));
            productDAO.insert(p);
            productBLL.validate(p);
            refreshTable();
            if (orderGUI != null) {
                orderGUI.loadData();
            }
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row, 0);
            Product p = new Product(id, nameField.getText(),
                    Integer.parseInt(quantityField.getText()),
                    Double.parseDouble(priceField.getText()));
            productDAO.update(p);
            productBLL.validate(p);
            refreshTable();
            if (orderGUI != null) {
                orderGUI.loadData();
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row, 0);
            productDAO.delete(id);
            refreshTable();
            if (orderGUI != null) {
                orderGUI.loadData();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                nameField.setText(model.getValueAt(row, 1).toString());
                priceField.setText(model.getValueAt(row, 2).toString());
                quantityField.setText(model.getValueAt(row, 3).toString());
            }
        });

        setVisible(true);
    }

    public void refreshTable() {
        model.setRowCount(0);
        for (Product p : productDAO.findAll()) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getQuantity()});
        }
    }

    public void setOrderGUI(OrderGUI orderGUI) {
        this.orderGUI = orderGUI;
    }
}
