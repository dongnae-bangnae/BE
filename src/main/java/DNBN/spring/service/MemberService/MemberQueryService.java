package DNBN.spring.service.MemberService;

import DNBN.spring.web.dto.response.MemberResponseDTO;

public interface MemberQueryService {
    MemberResponseDTO.MemberInfoDTO getMemberInfo(Long memberId);
}
