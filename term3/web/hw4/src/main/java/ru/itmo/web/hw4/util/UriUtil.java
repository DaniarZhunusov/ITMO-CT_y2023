package ru.itmo.web.hw4.util;

import java.util.Objects;

public class UriUtil {
    public static final String DEFAULT_PAGE_URI = "/index";

    public static boolean isNeededRedirectToMain(String uri) {
        return Objects.equals(uri, "/") || uri.isEmpty();
    }

    public static String getCurrentMenuPage(String uri) {
        if (uri.startsWith("/user") || uri.startsWith("/posts")) {
            return "/users";
        }
        return uri;
    }
}
