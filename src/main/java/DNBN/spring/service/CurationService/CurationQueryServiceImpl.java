package DNBN.spring.service.CurationService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CurationHandler;
import DNBN.spring.converter.CurationConverter;
import DNBN.spring.domain.Curation;
import DNBN.spring.repository.CurationRepository.CurationRepository;
import DNBN.spring.web.dto.response.CurationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurationQueryServiceImpl implements CurationQueryService {
    private final CurationRepository curationRepository;

    @Override
    public List<CurationResponseDTO.CurationPreviewDTO> getCurationsByMember(Long memberId) {
        List<Curation> curations = curationRepository.findAllByOrderByCreatedAtDesc();

        if (curations.isEmpty()) {
            throw new CurationHandler(ErrorStatus.CURATION_NOT_FOUND);
        }

        return curations.stream()
                .map(CurationConverter::toCurationPreviewDTO)
                .toList();
    }

    @Override
    public CurationResponseDTO.CurationDetailDTO getCuration(Long curationId){
        Curation curation = curationRepository.findById(curationId)
                .orElseThrow(() -> new CurationHandler(ErrorStatus.CURATION_NOT_FOUND));
        return CurationConverter.toCurationDetailDTO(curation);
    }
}
