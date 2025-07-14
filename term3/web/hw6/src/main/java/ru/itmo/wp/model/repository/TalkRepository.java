package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Talk;

import java.util.List;

public interface TalkRepository {
    void save(Talk talk);
    Talk find(long id);
    List<Talk> findAll();
    List<Talk> findAllById(long id);
}
