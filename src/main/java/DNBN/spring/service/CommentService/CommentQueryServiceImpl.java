package DNBN.spring.service.CommentService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.converter.CommentConverter;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Comment;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.web.dto.CommentCursorResponseDTO;
import DNBN.spring.web.dto.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentCursorResponseDTO getComments(Long articleId, Long cursor, int limit) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));

        List<Comment> comments = commentRepository.findParentCommentsByCursor(articleId, cursor, limit + 1);
        boolean hasNext = comments.size() > limit;
        if (hasNext) comments.remove(comments.size() - 1);
        Long nextCursor = hasNext && !comments.isEmpty() ? comments.get(comments.size() - 1).getCommentId() : null;

        List<CommentResponseDTO> dtos = comments.stream()
                .map(CommentConverter::toCommentResponseDTO)
                .collect(Collectors.toList());

        return CommentCursorResponseDTO.builder()
                .comments(dtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public CommentCursorResponseDTO getReplies(Long parentCommentId, Long cursor, int limit) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        List<Comment> replies = commentRepository.findRepliesByParentId(parentCommentId, cursor, limit + 1);
        boolean hasNext = replies.size() > limit;
        if (hasNext) replies.remove(replies.size() - 1);
        Long nextCursor = hasNext && !replies.isEmpty() ? replies.get(replies.size() - 1).getCommentId() : null;

        List<CommentResponseDTO> dtos = replies.stream()
                .map(CommentConverter::toCommentResponseDTO)
                .collect(Collectors.toList());

        return CommentCursorResponseDTO.builder()
                .comments(dtos)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
