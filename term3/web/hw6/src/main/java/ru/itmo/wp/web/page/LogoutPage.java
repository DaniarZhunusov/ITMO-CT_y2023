package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class LogoutPage extends Page {
    private void action(HttpServletRequest request, Map<String, Object> view) {
        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            eventService.save(new Event(getUser().getId(), Event.Type.LOGOUT));
            setUser(null);
            setMessage("Goodbye. Hope to see you soon!");
        }
        throw new RedirectException("/index");
    }
}
