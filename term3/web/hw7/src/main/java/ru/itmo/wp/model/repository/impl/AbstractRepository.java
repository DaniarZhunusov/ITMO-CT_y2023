package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.exception.RepositoryException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractRepository<E> {
    private final DataSource dataSource = DatabaseUtils.getDataSource();

    protected E findBy(String sqlPattern, PatternFiller patternFiller, String errorMessage) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
                patternFiller.fill(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return toInstance(statement.getMetaData(), resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(errorMessage, e);
        }
    }


    protected List<E> findListBy(String sqlPattern, PatternFiller patternFiller, String errorMessage) {
        List<E> instances = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
                patternFiller.fill(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    E instance;
                    while ((instance = toInstance(statement.getMetaData(), resultSet)) != null) {
                        instances.add(instance);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(errorMessage, e);
        }
        return instances;
    }

    protected void update(String sqlPattern, PatternFiller patternFiller, String errorMessage) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
                patternFiller.fill(statement);
                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException(errorMessage);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(errorMessage, e);
        }
    }

    protected void save(E instance, String sqlPattern, PatternFiller patternFiller, MissingParametersSetter<E> setter,
                        String errorMessage) {

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlPattern, Statement.RETURN_GENERATED_KEYS)) {
                patternFiller.fill(statement);
                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException(errorMessage);
                } else {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        setter.set(generatedKeys, instance);
                    } else {
                        throw new RepositoryException(errorMessage + " [no autogenerated fields]");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(errorMessage, e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected long countBy(String sqlPattern, PatternFiller patternFiller, String errorMessage) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
                patternFiller.fill(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(errorMessage, e);
        }
    }

    protected boolean contains(String sqlPattern, PatternFiller patternFiller, String errorMessage) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sqlPattern)) {
                patternFiller.fill(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(errorMessage, e);
        }
    }

    abstract protected E toInstance(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException;
}
