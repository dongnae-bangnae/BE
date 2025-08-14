package DNBN.spring.service.PlaceService;

import DNBN.spring.web.dto.response.PlaceResponseDTO;

public interface PlaceQueryService {
    PlaceResponseDTO.SavedPlaceListDTO getSavedPlaces(Long categoryId, Long memberId, Long cursor, Long limit);
    PlaceResponseDTO.MapPlacesResultDTO getPlacesInMapBounds(
            Long memberId,
            Double latMin, Double latMax,
            Double lngMin, Double lngMax
    );
}
