package DNBN.spring.repository.CommentRepository;

import DNBN.spring.domain.Comment;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByArticle(Article article);
    List<Comment> findAllByMember(Member member);
    List<Comment> findAllByPlace(Place place);
    List<Comment> findAllByRegion(Region region);
}

