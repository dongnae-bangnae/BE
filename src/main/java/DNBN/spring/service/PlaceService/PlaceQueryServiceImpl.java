package DNBN.spring.service.PlaceService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.converter.PlaceConverter;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.mapping.SavePlace;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepositoryCustom;
import DNBN.spring.repository.SavePlaceRepository.SavePlaceRepository;
import DNBN.spring.repository.SavePlaceRepository.SavePlaceRepositoryImpl;
import DNBN.spring.web.dto.PlaceRequestDTO;
import DNBN.spring.web.dto.PlaceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PlaceQueryServiceImpl implements PlaceQueryService {

    private final CategoryRepository categoryRepository;
    private final SavePlaceRepository savePlaceRepository;
    private final PlaceRepositoryCustom placeRepositoryCustom;

    @Override
    public PlaceResponseDTO.SavedPlaceListDTO getSavedPlaces(Long categoryId, Long memberId, Long cursor, Long limit) {
        // 1. 카테고리 본인 것인지 확인
        Category category = categoryRepository
                .findByCategoryIdAndMemberAndDeletedAtIsNull(categoryId, Member.builder().id(memberId).build())
                .orElseThrow(() -> new CategoryHandler(ErrorStatus._FORBIDDEN));

        // 2. 저장된 장소 조회 (커서 기반)
        List<SavePlace> savePlaces = savePlaceRepository.findSavedPlaces(categoryId, cursor, limit + 1);

        boolean hasNext = savePlaces.size() > limit;
        if (hasNext) savePlaces.remove(savePlaces.size() - 1);

        // 3. 변환 (컨버터 사용)
        List<PlaceResponseDTO.SavedPlacePreviewDTO> places = PlaceConverter.toSavedPlacePreviewDTOList(savePlaces);
        Long nextCursor = savePlaces.isEmpty() ? null : savePlaces.get(savePlaces.size() - 1).getId();

        return PlaceResponseDTO.SavedPlaceListDTO.builder()
                .places(places)
                .cursor(nextCursor)
                .limit(limit)
                .hasNext(hasNext)
                .build();
    }

//    @Override
//    public PlaceResponseDTO.MapPlacesResultDTO getPlacesInMapBounds(PlaceRequestDTO.MapSearchDTO request) {
//        List<Place> places = placeRepositoryCustom.findAllInBounds(
//                request.getLatMin(), request.getLatMax(),
//                request.getLngMin(), request.getLngMax()
//        );
//        return PlaceConverter.toMapPlacesResult(places);
//    }

    @Override
    public PlaceResponseDTO.MapPlacesResultDTO getPlacesInMapBounds(
            Long memberId, Double latMin, Double latMax, Double lngMin, Double lngMax
    ) {
        List<Place> places = placeRepositoryCustom.findAllInBounds(
                latMin, latMax, lngMin, lngMax
        );

        return PlaceConverter.toMapPlacesResult(
                places, memberId, savePlaceRepository
        );
    }
}
