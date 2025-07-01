package DNBN.spring.converter;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.MemberResponseDTO;

public class MemberConverter {
    public static MemberResponseDTO.MemberInfoDTO toMemberInfoDTO(Member member) {
        return MemberResponseDTO.MemberInfoDTO.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .gender(member.getGender().name())
                .build();
    }
}
