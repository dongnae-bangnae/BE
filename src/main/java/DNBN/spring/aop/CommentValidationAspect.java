package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.domain.Comment;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.web.dto.request.CommentRequestDTO;
import DNBN.spring.web.dto.request.CommentUpdateRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
        String methodName = joinPoint.getSignature().getName();
        
        if ("createComment".equals(methodName)) {
            validateCreateComment(args);
        } else if ("deleteComment".equals(methodName)) {
            validateDeleteComment(args);
        } else {
            throw new IllegalArgumentException("❌ CommentValidationAspect: 지원하지 않는 메서드: " + methodName);
        }
    }

    private void validateCreateComment(Object[] args) {
        if (args.length < 3 || !(args[0] instanceof Long) || !(args[1] instanceof Long) || !(args[2] instanceof CommentRequestDTO)) {
            throw new IllegalArgumentException("❌ CommentValidationAspect: createComment 파라미터 타입/순서 오류");
        }
        
        Long memberId = (Long) args[0];
        Long articleId = (Long) args[1];
        CommentRequestDTO requestDto = (CommentRequestDTO) args[2];
        
        // parentComment 검증
        if (requestDto.parentCommentId() != null) {
            Comment parentComment = commentRepository.findById(requestDto.parentCommentId())
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
            
            // parentComment가 해당 게시글에 속하는지 검증
            if (!parentComment.getArticle().getArticleId().equals(articleId)) {
                throw new CommentHandler(ErrorStatus.COMMENT_FORBIDDEN);
            }
        }
    }

    private void validateDeleteComment(Object[] args) {
        if (args.length < 3 || !(args[0] instanceof Long) || !(args[1] instanceof Long) || !(args[2] instanceof Long)) {
            throw new IllegalArgumentException("❌ CommentValidationAspect: deleteComment 파라미터 타입/순서 오류");
        }
        
        Long memberId = (Long) args[0];
        Long commentId = (Long) args[1];
        Long articleId = (Long) args[2];
        
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
        
        validateCommentOwnership(comment, memberId, articleId);
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
}