package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;

import java.util.List;

public interface RegionRepositoryCustom {
    List<Region> searchByKeyword(String keyword, Long cursor, int limit);
    
    /**
     * 위도/경도로 가장 가까운 지역 찾기
     * 범위 기반 근사치 검색을 사용
     */
    Region findRegionByCoordinates(Double latitude, Double longitude);
    
    /**
     * 위도/경도로 가장 가까운 지역 찾기 (정확도 향상, 성능 저하)
     * Haversine 공식을 사용하여 정확한 거리를 계산
     */
    Region findRegionByCoordinatesAccurate(Double latitude, Double longitude);
}
