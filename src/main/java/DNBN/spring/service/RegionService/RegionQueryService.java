package DNBN.spring.service.RegionService;

import DNBN.spring.web.dto.RegionResponseDTO;

public interface RegionQueryService {
    RegionResponseDTO.SearchRegionResult searchRegion(String keyword, Long cursor, int limit);
}
