package DNBN.spring.converter;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.ProfileImage;
import DNBN.spring.domain.Region;
import DNBN.spring.web.dto.MemberResponseDTO;
import DNBN.spring.web.dto.RegionResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MemberConverter {

    public static ProfileImage toProfileImage(String imageUrl, Member member) {
        return ProfileImage.builder()
                .imageUrl(imageUrl)
                .member(member)
                .build();
    }

    public static MemberResponseDTO.OnboardingResultDTO toOnboardingResponseDTO(Member member) {
        return MemberResponseDTO.OnboardingResultDTO.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .chosenRegionIds(
                        member.getLikePlaceList().stream()
                                .map(lp -> lp.getRegion().getId())
                                .toList()
                )
                .isOnboardingCompleted(member.isOnboardingCompleted())
                .build();
    }

    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {
        List<RegionResponseDTO.RegionPreviewnDTO> likePlaces = member.getLikePlaceList()
                .stream()
                .map(lp -> {
                    Region region = lp.getRegion();
                    return RegionResponseDTO.RegionPreviewnDTO.builder()
                            .id(region.getId())
                            .name(region.getName())
                            .build();
                })
                .collect(Collectors.toList());

        // 이미지 null일 때도 조회할 수 있도록 -> dnbn 기본 이미지 들어올 경우 이 코드 필요없을지도...
        String profileImageUrl = member.getProfileImage() != null
                ? member.getProfileImage().getImageUrl()
                : null;

        return MemberResponseDTO.MemberInfoDTO.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
//                .profileImage(member.getProfileImage().getImageUrl())
                .profileImage(profileImageUrl)
                .likePlaces(likePlaces)
                .build();
    }
}
