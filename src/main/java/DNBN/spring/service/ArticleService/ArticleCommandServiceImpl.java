package DNBN.spring.service.ArticleService;

import DNBN.spring.aop.annotation.ValidateS3ImageUpload;
import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.ArticlePhotoHandler;
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
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.ArticleRequestDTO;
import DNBN.spring.web.dto.ArticleWithLocationRequestDTO;
import java.util.ArrayList;
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

    @Override
    @ValidateS3ImageUpload
    public ArticleWithPhotos createArticle(Long memberId, ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = getMember(memberId);
        Category category = getCategory(request.categoryId());
        Place place = getPlace(request.placeId());
        Region region = getRegion(request.regionId());

        // TODO: Place 업데이트 필요 🚩🚩
        String placeName = request.placeName();
        String pinCategory = request.pinCategory();
        log.info("Creating new place with name: {}, pinCategory: {}", placeName, pinCategory);

        Article article = createArticleEntity(member, category, place, region, request);
        articleRepository.save(article);

        List<ArticlePhoto> photos = handleImages(article, place, region, mainImage, imageFiles);
        return new ArticleWithPhotos(article, photos);
    }

    @Override
    @ValidateS3ImageUpload
    public ArticleWithPhotos createArticle(Long memberId, ArticleWithLocationRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = getMember(memberId);
        Category category = getCategory(request.categoryId());

        // TODO: Place 및 Region 생성 로직 추가 필요 🚩🚩
        String placeName = request.placeName();
        String pinCategory = request.pinCategory();
        String detailAddress = request.detailAddress();
        Double latitude = request.latitude();
        Double longitude = request.longitude();
        log.info("Creating new place with name: {}, pinCategory: {}, detailAddress: {}, latitude: {}, longitude: {}", placeName, pinCategory, detailAddress, latitude, longitude);

        Place place = getPlace(1L);
        Region region = getRegion(1L);

        Article article = createArticleEntity(member, category, place, region, request);
        articleRepository.save(article);

        List<ArticlePhoto> photos = handleImages(article, place, region, mainImage, imageFiles);
        return new ArticleWithPhotos(article, photos);
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

    // TODO: 팩토리 검토
    private Article createArticleEntity(Member member, Category category, Place place, Region region, ArticleRequestDTO request) {
        // placeName, pinCategory 활용 예시 (추후 도메인/DB 반영 필요)
        String placeName = request.placeName();
        String pinCategory = request.pinCategory();
        // ...기존 빌더 코드...
        return Article.builder()
                .member(member)
                .category(category)
                .place(place)
                .region(region)
                .title(request.title())
                .date(request.date())
                .content(request.content())
                .likesCount(0L)
                .spamCount(0L)
                // 필요시 placeName, pinCategory를 Article에 저장하도록 확장 가능
                .build();
    }
    private Article createArticleEntity(Member member, Category category, Place place, Region region, ArticleWithLocationRequestDTO request) {
        return Article.builder()
                .member(member)
                .category(category)
                .place(place)
                .region(region)
                .title(request.title())
                .date(request.date())
                .content(request.content())
                .likesCount(0L)
                .spamCount(0L)
                .build();
    }

    // TODO: SRP 위반
    private List<ArticlePhoto> handleImages(Article article, Place place, Region region, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        List<ArticlePhoto> photos = new ArrayList<>();
        List<String> uploadedKeys = new ArrayList<>();
        try {
            if (mainImage != null && !mainImage.isEmpty()) {
                String uuid = java.util.UUID.randomUUID().toString();
                String mainImageKey = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), mainImage);
                if (mainImageKey == null || mainImageKey.isBlank()) {
                    throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
                }
                uploadedKeys.add(mainImageKey);
                ArticlePhoto mainPhoto = ArticlePhoto.builder()
                        .article(article)
                        .place(place)
                        .region(region)
                        .fileKey(mainImageKey)
                        .orderIndex(0)
                        .isMain(true)
                        .build();
                photos.add(articlePhotoRepository.save(mainPhoto));
            }
            if (imageFiles != null) {
                int idx = 1;
                for (MultipartFile file : imageFiles) {
                    if (file != null && !file.isEmpty()) {
                        String uuid = java.util.UUID.randomUUID().toString();
                        String imageKey = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), file);
                        if (imageKey == null || imageKey.isBlank()) {
                            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
                        }
                        uploadedKeys.add(imageKey);
                        ArticlePhoto photo = ArticlePhoto.builder()
                                .article(article)
                                .place(place)
                                .region(region)
                                .fileKey(imageKey)
                                .orderIndex(idx++)
                                .isMain(false)
                                .build();
                        photos.add(articlePhotoRepository.save(photo));
                    }
                }
            }
        } catch (Exception e) {
            log.error("S3 업로드 실패", e);
            for (String key : uploadedKeys) {
                try {
                    s3Manager.deleteFile(key);
                } catch (Exception ex) {
                    log.error("S3 롤백(파일 삭제) 실패: {}", key, ex);
                }
            }
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
        }
        return photos;
    }

    @Override
    public void deleteArticle(Long memberId, Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleHandler(ErrorStatus.ARTICLE_NOT_FOUND));
        if (!article.getMember().getId().equals(memberId)) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_FORBIDDEN);
        }
        if (article.getDeletedAt() != null) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_ALREADY_DELETED);
        }
        article.delete(); // dirty checking
    }
}
