package DNBN.spring.service.ArticleLikeService;

import DNBN.spring.domain.ArticleLikeId;
import DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository;
import DNBN.spring.web.dto.LikeStatusResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeQueryService {

    private final ArticleLikeRepository articleLikeRepository;

    public LikeStatusResponseDTO getLikeStatus(Long articleId, Long memberId) {
        long likesCount = articleLikeRepository.countByArticle_ArticleId(articleId);

        return new LikeStatusResponseDTO(articleId, likesCount);
    }
}
