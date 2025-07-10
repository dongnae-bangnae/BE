package DNBN.spring.web.dto;

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
        List<Long> chosenRegionIds; // regionId 리스트로 받음
    }
}
