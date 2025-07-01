package DNBN.spring.converter;

import DNBN.spring.domain.Member;
import DNBN.spring.domain.Region;
import DNBN.spring.web.dto.MemberResponseDTO;
import DNBN.spring.web.dto.RegionResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MemberConverter {
    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {
        List<RegionResponseDTO.RegioPreviewnDTO> likePlaces = member.getLikePlaceList()
                .stream()
                .map(lp -> {
                    Region region = lp.getRegion();
                    return RegionResponseDTO.RegioPreviewnDTO.builder()
                            .id(region.getId())
                            .name(region.getName())
                            .build();
                })
                .collect(Collectors.toList());

        return MemberResponseDTO.MemberInfoDTO.builder()
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .likePlaces(likePlaces)
                .build();
    }
}
