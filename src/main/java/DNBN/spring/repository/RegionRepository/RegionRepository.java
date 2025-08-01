package DNBN.spring.repository.RegionRepository;

import DNBN.spring.domain.Region;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RegionRepository extends JpaRepository<Region, Long>, RegionRepositoryCustom {
  Optional<Region> findByProvinceAndCityAndDistrict(String province, String city, String district);

  @Query("SELECT r FROM Region r WHERE (SELECT COUNT(p) FROM Place p WHERE p.region = r) >= 3")
  List<Region> findRegionsWithAtLeastThreePlaces();

}
