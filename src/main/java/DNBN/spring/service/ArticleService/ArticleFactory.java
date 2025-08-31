package DNBN.spring.service.ArticleService;

import DNBN.spring.domain.Article;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ArticleFactory {
    public Article create(Member member, Category category, Place place, Region region, String title, LocalDate date, String content) {
        return Article.builder()
                .member(member)
                .category(category)
                .place(place)
                .region(region)
                .title(title)
                .date(date)
                .content(content)
                .likesCount(0L)
                .spamCount(0L)
                .build();
    }
}
