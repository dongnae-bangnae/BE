package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import DNBN.spring.domain.QRegion;
import DNBN.spring.domain.QPlace;

import java.util.List;
import DNBN.spring.domain.Place;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QRegion region = QRegion.region;
    private final QPlace place = QPlace.place;

    @Override
    public List<Region> searchByKeyword(String keyword, Long cursor, int limit) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                    region.province.containsIgnoreCase(keyword)
                            .or(region.city.containsIgnoreCase(keyword))
                            .or(region.district.containsIgnoreCase(keyword))
            );
        }

        if (cursor != null) {
            builder.and(region.id.gt(cursor));
        }

        return jpaQueryFactory.selectFrom(region)
                .where(builder)
                .orderBy(region.id.asc())
                .limit(limit)
                .fetch();
    }
    
    @Override
    public Region findRegionByCoordinates(Double latitude, Double longitude) {
        // 방법 1: 범위 기반 검색 후 가장 가까운 지역 찾기 (빠름)
        // 위도/경도를 소수점 6자리로 반올림하여 근사치 매칭
        // 0.001도(약 111m) 단위로 구분
        Double latRounded = Math.round(latitude * 1000000.0) / 1000000.0;
        Double lngRounded = Math.round(longitude * 1000000.0) / 1000000.0;
        
        // 위도/경도 범위 설정 (약 500m 반경)
        Double latRange = 0.005; // 약 555m
        Double lngRange = 0.005; // 약 555m
        
        // 위도에 따른 경도 범위 조정 (지구 곡률 고려)
        Double adjustedLngRange = lngRange / Math.cos(Math.toRadians(latRounded));
        
        // 범위 내의 Place들을 찾아서 가장 가까운 Region 반환
        // 유클리드 거리의 제곱으로 정렬 (실제 거리와 비례)
        return jpaQueryFactory
                .select(place.region)
                .from(place)
                .where(place.latitude.between(latRounded - latRange, latRounded + latRange)
                        .and(place.longitude.between(lngRounded - adjustedLngRange, lngRounded + adjustedLngRange)))
                .orderBy(place.latitude.subtract(latRounded).multiply(place.latitude.subtract(latRounded))
                        .add(place.longitude.subtract(lngRounded).multiply(place.longitude.subtract(lngRounded))).asc())
                .fetchFirst();
    }
    
    @Override
    public Region findRegionByCoordinatesAccurate(Double latitude, Double longitude) {
        // 방법 2: 정확한 거리 계산을 사용한 지역 찾기 (정확함, 상대적으로 느림)
        // 먼저 넓은 범위에서 후보들을 찾고, 정확한 거리로 정렬
        
        // 넓은 범위 설정 (약 2km 반경)
        Double latRange = 0.02; // 약 2.2km
        Double lngRange = 0.02; // 약 2.2km
        
        // 위도에 따른 경도 범위 조정
        Double adjustedLngRange = lngRange / Math.cos(Math.toRadians(latitude));
        
        // 범위 내의 모든 Place들을 가져와 거리 계산
        List<Place> candidates = jpaQueryFactory
                .selectFrom(place)
                .where(place.latitude.between(latitude - latRange, latitude + latRange)
                        .and(place.longitude.between(longitude - adjustedLngRange, longitude + adjustedLngRange)))
                .fetch();
        
        if (candidates.isEmpty()) {
            // 범위 내에 Place가 없으면 전체에서 가장 가까운 것 찾기
            return jpaQueryFactory
                    .selectFrom(place)
                    .orderBy(place.latitude.subtract(latitude).multiply(place.latitude.subtract(latitude))
                            .add(place.longitude.subtract(longitude).multiply(place.longitude.subtract(longitude))).asc())
                    .select(place.region)
                    .fetchFirst();
        }
        
        // 거리 계산으로 가장 가까운 Place 찾기
        Place nearestPlace = candidates.stream()
                .min((p1, p2) -> {
                    double dist1 = calculateDistance(latitude, longitude, p1.getLatitude(), p1.getLongitude());
                    double dist2 = calculateDistance(latitude, longitude, p2.getLatitude(), p2.getLongitude());
                    return Double.compare(dist1, dist2);
                })
                .orElse(candidates.get(0));
        
        return nearestPlace.getRegion();
    }
    
    /**
     * Haversine 공식을 사용한 거리 계산
     * 거리 단위: 미터
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // 지구 반지름 (미터)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
