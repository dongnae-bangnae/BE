package DNBN.spring.repository.ArticleRepository;

import DNBN.spring.domain.Article;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findArticlesByCategoryWithCursor(Long categoryId, Long cursor, Long limit);

    List<Article> findArticlesByPlaceWithCursor(Long placeId, LocalDateTime cursorCreatedAt, Long cursorArticleId, Long limit);
}
