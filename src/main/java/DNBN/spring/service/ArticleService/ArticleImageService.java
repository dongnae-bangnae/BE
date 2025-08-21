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
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleImageService {
    private final AmazonS3Manager s3Manager;
    private final ArticlePhotoRepository articlePhotoRepository;

    public List<ArticlePhoto> uploadAndSaveImages(Article article, Place place, Region region, MultipartFile mainImage, List<MultipartFile> imageFiles) {
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_MAIN_IMAGE_REQUIRED);
        }
        
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
        } catch (ArticlePhotoHandler e) {
            log.error("S3 업로드 실패 - ArticlePhotoHandler", e);
            rollbackUploadedFiles(uploadedKeys);
            throw e;
        } catch (Exception e) {
            log.error("S3 업로드 실패 - 예상치 못한 오류", e);
            rollbackUploadedFiles(uploadedKeys);
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_S3_UPLOAD_FAILED);
        }
        return photos;
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

