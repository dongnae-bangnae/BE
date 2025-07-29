package DNBN.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurationResponseDTO {
    private Long curationId;
    private Long memberId;
    private Long regionId;
    private Long regionName;
    private String title;
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
