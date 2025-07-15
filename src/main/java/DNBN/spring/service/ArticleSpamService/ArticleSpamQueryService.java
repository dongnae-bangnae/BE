package DNBN.spring.service.ArticleSpamService;

import DNBN.spring.repository.ArticleSpamRepository.ArticleSpamRepository;
import DNBN.spring.web.dto.SpamStatusResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleSpamQueryService {

    private final ArticleSpamRepository articleSpamRepository;

    public SpamStatusResponseDTO getSpamStatus(Long articleId, Long memberId) {
        boolean exists = articleSpamRepository.existsByArticleIdAndMemberId(articleId, memberId);
        return new SpamStatusResponseDTO(exists);
    }
}