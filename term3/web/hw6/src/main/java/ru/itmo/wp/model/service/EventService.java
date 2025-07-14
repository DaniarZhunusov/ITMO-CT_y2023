package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.repository.impl.EventRepositoryImpl;

public class EventService {
    private final EventRepositoryImpl eventRepositoryImpl = new EventRepositoryImpl();

    public void save(Event event) {
        eventRepositoryImpl.save(event);
    }
}