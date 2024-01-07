package by.iba.repository;

import by.iba.connection.ConnectionPool;
import by.iba.repository.PersonRepository;
import by.iba.repository.UserRepository;

import java.sql.Connection;

public class RepositoryCreator implements AutoCloseable {
    private ConnectionPool connectionPool;
    private Connection connection;

    public RepositoryCreator() {
        connectionPool = ConnectionPool.getInstance();
        connection = connectionPool.getConnection();
    }

    public UserRepository getUserRepository() {
        return new UserRepository(connection);
    }

    public PersonRepository getPersonRepository() {
        return new PersonRepository(connection);
    }

    @Override
    public void close() {
        connectionPool.releaseConnection(connection);
    }
}
