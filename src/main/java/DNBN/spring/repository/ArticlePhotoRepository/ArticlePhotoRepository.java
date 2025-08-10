package DNBN.spring.repository.ArticlePhotoRepository;

import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticlePhotoRepository extends JpaRepository<ArticlePhoto, Long> {
    List<ArticlePhoto> findAllByArticle(Article article);
    Optional<ArticlePhoto> findFirstByArticleAndIsMainTrue(Article article);
    List<ArticlePhoto> findAllByArticle_Member(Member member);
}

