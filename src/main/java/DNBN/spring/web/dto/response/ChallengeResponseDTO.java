package DNBN.spring.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChallengeResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeDetailDTO {
        private Long challengeId;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}
