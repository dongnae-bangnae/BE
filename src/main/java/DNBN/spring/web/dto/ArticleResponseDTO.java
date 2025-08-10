package DNBN.spring.web.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleResponseDTO {
    private final Long articleId;
    private final Long memberId;
    private final Long categoryId;
    private final Long placeId;
    private final Long regionId;
    private final String placeName;
    private final String pinCategory;
    private final String detailAddress;
    private final String title;
    private final String date;
    private final String content;
    private final String mainImageUuid;
    private final List<String> imageUuids;
    private final Long likeCount;
    private final Long spamCount;
    private final String createdAt;
    private final String updatedAt;

    @Builder
    private ArticleResponseDTO(Long articleId, Long memberId, Long categoryId, Long placeId, Long regionId, String placeName, String pinCategory, String detailAddress, String title, String date, String content, String mainImageUuid, List<String> imageUuids, Long likeCount, Long spamCount, String createdAt, String updatedAt) {
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
        this.imageUuids = imageUuids == null ? null : List.copyOf(imageUuids);
        this.likeCount = likeCount;
        this.spamCount = spamCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Builder
    @Getter
    public static class ArticlePreviewDTO {
        private final Long articleId;
        private final String pinCategory;
        private final String imageUrl;
        private final String title;
        private final Long likes;
        private final Long spam;
        private final Long comments;

        private ArticlePreviewDTO(Long articleId, String pinCategory, String imageUrl, String title, Long likes, Long spam, Long comments) {
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
        private final List<ArticlePreviewDTO> articles;
        private final Long cursor;
        private final Long limit;
        private final boolean hasNext;

        private ArticleListDTO(List<ArticlePreviewDTO> articles, Long cursor, Long limit, boolean hasNext) {
            this.articles = articles == null ? null : List.copyOf(articles);
            this.cursor = cursor;
            this.limit = limit;
            this.hasNext = hasNext;
        }
    }

    @Builder
    @Getter
    public static class ArticleDetailDTO {
        private final Long articleId;
        private final Long memberId;
        private final Long categoryId;
        private final Long placeId;
        private final Long regionId;
        private final String placeName;
        private final String pinCategory;
        private final String detailAddress;
        private final String title;
        private final String date;
        private final String content;
        private final String mainImageUuid;
        private final List<String> imageUuids;
        private final Long likeCount;
        private final Long spamCount;
        private final String createdAt;
        private final String updatedAt;

        private ArticleDetailDTO(Long articleId, Long memberId, Long categoryId, Long placeId, Long regionId, String placeName, String pinCategory, String detailAddress, String title, String date, String content, String mainImageUuid, List<String> imageUuids, Long likeCount, Long spamCount, String createdAt, String updatedAt) {
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
            this.imageUuids = imageUuids == null ? null : List.copyOf(imageUuids);
            this.likeCount = likeCount;
            this.spamCount = spamCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }

    @Builder
    @Getter
    public static class ArticleListItemDTO {
        private final Long memberId;
        private final Long articleId;
        private final Long regionId;
        private final Long placeId;
        private final String nickname;
        private final String title;
        private final String pinCategory;
        private final String mainImageUuid;
        private final Long likeCount;
        private final Long spamCount;
        private final Long commentCount;
        private final Boolean isLiked;
        private final Boolean isSpammed;
        private final Boolean isMine;
        private final String createdAt;
        private final String updatedAt;

        private ArticleListItemDTO(Long memberId, Long articleId, Long regionId, Long placeId, String nickname, String title, String pinCategory, String mainImageUuid, Long likeCount, Long spamCount, Long commentCount, Boolean isLiked, Boolean isSpammed, Boolean isMine, String createdAt, String updatedAt) {
            this.memberId = memberId;
            this.articleId = articleId;
            this.regionId = regionId;
            this.placeId = placeId;
            this.nickname = nickname;
            this.title = title;
            this.pinCategory = pinCategory;
            this.mainImageUuid = mainImageUuid;
            this.likeCount = likeCount;
            this.spamCount = spamCount;
            this.commentCount = commentCount;
            this.isLiked = isLiked;
            this.isSpammed = isSpammed;
            this.isMine = isMine;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}
