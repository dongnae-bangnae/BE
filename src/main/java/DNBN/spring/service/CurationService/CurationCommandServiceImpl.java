package DNBN.spring.service.CurationService;

import DNBN.spring.apiPayload.code.status.ErrorStatus;
import DNBN.spring.apiPayload.exception.handler.CurationHandler;
import DNBN.spring.converter.CurationConverter;
import DNBN.spring.domain.*;
import DNBN.spring.domain.enums.PinCategory;
import DNBN.spring.domain.mapping.CurationPlace;
import DNBN.spring.domain.mapping.LikeRegion;
import DNBN.spring.repository.ArticlePhotoRepository.ArticlePhotoRepository;
import DNBN.spring.repository.ArticleRepository.ArticleRepository;
import DNBN.spring.repository.CurationPlaceRepository.CurationPlaceRepository;
import DNBN.spring.repository.CurationRepository.CurationRepository;
import DNBN.spring.repository.LikeRegionRepository.LikeRegionRepository;
import DNBN.spring.repository.MemberRepository.MemberRepository;
import DNBN.spring.repository.PlaceRepository.PlaceRepository;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.response.CurationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static DNBN.spring.domain.QRegion.region;

@Service
@RequiredArgsConstructor
public class CurationCommandServiceImpl implements CurationCommandService {
    private final RegionRepository regionRepository;
    private final PlaceRepository placeRepository;
    private final CurationRepository curationRepository;
    private final CurationPlaceRepository curationPlaceRepository;
    private final ArticleRepository articleRepository;
    private final ArticlePhotoRepository articlePhotoRepository;

    @Override
    public List<CurationResponseDTO> generateCurations() {
        // 장소 3개 이상인 지역 조회
        List<Region> candidateRegions = regionRepository.findRegionsWithAtLeastThreePlaces();
        if (candidateRegions.isEmpty()) {
            throw new CurationHandler(ErrorStatus.CURATION_NO_VALID_REGION);
        }

        System.out.println(candidateRegions.size());

        // 랜덤 지역 선택
        Collections.shuffle(candidateRegions);
        List<Region> selectedRegions = candidateRegions.subList(0, Math.min(5, candidateRegions.size()));

        LocalDate startOfWeek = getStartOfThisWeek();
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<CurationResponseDTO> resultList = new ArrayList<>();

        // 이번 주에 생성된 큐레이션이 있는지 확인
        for (Region region : selectedRegions) {
            // 해당 지역에 이미 이번 주 큐레이션이 있는지 확인
            Optional<Curation> existing = curationRepository.findByRegionAndCreatedAtBetween(region, startOfWeek, endOfWeek);
            if (existing.isPresent()) {
                resultList.add(CurationConverter.toCurationResponseDTO(existing.get()));
                continue;
            }

            // 해당 지역의 장소 조회
            List<Place> places = placeRepository.findAllByRegion(region);
            if (places.size() < 3) continue;

            Collections.shuffle(places);
            List<Place> selectedPlaces = places.subList(0, 3);

            // 썸네일 이미지
            String thumbnailImageUrl = null;
            Place firstPlace = selectedPlaces.get(0);
            List<Article> articles = articleRepository.findByPlaceOrderByCreatedAtAsc(firstPlace);
            if (!articles.isEmpty()) {
                List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(articles.get(0));
                if (!photos.isEmpty()) {
                    thumbnailImageUrl = photos.get(0).toString(); // 실제 이미지 URL 필드 사용
                }
            }

            // 큐레이션 저장
            Curation curation = Curation.builder()
                    .createdAt(LocalDate.now())
                    .region(region)
                    .title("이번주 테스트 큐레이션")
                    .thumbnailImageUrl(thumbnailImageUrl)
                    .likeCount(0L)
                    .commentCount(0L)
                    .build();
            curation = curationRepository.save(curation);

            // 장소 매핑 저장
            for (Place place : selectedPlaces) {
                CurationPlace cp = CurationPlace.builder()
                        .curation(curation)
                        .place(place)
                        .build();
                curation.getCurationPlaces().add(cp);
                curationPlaceRepository.save(cp);
            }

            // DTO 변환 및 추가
            List<CurationResponseDTO.Places> dtoList = selectedPlaces.stream()
                    .map(place -> CurationResponseDTO.Places.builder()
                            .likePlaceId(place.getPlaceId())
                            .name(place.getTitle())
                            .pinCategory(place.getPinCategory().name())
                            .build())
                    .toList();

            CurationResponseDTO dto = CurationResponseDTO.builder()
                    .curationId(curation.getCurationId())
                    .regionId(region.getId())
                    .regionName(region.getFullName())
                    .title(curation.getTitle())
                    .createdAt(curation.getCreatedAt())
                    .likeCount(curation.getLikeCount())
                    .commentCount(curation.getCommentCount())
                    .likePlaces(dtoList)
                    .build();

            resultList.add(dto);
        }

        return resultList;
    }

    @Override
    public LocalDate getStartOfThisWeek() {
        return LocalDate.now().with(java.time.DayOfWeek.MONDAY);
    }
}
