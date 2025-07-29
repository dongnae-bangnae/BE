package DNBN.spring.repository.PlaceRepository;

import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByRegion(Region region);
    Optional<Place> findByLatitudeAndLongitude(Double lat, Double lng);

    @Query("SELECT p FROM Place p WHERE p.region.id IN :regionIds")
    List<Place> findByRegionIds(@Param("regionIds") List<Long> regionIds);

    @Query("SELECT p FROM Place p WHERE p.region.id IN :regionIds")
    List<Place> findByRegionIdIn(@Param("regionIds") List<Long> regionIds);
}

