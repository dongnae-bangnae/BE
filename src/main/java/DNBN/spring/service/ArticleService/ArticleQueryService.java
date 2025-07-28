package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.web.dto.ArticleResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleQueryService {
    Page<Article> getArticleListByRegion(Long memberId, Integer page);
    ArticleResponseDTO.ArticleListDTO getArticlesByCategory(Long categoryId, Long memberId, Long cursor, Long limit);
}
