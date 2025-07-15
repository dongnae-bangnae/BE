package DNBN.spring.repository.ArticleLikeRepository;

import DNBN.spring.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    boolean existsByArticleIdAndMemberId(Long articleId, Long memberId);
    Optional<ArticleLike> findByArticleIdAndMemberId(Long articleId, Long memberId);
    long countByArticleId(Long articleId);
}