package DNBN.spring.repository.CommentRepository;

import DNBN.spring.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findAllByArticle(DNBN.spring.domain.Article article);
}
