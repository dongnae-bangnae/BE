package DNBN.spring.service.ArticleLikeService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticleLike;
import DNBN.spring.domain.ArticleLikeId;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.LikeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static DNBN.spring.domain.QArticle.article;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository articleLikeRepository;

    public LikeResponseDTO likeArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));

        ArticleLikeId likeId = new ArticleLikeId(articleId, memberId);

        if (articleLikeRepository.existsById(likeId)) {
            throw new GeneralException(ErrorStatus.ALREADY_LIKED);
        }

        ArticleLike articleLike = ArticleLike.builder()
                .article(article)
                .member(memberRepository.getReferenceById(memberId))
                .build();

        articleLikeRepository.save(articleLike);

        long likesCount = articleLikeRepository.countByArticle(article);
        long spamCount = 0;
        return LikeResponseDTO.of(articleId, likesCount, spamCount);
    }

    public LikeResponseDTO unlikeArticle(Long articleId, Long memberId) {
        ArticleLikeId likeId = new ArticleLikeId(articleId, memberId);
        ArticleLike articleLike = articleLikeRepository.findById(likeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_LIKED));

        articleLikeRepository.delete(articleLike);
        Article article = articleLike.getArticle();
        long likesCount = articleLikeRepository.countByArticle(article);
        long spamCount = 0;
        return LikeResponseDTO.of(articleId, likesCount, spamCount);
    }
}
