package DNBN.spring.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PlaceResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavePlaceResultDTO {
        private Long placeId;
        private Long categoryId;
    }

    @Getter
    @Builder
    public static class SavedPlacePreviewDTO {
        private Long placeId;
        private String title;
        private String pinCategory;
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @Builder
    public static class SavedPlaceListDTO {
        private List<SavedPlacePreviewDTO> places;
        private Long cursor;
        private Long limit;
        private boolean hasNext;
    }

    @Builder
    @Getter
    public static class MapPlaceDTO {
        private Long placeId;
        private Long regionId;
        private String title;
        private Double latitude;
        private Double longitude;
        private String pinCategory;
        private String address;
        @JsonProperty("isSaved")
        private boolean saved;
    }

    @Builder
    @Getter
    public static class MapPlacesResultDTO {
        private List<MapPlaceDTO> places;
    }
}
