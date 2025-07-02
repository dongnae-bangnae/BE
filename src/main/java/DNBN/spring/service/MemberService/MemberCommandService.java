package DNBN.spring.service.MemberService;

import DNBN.spring.web.dto.MemberRequestDTO;
import DNBN.spring.web.dto.MemberResponseDTO;

public interface MemberCommandService {
    MemberResponseDTO.OnboardingResultDTO onboardingMember(Long memberId, MemberRequestDTO.OnboardingDTO request);
    void logout(Long memberId);
}
