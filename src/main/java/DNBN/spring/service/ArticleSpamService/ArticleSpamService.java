package DNBN.spring.service.ArticleSpamService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticleSpam;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.SpamResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleSpamService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final DNBN.spring.repository.ArticleSpamRepository.ArticleSpamRepository articleSpamRepository;

    public SpamResponseDTO spamArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));

        if (articleSpamRepository.existsByArticleIdAndMemberId(articleId, memberId)) {
            throw new GeneralException(ErrorStatus.ALREADY_LIKED);
        }

        ArticleSpam articleSpam = ArticleSpam.builder()
                .article(article)
                .member(memberRepository.getReferenceById(memberId))
                .build();

        articleSpamRepository.save(articleSpam);

        long spamsCount = articleSpamRepository.countByArticleId(articleId);
        long spamCount = 0;
        return SpamResponseDTO.of(articleId, spamsCount, spamCount);
    }

    public SpamResponseDTO unspamArticle(Long articleId, Long memberId) {
        ArticleSpam articleSpam = articleSpamRepository.findByArticleIdAndMemberId(articleId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_LIKED));

        articleSpamRepository.delete(articleSpam);
        long spamsCount = articleSpamRepository.countByArticleId(articleId);
        long spamCount = 0;
        return SpamResponseDTO.of(articleId, spamsCount, spamCount);
    }
}
