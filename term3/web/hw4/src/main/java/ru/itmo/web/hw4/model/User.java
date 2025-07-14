package ru.itmo.web.hw4.model;

public class User {
    private final long id;
    private final String handle;
    private final String name;
    private final UserColor userColor;

    public User(long id, String handle, String name, UserColor userColor) {
        this.id = id;
        this.handle = handle;
        this.name = name;
        this.userColor = userColor;
    }

    public long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public UserColor getUserColor() {
        return userColor;
    }
}
