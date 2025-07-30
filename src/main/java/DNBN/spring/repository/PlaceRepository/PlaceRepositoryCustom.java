package DNBN.spring.repository.PlaceRepository;

import DNBN.spring.domain.Place;
import java.util.List;

public interface PlaceRepositoryCustom {
    List<Place> findAllInBounds(Double latMin, Double latMax, Double lngMin, Double lngMax);
}