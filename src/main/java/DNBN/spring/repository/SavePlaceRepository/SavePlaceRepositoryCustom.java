package DNBN.spring.repository.SavePlaceRepository;

import DNBN.spring.domain.mapping.SavePlace;

import java.util.List;

public interface SavePlaceRepositoryCustom {
    List<SavePlace> findSavedPlaces(Long categoryId, Long cursor, Long limit);
}
