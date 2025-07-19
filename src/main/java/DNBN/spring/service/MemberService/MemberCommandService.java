package DNBN.spring.service.MemberService;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.MemberRequestDTO;
import DNBN.spring.web.dto.MemberResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberCommandService {
    Member onboardingMember(Long memberId, MemberRequestDTO.OnboardingDTO request, MultipartFile profileImage);
    void logout(Long memberId);
    void deleteMember(Long memberId);
    void changeMemberNickname(Long memberId, String newNickname);
    MemberResponseDTO.ChosenRegionsDTO updateRegions(Long memberId, List<Long> regionIds);
}
