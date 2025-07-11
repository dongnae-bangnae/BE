package DNBN.spring.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MemberRequestDTO {
    @Getter
    @Setter
    public static class OnboardingDTO {
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname;

//        String profileImage;

        @NotEmpty(message = "좋아하는 동네는 최소 1개 이상 선택해야 합니다.")
        @Schema(description = "좋아하는 동네 ID 리스트", example = "[1, 2]")
        List<Long> chosenRegionIds; // regionId 리스트로 받음
    }
}
