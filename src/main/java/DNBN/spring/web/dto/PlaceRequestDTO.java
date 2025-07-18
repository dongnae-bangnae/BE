package DNBN.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
}
