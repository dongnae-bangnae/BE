package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.web.dto.ArticleRequestDTO;
import DNBN.spring.web.dto.ArticleUpdateRequestDTO;
import DNBN.spring.web.dto.ArticleWithLocationRequestDTO;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArticleValidationAspect {
    @Value("${article.validation.title.min-length}")
    private int titleMinLength;
    @Value("${article.validation.title.max-length}")
    private int titleMaxLength;
    @Value("${article.validation.content.min-length}")
    private int contentMinLength;
    @Value("${article.validation.content.max-length}")
    private int contentMaxLength;

    @Before("@annotation(DNBN.spring.aop.annotation.ValidateArticle)")
    public void validateArticle(JoinPoint joinPoint) {
      Object[] args = joinPoint.getArgs();
      Object dto = extractDto(args); // null 검증 생략

      Pair<String, String> titleAndContent = null;
      // DTO가 Record인 경우만 구현 (Article의 request DTO는 모두 Record)
      if (dto != null && dto.getClass().isRecord()) {
        titleAndContent = extractTitleAndContentFromRecord(dto);
      }

      // title과 content 길이 검증
      String title = Objects.requireNonNull(titleAndContent).getFirst();
      String content = titleAndContent.getSecond();
      validateLength(title, titleMinLength, titleMaxLength,
          ErrorStatus.ARTICLE_TITLE_NULL_ERROR, ErrorStatus.ARTICLE_TITLE_LENGTH_VALIDATION_ERROR);
      validateLength(content, contentMinLength, contentMaxLength,
          ErrorStatus.ARTICLE_CONTENT_NULL_ERROR, ErrorStatus.ARTICLE_CONTENT_LENGTH_VALIDATION_ERROR);
    }
    
    private Object extractDto(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof ArticleRequestDTO ||
                arg instanceof ArticleWithLocationRequestDTO ||
                arg instanceof ArticleUpdateRequestDTO) {
                return arg;
            }
        }
        return null;
    }

    private Pair<String, String> extractTitleAndContentFromRecord(Object recordDto) {
        try {
            var titleMethod = recordDto.getClass().getMethod("title");
            var contentMethod = recordDto.getClass().getMethod("content");
            String title = (String) titleMethod.invoke(recordDto);
            String content = (String) contentMethod.invoke(recordDto);

            return Pair.of(title, content);
        } catch (Exception e) {
            throw new IllegalArgumentException("❌ ArticleValidationAspect: DTO에서 title/content 추출 실패", e);
        }
    }

    private void validateLength(String value, int min, int max, ErrorStatus nullErrorStatus, ErrorStatus lengthErrorStatus) {
        if (value == null) {
            throw new ArticleHandler(nullErrorStatus);
        }
        if (value.length() < min || value.length() > max) {
            throw new ArticleHandler(lengthErrorStatus);
        }
    }
    
}
