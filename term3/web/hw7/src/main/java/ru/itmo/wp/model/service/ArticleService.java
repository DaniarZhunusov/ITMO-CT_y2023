package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

import java.util.List;

public class ArticleService {
    ArticleRepository articleRepository = new ArticleRepositoryImpl();
    UserService userService = new UserService();

    public void create(Article article) {
        articleRepository.save(article);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<Article> findAllByTime() {
        return articleRepository.findAllByTime();
    }

    public ArticleRecord toArticleRecord(Article article) {
        return new ArticleRecord(article.getTitle(), article.getText(),
                article.getCreationTime(), userService.find(article.getUserId()).getLogin());
    }
    public List<ArticleRecord> toArticleRecord (List<Article> list) {
        return list.stream().map(this::toArticleRecord).toList();
    }

    public void changeHidden(Article article, boolean value) {
        article.setHidden(value);
        articleRepository.changeHidden(article, value);
    }

    public Article find(long id) {
        return articleRepository.find(id);
    }
    public List<Article> findByUserId(long id) {
        return articleRepository.findByUserId(id);
    }
    public void validate(Article article) throws ValidationException {
        if (article.getTitle().trim().equals("")) {
            throw new ValidationException("Empty title");
        }

        if (article.getText().trim().equals("")) {
            throw new ValidationException("Empty text");
        }

        if (article.getTitle().length() < 5) {
            throw new ValidationException("Too short article title");
        }

        if (userService.find(article.getUserId()) == null) {
            throw new ValidationException("Invalid User");
        }

        if (article.getText().length() < 15) {
            throw new ValidationException("Too short article text");
        }
    }

    public boolean contains(long id) {
        return articleRepository.contains(id);
    }

}
