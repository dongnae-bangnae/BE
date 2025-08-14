package DNBN.spring.service.RegionService;

import DNBN.spring.converter.RegionConverter;
import DNBN.spring.domain.Region;
import DNBN.spring.repository.RegionRepository.RegionRepository;
import DNBN.spring.web.dto.RegionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RegionQueryServiceImpl implements RegionQueryService {

    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;

    @Override
    public RegionResponseDTO.SearchRegionResult searchRegion(String keyword, Long cursor, int limit) {
        List<Region> regions = regionRepository.searchByKeyword(keyword, cursor, limit);
        List<RegionResponseDTO.RegionFullNameDTO> previews = regions.stream()
                .map(regionConverter::toRegionPreviewDTO)
                .toList();

        Long nextCursor = previews.isEmpty() ? null : previews.get(previews.size() - 1).getRegionId();
        boolean hasNext = previews.size() == limit;

        return RegionResponseDTO.SearchRegionResult.builder()
                .regions(previews)
                .cursor(nextCursor)
                .limit((long) limit)
                .hasNext(hasNext)
                .build();
    }
}

