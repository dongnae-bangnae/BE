package DNBN.spring.repository.ArticleRepository;

import DNBN.spring.domain.Article;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> findArticlesByCategoryWithCursor(Long categoryId, Long cursor, Long limit);

    // V1: 단일 커서(Long) 방식
    List<Article> findArticlesByPlaceWithCursorV1(Long placeId, Long cursor, Long limit);

    // V2
    List<Article> findArticlesByPlaceWithCursorV2(Long placeId, LocalDateTime cursorCreatedAt, Long cursorArticleId, Long limit);
}
