package DNBN.spring.repository.ArticleRepository;

import DNBN.spring.domain.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findArticlesByCategoryWithCursor(Long categoryId, Long cursor, Long limit);

    List<Article> findArticlesByPlaceWithCursor(Long placeId, Long cursor, Long limit);
}
