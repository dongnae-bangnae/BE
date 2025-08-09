package DNBN.spring.validation;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TitleLengthValidator {
    @Value("${article.validation.title.min-length}")
    private int articleTitleMinLength;
    @Value("${article.validation.title.max-length}")
    private int articleTitleMaxLength;

    public boolean isValidArticleTitle(String title) {
        return title != null && title.length() >= articleTitleMinLength && title.length() <= articleTitleMaxLength;
    }

    public void validateArticleTitle(String content) {
        if (!isValidArticleTitle(content)) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_TITLE_LENGTH_INVALID);
        }
    }
}