package DNBN.spring.repository.ArticleSpamRepository;

import DNBN.spring.domain.ArticleSpam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleSpamRepository extends JpaRepository<ArticleSpam, Long> {
    boolean existsByArticleIdAndMemberId(Long articleId, Long memberId);
    Optional<ArticleSpam> findByArticleIdAndMemberId(Long articleId, Long memberId);
    long countByArticleId(Long articleId);
}