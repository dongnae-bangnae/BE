package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;

import java.util.List;

public interface RegionRepositoryCustom {
    List<Region> searchByKeyword(String keyword, Long cursor, int limit);
}
