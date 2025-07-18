package DNBN.spring.service.PlaceService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.mapping.SavePlace;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.SavePlaceRepository.SavePlaceRepository;
import DNBN.spring.web.dto.PlaceRequestDTO;
import DNBN.spring.web.dto.PlaceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PlaceCommandServiceImpl implements PlaceCommandService {

    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final SavePlaceRepository savePlaceRepository;

    @Override
    public PlaceResponseDTO.SavePlaceResultDTO savePlaceToCategory(Long memberId, Long placeId, PlaceRequestDTO.SavePlaceRequestDTO request) {

        // 1. 장소 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.REGION_NOT_FOUND)); // 필요시 PLACE_NOT_FOUND로 수정

        // 2. 사용자 본인 카테고리인지 확인
        Category category = categoryRepository.findByCategoryIdAndMemberAndDeletedAtIsNull(request.getCategoryId(), place.getRegion().getMember())
                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));

        if (!category.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 3. 중복 저장 여부 확인
        if (savePlaceRepository.existsByPlaceAndCategory(place, category)) {
            throw new GeneralException(ErrorStatus.CATEGORY_ALREADY_SAVED_FOR_PLACE);
        }

        // 4. 저장
        SavePlace savePlace = SavePlace.of(place, category);
        savePlaceRepository.save(savePlace);

        // 5. DTO 반환
        return PlaceResponseDTO.SavePlaceResultDTO.builder()
                .placeId(place.getPlaceId())
                .categoryId(category.getCategoryId())
                .build();
    }
}
