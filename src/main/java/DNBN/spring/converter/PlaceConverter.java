package DNBN.spring.converter;

import DNBN.spring.domain.Place;
import DNBN.spring.domain.mapping.SavePlace;
import DNBN.spring.repository.SavePlaceRepository.SavePlaceRepository;
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

    public static PlaceResponseDTO.MapPlacesResultDTO toMapPlacesResult(
            List<Place> places,
            Long memberId,
            SavePlaceRepository savePlaceRepository
    ) {
        List<PlaceResponseDTO.MapPlaceDTO> list = places.stream()
                .map(p -> {
                    boolean saved = savePlaceRepository
                            .existsByPlace_PlaceIdAndCategory_Member_Id(p.getPlaceId(), memberId);
                    return PlaceResponseDTO.MapPlaceDTO.builder()
                            .placeId(p.getPlaceId())
                            .regionId(p.getRegion().getId())
                            .title(p.getTitle())
                            .latitude(p.getLatitude())
                            .longitude(p.getLongitude())
                            .pinCategory(p.getPinCategory().name())
                            .address(p.getAddress())
                            .saved(saved)
                            .build();
                })
                .collect(Collectors.toList());

        return PlaceResponseDTO.MapPlacesResultDTO.builder()
                .places(list)
                .build();
    }
}
