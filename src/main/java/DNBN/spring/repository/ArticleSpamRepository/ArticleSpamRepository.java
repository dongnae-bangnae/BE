package DNBN.spring.repository.ArticleSpamRepository;

import DNBN.spring.domain.ArticleSpam;
import DNBN.spring.domain.ArticleSpamId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleSpamRepository extends JpaRepository<ArticleSpam, ArticleSpamId> {
    boolean existsById(ArticleSpamId id);
    //boolean existsByArticle_ArticleIdAndMember_Id(Long articleId, Long memberId);
    Optional<ArticleSpam> findByArticle_ArticleIdAndMember_Id(Long articleId, Long memberId);
    long countByArticle_ArticleId(Long articleId);
}