package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private Long articleId;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Long parentCommentId;
}
