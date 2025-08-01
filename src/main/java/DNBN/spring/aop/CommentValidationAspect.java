package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.domain.Comment;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.web.dto.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CommentValidationAspect {
    private final CommentRepository commentRepository;

    @Before("execution(* DNBN.spring.service.CommentService.CommentCommandServiceImpl.deleteComment(..)) || " +
            "execution(* DNBN.spring.service.CommentService.CommentCommandServiceImpl.updateComment(..))")
    public void validateComment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long memberId = (Long) args[0];
        Long commentId = (Long) args[1];
        Long articleId = (Long) args[2];
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
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

    @Before("execution(* DNBN.spring.service.CommentService.CommentCommandServiceImpl.updateComment(..))")
    public void validateCommentContent(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object dto = args[3];
        String content = null;
        if (dto instanceof DNBN.spring.web.dto.CommentRequestDTO req) {
            content = req.content();
        } else if (dto instanceof DNBN.spring.web.dto.CommentUpdateRequestDTO req) {
            content = req.content();
        }
        if (content == null || content.length() < 2 || content.length() > 1000) {
            throw new CommentHandler(ErrorStatus.COMMENT_CONTENT_LENGTH_INVALID);
        }
    }
}
