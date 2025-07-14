package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.User;
import java.util.List;

public interface UserRepository {
    User find(long id);
    User findByLogin(String login);
    User findByLoginAndPasswordSha(String login, String passwordSha);
    List<User> findAll();
    void save(User user, String passwordSha);
    User findByEmailAndPasswordSha(String email, String passwordSha);
    User findByEmail(String email);
    long countAll();
    boolean contains(long id);
    void changeAdminStatus(long userId, boolean status);
}
