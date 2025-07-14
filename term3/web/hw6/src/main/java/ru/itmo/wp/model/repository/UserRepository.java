package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.User;

import java.util.List;

public interface UserRepository {
    User find(long id);

    User findByLogin(String login);
    User findByEmail(String email);

    User findByLoginOrEmailAndPassword(String login, String passwordSha);

    List<User> findAll();
    long findCount();

    void save(User user, String passwordSha);
}
