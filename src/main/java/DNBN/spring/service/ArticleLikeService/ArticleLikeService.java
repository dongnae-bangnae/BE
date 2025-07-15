package DNBN.spring.service.ArticleLikeService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticleLike;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.LikeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository articleLikeRepository;

    public LikeResponseDTO likeArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));

        if (articleLikeRepository.existsByArticleIdAndMemberId(articleId, memberId)) {
            throw new GeneralException(ErrorStatus.ALREADY_LIKED);
        }

        ArticleLike articleLike = ArticleLike.builder()
                .article(article)
                .member(memberRepository.getReferenceById(memberId))
                .build();

        articleLikeRepository.save(articleLike);

        long likesCount = articleLikeRepository.countByArticleId(articleId);
        long spamCount = 0;
        return LikeResponseDTO.of(articleId, likesCount, spamCount);
    }

    public LikeResponseDTO unlikeArticle(Long articleId, Long memberId) {
        ArticleLike articleLike = articleLikeRepository.findByArticleIdAndMemberId(articleId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_LIKED));

        articleLikeRepository.delete(articleLike);
        long likesCount = articleLikeRepository.countByArticleId(articleId);
        long spamCount = 0;
        return LikeResponseDTO.of(articleId, likesCount, spamCount);
    }
}
