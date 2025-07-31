package DNBN.spring.repository.SavePlaceRepository;

import DNBN.spring.domain.Category;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.mapping.SavePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavePlaceRepository extends JpaRepository<SavePlace, Long>, SavePlaceRepositoryCustom {
    boolean existsByPlace(Place place);
    boolean existsByPlace_PlaceIdAndCategory_Member_Id(Long placeId, Long memberId);
}
