package ru.itmo.wp.model.domain;

import java.io.Serializable;
import java.util.Date;

public class Event extends BaseElement implements Serializable {
    public enum Type {
        ENTER, LOGOUT
    }

    public Event(long userId, Event.Type type) {
        this.userId = userId;
        this.type = type;
    }

    public Event() {

    }

    private long id;
    private long userId;
    private Type type;
    private Date creationTime;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public Date getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
