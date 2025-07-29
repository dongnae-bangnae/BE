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
    public CurationResponseDTO generateCuration() {
        // 장소 3개 이상인 지역 조회
        List<Region> candidateRegions = regionRepository.findRegionsWithAtLeastThreePlaces();
        if (candidateRegions.isEmpty()) {
            throw new CurationHandler(ErrorStatus.CURATION_NO_VALID_REGION);
        }

        // 랜덤 지역 선택
        Collections.shuffle(candidateRegions);
        Region region = candidateRegions.get(0);

        LocalDate startOfWeek = getStartOfThisWeek();
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // 이번 주에 생성된 큐레이션이 있는지 확인
        Optional<Curation> existing = curationRepository.findByRegionAndCreatedAtBetween(
                region, startOfWeek, endOfWeek
        );
        if (existing.isPresent()) {
            return CurationConverter.toCurationResponseDTO(existing.get());
        }

        // 관심 지역에 속한 모든 Place 조회
        List<Place> places = placeRepository.findAllByRegion(region);
        if (places.size() < 3) {
            throw new CurationHandler(ErrorStatus.CURATION_NOT_ENOUGH_PLACES);
        }

        // 랜덤으로 3개 선택
        Collections.shuffle(places);
        List<Place> selectedPlaces = places.subList(0, 3);

        // 썸네일 이미지
        Place firstPlace = selectedPlaces.get(0);
        String thumbnailImageUrl = null;

        // 첫번째 장소의 첫번째 게시물 썸네일 이미지 가져오기
        List<Article> articles = articleRepository.findByPlaceOrderByCreatedAtAsc(firstPlace);

        System.out.println("📄 게시물 수 = " + articles.size());

        if (!articles.isEmpty()) {
            List<ArticlePhoto> photos = articlePhotoRepository.findAllByArticle(articles.get(0));

            System.out.println("🖼️ 대표 이미지 개수 = " + photos.size());
            if (!photos.isEmpty()) {
                thumbnailImageUrl = photos.get(0).toString();
            }
        }

        // 큐레이션 저장
        Curation curation = Curation.builder()
                .createdAt(LocalDate.now())
                .region(region)
                .title("이번주 테스트 큐레이션") // 필요시 동적으로 생성 가능
                .thumbnailImageUrl(thumbnailImageUrl)
                .likeCount(0L)
                .commentCount(0L)
                .build();
        curation = curationRepository.save(curation);

        // 큐레이션 장소 매핑 저장
        for (Place place : selectedPlaces) {
            CurationPlace cp = CurationPlace.builder()
                    .curation(curation)
                    .place(place)
                    .build();
            curation.getCurationPlaces().add(cp); // 중요: 양방향 연결
            curationPlaceRepository.save(cp);
        }

        // 응답 DTO 구성
        List<CurationResponseDTO.Places> dtoList = selectedPlaces.stream()
                .map(place -> CurationResponseDTO.Places.builder()
                        .likePlaceId(place.getPlaceId())
                        .name(place.getTitle())
                        .pinCategory(place.getPinCategory().name())
                        .build())
                .toList();

        return CurationResponseDTO.builder()
                .curationId(curation.getCurationId())
                .regionId(region.getId())
                .regionName(region.getFullName())
                .title(curation.getTitle())
                .createdAt(curation.getCreatedAt())
                .likeCount(curation.getLikeCount())
                .commentCount(curation.getCommentCount())
                .likePlaces(dtoList)
                .build();
    }

    @Override
    public LocalDate getStartOfThisWeek() {
        return LocalDate.now().with(java.time.DayOfWeek.MONDAY);
    }
}
