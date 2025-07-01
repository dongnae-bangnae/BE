package DNBN.spring.web.dto;

import DNBN.spring.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MemberResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnboardingResultDTO {
        private Long memberId;
        private String nickname;
        private List<Long> chosenRegionIds;
        private Boolean isOnboardingCompleted;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfoDTO {
        Long memberId;
        String nickname;
        String profileImage;
        List<RegionResponseDTO.RegioPreviewnDTO> likePlaces;
    }
}
