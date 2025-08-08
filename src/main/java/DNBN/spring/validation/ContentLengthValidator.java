package DNBN.spring.validation;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TODO: request DTO가 record여서, Service에서 직접 ContentLengthValidator를 호출해야 함.
@Getter
@Component
public class ContentLengthValidator {
    @Value("${article.validation.content.min-length}")
    private int articleContentMinLength;
    @Value("${article.validation.content.max-length}")
    private int articleContentMaxLength;
    @Value("${article.comment.validation.content.min-length}")
    private int commentContentMinLength;
    @Value("${article.comment.validation.content.max-length}")
    private int commentContentMaxLength;

    public boolean isValidArticleContent(String content) {
        return content != null && content.length() >= articleContentMinLength && content.length() <= articleContentMaxLength;
    }

    public boolean isValidCommentContent(String content) {
        return content != null && content.length() >= commentContentMinLength && content.length() <= commentContentMaxLength;
    }

    public void validateArticleContent(String content) {
        if (!isValidArticleContent(content)) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_CONTENT_LENGTH_INVALID);
        }
    }

    public void validateCommentContent(String content) {
        if (!isValidCommentContent(content)) {
            throw new CommentHandler(ErrorStatus.COMMENT_CONTENT_LENGTH_INVALID);
        }
    }
}
