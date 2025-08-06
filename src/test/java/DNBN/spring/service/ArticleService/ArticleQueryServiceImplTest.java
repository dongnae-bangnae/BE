package DNBN.spring.service.ArticleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // @Value 필드 수동 주입
    articleQueryService.getClass().getDeclaredFields();
    try {
      var field = ArticleQueryServiceImpl.class.getDeclaredField("defaultImageUuid");
      field.setAccessible(true);
      field.set(articleQueryService, DEFAULT_IMAGE_UUID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*
    * 일반적인 게시글 조회 검증
   */
  @Test
  void getArticleList_정상_조회() throws NoSuchFieldException, IllegalAccessException {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 2L;

    Member member = Member.builder().id(memberId).nickname("테스터").build();
    Place place = Place.builder().placeId(placeId).title("장소").pinCategory(PinCategory.CAFE).address("주소").build();
    Article article1 = Article.builder()
        .articleId(10L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목1")
        .content("내용1")
        .likesCount(5L)
        .spamCount(0L)
        .commentCount(1L)
        .date(LocalDate.now())
        .build();
    Article article2 = Article.builder()
        .articleId(9L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목2")
        .content("내용2")
        .likesCount(3L)
        .spamCount(1L)
        .commentCount(2L)
        .date(LocalDate.now())
        .build();

    // createdAt, updatedAt 필드 설정
    for (Article article : List.of(article1, article2)) {
      Field createdAtField = Article.class.getSuperclass().getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(article, LocalDateTime.now());

      Field updatedAtField = Article.class.getSuperclass().getDeclaredField("updatedAt");
      updatedAtField.setAccessible(true);
      updatedAtField.set(article, LocalDateTime.now());
    }

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
    * 삭제된 게시물은 제외하고 조회되는지 검증
   */
  @Test
  void getArticleList_삭제된_게시물_제외_정상_조회() throws Exception {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 3L;

    Member member = Member.builder().id(memberId).nickname("테스터").build();
    Place place = Place.builder().placeId(placeId).title("장소").pinCategory(PinCategory.CAFE).address("주소").build();

    Article article1 = Article.builder()
        .articleId(10L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목1")
        .content("내용1")
        .likesCount(5L)
        .spamCount(0L)
        .commentCount(1L)
        .date(LocalDate.now())
        .build();

    Article article2 = Article.builder()
        .articleId(9L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목2")
        .content("내용2")
        .likesCount(3L)
        .spamCount(1L)
        .commentCount(2L)
        .date(LocalDate.now())
        .build();

    Article deletedArticle = Article.builder()
        .articleId(8L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("삭제된글")
        .content("삭제됨")
        .likesCount(3L)
        .spamCount(4L)
        .commentCount(5L)
        .date(LocalDate.now())
        .build();

    // deletedAt, createdAt, updatedAt 필드 설정
    Field deletedAtField = Article.class.getDeclaredField("deletedAt");
    deletedAtField.setAccessible(true);
    deletedAtField.set(deletedArticle, LocalDateTime.now());

    for (Article article : List.of(article1, article2, deletedArticle)) {
      Field createdAtField = Article.class.getSuperclass().getDeclaredField("createdAt");
      createdAtField.setAccessible(true);
      createdAtField.set(article, LocalDateTime.now());

      Field updatedAtField = Article.class.getSuperclass().getDeclaredField("updatedAt");
      updatedAtField.setAccessible(true);
      updatedAtField.set(article, LocalDateTime.now());
    }

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
  void getArticleList_좋아요_스팸_여부_검증() throws Exception {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = 1L;

    Member member = Member.builder().id(memberId).nickname("테스터").build();
    Place place = Place.builder().placeId(placeId).title("장소").pinCategory(PinCategory.CAFE).address("주소").build();
    Article article = Article.builder()
        .articleId(10L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목")
        .content("내용")
        .likesCount(1L)
        .spamCount(1L)
        .commentCount(0L)
        .date(LocalDate.now())
        .build();

    Field createdAtField = Article.class.getSuperclass().getDeclaredField("createdAt");
    createdAtField.setAccessible(true);
    createdAtField.set(article, LocalDateTime.now());
    Field updatedAtField = Article.class.getSuperclass().getDeclaredField("updatedAt");
    updatedAtField.setAccessible(true);
    updatedAtField.set(article, LocalDateTime.now());

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
  void getArticleList_cursor_limit_null_정상_조회() throws Exception {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = null;
    Long limit = null; // limit null

    Member member = Member.builder().id(memberId).nickname("테스터").build();
    Place place = Place.builder().placeId(placeId).title("장소").pinCategory(PinCategory.CAFE).address("주소").build();
    Article article = Article.builder()
        .articleId(10L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목")
        .content("내용")
        .likesCount(0L)
        .spamCount(0L)
        .commentCount(0L)
        .date(LocalDate.now())
        .build();

    Field createdAtField = Article.class.getSuperclass().getDeclaredField("createdAt");
    createdAtField.setAccessible(true);
    createdAtField.set(article, LocalDateTime.now());
    Field updatedAtField = Article.class.getSuperclass().getDeclaredField("updatedAt");
    updatedAtField.setAccessible(true);
    updatedAtField.set(article, LocalDateTime.now());

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
  void getArticleList_cursor_마이너스1_정상_조회() throws Exception {
    // given
    Long memberId = 1L;
    Long placeId = 2L;
    Long cursor = -1L;
    Long limit = 1L;

    Member member = Member.builder().id(memberId).nickname("테스터").build();
    Place place = Place.builder().placeId(placeId).title("장소").pinCategory(PinCategory.CAFE).address("주소").build();
    Article article = Article.builder()
        .articleId(10L)
        .member(member)
        .place(place)
        .region(Region.builder().id(100L).build())
        .title("제목")
        .content("내용")
        .likesCount(0L)
        .spamCount(0L)
        .commentCount(0L)
        .date(LocalDate.now())
        .build();

    Field createdAtField = Article.class.getSuperclass().getDeclaredField("createdAt");
    createdAtField.setAccessible(true);
    createdAtField.set(article, LocalDateTime.now());
    Field updatedAtField = Article.class.getSuperclass().getDeclaredField("updatedAt");
    updatedAtField.setAccessible(true);
    updatedAtField.set(article, LocalDateTime.now());

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
}