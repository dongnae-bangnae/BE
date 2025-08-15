package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.web.dto.request.ArticleRequestDTO;
import DNBN.spring.web.dto.request.ArticleUpdateRequestDTO;
import DNBN.spring.web.dto.request.ArticleWithLocationRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ArticleValidationAspect {
    private static final int MEMBER_ID_INDEX = 0;
    private static final int ARTICLE_ID_INDEX = 1;
    private static final String PARAMETER_ERROR_MESSAGE = "❌ ArticleValidationAspect: memberId 파라미터 타입/순서 오류";
    
    @Autowired
    private ArticleRepository articleRepository;

    @Before("@annotation(DNBN.spring.aop.annotation.ValidateArticle)")
    public void validateArticle(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        
        validateParameterStructure(args);
        
        Long memberId = extractMemberId(args);
        Long articleId = extractArticleId(args);
        Object dto = extractDto(args);
        
        validatePinCategory(dto);

        // articleId가 있으면(수정/삭제) 권한 및 삭제 여부 검증, 없으면(생성) 생략
        if (articleId != null) {
            Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
            if (!article.getMember().getId().equals(memberId)) {
                throw new ArticleHandler(ErrorStatus.ARTICLE_FORBIDDEN);
            }
            if (article.getDeletedAt() != null) {
                throw new ArticleHandler(ErrorStatus.ARTICLE_ALREADY_DELETED);
            }
        }
    }

    private void validateParameterStructure(Object[] args) {
        if (args.length < 1 || !(args[MEMBER_ID_INDEX] instanceof Long)) {
            throw new IllegalArgumentException(PARAMETER_ERROR_MESSAGE);
        }
    }

    private void validatePinCategory(Object dto) {
        if (dto == null) return;
        try {
            var pinCategoryMethod = dto.getClass().getMethod("pinCategory");
            String pinCategory = (String) pinCategoryMethod.invoke(dto);
            if (pinCategory == null) throw new PlaceHandler(ErrorStatus.PIN_CATEGORY_INVALID);
            PinCategory.valueOf(pinCategory.toUpperCase());
        } catch (NoSuchMethodException e) {
            // pinCategory 필드가 없는 DTO는 무시
        } catch (Exception e) {
            throw new PlaceHandler(ErrorStatus.PIN_CATEGORY_INVALID);
        }
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

    private Long extractMemberId(Object[] args) {
        return (Long) args[MEMBER_ID_INDEX];
    }

    private Long extractArticleId(Object[] args) {
        if (args.length > 1 && args[ARTICLE_ID_INDEX] instanceof Long) {
            return (Long) args[ARTICLE_ID_INDEX];
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

    private Long extractLongArg(Object[] args, int idx, String name) {
        if (args.length > idx && args[idx] instanceof Long value) {
            return value;
        }
        return null;
    }
}
