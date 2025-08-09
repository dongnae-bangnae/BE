package DNBN.spring.service.CommentService;

import DNBN.spring.aop.annotation.ValidateComment;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CommentHandler;
import DNBN.spring.converter.CommentConverter;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.Comment;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Notification;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.NotificationRepository.NotificationRepository;
import DNBN.spring.validation.ContentLengthValidator;
import DNBN.spring.validation.TitleLengthValidator;
import DNBN.spring.web.dto.CommentRequestDTO;
import DNBN.spring.web.dto.CommentResponseDTO;
import DNBN.spring.web.dto.CommentUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final ContentLengthValidator contentLengthValidator;
    private final TitleLengthValidator titleLengthValidator;

    private static final int CONTENT_MIN_LENGTH = 2;
    private static final int CONTENT_MAX_LENGTH = 1000;
    private final MemberRepository memberRepository;

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
    }

    @Override
    public CommentResponseDTO createComment(Long memberId, Long articleId, CommentRequestDTO request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.MEMBER_NOT_FOUND));

        contentLengthValidator.validateCommentContent(request.content());

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
            // parentComment의 articleId 일치 검증은 Aspect에서 처리
        }
        Comment comment = Comment.builder()
                .article(article)
                .member(member)
                .content(request.content())
                .parentComment(parentComment)
                .build();

        commentRepository.save(comment);

        // (1) 원글 작성자
        Member articleOwner = article.getMember();
        // (2) 본인이 자신의 글에 댓글 단 경우 알림 보낼 필요 없으면 생략
        if (!articleOwner.getId().equals(memberId)) {
            Notification notification = Notification.builder()
                    .member(articleOwner)
                    .comment(comment)
                    .hidden(false)
                    .build();
            notificationRepository.save(notification);
        }

        return CommentConverter.toCommentResponseDTO(comment);
    }

    @Override
    @ValidateComment
    public void deleteComment(Long memberId, Long commentId, Long articleId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
        comment.delete();

        notificationRepository.findByComment_CommentId(commentId)
                .forEach(notificationRepository::delete);
    }

    @Override
    @ValidateComment
    public CommentResponseDTO updateComment(Long memberId, Long commentId, Long articleId, CommentUpdateRequestDTO request) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        contentLengthValidator.validateCommentContent(request.content());

        comment.updateContent(request.content());
        return CommentConverter.toCommentResponseDTO(comment);
    }
}
