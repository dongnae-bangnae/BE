package DNBN.spring.service.CommentService;

import DNBN.spring.web.dto.request.CommentRequestDTO;
import DNBN.spring.web.dto.response.CommentResponseDTO;
import DNBN.spring.web.dto.request.CommentUpdateRequestDTO;

public interface CommentCommandService {
    CommentResponseDTO createComment(Long memberId, Long articleId, CommentRequestDTO request);
    void deleteComment(Long memberId, Long commentId, Long articleId);
    CommentResponseDTO updateComment(Long memberId, Long commentId, Long articleId, CommentUpdateRequestDTO request);
}
