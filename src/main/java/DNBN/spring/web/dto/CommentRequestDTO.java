package DNBN.spring.web.dto;

public record CommentRequestDTO(
    String content,
    Long parentCommentId // null: 일반 댓글, not null: 대댓글
) { }
