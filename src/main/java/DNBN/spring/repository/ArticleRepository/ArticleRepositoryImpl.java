package DNBN.spring.repository.ArticleRepository;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.QArticle;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Article> findArticlesByCategoryWithCursor(Long categoryId, Long cursor, Long limit) {
        QArticle article = QArticle.article;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(article.category.categoryId.eq(categoryId));
        builder.and(article.deletedAt.isNull());

        if (cursor != null) {
            builder.and(article.articleId.lt(cursor));
        }

        return queryFactory
                .selectFrom(article)
                .where(builder)
                .orderBy(article.articleId.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Article> findArticlesByPlaceWithCursor(Long placeId, LocalDateTime cursorCreatedAt, Long cursorArticleId, Long limit) {
        QArticle article = QArticle.article;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(article.place.placeId.eq(placeId));
        builder.and(article.deletedAt.isNull());

        if (cursorCreatedAt != null && cursorArticleId != null) {
            builder.and(
                article.createdAt.lt(cursorCreatedAt)
                .or(article.createdAt.eq(cursorCreatedAt).and(article.articleId.lt(cursorArticleId)))
            );
        }

        return queryFactory
            .selectFrom(article)
            .where(builder)
            .orderBy(article.createdAt.desc(), article.articleId.desc())
            .limit(limit)
            .fetch();
    }
}
