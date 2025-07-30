package DNBN.spring.service.CurationService;

import DNBN.spring.web.dto.response.CurationResponseDTO;
import DNBN.spring.web.dto.response.RegionCurationDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CurationQueryService {
    List<CurationResponseDTO.CurationPreviewDTO> getCurationsByMember(Long memberId);
}
