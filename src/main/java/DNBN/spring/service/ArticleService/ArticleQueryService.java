package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.web.dto.response.ArticleResponseDTO;
import DNBN.spring.web.dto.response.PostResponseDTO;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleQueryService {
    Page<Article> getArticleListByRegion(Long memberId, Integer page);

    ArticleResponseDTO.ArticleListDTO getArticlesByCategory(Long categoryId, Long memberId, Long cursor, Long limit);

    PostResponseDTO.PostPreViewDTO getTopChallengeArticle();

    ArticleResponseDTO.ArticleDetailDTO getArticleDetail(Long articleId);

    // V1: 단일 커서(Long) 방식
    List<ArticleResponseDTO.ArticleListItemDTO> getArticleListV1(Long memberId, Long regionId, Long cursor, Long limit);

    // V2: 복합 커서(LocalDateTime, Long) 방식
    List<ArticleResponseDTO.ArticleListItemDTO> getArticleListV2(Long memberId, Long placeId, java.time.LocalDateTime cursorCreatedAt, Long cursorArticleId, Long limit);
}
