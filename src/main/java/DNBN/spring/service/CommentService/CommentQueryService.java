package DNBN.spring.service.CommentService;

import DNBN.spring.web.dto.CommentCursorResponseDTO;

public interface CommentQueryService {
    CommentCursorResponseDTO getComments(Long articleId, Long cursor, int limit);
    CommentCursorResponseDTO getReplies(Long parentCommentId, Long cursor, int limit);
}
