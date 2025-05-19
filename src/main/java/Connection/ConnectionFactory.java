package Connection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/ordersmanagement?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static ConnectionFactory singleInstance = new ConnectionFactory();

    private ConnectionFactory() {
        try{
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private Connection createConnection() {
        Connection connection = null;

        try{
            connection = DriverManager.getConnection(DBURL, USER, PASSWORD);
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Nu te poti conecta la baza de date");
            ex.printStackTrace();
        }
        return connection;
    }

    public static Connection getConnection() {
        return singleInstance.createConnection();
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Conexiunea nu poate fi inchisa");
            }
        }
    }

    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Conexiunea nu poate fi inchisa");
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Conexiunea nu poate fi inchisa");
            }
        }
    }
}
