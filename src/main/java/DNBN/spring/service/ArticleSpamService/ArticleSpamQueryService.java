package DNBN.spring.service.ArticleSpamService;

import DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository;
import DNBN.spring.repository.ArticleSpamRepository.ArticleSpamRepository;
import DNBN.spring.web.dto.LikeStatusResponseDTO;
import DNBN.spring.web.dto.SpamStatusResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleSpamQueryService {

    private final ArticleSpamRepository articleSpamRepository;

    public SpamStatusResponseDTO getSpamStatus(Long articleId, Long memberId) {
        long spamsCount = articleSpamRepository.countByArticle_ArticleId(articleId);

        return new SpamStatusResponseDTO(articleId, spamsCount);
    }
}
