package DNBN.spring.service.CurationService;

import DNBN.spring.domain.Member;
import DNBN.spring.web.dto.response.CurationResponseDTO;

public interface CurationCommandService {
    CurationResponseDTO generateCuration(Member member);
}
