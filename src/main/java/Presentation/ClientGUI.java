package Presentation;

import BusinessLogic.ClientBLL;
import DataAcces.AbstractDAO;
import Model.Client;
import DataAcces.ClientDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 */

public class ClientGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField;
    private ClientBLL clientBLL = new ClientBLL();
    private AbstractDAO<Client> clientDAO = new AbstractDAO<Client>() {};

    public ClientGUI(OrderGUI orderGUI) {

        setTitle("Client Management");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(1, 2));
        nameField = new JTextField();
        form.add(new JLabel("Name:"));
        form.add(nameField);
        add(form, BorderLayout.NORTH);

        model = new DefaultTableModel() {
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
            Client c = new Client(0, nameField.getText());
            clientBLL.validate(c);
            clientDAO.insert(c);
            refreshTable();
            orderGUI.loadData();
        });

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row, 0);
            Client c = new Client(id, nameField.getText());
            clientBLL.validate(c);
            clientDAO.update(c);
            refreshTable();
            orderGUI.loadData();
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            int id = (int) model.getValueAt(row, 0);
            clientDAO.delete(id);
            refreshTable();
            orderGUI.loadData();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                nameField.setText(model.getValueAt(row, 1).toString());
            }
        });

        setVisible(true);
    }

    private void refreshTable() {
        List<Client> clients = clientDAO.findAll();
        try {
            String[] headers = clientDAO.getTableHeaders(clients.get(0));
            Object[][] data = clientDAO.getTableData(clients);
            model.setDataVector(data, headers);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
