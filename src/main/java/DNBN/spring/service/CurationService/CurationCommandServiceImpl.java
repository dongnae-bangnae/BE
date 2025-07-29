package DNBN.spring.service.CurationService;

import DNBN.spring.converter.CurationConverter;
import DNBN.spring.domain.Curation;
import DNBN.spring.domain.Member;
import DNBN.spring.domain.Place;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.domain.mapping.CurationPlace;
import DNBN.spring.domain.mapping.LikeRegion;
import DNBN.spring.repository.CurationPlaceRepository.CurationPlaceRepository;
import DNBN.spring.repository.CurationRepository.CurationRepository;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.web.dto.response.CurationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurationCommandServiceImpl implements CurationCommandService {
    private final LikeRegionRepository likeRegionRepository;
    private final PlaceRepository placeRepository;
    private final CurationRepository curationRepository;
    private final CurationPlaceRepository curationPlaceRepository;
    private final MemberRepository memberRepository;

    @Override
    public CurationResponseDTO generateCuration(Member member) {
        // 관심 지역 3개 중 첫번째 관심지역 가져오기
        List<LikeRegion> likeRegions = likeRegionRepository.findByMember(member);
        List<Long> regionIds = likeRegions.stream()
                .map(lr -> lr.getRegion().getId())
                .toList();

        // 관심 지역의 Place 중, 게시글이 1개 이상 있는 Place 조회
        List<Place> candidatePlaces = placeRepository.findPlacesWithArticlesByRegionIds(regionIds);

        // 카테고리 별로 중복 없이 필터링
        Map<PinCategory, Place> categoryToPlace = new HashMap<>();
        for (Place place : candidatePlaces) {
            if (!categoryToPlace.containsKey(place.getPinCategory())) {
                categoryToPlace.put(place.getPinCategory(), place);
            }
            if (categoryToPlace.size() >= 5) break;
        }

        // 게시물이 3개 이상일 때만 큐레이션 생성
        if (categoryToPlace.size() < 3) {
            throw new IllegalStateException("큐레이션 생성을 위한 게시글이 충분하지 않습니다.");
        }

        // 큐레이션 저장
        Curation curation = Curation.builder()
                .createdAt(LocalDate.now())
                .build();
        curation = curationRepository.save(curation);

        // 장소 저장
        for (Place place : categoryToPlace.values()) {
            CurationPlace cp = CurationPlace.builder()
                    .curation(curation)
                    .place(place)
                    .build();
            cp = curationPlaceRepository.save(cp);
        }

        return CurationConverter.toCurationResponseDTO(curation);
    }
}
