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

    @Before("@annotation(DNBN.spring.aop.annotation.ValidateS3ImageUpload)")
    public void validateS3ImageUpload(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        int imageCount = 0;
        // 파라미터 검증 및 다양한 MultipartFile 타입 지원
        for (Object arg : args) {
            if (arg instanceof MultipartFile file) {
                if (!file.isEmpty()) {
                    imageCount++;
                    validateFile(file);
                }
            } else if (arg instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof MultipartFile) {
                for (Object o : list) {
                    MultipartFile file = (MultipartFile) o;
                    if (file != null && !file.isEmpty()) {
                        imageCount++;
                        validateFile(file);
                    }
                }
            } else if (arg instanceof MultipartFile[] arr) {
                for (MultipartFile file : arr) {
                    if (file != null && !file.isEmpty()) {
                        imageCount++;
                        validateFile(file);
                    }
                }
            }
        }
        if (imageCount > maxImageCount) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_COUNT_EXCEEDED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > maxImageSize) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_TOO_LARGE);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_INVALID_TYPE);
        }
    }
}
