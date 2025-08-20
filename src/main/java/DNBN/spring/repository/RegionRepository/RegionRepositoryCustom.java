package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;

import java.util.List;

public interface RegionRepositoryCustom {
    List<Region> searchByKeyword(String keyword, Long cursor, int limit);
    
    /**
     * 위도/경도로 가장 가까운 지역을 찾습니다.
     */
    Region findRegionByCoordinates(Double latitude, Double longitude);
}
