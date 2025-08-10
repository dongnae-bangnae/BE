package DNBN.spring.repository.CommentRepository;

import DNBN.spring.domain.Comment;
import java.util.List;

public interface CommentRepositoryCustom {
    List<Comment> findParentCommentsByCursor(Long articleId, Long cursor, int limit);
    List<Comment> findRepliesByParentId(Long parentCommentId, Long cursor, int limit);
}

