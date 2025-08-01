package DNBN.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class CurationResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurationDetailDTO {
        private Long curationId;
        private Long regionId;
        private String regionName;
        private String title;
        private String thumbnailImageUrl;
        private Long likeCount;
        private Long commentCount;
        private LocalDate createdAt;
        private List<Places> likePlaces;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Places {
            private Long likePlaceId;
            private String name;
            private String pinCategory;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurationPreviewDTO {
        private Long curationId;
        private Long regionId;
        private String regionName;
        private String title;
        private String thumbnailImageUrl;
        private Long likeCount;
        private Long commentCount;
        private LocalDate createdAt;
    }
}
