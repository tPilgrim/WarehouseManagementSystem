package DataAcces;

import Model.Bill;
import Connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 */

public class BillDAO {

    /**
     * Inserts a new bill into the database
     *
     * @param bill the bill to be added
     */
    public void insert(Bill bill) {
        String query = "INSERT INTO bill (orderId, clientName, productName, quantity, totalPrice) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bill.orderId());
            statement.setString(2, bill.clientName());
            statement.setString(3, bill.productName());
            statement.setInt(4, bill.quantity());
            statement.setDouble(5, bill.totalPrice());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all bills from the databas
     *
     * @return a list of all bills
     */
    public List<Bill> findAll() {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bill";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill(
                        rs.getInt("id"),
                        rs.getInt("orderId"),
                        rs.getString("clientName"),
                        rs.getString("productName"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalPrice")
                );
                bills.add(bill);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }
}
