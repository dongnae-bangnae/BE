package DNBN.spring.service.PlaceService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.GeneralException;
import DNBN.spring.apiPayload.exception.handler.CategoryHandler;
import DNBN.spring.apiPayload.exception.handler.MemberHandler;
import DNBN.spring.apiPayload.exception.handler.PlaceHandler;
import DNBN.spring.domain.Category;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.mapping.SavePlace;
import DNBN.spring.repository.CategoryRepository.CategoryRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Override
    public PlaceResponseDTO.SavePlaceResultDTO savePlaceToCategory(Long memberId, Long placeId, PlaceRequestDTO.SavePlaceDTO request) {
        // 1. 장소 조회
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_NOT_FOUND));

        // 2. 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 3. 사용자 본인 카테고리인지 확인
        Category category = categoryRepository
                .findByCategoryIdAndMemberAndDeletedAtIsNull(request.getCategoryId(), member)
                .orElseThrow(() -> new CategoryHandler(ErrorStatus._FORBIDDEN));

        // 4. 이미 다른 카테고리에 저장된 장소인지 검사
        if (savePlaceRepository.existsByPlace(place)) {
            throw new PlaceHandler(ErrorStatus.CATEGORY_ALREADY_SAVED_FOR_PLACE);
        }

        // 5. 저장
        SavePlace savePlace = SavePlace.of(place, category);
        savePlaceRepository.save(savePlace);

        // 6. DTO 반환
        return PlaceResponseDTO.SavePlaceResultDTO.builder()
                .placeId(place.getPlaceId())
                .categoryId(category.getCategoryId())
                .build();
    }
}
