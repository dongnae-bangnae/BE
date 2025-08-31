package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.validation.validator.ContentLengthValidator;
import DNBN.spring.validation.validator.TitleLengthValidator;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUpdater {
    private final TitleLengthValidator titleLengthValidator;
    private final ContentLengthValidator contentLengthValidator;

    public void updateTitle(Article article, String title) {
        titleLengthValidator.validateArticleTitle(title);
        article.updateTitle(title);
    }
    public void updateContent(Article article, String content) {
        contentLengthValidator.validateArticleContent(content);
        article.updateContent(content);
    }
    public void updateDate(Article article, LocalDate date) {
        article.updateDate(date);
    }
    public void updateCategory(Article article, Category category) {
        article.updateCategory(category);
    }
    public void updateRegion(Article article, Region region) {
        article.updateRegion(region);
    }
    public void updatePlace(Article article, Place place) {
        article.updatePlace(place);
    }
}
