package DNBN.spring.service.PlaceService;

import DNBN.spring.web.dto.PlaceRequestDTO;
import DNBN.spring.web.dto.PlaceResponseDTO;

public interface PlaceCommandService {
    PlaceResponseDTO.SavePlaceResultDTO savePlaceToCategory(Long memberId, Long placeId, PlaceRequestDTO.SavePlaceDTO request);
}
