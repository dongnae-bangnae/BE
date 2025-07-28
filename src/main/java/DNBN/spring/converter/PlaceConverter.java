package DNBN.spring.converter;

import DNBN.spring.domain.mapping.SavePlace;
import DNBN.spring.web.dto.PlaceResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceConverter {

    public static PlaceResponseDTO.SavedPlacePreviewDTO toSavedPlacePreviewDTO(SavePlace savePlace) {
        return PlaceResponseDTO.SavedPlacePreviewDTO.builder()
                .placeId(savePlace.getPlace().getPlaceId())
                .title(savePlace.getPlace().getTitle())
                .pinCategory(savePlace.getPlace().getPinCategory().name())
                .latitude(savePlace.getPlace().getLatitude())
                .longitude(savePlace.getPlace().getLongitude())
                .build();
    }

    public static List<PlaceResponseDTO.SavedPlacePreviewDTO> toSavedPlacePreviewDTOList(List<SavePlace> savePlaces) {
        return savePlaces.stream()
                .map(PlaceConverter::toSavedPlacePreviewDTO)
                .collect(Collectors.toList());
    }
}
