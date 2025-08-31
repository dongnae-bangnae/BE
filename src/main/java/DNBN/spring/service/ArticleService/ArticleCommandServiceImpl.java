package DNBN.spring.service.ArticleService;

import DNBN.spring.aop.annotation.ValidateArticle;
import DNBN.spring.aop.annotation.ValidateS3ImageUpload;
import DNBN.spring.aws.s3.AmazonS3Manager;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.util.EntityFinder;
import DNBN.spring.validation.validator.ContentLengthValidator;
import DNBN.spring.validation.validator.TitleLengthValidator;
import DNBN.spring.web.dto.request.ArticleRequestDTO;
import DNBN.spring.web.dto.request.ArticleUpdateRequestDTO;
import DNBN.spring.web.dto.request.ArticleWithLocationRequestDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ArticleCommandServiceImpl implements ArticleCommandService {

    private final ArticleRepository articleRepository;
    private final ArticlePhotoRepository articlePhotoRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final AmazonS3Manager s3Manager;
    private final TitleLengthValidator titleLengthValidator;
    private final ContentLengthValidator contentLengthValidator;
    private final ArticleImageService articleImageService;
    private final PlaceUpdater placeUpdater;
    private final ArticleFactory articleFactory;
    private final ArticleImageUploadService articleImageUploadService;

    @Override
    @ValidateS3ImageUpload
    @ValidateArticle
    public ArticleWithPhotos createArticle(Long memberId, ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = getMember(memberId);
        Category category = getCategory(request.categoryId());
        Place place = getPlace(request.placeId());
        Region region = place.getRegion();

        updatePlaceInfo(place, request);
        validateArticleContent(request);
        
        return createArticleInternal(member, category, place, region, request, mainImage, imageFiles);
    }

    @Override
    @ValidateS3ImageUpload
    @ValidateArticle
    public ArticleWithPhotos createArticle(Long memberId, ArticleWithLocationRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = getMember(memberId);
        Category category = getCategory(request.categoryId());
        Region region = findRegionByCoordinatesAccurate(request.latitude(), request.longitude());
        log.debug("게시글 생성 API (미등록) - 위경도로 계산한 지역 ID: {}", region.getId());

        validateArticleContent(request);
        Place place = createAndSaveNewPlace(request, region);
        
        return createArticleInternal(member, category, place, region, request, mainImage, imageFiles);
    }

    // 로직 복잡..
    @Override
    @ValidateS3ImageUpload
    @ValidateArticle
    public ArticleWithPhotos updateArticle(Long memberId, Long articleId, ArticleUpdateRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Article article = getArticle(articleId);

        updateArticleAndPlace(article, request);
        updateArticleImages(article, mainImage, imageFiles);

        List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(article);
        return new ArticleWithPhotos(article, photos);
    }

    @Override
    @ValidateArticle
    public void deleteArticle(Long memberId, Long articleId) {
        Article article = getArticle(articleId);
        article.delete();
    }

    private void updatePlaceInfo(Place place, ArticleRequestDTO request) {
        place.updateTitle(request.placeName());
        place.updatePinCategory(PinCategory.valueOf(request.pinCategory().toUpperCase()));
    }

    private void validateArticleContent(ArticleRequestDTO request) {
        titleLengthValidator.validateArticleTitle(request.title());
        contentLengthValidator.validateArticleContent(request.content());
    }

    private void validateArticleContent(ArticleWithLocationRequestDTO request) {
        titleLengthValidator.validateArticleTitle(request.title());
        contentLengthValidator.validateArticleContent(request.content());
    }

    private ArticleWithPhotos createArticleInternal(Member member, Category category, Place place, Region region, 
            ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {

        List<ArticlePhoto> photos = articleImageUploadService.uploadImages(place, region, mainImage, imageFiles);
        Article article = articleFactory.create(
            member,
            category,
            place,
            region,
            request.title(),
            request.date(),
            request.content()
        );
        articleRepository.save(article);
        List<ArticlePhoto> savedPhotos = articleImageUploadService.saveImagesWithArticle(photos, article);

        return new ArticleWithPhotos(article, savedPhotos);
    }

    private ArticleWithPhotos createArticleInternal(Member member, Category category, Place place, Region region, 
            ArticleWithLocationRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {

        List<ArticlePhoto> photos = articleImageUploadService.uploadImages(place, region, mainImage, imageFiles);
        Article article = articleFactory.create(
            member,
            category,
            place,
            region,
            request.title(),
            request.date(),
            request.content()
        );
        articleRepository.save(article);
        List<ArticlePhoto> savedPhotos = articleImageUploadService.saveImagesWithArticle(photos, article);

        return new ArticleWithPhotos(article, savedPhotos);
    }

    private Place createAndSaveNewPlace(ArticleWithLocationRequestDTO request, Region region) {
        Place place = Place.builder()
                .region(region)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .title(request.placeName())
                .address(request.detailAddress())
                .pinCategory(PinCategory.valueOf(request.pinCategory().toUpperCase()))
                .build();

        return placeRepository.save(place);
    }

    private void updateArticleAndPlace(Article article, ArticleUpdateRequestDTO request) {
        if (request.title() != null) {
            article.updateTitle(request.title());
        }
        if (request.content() != null) {
            article.updateContent(request.content());
        }
        if (request.date() != null) {
            article.updateDate(request.date());
        }
        if (request.categoryId() != null) {
            Category category = getCategory(request.categoryId());
            article.updateCategory(category);
        }
        if (request.regionId() != null) {
            Region region = getRegion(request.regionId());
            article.updateRegion(region);
        }
        if (request.placeId() != null) {
            Place place = getPlace(request.placeId());
            article.updatePlace(place);
        }
        placeUpdater.updatePlaceEntity(article.getPlace(), request);
    }

    private void updateArticleImages(Article article, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        if (!hasNewImages(mainImage, imageFiles)) {
            return;
        }

        deleteExistingImages(article);
        uploadNewImages(article, mainImage, imageFiles);
    }

    private boolean hasNewImages(MultipartFile mainImage, List<MultipartFile> imageFiles) {
        return (mainImage != null && !mainImage.isEmpty()) || 
               (imageFiles != null && !imageFiles.isEmpty());
    }

    private void deleteExistingImages(Article article) {
        List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(article);
        for (ArticlePhoto photo : photos) {
            s3Manager.deleteFile(photo.getFileKey());
            articlePhotoRepository.delete(photo);
        }
    }

    private void uploadNewImages(Article article, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        // 이미지 업로드 먼저 진행
        List<ArticlePhoto> photos = articleImageUploadService.uploadImages(article.getPlace(), article.getRegion(), mainImage, imageFiles);
        articleImageUploadService.saveImagesWithArticle(photos, article);
    }

    /*
     * 도메인 및 서비스 구조 리팩토링으로 조회 로직이 자주 바뀌어
     * getArticle, getMember 등 얇은 래퍼 메서드를 유지함
     * 추후 구조가 안정화되면 EntityFinder 직접 호출로 변경 고려
     */
    private Article getArticle(Long articleId) {
        return EntityFinder.getArticleOrThrow(articleRepository, articleId);
    }

    private Member getMember(Long memberId) {
        return EntityFinder.getMemberOrThrow(memberRepository, memberId);
    }

    private Category getCategory(Long categoryId) {
        return EntityFinder.getCategoryOrThrow(categoryRepository, categoryId);
    }

    private Place getPlace(Long placeId) {
        return EntityFinder.getPlaceOrThrow(placeRepository, placeId);
    }

    private Region getRegion(Long regionId) {
        return EntityFinder.getRegionOrThrow(regionRepository, regionId);
    }

    private Region findRegionByCoordinates(Double latitude, Double longitude) {
        return regionRepository.findRegionByCoordinates(latitude, longitude);
    }

    private Region findRegionByCoordinatesAccurate(Double latitude, Double longitude) {
        return regionRepository.findRegionByCoordinatesAccurate(latitude, longitude);
    }
}
