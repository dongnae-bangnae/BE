package DNBN.spring.repository.PlaceRepository;

import DNBN.spring.domain.Place;
import DNBN.spring.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByRegion(Region region);
    Optional<Place> findByLatitudeAndLongitude(Double lat, Double lng);
}

