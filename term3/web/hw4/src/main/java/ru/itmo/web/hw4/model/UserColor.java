package ru.itmo.web.hw4.model;

public enum UserColor {
    RED("Red"),
    BLUE("Blue"),
    GREEN("Green");

    private final String cssClass;

    UserColor(String cssClass) {
        this.cssClass = cssClass;
    }

    public String toCssClass() {
        return cssClass;
    }
}