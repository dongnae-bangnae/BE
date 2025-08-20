package DNBN.spring.service.ArticleService;

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

    @InjectMocks
    private ArticleCommandServiceImpl articleCommandService;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
                .commentCount(0L)
                .likesCount(0L)
                .spamCount(0L)
                .date(date)
                .build();
    }

    @Nested
    @DisplayName("게시물 생성 API (기존 장소)")
    class CreateArticleTest {

        @Test
        @DisplayName("이미지 없이 게시물 생성 시 성공")
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
        @DisplayName("대표 이미지 1개로 게시물 생성 시 성공")
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
        @DisplayName("여러 이미지를 첨부하여 게시물 생성 시 성공")
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
        @DisplayName("존재하지 않는 장소로 생성 시 예외 발생")
        void createArticle_placeNotFound() {
            ArticleRequestDTO request = getRequest();
            Member member = getMember();
            Category category = getCategory();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(placeRepository.findById(placeId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                articleCommandService.createArticle(memberId, request, null, null);
            });
        }

        @Test
        @DisplayName("존재하지 않는 지역으로 생성 시 예외 발생")
        void createArticle_regionNotFound() {
            ArticleRequestDTO request = getRequest();
            Member member = getMember();
            Category category = getCategory();
            Place place = getPlace();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(placeRepository.findById(placeId)).thenReturn(Optional.of(place));
            when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

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
    @DisplayName("게시물 생성 API (새 장소)")
    class CreateArticleWithLocationTest {
        @Test
        @DisplayName("위치 정보와 함께 게시물 생성 시 성공")
        void createArticleWithLocation_success() {
            ArticleWithLocationRequestDTO request = getWithLocationRequest();
            Member member = getMember();
            Category category = getCategory();
            Region region = getRegion();
            Place place = Place.builder()
                .placeId(999L)
                .region(region)
                .latitude(latitude)
                .longitude(longitude)
                .title(placeName)
                .address(detailAddress)
                .pinCategory(PinCategory.valueOf(pinCategory))
                .build();
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
            verify(placeRepository).save(any(Place.class));
            verify(articleRepository).save(article);
            verify(articleImageService).uploadAndSaveImages(article, place, region, null, null);
        }

        @Test
        @DisplayName("위치 정보와 함께 이미지 첨부하여 게시물 생성 시 성공")
        void createArticleWithLocation_withImages_success() {
            ArticleWithLocationRequestDTO request = getWithLocationRequest();
            Member member = getMember();
            Category category = getCategory();
            Region region = getRegion();
            Place place = Place.builder()
                .placeId(999L)
                .region(region)
                .latitude(latitude)
                .longitude(longitude)
                .title(placeName)
                .address(detailAddress)
                .pinCategory(PinCategory.valueOf(pinCategory))
                .build();
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
            assertTrue(result.photos.get(0).getIsMain());
            assertFalse(result.photos.get(1).getIsMain());
            verify(placeRepository).save(any(Place.class));
            verify(articleRepository).save(article);
            verify(articleImageService).uploadAndSaveImages(article, place, region, mainImage, imageFiles);
        }

        @Test
        @DisplayName("위치 정보와 함께 게시물 생성 시 존재하지 않는 멤버로 예외 발생")
        void createArticleWithLocation_memberNotFound() {
            ArticleWithLocationRequestDTO request = getWithLocationRequest();
            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                articleCommandService.createArticle(memberId, request, null, null);
            });
        }

        @Test
        @DisplayName("위치 정보와 함께 게시물 생성 시 존재하지 않는 카테고리로 예외 발생")
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
        @DisplayName("위치 정보와 함께 게시물 생성 시 존재하지 않는 지역으로 예외 발생")
        void createArticleWithLocation_regionNotFound() {
            ArticleWithLocationRequestDTO request = getWithLocationRequest();
            Member member = getMember();
            Category category = getCategory();
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                articleCommandService.createArticle(memberId, request, null, null);
            });
        }

        @Test
        @DisplayName("위치 정보와 함께 게시물 생성 시 제목/내용 유효성 검증 실패로 예외 발생")
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
}