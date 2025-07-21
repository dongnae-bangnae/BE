package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ArticleQueryService {
    Page<Article> getArticleListByRegion(List<Long> regionIds, Integer page);
}
