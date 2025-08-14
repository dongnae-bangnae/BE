package DNBN.spring.service.ArticleLikeService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticleLike;
import DNBN.spring.domain.ArticleLikeId;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.LikeResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static DNBN.spring.domain.QArticle.article;
import static DNBN.spring.domain.QMember.member;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository articleLikeRepository;
    @Transactional
    public LikeResponseDTO likeArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));

        ArticleLikeId likeId = new ArticleLikeId(articleId, memberId);

        if (articleLikeRepository.existsById(likeId)) {
            throw new GeneralException(ErrorStatus.ALREADY_LIKED);
        }
        ArticleLike articleLike = ArticleLike.builder()
                .id(likeId)
                .article(article)
                .member(memberRepository.getReferenceById(memberId))
                .build();

        articleLikeRepository.save(articleLike);
        article.increaseLikeCount();
        long likesCount = article.getLikesCount();
        return LikeResponseDTO.of(articleId, likesCount);
    }

    @Transactional
    public LikeResponseDTO unlikeArticle(Long articleId, Long memberId) {
        ArticleLikeId likeId = new ArticleLikeId(articleId, memberId);
        ArticleLike articleLike = articleLikeRepository.findById(likeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_LIKED));

        articleLikeRepository.delete(articleLike);
        Article article = articleLike.getArticle();
        article.decreaseLikeCount();
        long likesCount = article.getLikesCount();
        return LikeResponseDTO.of(articleId, likesCount);
    }
}
