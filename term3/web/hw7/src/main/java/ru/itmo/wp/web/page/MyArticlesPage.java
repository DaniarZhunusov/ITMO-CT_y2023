package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyArticlesPage extends Page {
    private final ArticleService articleService = new ArticleService();
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        User user = getUser();
        if (user == null) {
            setSplashError("Only for logged in users");
            throw new RedirectException("/index");
        }
        view.put("articles", articleService.findByUserId(user.getId()));
    }

    @Json
    private void hide(HttpServletRequest request, Map<String, Object> view) {
         changeHidden(request, view, true);
    }

    @Json
    private void show(HttpServletRequest request, Map<String, Object> view) {
         changeHidden(request, view, false);
    }

    @Json
    private void changeHidden(HttpServletRequest request, Map<String, Object> view, boolean value) {
        User user = getUser();
        view.put("hidden", !value);
        String s = request.getParameter("articleId");
        long id;
        try {
            id = Long.parseLong(s);
        } catch (NumberFormatException | RepositoryException e) {
            view.put("error", "Invalid article");
            return;
        }
        Article article;
        if (articleService.contains(id)) {
            article = articleService.find(id);
        } else {
            view.put("error", "Invalid article");
            return;
        }

        if (user == null || article.getUserId() != user.getId()) {
            setSplashError("Unexpected user");
            throw new RedirectException("/index");
        }
        articleService.changeHidden(article, value);
        view.put("hidden", value);
    }
}
