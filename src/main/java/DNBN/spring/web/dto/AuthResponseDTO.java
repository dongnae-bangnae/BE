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
    public class LoginResultDTO {
        private String accessToken;
        private String refreshToken;
        private Long memberId;
        private Boolean isOnboardingCompleted;
    }
}
