package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @noinspection unused
 */
public class UsersPage extends Page {
    private final UserService userService = new UserService();


    @Json
    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    @Json
    private void disable(HttpServletRequest request, Map<String, Object> view) {
        changeAdminStatus(request, view, false);
    }

    @Json
    private void enable(HttpServletRequest request, Map<String, Object> view) {
        changeAdminStatus(request, view, true);
    }

    private void changeAdminStatus(HttpServletRequest request, Map<String, Object> view, boolean status) {
        User user = getUser();
        long userId;
        try {
            userId = Long.parseLong(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            view.put("error", "Invalid changeable user");
            return;
        }

        if (user == null || !getUser().isAdmin()) {
            setSplashError("Enable only for admins");
            throw new RedirectException("/index");
        }

        if (!userService.contains(userId)) {
            view.put("error", "Invalid changeable user");
            return;
        }
        userService.changeAdminStatus(userId, status);
        if (userId == user.getId()) {
            user.setAdmin(status);
        }
        view.put("admin", status);

    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user", userService.find(Long.parseLong(request.getParameter("userId"))));
    }
}
