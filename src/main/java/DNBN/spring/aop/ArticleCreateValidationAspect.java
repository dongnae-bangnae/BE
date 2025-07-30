package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.web.dto.ArticleRequestDTO;
import DNBN.spring.web.dto.ArticleWithLocationRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArticleCreateValidationAspect {
    @Value("${article.validation.title.min-length}")
    private int titleMinLength;
    @Value("${article.validation.title.max-length}")
    private int titleMaxLength;
    @Value("${article.validation.content.min-length}")
    private int contentMinLength;
    @Value("${article.validation.content.max-length}")
    private int contentMaxLength;

    @Before("execution(* DNBN.spring.service.ArticleService.ArticleCommandServiceImpl.createArticle(..))")
    public void validateCreateArticle(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object dto = args[1];
        String title = null;
        String content = null;
        if (dto instanceof ArticleRequestDTO request) {
            title = request.title();
            content = request.content();
        } else if (dto instanceof ArticleWithLocationRequestDTO request) {
            title = request.title();
            content = request.content();
        }
        if (title == null || title.length() < titleMinLength || title.length() > titleMaxLength) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_TITLE_LENGTH_INVALID);
        }
        if (content == null || content.length() < contentMinLength || content.length() > contentMaxLength) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_CONTENT_LENGTH_INVALID);
        }
    }
}
