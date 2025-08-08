package DNBN.spring.validation;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
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

    public void validateCommentTitle(String title) {
        if (title == null || title.length() < 2 || title.length() > 100) {
            throw new CommentHandler(ErrorStatus.COMMENT_TITLE_LENGTH_INVALID);
        }
    }
}
