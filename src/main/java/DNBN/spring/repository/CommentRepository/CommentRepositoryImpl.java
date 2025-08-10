package DNBN.spring.repository.CommentRepository;

import DNBN.spring.domain.Comment;
import DNBN.spring.domain.QComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findParentCommentsByCursor(Long articleId, Long cursor, int limit) {
        QComment comment = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.article.articleId.eq(articleId));
        builder.and(comment.parentComment.isNull());
        builder.and(comment.deletedAt.isNull());
        if (cursor != null) {
            builder.and(comment.commentId.lt(cursor));
        }
        return queryFactory
                .selectFrom(comment)
                .where(builder)
                .orderBy(comment.commentId.desc())
                .limit(limit + 1)
                .fetch();
    }

    @Override
    public List<Comment> findRepliesByParentId(Long parentCommentId, Long cursor, int limit) {
        QComment comment = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.parentComment.commentId.eq(parentCommentId));
        builder.and(comment.deletedAt.isNull());
        if (cursor != null) {
            builder.and(comment.commentId.lt(cursor));
        }
        return queryFactory
                .selectFrom(comment)
                .where(builder)
                .orderBy(comment.commentId.desc())
                .limit(limit + 1)
                .fetch();
    }
}