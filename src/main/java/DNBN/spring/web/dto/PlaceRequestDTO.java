package DNBN.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class PlaceRequestDTO {
    @Getter
    @Setter
    public static class SavePlaceDTO {
        @NotNull(message = "카테고리 ID는 필수입니다.")
        @Schema(description = "카테고리 ID", example = "1")
        private Long categoryId;
    }

    @Getter @Setter
    public static class MapSearchDTO {
        @NotNull(message = "latMin은 필수입니다.")
        @Digits(integer = 3, fraction = 5, message = "소수점 5자리까지 입력")
        @Schema(description = "지도 화면의 최소 위도")
        private Double latMin;

        @NotNull(message = "latMax는 필수입니다.")
        @Digits(integer = 3, fraction = 5, message = "소수점 5자리까지 입력")
        @Schema(description = "지도 화면의 최대 위도")
        private Double latMax;

        @NotNull(message = "lngMin은 필수입니다.")
        @Digits(integer = 3, fraction = 5, message = "소수점 5자리까지 입력")
        @Schema(description = "지도 화면의 최소 경도")
        private Double lngMin;

        @NotNull(message = "lngMax는 필수입니다.")
        @Digits(integer = 3, fraction = 5, message = "소수점 5자리까지 입력")
        @Schema(description = "지도 화면의 최대 경도")
        private Double lngMax;
    }
}
