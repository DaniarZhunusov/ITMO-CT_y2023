package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.database.DatabaseUtils;
import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.TalkRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TalkRepositoryImpl extends BasicRepositoryImpl<Talk> implements TalkRepository {
    private final DataSource DATA_SOURCE = DatabaseUtils.getDataSource();

    @Override
    public Talk find(long id) {
        return super.find(id, "Talk");
    }

    @Override
    public void save(Talk talk) {
        save(talk,
                "INSERT INTO `Talk` (`sourceUserId`, `targetUserId`, `text`, `creationTime`) VALUES (?, ?, ?, NOW())",
                Statement.RETURN_GENERATED_KEYS,
                String.valueOf(talk.getSourceUserId()),
                String.valueOf(talk.getTargetUserId()),
                talk.getText());
    }

    @Override
    protected Talk toElement(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Talk talk = new Talk();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id":
                    talk.setId(resultSet.getLong(i));
                    break;
                case "sourceUserId":
                    talk.setSourceUserId(resultSet.getLong(i));
                    break;
                case "targetUserId":
                    talk.setTargetUserId(resultSet.getLong(i));
                    break;
                case "text":
                    talk.setText(resultSet.getString(i));
                    break;
                case "creationTime":
                    talk.setCreationTime(resultSet.getTimestamp(i));
                    break;
                default:
                    // No operations.
            }
        }
        return talk;
    }

    private List<Talk> findAllBy(String sqlRequest, Object... params) {
        List<Talk> talks = new ArrayList<>();
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM Talk " + sqlRequest)) {
                setParams(params, statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    Talk talk;
                    while ((talk = toElement(statement.getMetaData(), resultSet)) != null) {
                        talks.add(talk);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't find talks", e);
        }
        return talks;
    }

    private void setParams(Object[] params, PreparedStatement statement) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Long) {
                statement.setLong(i + 1, (Long) params[i]);
            } else if (params[i] instanceof String) {
                statement.setString(i + 1, (String) params[i]);
            } else {
                throw new RepositoryException(
                        String.join(" ", "Unknown parameter:", params[i].toString())
                );
            }
        }
    }


    @Override
    public List<Talk> findAll() {
        return findAllBy("ORDER BY creationTime DESC");
    }

    @Override
    public List<Talk> findAllById(long id) {
        return findAllBy("WHERE sourceUserId=? OR targetUserId=? ORDER BY creationTime DESC", id, id);
    }
}
