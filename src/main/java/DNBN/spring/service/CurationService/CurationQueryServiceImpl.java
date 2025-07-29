package DNBN.spring.service.CurationService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CurationHandler;
import DNBN.spring.converter.CurationConverter;
import DNBN.spring.domain.Curation;
import DNBN.spring.domain.Region;
import DNBN.spring.domain.mapping.LikeRegion;
import DNBN.spring.repository.CurationRepository.CurationRepository;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.response.CurationResponseDTO;
import DNBN.spring.web.dto.response.RegionCurationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurationQueryServiceImpl implements CurationQueryService {
    private final CurationRepository curationRepository;

    @Override
    public List<CurationResponseDTO> getCurationsByMember(Long memberId) {
        List<Curation> curations = curationRepository.findByMemberId(memberId);

        if (curations.isEmpty()) {
            throw new CurationHandler(ErrorStatus.CURATION_NOT_FOUND);
        }

        return curations.stream()
                .map(CurationConverter::toCurationResponseDTO)
                .toList();
    }
}
