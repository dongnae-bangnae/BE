package DNBN.spring.service.MemberService;

import DNBN.spring.web.dto.MemberResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public class MemberQueryService {
    MemberResponseDTO.MemberInfoDTO getMemberInfo(HttpServletRequest request);
}
