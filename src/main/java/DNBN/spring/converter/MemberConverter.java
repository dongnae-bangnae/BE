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
                .memberId(member.getId())
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

        return MemberResponseDTO.MemberInfoDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .likePlaces(likePlaces)
                .build();
    }
}
