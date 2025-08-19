package DNBN.spring.service.MemberService;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.request.MemberRequestDTO;
import DNBN.spring.web.dto.response.MemberResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberCommandService {
    Member onboardingMember(Long memberId, MemberRequestDTO.OnboardingDTO request, MultipartFile profileImage);
    void logout(HttpServletResponse response, Long memberId);
    void deleteMember(Long memberId);
    MemberResponseDTO.NicknameUpdateResultDTO updateMemberNickname(Long memberId, String newNickname);
    MemberResponseDTO.ChosenRegionsDTO updateRegions(Long memberId, List<Long> regionIds);
    MemberResponseDTO.ProfileImageUpdateResultDTO updateProfileImage(Long memberId, MultipartFile profileImage);
}
