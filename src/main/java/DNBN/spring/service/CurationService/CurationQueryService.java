package DNBN.spring.service.CurationService;

import DNBN.spring.web.dto.response.CurationResponseDTO;

import java.util.List;

public interface CurationQueryService {
    List<CurationResponseDTO.CurationPreviewDTO> getCurationsByMember(Long memberId);
    CurationResponseDTO.CurationDetailDTO getCuration(Long curationId);
}
