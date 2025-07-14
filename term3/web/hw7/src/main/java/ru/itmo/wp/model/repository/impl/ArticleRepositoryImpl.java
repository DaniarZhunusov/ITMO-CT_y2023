package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.repository.ArticleRepository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class ArticleRepositoryImpl extends AbstractRepository<Article> implements ArticleRepository {
    MissingParametersSetter<Article> setter = (generatedKeys, instance) -> {
        instance.setId(generatedKeys.getLong(1));
        instance.setCreationTime(find(instance.getId()).getCreationTime());
    };

    @Override
    protected Article toInstance(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Article article = new Article();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id":
                    article.setId(resultSet.getLong(i));
                    break;
                case "userId":
                    article.setUserId(resultSet.getLong(i));
                    break;
                case "title":
                    article.setTitle(resultSet.getString(i));
                    break;
                case "text":
                    article.setText(resultSet.getString(i));
                    break;
                case "hidden":
                    article.setHidden(resultSet.getBoolean(i));
                    break;
                case "creationTime":
                    article.setCreationTime(resultSet.getTimestamp(i));
                    break;
                default:
                    // No operations.
            }
        }

        return article;
    }

    @Override
    public void save(Article article) {
        PatternFiller patternFiller = statement -> {
            statement.setLong(1, article.getUserId());
            statement.setString(2, article.getTitle());
            statement.setString(3, article.getText());
            statement.setBoolean(4, article.isHidden());
        };
        save(article, "INSERT INTO `Article` (`userId`, `title`, `text`, `creationTime`, `hidden`) VALUES (?, ?, ?, NOW(), ?)",
                patternFiller, setter, "Can't save Article.");

    }

    @Override
    public Article find(long id) {
        PatternFiller filler = statement -> statement.setLong(1, id);
        return findBy("SELECT * FROM Article WHERE id=?", filler, "Can't find Article.");
    }

    @Override
    public List<Article> findAll() {
        return findListBy("SELECT * FROM Article ORDER BY id DESC", x -> {
        }, "Can't find Article.");
    }

    @Override
    public List<Article> findAllByTime() {
        return findListBy("SELECT * FROM Article WHERE hidden=false ORDER BY creationTime DESC", x -> {
        }, "Can't find Article.");
    }

    @Override
    public List<Article> findByUserId(long userId) {
        return findListBy("SELECT * FROM Article WHERE userId=? ORDER BY id DESC",
                x -> x.setLong(1, userId), "Can't find Article.");
    }

    @Override
    public void changeHidden(Article article, boolean value) {
        PatternFiller filler = statement ->
        {
            statement.setBoolean(1, value);
            statement.setLong(2, article.getId());
        };
        update("UPDATE Article SET hidden=? WHERE id=?", filler, "Can't update Article.");
    }

    @Override
    public boolean contains(long id) {
        PatternFiller filler = statement -> statement.setLong(1, id);
        return countBy("SELECT COUNT(*) FROM Article WHERE id=?", filler, "Can't find Article.") >= 1;
    }
}
