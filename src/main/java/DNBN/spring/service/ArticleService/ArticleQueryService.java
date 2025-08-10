package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.web.dto.ArticleResponseDTO;
import DNBN.spring.web.dto.response.PostResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleQueryService {
    Page<Article> getArticleListByRegion(Long memberId, Integer page);

    ArticleResponseDTO.ArticleListDTO getArticlesByCategory(Long categoryId, Long memberId, Long cursor, Long limit);

    PostResponseDTO.PostPreViewDTO getTopChallengeArticle();

    ArticleResponseDTO.ArticleDetailDTO getArticleDetail(Long articleId);

    List<ArticleResponseDTO.ArticleListItemDTO> getArticleList(Long memberId, Long regionId, Long cursor, Long limit);
}
