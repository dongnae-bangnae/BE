package DNBN.spring.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RegionResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionPreviewnDTO {
        Long id;
        String name;
    }

    @Getter
    @Builder
    public static class RegionInfoDTO {
        private Long regionId;
        private String province;
        private String city;
        private String district;
    }

    @Getter
    @Builder
    public static class RegionFullNameDTO {
        private Long regionId;
        private String province;
        private String city;
        private String district;
    }

    @Getter
    @Builder
    public static class SearchRegionResult {
        private List<RegionFullNameDTO> regions;
        private Long cursor;
        private Long limit;
        private boolean hasNext;
    }
}
