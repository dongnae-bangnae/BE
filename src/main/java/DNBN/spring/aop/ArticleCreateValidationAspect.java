package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticleHandler;
import DNBN.spring.apiPayload.exception.handler.ArticlePhotoHandler;
import DNBN.spring.web.dto.ArticleRequestDTO;
import DNBN.spring.web.dto.ArticleWithLocationRequestDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Aspect
@Component
public class ArticleCreateValidationAspect {
    @Value("${article.validation.title.min-length}")
    private int titleMinLength;
    @Value("${article.validation.title.max-length}")
    private int titleMaxLength;
    @Value("${article.validation.content.min-length}")
    private int contentMinLength;
    @Value("${article.validation.content.max-length}")
    private int contentMaxLength;
    @Value("${article.validation.image.max-count}")
    private int maxImageCount;
    @Value("${article.validation.image.max-size}")
    private long maxImageSize;

    @Before("execution(* DNBN.spring.service.ArticleService.ArticleCommandServiceImpl.createArticle(..))")
    public void validateCreateArticle(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object dto = args[1];
        MultipartFile mainImage = (MultipartFile) args[2];
        List<MultipartFile> imageFiles = (List<MultipartFile>) args[3];

        String title = null;
        String content = null;
        if (dto instanceof ArticleRequestDTO request) {
            title = request.title();
            content = request.content();
        } else if (dto instanceof ArticleWithLocationRequestDTO request) {
            title = request.title();
            content = request.content();
        }
        if (title == null || title.length() < titleMinLength || title.length() > titleMaxLength) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_TITLE_LENGTH_INVALID);
        }
        if (content == null || content.length() < contentMinLength || content.length() > contentMaxLength) {
            throw new ArticleHandler(ErrorStatus.ARTICLE_CONTENT_LENGTH_INVALID);
        }
        if (mainImage == null || mainImage.isEmpty()) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_MAIN_IMAGE_REQUIRED);
        }
        int imageCount = (!mainImage.isEmpty() ? 1 : 0)
                + (imageFiles != null ? (int) imageFiles.stream().filter(f -> f != null && !f.isEmpty()).count() : 0);
        if (imageCount > maxImageCount) {
            throw new ArticlePhotoHandler(ErrorStatus.ARTICLE_PHOTO_IMAGE_COUNT_EXCEEDED);
        }
        if (!mainImage.isEmpty()) {
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
