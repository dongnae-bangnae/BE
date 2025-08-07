package DNBN.spring.service.ArticleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.repository.ArticleLikeRepository.ArticleLikeRepository;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.ArticleSpamRepository.ArticleSpamRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import DNBN.spring.domain.*;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.web.dto.ArticleResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ArticleQueryServiceImplTest {

  @Mock
  private ArticleRepository articleRepository;
  @Mock
  private ArticlePhotoRepository articlePhotoRepository;
  @Mock
  private ArticleLikeRepository articleLikeRepository;
  @Mock
  private ArticleSpamRepository articleSpamRepository;
  @Mock
  private DNBN.spring.repository.MemberRepository.MemberRepository memberRepository;
  @Mock
  private DNBN.spring.repository.PlaceRepository.PlaceRepository placeRepository;
  @Mock
  private DNBN.spring.repository.ArticleRepository.ArticleRepositoryCustom articleRepositoryCustom;

  @InjectMocks
  private ArticleQueryServiceImpl articleQueryService;

  private static final String DEFAULT_IMAGE_UUID = "ae383fd6-4211-496b-a631-827954b03306";

  private Member member;
  private Place place;
  private Region region;
  private Category category;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    try {
      var field = ArticleQueryServiceImpl.class.getDeclaredField("defaultImageUuid");
      field.setAccessible(true);
      field.set(articleQueryService, DEFAULT_IMAGE_UUID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    member = Member.builder().id(1L).nickname("테스터").build();
    place = Place.builder().placeId(2L).title("장소").pinCategory(PinCategory.CAFE).address("주소").build();
    region = Region.builder().id(100L).build();
    category = Category.builder().categoryId(3L).name("카테고리").member(member).build();
  }

  /**
   * Article 생성을 위한 팩토리 메서드
   */
  private Article createArticle(Long articleId, String title, String content, Long likesCount, Long spamCount, Long commentCount) {
    Article article = Article.builder()
        .articleId(articleId)
        .member(member)
        .place(place)
        .region(region)
        .category(category)
        .title(title)
        .content(content)
        .likesCount(likesCount)
        .spamCount(spamCount)
        .commentCount(commentCount)
        .date(LocalDate.now())
        .build();

    try {
      Field createdAtField = Article.class.getSuperclass().getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(article, LocalDateTime.now());

      Field updatedAtField = Article.class.getSuperclass().getDeclaredField("updatedAt");
      updatedAtField.setAccessible(true);
      updatedAtField.set(article, LocalDateTime.now());
    } catch (Exception e) {
      throw new RuntimeException("Article 생성 중 오류 발생", e);
    }

    return article;
  }

  /**
   * 삭제된 Article 생성을 위한 팩토리 메서드
   */
  private Article createDeletedArticle(Long articleId, String title, String content, Long likesCount, Long spamCount, Long commentCount) {
    Article article = createArticle(articleId, title, content, likesCount, spamCount, commentCount);
    try {
      Field deletedAtField = Article.class.getDeclaredField("deletedAt");
      deletedAtField.setAccessible(true);
      deletedAtField.set(article, LocalDateTime.now());
    } catch (Exception e) {
      throw new RuntimeException("Article 삭제 필드 설정 중 오류 발생", e);
    }
    return article;
  }

  /*
    * 일반적인 게시글 조회 검증
   */
  @Test
  void getArticleList_정상_조회() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 2L;

    Article article1 = createArticle(10L, "제목1", "내용1", 5L, 0L, 1L);
    Article article2 = createArticle(9L, "제목2", "내용2", 3L, 1L, 2L);

    List<Article> articles = List.of(article1, article2);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.of(place));
    when(articleRepositoryCustom.findArticlesByPlaceWithCursor(placeId, cursor, limit + 1)).thenReturn(articles);
    when(articlePhotoRepository.findFirstByArticleAndIsMainTrue(any())).thenReturn(Optional.empty());
    when(articleLikeRepository.existsById(any(ArticleLikeId.class))).thenReturn(false);
    when(articleSpamRepository.existsById(any())).thenReturn(false);

    // when
    List<ArticleResponseDTO.ArticleListItemDTO> result = articleQueryService.getArticleList(memberId, placeId, cursor, limit);

    // then
    assertEquals(2, result.size());
    assertEquals(article1.getArticleId(), result.get(0).getArticleId());
    assertEquals(article2.getArticleId(), result.get(1).getArticleId());
    assertEquals(DEFAULT_IMAGE_UUID, result.get(0).getMainImageUuid());
    assertFalse(result.get(0).getIsLiked());
    assertFalse(result.get(0).getIsSpammed());
    assertTrue(result.get(0).getIsMine());
  }

  /*
    * 게시물이 없을 때 빈 리스트가 반환되는지 검증
   */
  @Test
  void getArticleList_게시물없을때_빈리스트_반환() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 5L;

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.of(place));
    when(articleRepositoryCustom.findArticlesByPlaceWithCursor(placeId, cursor, limit + 1)).thenReturn(List.of());

    // when
    List<ArticleResponseDTO.ArticleListItemDTO> result = articleQueryService.getArticleList(memberId, placeId, cursor, limit);

    // then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  /*
    * Soft Delete로 삭제 처리된 게시물을 제외하고 조회되는지 검증
   */
  @Test
  void getArticleList_삭제된_게시물_제외_정상_조회() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 3L;

    Article article1 = createArticle(10L, "제목1", "내용1", 5L, 0L, 1L);
    Article article2 = createArticle(9L, "제목2", "내용2", 3L, 1L, 2L);
    Article deletedArticle = createDeletedArticle(8L, "삭제된글", "삭제됨", 3L, 4L, 5L);

    List<Article> articles = List.of(article1, article2);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.of(place));
    when(articleRepositoryCustom.findArticlesByPlaceWithCursor(placeId, cursor, limit + 1)).thenReturn(articles);
    when(articlePhotoRepository.findFirstByArticleAndIsMainTrue(any())).thenReturn(Optional.empty());
    when(articleLikeRepository.existsById(any(ArticleLikeId.class))).thenReturn(false);
    when(articleSpamRepository.existsById(any())).thenReturn(false);

    // when
    List<ArticleResponseDTO.ArticleListItemDTO> result = articleQueryService.getArticleList(memberId, placeId, cursor, limit);

    // then
    // 삭제된 게시물은 result에 포함되지 않아야 함
    assertEquals(2, result.size());
    assertTrue(result.stream().noneMatch(dto -> dto.getArticleId().equals(deletedArticle.getArticleId())));
  }

  /*
    * 좋아요/스팸 처리를 한 게시글일 때, true로 반환되는지 검증
   */
  @Test
  void getArticleList_좋아요_스팸_여부_검증() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 1L;

    Article article = createArticle(10L, "제목", "내용", 1L, 1L, 0L);
    List<Article> articles = List.of(article);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.of(place));
    when(articleRepositoryCustom.findArticlesByPlaceWithCursor(placeId, cursor, limit + 1)).thenReturn(articles);
    when(articlePhotoRepository.findFirstByArticleAndIsMainTrue(any())).thenReturn(Optional.empty());
    when(articleLikeRepository.existsById(any(ArticleLikeId.class))).thenReturn(true); // 좋아요 true
    when(articleSpamRepository.existsById(any())).thenReturn(true); // 스팸 true

    // when
    List<ArticleResponseDTO.ArticleListItemDTO> result = articleQueryService.getArticleList(memberId, placeId, cursor, limit);

    // then
    // 좋아요/스팸 여부가 true로 반환되는지 검증
    assertEquals(1, result.size());
    assertTrue(result.get(0).getIsLiked());
    assertTrue(result.get(0).getIsSpammed());
  }

  /*
    * limit가 null인 경우, 기본 limit(10)으로 처리되어야 하며 정상적으로 조회되는지 검증
   */
  @Test
  void getArticleList_cursor_limit_null_정상_조회() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = null; // limit null

    Article article = createArticle(10L, "제목", "내용", 0L, 0L, 0L);
    List<Article> articles = List.of(article);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.of(place));
    // 기본 limit(10) + 1 = 11
    when(articleRepositoryCustom.findArticlesByPlaceWithCursor(placeId, cursor, 11L)).thenReturn(articles);
    when(articlePhotoRepository.findFirstByArticleAndIsMainTrue(any())).thenReturn(Optional.empty());
    when(articleLikeRepository.existsById(any(ArticleLikeId.class))).thenReturn(false);
    when(articleSpamRepository.existsById(any())).thenReturn(false);

    // when
    List<ArticleResponseDTO.ArticleListItemDTO> result = articleQueryService.getArticleList(memberId, placeId, cursor, limit);

    // then
    // limit이 null이어도 정상적으로 조회되는지 검증
    assertEquals(1, result.size());
    assertEquals(article.getArticleId(), result.get(0).getArticleId());
  }

  /*
    * cursor가 -1인 경우, null로 처리되어 정상적으로 조회되는지 검증
   */
  @Test
  void getArticleList_cursor_마이너스1_정상_조회() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = -1L;
    Long limit = 1L;

    Article article = createArticle(10L, "제목", "내용", 0L, 0L, 0L);
    List<Article> articles = List.of(article);

    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.of(place));
    // cursor가 -1이면 null로 처리되어야 함
    when(articleRepositoryCustom.findArticlesByPlaceWithCursor(placeId, null, limit + 1)).thenReturn(articles);
    when(articlePhotoRepository.findFirstByArticleAndIsMainTrue(any())).thenReturn(Optional.empty());
    when(articleLikeRepository.existsById(any(ArticleLikeId.class))).thenReturn(false);
    when(articleSpamRepository.existsById(any())).thenReturn(false);

    // when
    List<ArticleResponseDTO.ArticleListItemDTO> result = articleQueryService.getArticleList(memberId, placeId, cursor, limit);

    // then
    // cursor가 -1일 때도 정상적으로 조회되는지 검증
    assertEquals(1, result.size());
    assertEquals(article.getArticleId(), result.get(0).getArticleId());
  }

  /*
   * Member가 존재하지 않을 때 예외 발생 검증
   */
  @Test
  void getArticleList_멤버없음_예외() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(MemberHandler.class, () -> {
      articleQueryService.getArticleList(memberId, placeId, null, null);
    });
  }

  /*
   * Place가 존재하지 않을 때 예외 발생 검증
   */
  @Test
  void getArticleList_장소없음_예외() {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(placeRepository.findPlaceByPlaceId(placeId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(ArticleHandler.class, () -> {
      articleQueryService.getArticleList(memberId, placeId, null, null);
    });
  }
}

