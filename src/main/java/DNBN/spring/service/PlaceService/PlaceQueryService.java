package DNBN.spring.service.PlaceService;

import DNBN.spring.web.dto.PlaceResponseDTO;

public interface PlaceQueryService {
    PlaceResponseDTO.SavedPlaceListDTO getSavedPlaces(Long categoryId, Long memberId, Long cursor, Long limit);
}
