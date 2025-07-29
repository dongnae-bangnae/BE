package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.web.dto.response.PostResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleQueryService {
    Page<Article> getArticleListByRegion(Long memberId, Integer page);
    PostResponseDTO.PostPreViewDTO getTopChallengeArticle();
}
