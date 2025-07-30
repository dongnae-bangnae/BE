package DNBN.spring.service.CommentService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.converter.CommentConverter;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Comment;
import DNBN.spring.domain.Member;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.web.dto.CommentRequestDTO;
import DNBN.spring.web.dto.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    private static final int CONTENT_MIN_LENGTH = 2;
    private static final int CONTENT_MAX_LENGTH = 1000;
    private final MemberRepository memberRepository;

    @Override
    public CommentResponseDTO createComment(Long memberId, Long articleId, CommentRequestDTO request) {

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (request.content() == null || request.content().length() < CONTENT_MIN_LENGTH || request.content().length() > CONTENT_MAX_LENGTH) {
            throw new CommentHandler(ErrorStatus.COMMENT_CONTENT_LENGTH_INVALID);
        }

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

            if (!parentComment.getArticle().getArticleId().equals(articleId)) {
                throw new CommentHandler(ErrorStatus.COMMENT_FORBIDDEN);
            }
        }
        Comment comment = Comment.builder()
                .article(article)
                .member(member)
                .content(request.content())
                .parentComment(parentComment)
                .build();

        commentRepository.save(comment);
        return CommentConverter.toCommentResponseDTO(comment);
    }

    @Override
    public void deleteComment(Long memberId, Long commentId, Long articleId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getArticle().getArticleId().equals(articleId)) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND);
        }
        if (!comment.getMember().getId().equals(memberId)) {
            throw new CommentHandler(ErrorStatus.COMMENT_FORBIDDEN);
        }
        if (comment.getDeletedAt() != null) {
            throw new CommentHandler(ErrorStatus.COMMENT_ALREADY_DELETED);
        }

        comment.delete();
    }

    @Override
    public CommentResponseDTO updateComment(Long memberId, Long commentId, Long articleId, CommentRequestDTO request) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getArticle().getArticleId().equals(articleId)) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND);
        }
        if (!comment.getMember().getId().equals(memberId)) {
            throw new CommentHandler(ErrorStatus.COMMENT_FORBIDDEN);
        }
        if (comment.getDeletedAt() != null) {
            throw new CommentHandler(ErrorStatus.COMMENT_ALREADY_DELETED);
        }
        if (request.content() == null || request.content().length() < CONTENT_MIN_LENGTH || request.content().length() > CONTENT_MAX_LENGTH) {
            throw new CommentHandler(ErrorStatus.COMMENT_CONTENT_LENGTH_INVALID);
        }
        comment.updateContent(request.content());
        // JPA dirty checking으로 자동 업데이트
        return CommentConverter.toCommentResponseDTO(comment);
    }
}
