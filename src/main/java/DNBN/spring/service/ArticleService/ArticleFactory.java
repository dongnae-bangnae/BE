package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.web.dto.request.ArticleRequestDTO;
import DNBN.spring.web.dto.request.ArticleWithLocationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class ArticleFactory {
    public Article create(Member member, Category category, Place place, Region region, ArticleRequestDTO request) {
        return Article.createFromRequest(member, category, place, region, request);
    }

    public Article create(Member member, Category category, Place place, Region region, ArticleWithLocationRequestDTO request) {
        return Article.createFromRequest(member, category, place, region, request);
    }
}
