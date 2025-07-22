package DNBN.spring.service.ArticleService;

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
    private static final int MAX_IMAGE_COUNT = 10;
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int TITLE_MIN_LENGTH = 2;
    private static final int TITLE_MAX_LENGTH = 100;
    private static final int CONTENT_MIN_LENGTH = 10;
    private static final int CONTENT_MAX_LENGTH = 5000;

    private final ArticleRepository articleRepository;
    private final ArticlePhotoRepository articlePhotoRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;
    private final RegionRepository regionRepository;
    private final AmazonS3Manager s3Manager;

    @Override
    public ArticleWithPhotos createArticle(Long memberId, ArticleRequestDTO request, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryHandler(ErrorStatus.CATEGORY_NOT_FOUND));
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));
        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new RegionHandler(ErrorStatus.REGION_NOT_FOUND));

        // 게시물 제목/내용 길이 제한 (제목 2~100자, 내용 10~5000자)
        if (request.title() == null || request.title().length() < TITLE_MIN_LENGTH || request.title().length() > TITLE_MAX_LENGTH) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_TITLE_LENGTH_INVALID);
        }
        if (request.content() == null || request.content().length() < CONTENT_MIN_LENGTH || request.content().length() > CONTENT_MAX_LENGTH) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_CONTENT_LENGTH_INVALID);
        }

        // 대표 이미지 필수 여부 검증
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_MAIN_IMAGE_REQUIRED);
        }

        // 이미지 개수 제한 (최대 10장)
        int imageCount = (!mainImage.isEmpty() ? 1 : 0)
                + (imageFiles != null ? (int) imageFiles.stream().filter(f -> f != null && !f.isEmpty()).count() : 0);
        if (imageCount > MAX_IMAGE_COUNT) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_COUNT_EXCEEDED);
        }

        // 이미지 파일 크기/타입 제한
        if (!mainImage.isEmpty()) {
            if (mainImage.getSize() > MAX_IMAGE_SIZE) {
                throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_TOO_LARGE);
            }
            String contentType = mainImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_INVALID_TYPE);
            }
        }
        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    if (file.getSize() > MAX_IMAGE_SIZE) {
                        throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_TOO_LARGE);
                    }
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_INVALID_TYPE);
                    }
                }
            }
        }

        Article article = Article.builder()
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
        articleRepository.save(article);

        List<ArticlePhoto> photos = new ArrayList<>();
        List<String> uploadedKeys = new ArrayList<>(); // 업로드된 S3 key 추적
        try {
            // 대표 이미지 S3 업로드 및 UUID 저장
            if (mainImage != null && !mainImage.isEmpty()) {
                String uuid = java.util.UUID.randomUUID().toString();
                String mainImageKey = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), mainImage);
                if (mainImageKey == null || mainImageKey.isBlank()) {
                    throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
                }
                uploadedKeys.add(mainImageKey); // 업로드 성공 시 key 저장
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
            // 추가 이미지 S3 업로드 및 UUID 저장
            if (imageFiles != null) {
                int idx = 1;
                for (MultipartFile file : imageFiles) {
                    if (file != null && !file.isEmpty()) {
                        String uuid = java.util.UUID.randomUUID().toString();
                        String imageKey = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), file);
                        if (imageKey == null || imageKey.isBlank()) {
                            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
                        }
                        uploadedKeys.add(imageKey); // 업로드 성공 시 key 저장
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
            // 업로드된 파일이 있다면 S3에서 삭제
            for (String key : uploadedKeys) {
                try {
                    s3Manager.deleteFile(key);
                } catch (Exception ex) {
                    log.error("S3 롤백(파일 삭제) 실패: {}", key, ex);
                }
            }
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
        }
        return new ArticleWithPhotos(article, photos);
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
