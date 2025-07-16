package DNBN.spring.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        @Size(min = 1, max = 20, message = "카테고리 이름은 1자 이상 20자 이하여야 합니다.")
        String name,

        @NotBlank(message = "색상은 필수입니다.")
        String color,
        Long placeId,
        Long regionId

) { }