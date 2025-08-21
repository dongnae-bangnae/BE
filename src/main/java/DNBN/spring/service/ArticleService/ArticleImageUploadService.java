package DNBN.spring.service.ArticleService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticlePhotoHandler;
import DNBN.spring.aws.s3.AmazonS3Manager;
import DNBN.spring.domain.Article;
import DNBN.spring.domain.ArticlePhoto;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
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
public class ArticleImageUploadService {
    private final AmazonS3Manager s3Manager;
    private final ArticlePhotoRepository articlePhotoRepository;

    /**
     * 이미지 업로드만 수행하고 ArticlePhoto 객체들을 반환합니다.
     * Article이 생성되기 전에 호출되어야 합니다.
     */
    public List<ArticlePhoto> uploadImages(Place place, Region region, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        // 메인 이미지 필수 검증
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_MAIN_IMAGE_REQUIRED);
        }
        
        List<ArticlePhoto> photos = new ArrayList<>();
        List<String> uploadedKeys = new ArrayList<>();
        
        try {
            // 메인 이미지 업로드 (필수)
            String mainImageKey = uploadSingleImage(mainImage);
            uploadedKeys.add(mainImageKey);
            
            ArticlePhoto mainPhoto = ArticlePhoto.builder()
                    .place(place)
                    .region(region)
                    .fileKey(mainImageKey)
                    .orderIndex(0)
                    .isMain(true)
                    .build();
            photos.add(mainPhoto);
            
            // 일반 이미지 업로드 (선택)
            if (imageFiles != null) {
                int idx = 1;
                for (MultipartFile file : imageFiles) {
                    if (file != null && !file.isEmpty()) {
                        String imageKey = uploadSingleImage(file);
                        uploadedKeys.add(imageKey);
                        
                        ArticlePhoto photo = ArticlePhoto.builder()
                                .place(place)
                                .region(region)
                                .fileKey(imageKey)
                                .orderIndex(idx++)
                                .isMain(false)
                                .build();
                        photos.add(photo);
                    }
                }
            }
            
            return photos;
        } catch (Exception e) {
            log.error("S3 업로드 실패", e);
            rollbackUploadedFiles(uploadedKeys);
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
        }
    }

    /**
     * 업로드된 이미지들을 Article과 연결하여 DB에 저장합니다.
     */
    public List<ArticlePhoto> saveImagesWithArticle(ArticlePhoto photo, Article article) {
        photo.setArticle(article);
        return List.of(articlePhotoRepository.save(photo));
    }

    public List<ArticlePhoto> saveImagesWithArticle(List<ArticlePhoto> photos, Article article) {
        photos.forEach(photo -> photo.setArticle(article));
        return articlePhotoRepository.saveAll(photos);
    }

    private String uploadSingleImage(MultipartFile file) {
        String uuid = java.util.UUID.randomUUID().toString();
        String key = s3Manager.uploadFile(s3Manager.generateArticlePhotoKeyName(uuid), file);
        
        if (key == null || key.isBlank()) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
        }
        
        return key;
    }

    private void rollbackUploadedFiles(List<String> uploadedKeys) {
        for (String key : uploadedKeys) {
            try {
                s3Manager.deleteFile(key);
            } catch (Exception ex) {
                log.error("S3 롤백(파일 삭제) 실패: {}", key, ex);
            }
        }
    }
}
