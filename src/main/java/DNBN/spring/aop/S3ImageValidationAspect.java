package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticlePhotoHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * S3 이미지 업로드 관련 공통 검증 Aspect
 * (파일 개수, 크기, 타입 등)
 */
@Aspect
@Component
public class S3ImageValidationAspect {
    @Value("${article.validation.image.max-count}")
    private int maxImageCount;
    @Value("${article.validation.image.max-size}")
    private long maxImageSize;

    @Before("execution(* *(.., org.springframework.web.multipart.MultipartFile, java.util.List, ..)) && @annotation(DNBN.spring.aop.annotation.ValidateS3ImageUpload)")
    public void validateS3ImageUpload(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MultipartFile mainImage = null;
        List<MultipartFile> imageFiles = null;
        for (Object arg : args) {
            if (mainImage == null && arg instanceof MultipartFile file) {
                mainImage = file;
            } else if (imageFiles == null && arg instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof MultipartFile) {
                imageFiles = (List<MultipartFile>) list;
            }
        }
        int imageCount = (mainImage != null && !mainImage.isEmpty() ? 1 : 0)
                + (imageFiles != null ? (int) imageFiles.stream().filter(f -> f != null && !f.isEmpty()).count() : 0);
        if (imageCount > maxImageCount) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_COUNT_EXCEEDED);
        }
        if (mainImage != null && !mainImage.isEmpty()) {
            if (mainImage.getSize() > maxImageSize) {
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
                    if (file.getSize() > maxImageSize) {
                        throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_TOO_LARGE);
                    }
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_INVALID_TYPE);
                    }
                }
            }
        }
    }
}

