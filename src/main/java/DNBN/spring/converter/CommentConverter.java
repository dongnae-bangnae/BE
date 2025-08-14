package DNBN.spring.converter;

import DNBN.spring.domain.Comment;
import DNBN.spring.web.dto.CommentResponseDTO;

public class CommentConverter {
    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .articleId(comment.getArticle().getArticleId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .updatedAt(comment.getUpdatedAt().toString())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .build();
    }
}
