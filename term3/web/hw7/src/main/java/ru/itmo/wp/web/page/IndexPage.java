package ru.itmo.wp.web.page;

import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class IndexPage extends Page {
    private final ArticleService articleService = new ArticleService();

    @Json
    private void findAllPosts(HttpServletRequest request, Map<String, Object> view) {
        view.put("articles", articleService.toArticleRecord(articleService.findAllByTime()));
    }
}
