package DNBN.spring.service.ArticleService;

import DNBN.spring.aop.annotation.ValidateArticle;
import DNBN.spring.aop.annotation.ValidateS3ImageUpload;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.apiPayload.exception.handler.RegionHandler;
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
    private final ArticleUpdater articleUpdater;
    private final PlaceUpdater placeUpdater;
    private final ArticleFactory articleFactory;

    @Override
    @ValidateS3ImageUpload
    @ValidateArticle
    public ArticleWithPhotos createArticle(Long memberId, ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = getMember(memberId);
        Category category = getCategory(request.categoryId());
        Place place = getPlace(request.placeId());
        Region region = getRegion(request.regionId());

        place.updateTitle(request.placeName());
        place.updatePinCategory(PinCategory.valueOf(request.pinCategory().toUpperCase()));

        titleLengthValidator.validateArticleTitle(request.title());
        contentLengthValidator.validateArticleContent(request.content());

        Article article = articleFactory.create(member, category, place, region, request);
        articleRepository.save(article);

        articleImageService.uploadAndSaveImages(article, place, region, mainImage, imageFiles);
        List<ArticlePhoto> savedPhotos = articlePhotoRepository.findAllByArticle(article);
        return new ArticleWithPhotos(article, savedPhotos);
    }

    @Override
    @ValidateS3ImageUpload
    @ValidateArticle
    public ArticleWithPhotos createArticle(Long memberId, ArticleWithLocationRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = getMember(memberId);
        Category category = getCategory(request.categoryId());
        Region region    = getRegion(request.regionId());

        titleLengthValidator.validateArticleTitle(request.title());
        contentLengthValidator.validateArticleContent(request.content());

        Place place = Place.builder()
                .region(region)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .title(request.placeName())
                .address(request.detailAddress())
                .pinCategory(PinCategory.valueOf(request.pinCategory().toUpperCase()))
                .build();

        place = placeRepository.save(place);

        Article article = articleFactory.create(member, category, place, region, request);
        articleRepository.save(article);

        articleImageService.uploadAndSaveImages(article, place, region, mainImage, imageFiles);
        List<ArticlePhoto> savedPhotos = articlePhotoRepository.findAllByArticle(article);
        return new ArticleWithPhotos(article, savedPhotos);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }
    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
    }
    private Place getPlace(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
    }
    private Region getRegion(Long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND));
    }

    @Override
    @ValidateS3ImageUpload
    @ValidateArticle
    public ArticleWithPhotos updateArticle(Long memberId, Long articleId, ArticleUpdateRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Article article = getArticle(articleId);

        articleUpdater.updateArticleEntity(article, request);
        placeUpdater.updatePlaceEntity(article.getPlace(), request);

        List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(article);
        // 새로운 이미지가 제공된 경우, 기존 이미지 삭제 후 새로 추가
        if ((mainImage != null && !mainImage.isEmpty()) || (imageFiles != null && !imageFiles.isEmpty())) {
            for (ArticlePhoto photo : photos) {
                s3Manager.deleteFile(photo.getFileKey());
                articlePhotoRepository.delete(photo);
            }

            articleImageService.uploadAndSaveImages(article, article.getPlace(), article.getRegion(), mainImage, imageFiles);
            photos = articlePhotoRepository.findAllByArticle(article);
        }

        return new ArticleWithPhotos(article, photos);
    }

    private Article getArticle(Long articleId) {
        return articleRepository.findById(articleId)
            .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
    }

    @Override
    @ValidateArticle
    public void deleteArticle(Long memberId, Long articleId) {
        Article article = getArticle(articleId);
        article.delete(); // dirty checking
    }
}
