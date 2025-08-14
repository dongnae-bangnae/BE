package DNBN.spring.service.MemberService;

import DNBN.spring.web.dto.MemberResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface MemberQueryService {
    MemberResponseDTO.MemberInfoDTO getMemberInfo(Long memberId);
}
