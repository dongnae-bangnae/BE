package DNBN.spring.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class MemberInfoDTO {
        Long memberId;
        String nickname;
        String profileImage;
        List<RegionDTO> likePlaces;
    }
}
