package DNBN.spring.service.CommentService;

import DNBN.spring.domain.*;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CommentRepository.CommentRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.NotificationRepository.NotificationRepository;
import DNBN.spring.validation.validator.ContentLengthValidator;
import DNBN.spring.web.dto.request.CommentRequestDTO;
import DNBN.spring.web.dto.response.CommentResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.List;

class CommentCommandServiceImplTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private ContentLengthValidator contentLengthValidator;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private CommentCommandServiceImpl commentCommandService;

    private final Long memberId = 1L;
    private final Long articleId = 10L;
    private final Long commentId = 100L;
    private final String content = "댓글 내용";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Member getMember() {
        return Member.builder().id(memberId).build();
    }

    private Article getArticleWithCommentCount(long commentCount) {
        return Article.builder()
            .articleId(articleId)
            .commentCount(commentCount)
            .member(getMember())
            .build();
    }

    private Comment getComment(Article article, Member member) {
        return Comment.builder()
            .commentId(commentId)
            .article(article)
            .member(member)
            .content(content)
            .build();
    }

    private void setCreatedAndUpdatedAt(Object entity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        try {
            Class<?> clazz = entity.getClass().getSuperclass(); // BaseEntity
            Field createdField = clazz.getDeclaredField("createdAt");
            Field updatedField = clazz.getDeclaredField("updatedAt");
            createdField.setAccessible(true);
            updatedField.setAccessible(true);
            createdField.set(entity, createdAt);
            updatedField.set(entity, updatedAt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("댓글 작성/삭제 시 commentCount 동작 테스트")
    class CommentCountTest {

        @Test
        @DisplayName("댓글 작성 시 Article의 commentCount가 1 증가한다")
        void createComment_increaseCommentCount() {
            Article article = getArticleWithCommentCount(0L);
            Member member = getMember();
            CommentRequestDTO request = new CommentRequestDTO(content, null);

            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
                Comment c = invocation.getArgument(0);
                setCreatedAndUpdatedAt(c, LocalDateTime.now(), LocalDateTime.now());
                return c;
            });

            long before = article.getCommentCount() == null ? 0 : article.getCommentCount();

            CommentResponseDTO response = commentCommandService.createComment(memberId, articleId, request);

            assertNotNull(response);
            assertEquals(before + 1, article.getCommentCount());
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글 삭제 시 Article의 commentCount가 1 감소한다")
        void deleteComment_decreaseCommentCount() {
            Article article = getArticleWithCommentCount(2L); // 댓글 2개 있다고 가정
            Member member = getMember();
            Comment comment = getComment(article, member);
            setCreatedAndUpdatedAt(comment, LocalDateTime.now(), LocalDateTime.now());

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

            long before = article.getCommentCount();

            commentCommandService.deleteComment(memberId, commentId, articleId);

            assertEquals(Math.max(0, before - 1), article.getCommentCount());
            verify(commentRepository).findById(commentId);
        }

        @Test
        @DisplayName("댓글이 0개일 때 삭제해도 commentCount는 0 이하로 내려가지 않는다")
        void deleteComment_notBelowZero() {
            Article article = getArticleWithCommentCount(0L); // 빌더로 0L로 초기화
            Member member = getMember();
            Comment comment = getComment(article, member);
            setCreatedAndUpdatedAt(comment, LocalDateTime.now(), LocalDateTime.now());

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

            commentCommandService.deleteComment(memberId, commentId, articleId);

            assertEquals(0L, article.getCommentCount());
        }
    }

    @Nested
    @DisplayName("댓글 예외/경계 및 비즈니스 로직 테스트")
    class CommentExceptionAndBusinessTest {

        @Test
        @DisplayName("존재하지 않는 게시물로 댓글 생성 시 예외 발생")
        void createComment_articleNotFound() {
            when(articleRepository.findById(articleId)).thenReturn(Optional.empty());
            CommentRequestDTO request = new CommentRequestDTO(content, null);

            assertThrows(RuntimeException.class, () -> {
                commentCommandService.createComment(memberId, articleId, request);
            });
        }

        @Test
        @DisplayName("존재하지 않는 멤버로 댓글 생성 시 예외 발생")
        void createComment_memberNotFound() {
            Article article = getArticleWithCommentCount(0L);
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
            CommentRequestDTO request = new CommentRequestDTO(content, null);

            assertThrows(RuntimeException.class, () -> {
                commentCommandService.createComment(memberId, articleId, request);
            });
        }

        @Test
        @DisplayName("존재하지 않는 댓글 삭제 시 예외 발생")
        void deleteComment_commentNotFound() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(getArticleWithCommentCount(0L)));

            assertThrows(RuntimeException.class, () -> {
                commentCommandService.deleteComment(memberId, commentId, articleId);
            });
        }

        @Test
        @DisplayName("대댓글 생성 시 부모 댓글이 없으면 예외 발생")
        void createReply_parentCommentNotFound() {
            Article article = getArticleWithCommentCount(0L);
            Member member = getMember();
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(commentRepository.findById(999L)).thenReturn(Optional.empty()); // 부모 댓글 없음

            CommentRequestDTO request = new CommentRequestDTO(content, 999L);

            assertThrows(RuntimeException.class, () -> {
                commentCommandService.createComment(memberId, articleId, request);
            });
        }

        @Test
        @DisplayName("대댓글 생성 시 부모 댓글이 다른 게시물에 속하면 예외 발생")
        void createReply_parentCommentOfOtherArticle() {
            Article article = getArticleWithCommentCount(0L);
            Member member = getMember();
            Article otherArticle = getArticleWithCommentCount(0L);
            otherArticle = Article.builder().articleId(999L).commentCount(0L).member(member).build();
            Comment parentComment = getComment(otherArticle, member);
            setCreatedAndUpdatedAt(parentComment, LocalDateTime.now(), LocalDateTime.now());

            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(commentRepository.findById(parentComment.getCommentId())).thenReturn(Optional.of(parentComment));

            CommentRequestDTO request = new CommentRequestDTO(content, parentComment.getCommentId());

            assertThrows(RuntimeException.class, () -> {
                commentCommandService.createComment(memberId, articleId, request);
            });
        }

        @Test
        @DisplayName("댓글 삭제 시 알림(Notification)도 함께 삭제된다")
        void deleteComment_alsoDeletesNotification() {
            Article article = getArticleWithCommentCount(1L);
            Member member = getMember();
            Comment comment = getComment(article, member);
            setCreatedAndUpdatedAt(comment, LocalDateTime.now(), LocalDateTime.now());

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(notificationRepository.findByComment_CommentId(commentId)).thenReturn(List.of(Notification.builder().comment(comment).build()));

            commentCommandService.deleteComment(memberId, commentId, articleId);

            verify(notificationRepository).delete(any(Notification.class));
        }

        @Test
        @DisplayName("댓글 작성 시 본인 글에는 알림이 생성되지 않는다")
        void createComment_noNotificationIfSelf() {
            Article article = getArticleWithCommentCount(0L);
            Member member = getMember();
            when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
                Comment c = invocation.getArgument(0);
                setCreatedAndUpdatedAt(c, LocalDateTime.now(), LocalDateTime.now());
                return c;
            });

            CommentRequestDTO request = new CommentRequestDTO(content, null);

            commentCommandService.createComment(memberId, articleId, request);

            verify(notificationRepository, never()).save(any(Notification.class));
        }
    }
}