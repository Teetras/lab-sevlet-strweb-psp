package by.iba.repository;

import by.iba.builder.Builder;
import by.iba.builder.BuildFactory;
import by.iba.exception.RepositoryException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.sql.*;
import java.util.*;

public abstract class AbstractRepository<T> implements Repository<T> {
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(AbstractRepository.class);
    private static final String GET_ALL_QUERY = "SELECT * FROM ";
    private final String WHERE_ID_CONDITION = " WHERE id_" + getTableName() + "=(?)";
    protected final String DELETE_QUERY = "DELETE from " + getTableName() + " where id_" + getTableName() + "=(?)";

    protected abstract String getTableName();

    AbstractRepository(Connection connection) {
        this.connection = connection;
    }

    private static Integer getType(Object object) {
        if (object instanceof Integer) {
            return Types.INTEGER;
        }
        if (object instanceof Float) {
            return Types.FLOAT;
        }
        if (object instanceof String) {
            return Types.VARCHAR;
        } else {
            return Types.NULL;
        }
    }

    public static void prepare(PreparedStatement preparedStatement, List<Object> parameters) throws SQLException {
        int length = parameters.size();
        for (int i = 0; i < length; i++) {
            if (parameters.get(i) == null) {
                preparedStatement.setNull(i + 1, getType(parameters.get(i)));
            } else {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
        }
    }

    public static void prepare(PreparedStatement preparedStatement, Map<String, Object> fields, String tableName) throws SQLException {
        int i = 1;
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (!key.equals(SQLHelper.ID)) {
                if (value == null) {
                    preparedStatement.setNull(i++, getType(value));
                } else {
                    preparedStatement.setObject(i++, value);
                }
            }
        }
        Object id = fields.get(SQLHelper.ID);
        if (id != null) {
            preparedStatement.setString(i++, String.valueOf(id));
        }
    }

    List<T> executeQuery(String sql, Builder<T> builder, List<Object> parameters) throws RepositoryException {
        List<T> objects = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            prepare(preparedStatement, parameters);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                T item = builder.build(resultSet);
                objects.add(item);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
        return objects;
    }

    protected Optional<T> executeQueryForSingleResult(String query, Builder<T> builder, List<Object> parameters) throws RepositoryException {
        List<T> items = executeQuery(query, builder, parameters);
        return items.size() == 1 ? Optional.of(items.get(0)) : Optional.empty();
    }

    protected abstract Map<String, Object> getFields(T obj);

    @Override
    public Integer save(T object) throws RepositoryException {
        String sql;
        Map<String, Object> fields = getFields(object);
        sql = SQLHelper.makeInsertQuery(fields, getTableName());
        return executeSave(sql, fields);
    }

    private Integer executeSave(String query, Map<String, Object> fields) throws RepositoryException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prepare(preparedStatement, fields, getTableName());
            logger.info(preparedStatement.toString());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            Integer generatedId = null;
            while (resultSet.next()) {
                generatedId = resultSet.getInt(1);
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findAll() throws RepositoryException {
        Builder builder = BuildFactory.create(getTableName());
        String query = GET_ALL_QUERY + getTableName();
        return executeQuery(query, builder, Collections.emptyList());
    }
}

