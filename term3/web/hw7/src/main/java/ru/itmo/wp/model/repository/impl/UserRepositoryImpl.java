package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class UserRepositoryImpl extends AbstractRepository<User> implements UserRepository {
    private final MissingParametersSetter<User> setter = (generatedKeys, instance) -> {
        instance.setId(generatedKeys.getLong(1));
        instance.setCreationTime(find(instance.getId()).getCreationTime());
    };

    @Override
    public User find(long id) {
        PatternFiller patternFiller = statement -> statement.setLong(1, id);
        return findBy("SELECT * FROM User WHERE id=?",
                patternFiller, "Can't find User.");
    }

    @Override
    public User findByLogin(String login) {
        PatternFiller patternFiller = statement -> statement.setString(1, login);
        return findBy("SELECT * FROM User WHERE login=?",
                patternFiller, "Can't find User.");
    }

    @Override
    public User findByLoginAndPasswordSha(String login, String passwordSha) {
        PatternFiller patternFiller = statement -> {
            statement.setString(1, login);
            statement.setString(2, passwordSha);
        };
        return findBy("SELECT * FROM User WHERE login=? AND passwordSha=?",
                patternFiller, "Can't find User.");
    }

    @Override
    public User findByEmailAndPasswordSha(String email, String passwordSha) {
        PatternFiller patternFiller = statement -> {
            statement.setString(1, email);
            statement.setString(2, passwordSha);
        };
        return findBy("SELECT * FROM User WHERE email=? AND passwordSha=?",
                patternFiller, "Can't find User.");
    }

    @Override
    public List<User> findAll() {
        return findListBy("SELECT * FROM User ORDER BY id DESC", x -> {
        }, "Can't find User.");
    }

    @Override
    public User findByEmail(String email) {
        PatternFiller patternFiller = statement -> statement.setString(1, email);
        return findBy("SELECT * FROM User WHERE email=?",
                patternFiller, "Can't find User.");
    }

    @Override
    public void save(User user, String passwordSha) {
        PatternFiller patternFiller = statement -> {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getEmail());
            statement.setString(3, passwordSha);
        };
        save(user, "INSERT INTO `User` (`login`, `email`, `passwordSha`, `creationTime`) VALUES (?, ?, ?, NOW())",
                patternFiller, setter, "Can't save User.");
    }

    @Override
    public long countAll() {
        PatternFiller filler = x -> {
        };
        return countBy("SELECT COUNT(*) FROM `User`", filler, "Can't find User.");
    }

    @Override
    public void changeAdminStatus(long userId, boolean status) {
        PatternFiller patternFiller = statement -> {
            statement.setBoolean(1, status);
            statement.setLong(2, userId);
        };
        update("UPDATE User SET admin=? WHERE id=?", patternFiller, "Can't update User.");
    }

    @Override
    protected User toInstance(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        User user = new User();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id":
                    user.setId(resultSet.getLong(i));
                    break;
                case "login":
                    user.setLogin(resultSet.getString(i));
                    break;
                case "email":
                    user.setEmail(resultSet.getString(i));
                    break;
                case "creationTime":
                    user.setCreationTime(resultSet.getTimestamp(i));
                    break;
                case "admin":
                    user.setAdmin(resultSet.getBoolean(i));
                    break;
                default :
                    // No operations.
            }
        }

        return user;
    }

    @Override
    public boolean contains(long id) {
        PatternFiller filler = statement -> statement.setLong(1, id);
        return countBy("SELECT COUNT(*) FROM `User` WHERE id=?", filler, "Can't find User.") >= 1;
    }
}
