package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class Page {
    private final UserService userService = new UserService();
    private HttpServletRequest request;

    private void putUser(HttpServletRequest request, Map<String, Object> view) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            view.put("user", user);
        }
    }

    private void putMessage(HttpServletRequest request, Map<String, Object> view) {
        String message = (String) request.getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    private void putError(HttpServletRequest request, Map<String, Object> view) {
        String message = (String) request.getSession().getAttribute("splashError");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("splashError", message);
            request.getSession().removeAttribute("splashError");
        }
    }
    protected void removeUser() {
        request.getSession().removeAttribute("user");
    }

    protected void setMessage(String message) {
        if (request != null) {
            request.getSession().setAttribute("message", message);
        }
    }

    protected void setSplashError(String message) {
        if (request != null) {
            request.getSession().setAttribute("splashError", message);
        }
    }

    protected void setUser(User user) {
        request.getSession().setAttribute("user", user);
    }

    protected User getUser() {
        return (User) request.getSession().getAttribute("user");
    }

    protected void before(HttpServletRequest request, Map<String, Object> view) {
        this.request = request;
        putUser(request, view);
        putMessage(request, view);
        putError(request, view);
    }

    protected void after(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    protected void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }
}
