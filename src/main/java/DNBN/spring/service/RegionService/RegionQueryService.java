package DNBN.spring.service.RegionService;

import DNBN.spring.web.dto.response.RegionResponseDTO;

public interface RegionQueryService {
    RegionResponseDTO.SearchRegionResult searchRegion(String keyword, Long cursor, int limit);
}
