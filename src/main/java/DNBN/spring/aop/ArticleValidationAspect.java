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
        
        if (articleId != null) {
            validateArticleAccess(memberId, articleId);
        }
    }

    // 파라미터 검증 관련
    private void validateParameterStructure(Object[] args) {
        if (args.length < 1 || !(args[MEMBER_ID_INDEX] instanceof Long)) {
            throw new IllegalArgumentException(PARAMETER_ERROR_MESSAGE);
        }
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

    // DTO 검증 관련
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

    // 게시글 접근 검증 관련
    private void validateArticleAccess(Long memberId, Long articleId) {
        Article article = findArticleById(articleId);
        validateArticleOwnership(article, memberId);
        validateArticleNotDeleted(article);
    }

    private Article findArticleById(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
    }

    private void validateArticleOwnership(Article article, Long memberId) {
        if (!article.getMember().getId().equals(memberId)) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_FORBIDDEN);
        }
    }

    private void validateArticleNotDeleted(Article article) {
        if (article.getDeletedAt() != null) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_ALREADY_DELETED);
        }
    }
}
