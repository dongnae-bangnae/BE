package DNBN.spring.service.ArticleService;

import DNBN.spring.aws.s3.AmazonS3Manager;
import DNBN.spring.domain.*;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.validation.validator.ContentLengthValidator;
import DNBN.spring.validation.validator.TitleLengthValidator;
import DNBN.spring.web.dto.request.ArticleRequestDTO;
import DNBN.spring.web.dto.request.ArticleUpdateRequestDTO;
import DNBN.spring.web.dto.request.ArticleWithLocationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ArticleCommandServiceImplTest {

  @Mock private ArticleRepository articleRepository;
  @Mock private ArticlePhotoRepository articlePhotoRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private PlaceRepository placeRepository;
  @Mock private RegionRepository regionRepository;
  @Mock private TitleLengthValidator titleLengthValidator;
  @Mock private ContentLengthValidator contentLengthValidator;
  @Mock private ArticleImageService articleImageService;
  @Mock private ArticleUpdater articleUpdater;
  @Mock private PlaceUpdater placeUpdater;
  @Mock private ArticleFactory articleFactory;
  @Mock private AmazonS3Manager s3Manager;

  @InjectMocks
  private ArticleCommandServiceImpl articleCommandService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private final Long memberId = 1L;
  private final Long categoryId = 2L;
  private final Long placeId = 3L;
  private final Long regionId = 4L;
  private final String title = "테스트 제목";
  private final String content = "테스트 내용";
  private final String placeName = "장소명";
  private final String pinCategory = "CAFE";
  private final LocalDate date = LocalDate.now();
  private final String detailAddress = "상세주소";
  private final Double latitude = 37.123;
  private final Double longitude = 127.456;

  private ArticleRequestDTO getRequest() {
    return new ArticleRequestDTO(categoryId, placeId, placeName, pinCategory, regionId, title, date, content);
  }

  private ArticleWithLocationRequestDTO getWithLocationRequest() {
    return new ArticleWithLocationRequestDTO(categoryId, placeName, detailAddress, pinCategory, regionId, latitude, longitude, title, date, content);
  }

  private Member getMember() {
    return Member.builder().id(memberId).build();
  }

  private Category getCategory() {
    return Category.builder().categoryId(categoryId).build();
  }

  private Place getPlace() {
    return Place.builder().placeId(placeId).title(placeName).pinCategory(PinCategory.CAFE).build();
  }

  private Region getRegion() {
    return Region.builder().id(regionId).build();
  }

  private Article getArticle(Member member, Category category, Place place, Region region) {
    return Article.builder()
        .articleId(10L)
        .member(member)
        .category(category)
        .place(place)
        .region(region)
        .title(title)
        .content(content)
        .build();
  }

  @Nested
  @DisplayName("createArticle(ArticleRequestDTO) 단위 테스트")
  class CreateArticleWithRegisteredPlaceTest {

    @Test
    @DisplayName("정상적으로 게시물 생성 - 이미지 없음")
    void createArticle_success_noImage() {
      ArticleRequestDTO request = getRequest();
      Member member = getMember();
      Category category = getCategory();
      Place place = getPlace();
      Region region = getRegion();
      Article article = getArticle(member, category, place, region);

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      when(articleFactory.create(member, category, place, region, request)).thenReturn(article);
      when(articlePhotoRepository.findAllByArticle(article)).thenReturn(List.of());

      ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, request, null, null);

      assertNotNull(result);
      assertEquals(article, result.article);
      assertTrue(result.photos.isEmpty());
      verify(articleRepository).save(article);
      verify(articleImageService).uploadAndSaveImages(article, place, region, null, null);
    }

    @Test
    @DisplayName("정상적으로 게시물 생성 - 대표 이미지 1개")
    void createArticle_success_withMainImage() {
      ArticleRequestDTO request = getRequest();
      Member member = getMember();
      Category category = getCategory();
      Place place = getPlace();
      Region region = getRegion();
      Article article = getArticle(member, category, place, region);

      MultipartFile mainImage = mock(MultipartFile.class);
      List<ArticlePhoto> photos = List.of(
          ArticlePhoto.builder().fileKey("main-uuid").isMain(true).build()
      );

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      when(articleFactory.create(member, category, place, region, request)).thenReturn(article);
      when(articlePhotoRepository.findAllByArticle(article)).thenReturn(photos);

      ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, request, mainImage, null);

      assertNotNull(result);
      assertEquals(article, result.article);
      assertEquals(1, result.photos.size());
      assertTrue(result.photos.get(0).getIsMain());
      verify(articleRepository).save(article);
      verify(articleImageService).uploadAndSaveImages(article, place, region, mainImage, null);
    }

    @Test
    @DisplayName("정상적으로 게시물 생성 - 이미지 여러 개")
    void createArticle_success_withMultipleImages() {
      ArticleRequestDTO request = getRequest();
      Member member = getMember();
      Category category = getCategory();
      Place place = getPlace();
      Region region = getRegion();
      Article article = getArticle(member, category, place, region);

      MultipartFile mainImage = mock(MultipartFile.class);
      MultipartFile image1 = mock(MultipartFile.class);
      MultipartFile image2 = mock(MultipartFile.class);
      List<MultipartFile> imageFiles = List.of(image1, image2);

      List<ArticlePhoto> photos = List.of(
          ArticlePhoto.builder().fileKey("main-uuid").isMain(true).build(),
          ArticlePhoto.builder().fileKey("uuid-1").isMain(false).build(),
          ArticlePhoto.builder().fileKey("uuid-2").isMain(false).build()
      );

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      when(articleFactory.create(member, category, place, region, request)).thenReturn(article);
      when(articlePhotoRepository.findAllByArticle(article)).thenReturn(photos);

      ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, request, mainImage, imageFiles);

      assertNotNull(result);
      assertEquals(article, result.article);
      assertEquals(3, result.photos.size());
      verify(articleRepository).save(article);
      verify(articleImageService).uploadAndSaveImages(article, place, region, mainImage, imageFiles);
    }

    @Test
    @DisplayName("존재하지 않는 멤버로 생성 시 예외 발생")
    void createArticle_memberNotFound() {
      ArticleRequestDTO request = getRequest();
      when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> {
        articleCommandService.createArticle(memberId, request, null, null);
      });
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 생성 시 예외 발생")
    void createArticle_categoryNotFound() {
      ArticleRequestDTO request = getRequest();
      Member member = getMember();
      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> {
        articleCommandService.createArticle(memberId, request, null, null);
      });
    }

    @Test
    @DisplayName("제목/내용 유효성 검증 실패 시 예외 발생")
    void createArticle_validationFail() {
      ArticleRequestDTO request = getRequest();
      Member member = getMember();
      Category category = getCategory();
      Place place = getPlace();
      Region region = getRegion();

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      doThrow(new IllegalArgumentException("제목 길이 오류")).when(titleLengthValidator).validateArticleTitle(anyString());

      assertThrows(IllegalArgumentException.class, () -> {
        articleCommandService.createArticle(memberId, request, null, null);
      });
    }
  }

  @Nested
  @DisplayName("createArticle(ArticleWithLocationRequestDTO) 단위 테스트")
  class CreateArticleWithNewLocationTest {

    @Test
    @DisplayName("정상적으로 게시물 생성 - 신규 장소, 이미지 없음")
    void createArticleWithLocation_success_noImage() {
      ArticleWithLocationRequestDTO request = getWithLocationRequest();
      Member member = getMember();
      Category category = getCategory();
      Region region = getRegion();
      Place place = Place.builder().placeId(100L).title(placeName).latitude(latitude).longitude(longitude).address(detailAddress).pinCategory(PinCategory.CAFE).region(region).build();
      Article article = getArticle(member, category, place, region);

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      when(placeRepository.save(any(Place.class))).thenReturn(place);
      when(articleFactory.create(member, category, place, region, request)).thenReturn(article);
      when(articlePhotoRepository.findAllByArticle(article)).thenReturn(List.of());

      ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, request, null, null);

      assertNotNull(result);
      assertEquals(article, result.article);
      assertTrue(result.photos.isEmpty());
      verify(articleRepository).save(article);
      verify(articleImageService).uploadAndSaveImages(article, place, region, null, null);
    }

    @Test
    @DisplayName("정상적으로 게시물 생성 - 신규 장소, 이미지 여러 개")
    void createArticleWithLocation_success_withImages() {
      ArticleWithLocationRequestDTO request = getWithLocationRequest();
      Member member = getMember();
      Category category = getCategory();
      Region region = getRegion();
      Place place = Place.builder().placeId(100L).title(placeName).latitude(latitude).longitude(longitude).address(detailAddress).pinCategory(PinCategory.CAFE).region(region).build();
      Article article = getArticle(member, category, place, region);

      MultipartFile mainImage = mock(MultipartFile.class);
      MultipartFile image1 = mock(MultipartFile.class);
      List<MultipartFile> imageFiles = List.of(image1);

      List<ArticlePhoto> photos = List.of(
          ArticlePhoto.builder().fileKey("main-uuid").isMain(true).build(),
          ArticlePhoto.builder().fileKey("uuid-1").isMain(false).build()
      );

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      when(placeRepository.save(any(Place.class))).thenReturn(place);
      when(articleFactory.create(member, category, place, region, request)).thenReturn(article);
      when(articlePhotoRepository.findAllByArticle(article)).thenReturn(photos);

      ArticleCommandService.ArticleWithPhotos result = articleCommandService.createArticle(memberId, request, mainImage, imageFiles);

      assertNotNull(result);
      assertEquals(article, result.article);
      assertEquals(2, result.photos.size());
      verify(articleRepository).save(article);
      verify(articleImageService).uploadAndSaveImages(article, place, region, mainImage, imageFiles);
    }

    @Test
    @DisplayName("존재하지 않는 멤버로 생성 시 예외 발생")
    void createArticleWithLocation_memberNotFound() {
      ArticleWithLocationRequestDTO request = getWithLocationRequest();
      when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> {
        articleCommandService.createArticle(memberId, request, null, null);
      });
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 생성 시 예외 발생")
    void createArticleWithLocation_categoryNotFound() {
      ArticleWithLocationRequestDTO request = getWithLocationRequest();
      Member member = getMember();
      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

      assertThrows(RuntimeException.class, () -> {
        articleCommandService.createArticle(memberId, request, null, null);
      });
    }

    @Test
    @DisplayName("제목/내용 유효성 검증 실패 시 예외 발생")
    void createArticleWithLocation_validationFail() {
      ArticleWithLocationRequestDTO request = getWithLocationRequest();
      Member member = getMember();
      Category category = getCategory();
      Region region = getRegion();

      when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
      when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
      when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));
      doThrow(new IllegalArgumentException("제목 길이 오류")).when(titleLengthValidator).validateArticleTitle(anyString());

      assertThrows(IllegalArgumentException.class, () -> {
        articleCommandService.createArticle(memberId, request, null, null);
      });
    }
  }

  @Nested
@DisplayName("updateArticle 단위 테스트")
class UpdateArticleTest {

    private final Long memberId = 1L;
    private final Long articleId = 10L;

    private Article getArticle() {
        Place place = Place.builder().placeId(3L).build();
        Region region = Region.builder().id(4L).build();
        return Article.builder()
                .articleId(articleId)
                .place(place)
                .region(region)
                .build();
    }

    @Test
    @DisplayName("정상적으로 게시물 수정 - 이미지 변경 없음")
    void updateArticle_success_noImageChange() {
        Article article = getArticle();
        ArticleUpdateRequestDTO request = mock(ArticleUpdateRequestDTO.class);
        List<ArticlePhoto> photos = List.of(
                ArticlePhoto.builder().fileKey("uuid1").isMain(true).build()
        );

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articlePhotoRepository.findAllByArticle(article)).thenReturn(photos);

        ArticleCommandService.ArticleWithPhotos result = articleCommandService.updateArticle(memberId, articleId, request, null, null);

        assertNotNull(result);
        assertEquals(article, result.article);
        assertEquals(photos, result.photos);
        verify(articleUpdater).updateArticleEntity(article, request);
        verify(placeUpdater).updatePlaceEntity(article.getPlace(), request);
        verify(articlePhotoRepository, never()).delete(any());
        verify(s3Manager, never()).deleteFile(anyString());
        verify(articleImageService, never()).uploadAndSaveImages(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("정상적으로 게시물 수정 - 이미지 변경(대표/추가)")
    void updateArticle_success_withImageChange() {
        Article article = getArticle();
        ArticleUpdateRequestDTO request = mock(ArticleUpdateRequestDTO.class);
        MultipartFile mainImage = mock(MultipartFile.class);
        MultipartFile image1 = mock(MultipartFile.class);
        List<MultipartFile> imageFiles = List.of(image1);

        List<ArticlePhoto> oldPhotos = List.of(
                ArticlePhoto.builder().fileKey("old-uuid1").isMain(true).build(),
                ArticlePhoto.builder().fileKey("old-uuid2").isMain(false).build()
        );
        List<ArticlePhoto> newPhotos = List.of(
                ArticlePhoto.builder().fileKey("new-uuid1").isMain(true).build(),
                ArticlePhoto.builder().fileKey("new-uuid2").isMain(false).build()
        );

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articlePhotoRepository.findAllByArticle(article)).thenReturn(oldPhotos).thenReturn(newPhotos);

        ArticleCommandService.ArticleWithPhotos result = articleCommandService.updateArticle(memberId, articleId, request, mainImage, imageFiles);

        assertNotNull(result);
        assertEquals(article, result.article);
        assertEquals(newPhotos, result.photos);
        verify(articleUpdater).updateArticleEntity(article, request);
        verify(placeUpdater).updatePlaceEntity(article.getPlace(), request);
        verify(articlePhotoRepository, times(2)).delete(any());
        verify(s3Manager, times(2)).deleteFile(anyString());
        verify(articleImageService).uploadAndSaveImages(article, article.getPlace(), article.getRegion(), mainImage, imageFiles);
    }

    @Test
    @DisplayName("게시물이 존재하지 않을 때 예외 발생")
    void updateArticle_articleNotFound() {
        ArticleUpdateRequestDTO request = mock(ArticleUpdateRequestDTO.class);
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            articleCommandService.updateArticle(memberId, articleId, request, null, null);
        });
    }
}
}