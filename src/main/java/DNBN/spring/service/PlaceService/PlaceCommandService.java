package DNBN.spring.service.PlaceService;

import DNBN.spring.web.dto.request.PlaceRequestDTO;
import DNBN.spring.web.dto.response.PlaceResponseDTO;

public interface PlaceCommandService {
    PlaceResponseDTO.SavePlaceResultDTO savePlaceToCategory(Long memberId, Long placeId, PlaceRequestDTO.SavePlaceDTO request);
}
