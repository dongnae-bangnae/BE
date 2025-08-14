package DNBN.spring.aop;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.ArticlePhotoHandler;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class S3ImageValidationAspectTest {
    private S3ImageValidationAspect aspect;
    private final int maxImageCount = 3;
    private final long maxImageSize = 1024 * 1024; // 1MB

    @BeforeEach
    void setUp() {
        aspect = new S3ImageValidationAspect();
        ReflectionTestUtils.setField(aspect, "maxImageCount", maxImageCount);
        ReflectionTestUtils.setField(aspect, "maxImageSize", maxImageSize);
    }

    private MultipartFile mockImage(long size, String contentType, boolean empty) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(empty);
        when(file.getSize()).thenReturn(size);
        when(file.getContentType()).thenReturn(contentType);
        return file;
    }

    @Test
    @DisplayName("정상 이미지 파일 1개 업로드시 예외 없음")
    void validSingleImage() {
        MultipartFile file = mockImage(500_000, "image/png", false);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{file});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이미지 개수 초과시 예외 발생")
    void imageCountExceeded() {
        MultipartFile file1 = mockImage(100_000, "image/png", false);
        MultipartFile file2 = mockImage(100_000, "image/png", false);
        MultipartFile file3 = mockImage(100_000, "image/png", false);
        MultipartFile file4 = mockImage(100_000, "image/png", false);
        List<MultipartFile> files = Arrays.asList(file1, file2, file3, file4);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{files});
        assertThatThrownBy(() -> aspect.validateS3ImageUpload(joinPoint))
                .isInstanceOf(ArticlePhotoHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_PHOTO_IMAGE_COUNT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("이미지 크기 초과시 예외 발생")
    void imageSizeExceeded() {
        MultipartFile file = mockImage(maxImageSize + 1, "image/png", false);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{file});
        assertThatThrownBy(() -> aspect.validateS3ImageUpload(joinPoint))
                .isInstanceOf(ArticlePhotoHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_PHOTO_IMAGE_TOO_LARGE.getMessage());
    }

    @Test
    @DisplayName("이미지 타입이 아닌 경우 예외 발생")
    void invalidImageType() {
        MultipartFile file = mockImage(100_000, "application/pdf", false);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{file});
        assertThatThrownBy(() -> aspect.validateS3ImageUpload(joinPoint))
                .isInstanceOf(ArticlePhotoHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_PHOTO_IMAGE_INVALID_TYPE.getMessage());
    }

    @Test
    @DisplayName("MultipartFile[] 타입도 정상 동작")
    void multipartFileArray() {
        MultipartFile file1 = mockImage(100_000, "image/png", false);
        MultipartFile file2 = mockImage(100_000, "image/png", false);
        MultipartFile[] arr = new MultipartFile[]{file1, file2};
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{arr});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("빈 파일/빈 리스트는 무시")
    void emptyFilesIgnored() {
        MultipartFile file = mockImage(100_000, "image/png", true);
        List<MultipartFile> files = Collections.singletonList(file);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{files});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("contentType이 null인 경우 예외 발생")
    void nullContentType() {
        MultipartFile file = mockImage(100_000, null, false);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{file});
        assertThatThrownBy(() -> aspect.validateS3ImageUpload(joinPoint))
                .isInstanceOf(ArticlePhotoHandler.class)
                .hasMessageContaining(ErrorStatus.ARTICLE_PHOTO_IMAGE_INVALID_TYPE.getMessage());
    }

    @Test
    @DisplayName("List<MultipartFile>에 null 요소가 포함된 경우 NPE 없이 동작")
    void listWithNullElement() {
        MultipartFile file1 = mockImage(100_000, "image/png", false);
        List<MultipartFile> files = Arrays.asList(file1, null);
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{files});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("MultipartFile[]에 null 요소가 포함된 경우 NPE 없이 동작")
    void arrayWithNullElement() {
        MultipartFile file1 = mockImage(100_000, "image/png", false);
        MultipartFile[] arr = new MultipartFile[]{file1, null};
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{arr});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("모든 파일이 empty인 경우 예외 없음")
    void allFilesEmpty() {
        MultipartFile file1 = mockImage(100_000, "image/png", true);
        MultipartFile file2 = mockImage(100_000, "image/png", true);
        MultipartFile[] arr = new MultipartFile[]{file1, file2};
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{arr});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("지원하지 않는 파라미터 타입이 들어온 경우 무시")
    void unsupportedParameterType() {
        String notAFile = "not a file";
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{notAFile});
        assertThatCode(() -> aspect.validateS3ImageUpload(joinPoint)).doesNotThrowAnyException();
    }
}
