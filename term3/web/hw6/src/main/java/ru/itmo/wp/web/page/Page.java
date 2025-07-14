package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.model.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public abstract class Page {
    protected HttpServletRequest request;
    protected UserService userService = new UserService();
    protected EventService eventService = new EventService();


    protected void setMessage(String message) {
        request.getSession().setAttribute("message", message);
    }

    protected void setUser(User user) {
        request.getSession().setAttribute("user", user);
    }

    protected User getUser() {
        return (User) request.getSession().getAttribute("user");
    }

    public void before(HttpServletRequest request, Map<String, Object> view) {
        this.request = request;
        view.put("userCount", userService.findCount());

        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }

        String message = (String) request.getSession().getAttribute("message");
        if (message != null) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    public void after(HttpServletRequest request, Map<String, Object> view) {
    }

    private void action(HttpServletRequest request, Map<String, Object> view) {
    }
}
