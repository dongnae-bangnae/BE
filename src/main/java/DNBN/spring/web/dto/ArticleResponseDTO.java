package DNBN.spring.web.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleResponseDTO {
    // TODO: inner Class로 변경
    private Long articleId;
    private Long memberId;
    private Long categoryId;
    private Long placeId;
    private Long regionId;
    private String title;
    private LocalDate date;
    private String content;
    private String mainImageUuid;
    private List<String> imageUuids;
    private Long likeCount;
    private Long spamCount;
    private String createdAt;
    private String updatedAt;

    @Builder
    @Getter
    public static class ArticlePreviewDTO {
        private Long articleId;
        private String pinCategory;
        private String imageUrl;
        private String title;
        private Long likes;
        private Long spam;
        private Long comments;
    }

    @Builder
    @Getter
    public static class ArticleListDTO {
        private List<ArticlePreviewDTO> articles;
        private Long cursor;
        private Long limit;
        private boolean hasNext;
    }

}

