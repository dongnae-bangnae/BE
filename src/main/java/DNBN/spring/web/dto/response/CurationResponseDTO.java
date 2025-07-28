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
    private String title;
    private String placeName;
    private List<PlaceSummary> likePlaces;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceSummary {
        private Long likePlaceId;
        private String name;
        private String pinCategory;
    }
}
