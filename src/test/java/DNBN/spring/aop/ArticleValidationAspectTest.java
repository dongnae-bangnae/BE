package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.web.dto.request.ArticleRequestDTO;
import DNBN.spring.web.dto.request.ArticleUpdateRequestDTO;
import DNBN.spring.web.dto.request.ArticleWithLocationRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleValidationAspectTest {
    private ArticleRepository articleRepository;
    private ArticleValidationAspect aspect;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        aspect = new ArticleValidationAspect();
        // 필드 주입 방식이므로 리플렉션으로 주입
        java.lang.reflect.Field repoField;
        try {
            repoField = ArticleValidationAspect.class.getDeclaredField("articleRepository");
            repoField.setAccessible(true);
            repoField.set(aspect, articleRepository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("파라미터 타입/순서 오류시 예외 발생")
    void parameterTypeOrderError() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"notLong", 1L});
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파라미터 타입/순서 오류");
    }

    @Test
    @DisplayName("존재하지 않는 articleId 예외 발생")
    void notFoundArticle() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L});
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(ArticleHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("권한 없는 사용자 예외 발생")
    void forbiddenUser() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L});
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(article.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(99L); // 다른 사용자
        when(article.getDeletedAt()).thenReturn(null);
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.of(article));
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(ArticleHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("삭제된 게시글 예외 발생")
    void deletedArticle() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L});
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(article.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(article.getDeletedAt()).thenReturn(java.time.LocalDateTime.now());
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.of(article));
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(ArticleHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("DTO의 pinCategory가 잘못된 경우 예외 발생")
    void invalidPinCategory() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        ArticleRequestDTO dto = mock(ArticleRequestDTO.class);
        when(dto.pinCategory()).thenReturn("invalid");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, null, dto});
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(PlaceHandler.class)
                .hasMessageContaining(ErrorStatus.PIN_CATEGORY_INVALID.getMessage());
    }

    @Test
    @DisplayName("DTO의 pinCategory가 정상인 경우 예외 없음")
    void validPinCategory() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        ArticleRequestDTO dto = mock(ArticleRequestDTO.class);
        when(dto.pinCategory()).thenReturn(PinCategory.FOOD.name());
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, null, dto});
        assertThatCode(() -> aspect.validateArticle(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("articleId가 null인 경우 예외 발생")
    void nullArticleId() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, null});
        when(articleRepository.findById(null)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(ArticleHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("articleId 타입이 Long이 아닌 경우 예외 발생")
    void articleIdNotLong() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, "stringId"});
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파라미터 타입/순서 오류");
    }

    @Test
    @DisplayName("ArticleRequestDTO가 null인 경우 예외 없음")
    void nullArticleRequestDto() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, null});
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(article.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(article.getDeletedAt()).thenReturn(null);
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.of(article));
        assertThatCode(() -> aspect.validateArticle(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ArticleUpdateRequestDTO의 pinCategory가 잘못된 경우 예외 발생")
    void invalidPinCategoryInUpdateDto() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        ArticleUpdateRequestDTO dto = mock(ArticleUpdateRequestDTO.class);
        when(dto.pinCategory()).thenReturn("invalid");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, dto});
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(article.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(article.getDeletedAt()).thenReturn(null);
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.of(article));
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(PlaceHandler.class)
                .hasMessageContaining(ErrorStatus.PIN_CATEGORY_INVALID.getMessage());
    }

    @Test
    @DisplayName("ArticleWithLocationRequestDTO의 pinCategory가 잘못된 경우 예외 발생")
    void invalidPinCategoryInWithLocationDto() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        ArticleWithLocationRequestDTO dto = mock(ArticleWithLocationRequestDTO.class);
        when(dto.pinCategory()).thenReturn("invalid");
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, dto});
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(article.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(article.getDeletedAt()).thenReturn(null);
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.of(article));
        assertThatThrownBy(() -> aspect.validateArticle(joinPoint))
                .isInstanceOf(PlaceHandler.class)
                .hasMessageContaining(ErrorStatus.PIN_CATEGORY_INVALID.getMessage());
    }

    @Test
    @DisplayName("ArticleUpdateRequestDTO의 pinCategory가 정상인 경우 예외 없음")
    void validPinCategoryInUpdateDto() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        ArticleUpdateRequestDTO dto = mock(ArticleUpdateRequestDTO.class);
        when(dto.pinCategory()).thenReturn(PinCategory.FOOD.name());
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, dto});
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(article.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(article.getDeletedAt()).thenReturn(null);
        when(articleRepository.findById(2L)).thenReturn(java.util.Optional.of(article));
        assertThatCode(() -> aspect.validateArticle(joinPoint)).doesNotThrowAnyException();
    }
}
