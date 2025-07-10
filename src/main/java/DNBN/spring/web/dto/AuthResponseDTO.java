package DNBN.spring.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class AuthResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDTO { // 소셜 로그인
        private String accessToken;
        private String refreshToken;
        private Long memberId;
        private Boolean isOnboardingCompleted;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReissueTokenResponseDTO {
        private String accessToken;
    }
}
