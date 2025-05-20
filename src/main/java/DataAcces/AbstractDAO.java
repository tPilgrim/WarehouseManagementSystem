package DataAcces;

import Connection.ConnectionFactory;

import java.sql.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: Souca Vlad-Cristian
 * @since: May 05, 2025
 * Source: https://gitlab.com/utcn_dsrl/pt-reflection-example
 */

public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Builds a SELECT query for a field
     *
     * @param field the column name
     * @return the SQL query string
     */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    /**
     * Finds and returns all rows from the table.
     *
     * @return list of all objects from the table
     */
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        String query = "SELECT * FROM " + type.getSimpleName();
        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            list = createObjects(resultSet);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        }
        return list;
    }

    /**
     * Finds a row by ID.
     *
     * @param id the ID of the object
     * @return the object with the ID
     */
    public T findById(int id) {
        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Creates a list of objects from the database result.
     *
     * @param resultSet the result of the SQL query
     * @return a list of objects T
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T)ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Builds an INSERT SQL query for the object.
     *
     * @param t the object to insert
     * @param fieldValues the values to insert
     * @return the SQL query
     */
    String insertQuery(T t, List<Object> fieldValues){
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (value != null) {
                    fields.append(field.getName()).append(",");
                    values.append("?,");
                    fieldValues.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (fields.length() > 0){
            fields.setLength(fields.length() - 1);
        }
        if (values.length() > 0){
            values.setLength(values.length() - 1);
        }

        return "INSERT INTO `" + type.getSimpleName() + "` (" + fields + ") VALUES (" + values + ")";
    }

    /**
     * Inserts a new row.
     *
     * @param t the object to insert
     * @return the inserted object
     */
    public T insert(T t) {
        List<Object> fieldValues = new ArrayList<>();
        String query = insertQuery(t, fieldValues);

        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < fieldValues.size(); i++) {
                statement.setObject(i + 1, fieldValues.get(i));
            }

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                for (Field field : type.getDeclaredFields()) {
                    if (field.getName().equalsIgnoreCase("id")) {
                        field.setAccessible(true);
                        field.set(t, generatedKeys.getInt(1));
                        break;
                    }
                }
            }

        } catch (SQLException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        }
        return t;
    }

    /**
     * Builds an UPDATE SQL query for the object.
     *
     * @param t the object to update
     * @param fieldValues the updated values
     * @param idValue the object's ID
     * @return the SQL UPDATE query
     */
    String updateQuery(T t, List<Object> fieldValues, int[] idValue){
        StringBuilder setClause = new StringBuilder();

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (field.getName().equals("id")) {
                    idValue[0] = (Integer) value;
                    continue;
                }
                setClause.append(field.getName()).append(" = ?, ");
                fieldValues.add(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (setClause.length() > 0) setClause.setLength(setClause.length() - 2);

        return "UPDATE `" + type.getSimpleName() + "` SET " + setClause + " WHERE id = ?";
    }

    /**
     * Updates a row in the database.
     *
     * @param t the object to update
     * @return the updated object
     */
    public T update(T t) {
        List<Object> fieldValues = new ArrayList<>();
        int[] idValue = new int[1];
        String query = updateQuery(t, fieldValues, idValue);

        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < fieldValues.size(); i++) {
                statement.setObject(i + 1, fieldValues.get(i));
            }
            statement.setObject(fieldValues.size() + 1, idValue[0]);

            statement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        }
        return t;
    }

    /**
     * Deletes a row by its ID.
     *
     * @param id the ID od the object to delete
     */
    public void delete(int id) {
        String query = "DELETE FROM `" + type.getSimpleName() + "` WHERE id = ?";

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        }
    }

    public String[] getTableHeaders(T t) {

        Field[] fields = type.getDeclaredFields();
        String[] headers = new String[fields.length];

        int i = 0;
        for (Field field : type.getDeclaredFields()) {
            headers[i] = field.getName();
            i++;
        }

        return headers;
    }

    public Object[][] getTableData(List<T> objects) throws IllegalAccessException {

        int rowCount = objects.size();
        Field[] fields = objects.get(0).getClass().getDeclaredFields();
        int colCount = fields.length;

        Object[][] data = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            T object = objects.get(i);
            for (int j = 0; j < colCount; j++) {
                fields[j].setAccessible(true);
                data[i][j] = fields[j].get(object);
            }
        }

        return data;
    }
}

