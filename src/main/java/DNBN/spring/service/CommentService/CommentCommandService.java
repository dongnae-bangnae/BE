package DNBN.spring.service.CommentService;

import DNBN.spring.web.dto.CommentRequestDTO;
import DNBN.spring.web.dto.CommentResponseDTO;

public interface CommentCommandService {
    CommentResponseDTO createComment(Long memberId, Long articleId, CommentRequestDTO request);
    void deleteComment(Long memberId, Long commentId, Long articleId);
    CommentResponseDTO updateComment(Long memberId, Long commentId, Long articleId, CommentRequestDTO request);
}
