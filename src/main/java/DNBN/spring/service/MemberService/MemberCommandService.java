package DNBN.spring.service.MemberService;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.MemberRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MemberCommandService {
    Member onboardingMember(Long memberId, MemberRequestDTO.OnboardingDTO request, MultipartFile profileImage);
    void logout(Long memberId);
    void deleteMember(Long memberId);
}
