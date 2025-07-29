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
import java.util.*;

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

        // 관심 지역에 속한 모든 Place 조회
        List<Place> places = placeRepository.findByRegionIdIn(regionIds);
        if (places.size() < 3) {
            throw new IllegalStateException("큐레이션 생성을 위한 장소가 부족합니다.");
        }

        // 랜덤으로 3개 선택
        Collections.shuffle(places);
        List<Place> selectedPlaces = places.subList(0, 3);

        // 큐레이션 저장
        Curation curation = Curation.builder()
                .createdAt(LocalDate.now())
                .build();
        curation = curationRepository.save(curation);

        // 장소 저장
        for (Place place : selectedPlaces) {
            CurationPlace cp = CurationPlace.builder()
                    .curation(curation)
                    .place(place)
                    .build();

            curation.getCurationPlaces().add(cp);
            curationPlaceRepository.save(cp);
        }

        Curation fetched = curationRepository.findByIdWithPlaces(curation.getCurationId())
                .orElseThrow(() -> new IllegalStateException("큐레이션 조회 실패"));

        return CurationConverter.toCurationResponseDTO(fetched);
    }
}
