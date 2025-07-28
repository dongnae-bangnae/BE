package DNBN.spring.service.ArticleSpamService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.domain.*;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.SpamResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleSpamService {
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final DNBN.spring.repository.ArticleSpamRepository.ArticleSpamRepository articleSpamRepository;
    @Transactional
    public SpamResponseDTO spamArticle(Long articleId, Long memberId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.ARTICLE_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        ArticleSpamId spamId = new ArticleSpamId(articleId, memberId);

        if (articleSpamRepository.existsById(spamId)) {
            throw new GeneralException(ErrorStatus.ALREADY_SPAM_REPORTED);
        }

        ArticleSpam articleSpam = ArticleSpam.builder()
                .id(spamId)
                .article(article)
                .member(memberRepository.getReferenceById(memberId))
                .build();

        articleSpamRepository.save(articleSpam);
        article.increaseSpamCount();
        long spamsCount = article.getSpamCount();
        return SpamResponseDTO.of(articleId, spamsCount);
    }

    @Transactional
    public SpamResponseDTO unspamArticle(Long articleId, Long memberId) {
        ArticleSpam articleSpam = articleSpamRepository.findByArticle_ArticleIdAndMember_Id(articleId, memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_SPAM_REPORTED));

        articleSpamRepository.delete(articleSpam);
        Article article = articleSpam.getArticle();
        article.decreaseSpamCount();
        long spamsCount = article.getSpamCount();
        return SpamResponseDTO.of(articleId, spamsCount);
    }
}
