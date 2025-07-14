package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ArticlePage extends Page {
    private final ArticleService articleService = new ArticleService();
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        check(getUser());
    }

    private void check(User user) {
        if (user == null) {
            setSplashError("Articles available only for logged in users");
            throw new RedirectException("/index");
        }
    }

    @Json
    private void post(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = getUser();
        check(user);
        Article article = new Article();
        article.setTitle(request.getParameter("title").trim());
        article.setText(request.getParameter("text").trim());
        article.setHidden(Boolean.parseBoolean(request.getParameter("hidden")));
        article.setUserId(user.getId());
        articleService.validate(article);
        articleService.create(article);
        setMessage("Post was created");
        throw new RedirectException("/index");
    }

}