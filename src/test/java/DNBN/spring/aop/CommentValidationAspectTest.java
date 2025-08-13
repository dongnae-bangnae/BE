package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.domain.Comment;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.web.dto.request.CommentRequestDTO;
import DNBN.spring.web.dto.request.CommentUpdateRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentValidationAspectTest {
    private CommentRepository commentRepository;
    private CommentValidationAspect aspect;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        aspect = new CommentValidationAspect(commentRepository);
    }

    @Test
    @DisplayName("파라미터 타입/순서 오류시 예외 발생")
    void parameterTypeOrderError() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"notLong", 1L, 2L});
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파라미터 타입/순서 오류");
    }

    @Test
    @DisplayName("존재하지 않는 commentId 예외 발생")
    void notFoundComment() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, 3L});
        when(commentRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(CommentHandler.class)
                .hasMessageContaining(ErrorStatus.COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("권한 없는 사용자 예외 발생")
    void forbiddenUser() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, 3L});
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(comment.getArticle()).thenReturn(article);
        when(article.getArticleId()).thenReturn(3L);
        when(comment.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(99L); // 다른 사용자
        when(comment.getDeletedAt()).thenReturn(null);
        when(commentRepository.findById(2L)).thenReturn(java.util.Optional.of(comment));
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(CommentHandler.class)
                .hasMessageContaining(ErrorStatus.COMMENT_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("삭제된 댓글 예외 발생")
    void deletedComment() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, 3L});
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(comment.getArticle()).thenReturn(article);
        when(article.getArticleId()).thenReturn(3L);
        when(comment.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(comment.getDeletedAt()).thenReturn(java.time.LocalDateTime.now());
        when(commentRepository.findById(2L)).thenReturn(java.util.Optional.of(comment));
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(CommentHandler.class)
                .hasMessageContaining(ErrorStatus.COMMENT_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("parentCommentId가 존재하고, articleId 불일치시 예외 발생")
    void parentCommentArticleIdMismatch() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        CommentRequestDTO dto = mock(CommentRequestDTO.class);
        when(dto.parentCommentId()).thenReturn(10L);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, null, 3L, dto});
        Comment parentComment = mock(Comment.class);
        Article article = mock(Article.class);
        when(parentComment.getArticle()).thenReturn(article);
        when(article.getArticleId()).thenReturn(99L); // 불일치
        when(commentRepository.findById(10L)).thenReturn(java.util.Optional.of(parentComment));
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(CommentHandler.class)
                .hasMessageContaining(ErrorStatus.COMMENT_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("commentId가 null인 경우 예외 발생")
    void nullCommentId() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, null, 3L});
        when(commentRepository.findById(null)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(CommentHandler.class)
                .hasMessageContaining(ErrorStatus.COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("commentId 타입이 Long이 아닌 경우 예외 발생")
    void commentIdNotLong() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, "stringId", 3L});
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파라미터 타입/순서 오류");
    }

    @Test
    @DisplayName("articleId가 null인 경우 예외 발생")
    void nullArticleId() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, null});
        assertThatThrownBy(() -> aspect.validateComment(joinPoint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("파라미터 타입/순서 오류");
    }

    @Test
    @DisplayName("CommentRequestDTO가 null인 경우 예외 없음")
    void nullCommentRequestDto() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, 3L, null});
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(comment.getArticle()).thenReturn(article);
        when(article.getArticleId()).thenReturn(3L);
        when(comment.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(comment.getDeletedAt()).thenReturn(null);
        when(commentRepository.findById(2L)).thenReturn(java.util.Optional.of(comment));
        assertThatCode(() -> aspect.validateComment(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("CommentRequestDTO의 parentCommentId가 null인 경우 예외 없음")
    void parentCommentIdNullInDto() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        CommentRequestDTO dto = mock(CommentRequestDTO.class);
        when(dto.parentCommentId()).thenReturn(null);
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 2L, 3L, dto});
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        Member member = mock(Member.class);
        when(comment.getArticle()).thenReturn(article);
        when(article.getArticleId()).thenReturn(3L);
        when(comment.getMember()).thenReturn(member);
        when(member.getId()).thenReturn(1L);
        when(comment.getDeletedAt()).thenReturn(null);
        when(commentRepository.findById(2L)).thenReturn(java.util.Optional.of(comment));
        assertThatCode(() -> aspect.validateComment(joinPoint)).doesNotThrowAnyException();
    }
}
