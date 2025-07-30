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
    private String placeName;
    private String pinCategory;
    private String detailAddress;
    private String title;
    private String date;
    private String content;
    private String mainImageUuid;
    private List<String> imageUuids;
    private Long likeCount;
    private Long spamCount;
    private String createdAt;
    private String updatedAt;

    public ArticleResponseDTO(Long articleId, Long memberId, Long categoryId, Long placeId, Long regionId, String placeName, String pinCategory, String detailAddress, String title, String date, String content, String mainImageUuid, List<String> imageUuids, Long likeCount, Long spamCount, String createdAt, String updatedAt) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.categoryId = categoryId;
        this.placeId = placeId;
        this.regionId = regionId;
        this.placeName = placeName;
        this.pinCategory = pinCategory;
        this.detailAddress = detailAddress;
        this.title = title;
        this.date = date;
        this.content = content;
        this.mainImageUuid = mainImageUuid;
        // 방어적 복사
        this.imageUuids = imageUuids == null ? null : List.copyOf(imageUuids);
        this.likeCount = likeCount;
        this.spamCount = spamCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

        public ArticlePreviewDTO(Long articleId, String pinCategory, String imageUrl, String title, Long likes, Long spam, Long comments) {
            this.articleId = articleId;
            this.pinCategory = pinCategory;
            this.imageUrl = imageUrl;
            this.title = title;
            this.likes = likes;
            this.spam = spam;
            this.comments = comments;
        }
    }

    @Builder
    @Getter
    public static class ArticleListDTO {
        private List<ArticlePreviewDTO> articles;
        private Long cursor;
        private Long limit;
        private boolean hasNext;

        public ArticleListDTO(List<ArticlePreviewDTO> articles, Long cursor, Long limit, boolean hasNext) {
            // 방어적 복사
            this.articles = articles == null ? null : List.copyOf(articles);
            this.cursor = cursor;
            this.limit = limit;
            this.hasNext = hasNext;
        }
    }
}
