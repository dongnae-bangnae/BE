package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByProvinceAndCityAndDistrict(String province, String city, String district);

    List<Region> findByDistrictContaining(String keyword);
}