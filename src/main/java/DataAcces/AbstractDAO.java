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

    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

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
}

