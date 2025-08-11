package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.domain.Comment;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.web.dto.CommentRequestDTO;
import DNBN.spring.web.dto.CommentUpdateRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CommentValidationAspect {

    private final CommentRepository commentRepository;

    public CommentValidationAspect(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Before("@annotation(DNBN.spring.aop.annotation.ValidateComment)")
    public void validateComment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long memberId = extractLongArg(args, 0, "memberId");
        Long commentId = extractLongArg(args, 1, "commentId");
        Long articleId = extractLongArg(args, 2, "articleId");
        Comment comment = null;
        // createComment의 경우 parentCommentId만 존재
        if (commentId != null) {
            comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
            validateCommentOwnership(comment, memberId, articleId);
        } else {
            // createComment: parentCommentId 검증
            CommentRequestDTO requestDto = (CommentRequestDTO) extractDto(args);
            if (requestDto != null && requestDto.parentCommentId() != null) {
                Comment parentComment = commentRepository.findById(requestDto.parentCommentId())
                    .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
                if (!parentComment.getArticle().getArticleId().equals(articleId)) {
                    throw new CommentHandler(ErrorStatus.COMMENT_FORBIDDEN);
                }
            }
        }
    }

    private Long extractLongArg(Object[] args, int idx, String name) {
        if (args.length <= idx || !(args[idx] instanceof Long value)) {
            throw new IllegalArgumentException("❌ CommentValidationAspect: " + name + " 인자 오류");
        }
        return value;
    }

    private void validateCommentOwnership(Comment comment, Long memberId, Long articleId) {
        if (!comment.getArticle().getArticleId().equals(articleId)) {
            throw new CommentHandler(ErrorStatus.ARTICLE_NOT_FOUND);
        }
        if (!comment.getMember().getId().equals(memberId)) {
            throw new CommentHandler(ErrorStatus.COMMENT_FORBIDDEN);
        }
        if (comment.getDeletedAt() != null) {
            throw new CommentHandler(ErrorStatus.COMMENT_ALREADY_DELETED);
        }
    }

    private Object extractDto(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof CommentRequestDTO || arg instanceof CommentUpdateRequestDTO) {
                return arg;
            }
        }
        return null;
    }

    private Pair<String, String> extractContentFromDto(Object dto) {
        if (dto == null) {
            throw new CommentHandler(ErrorStatus.COMMENT_CONTENT_NULL_ERROR);
        }
        try {
            var contentMethod = dto.getClass().getMethod("content");
            String content = (String) contentMethod.invoke(dto);
            return Pair.of(content, null);
        } catch (Exception e) {
            throw new IllegalArgumentException("❌ CommentValidationAspect: DTO에서 content 추출 실패", e);
        }
    }
}