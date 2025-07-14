package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TalksPage extends Page {
    private final UserService userService = new UserService();
    private final TalkService talkService = new TalkService();

    @Override
    public void before(HttpServletRequest request, Map<String, Object> view) {
        super.before(request, view);
        User user = getUser();
        try {
            talkService.validateUser(user);
        } catch (ValidationException e) {
            setMessage("You must be logged in to access the talks");
            throw new RedirectException("/index");
        }
        putParameters(view, user.getId());
    }

    private void putParameters(Map<String, Object> view, long id) {
        view.put("talks", talkService.findAllById(id));
        view.put("users", userService.findAll());
    }

    private void sendTalk(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User sourceUser = getUser();
        try {
            talkService.validateUser(sourceUser);
        } catch (ValidationException e) {
            setMessage("You must be logged in to send the talks");
            throw new RedirectException("/index");
        }
        long sourceUserId = sourceUser.getId();
        String targetUserLogin = request.getParameter("targetUserLogin");
        String text = request.getParameter("text");
        try {
            talkService.validateText(text);
        } catch (ValidationException e) {
            putParameters(view, sourceUserId);
            throw e;
        }
        User targetUser = userService.findByLogin(targetUserLogin);
        talkService.validateUser(targetUser);
        talkService.saveTalk(sourceUserId, targetUser.getId(), text);

        request.getSession().setAttribute("message", "Message sent");
        throw new RedirectException("/talks");
    }
}
